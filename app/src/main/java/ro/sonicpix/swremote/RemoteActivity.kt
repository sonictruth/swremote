/*
 *
 */

package ro.sonicpix.swremote

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import ro.sonicpix.swremote.ble.ConnectionEventListener
import ro.sonicpix.swremote.ble.ConnectionManager
import ro.sonicpix.swremote.ble.ConnectionManager.parcelableExtraCompat
import ro.sonicpix.swremote.ble.toHexString
import ro.sonicpix.swremote.databinding.ActivityRemoteBinding
import timber.log.Timber
import java.util.UUID

const val notificationChrUUID = "0000ff02-0000-1000-8000-00805f9b34fb"
const val commandChrUUID = "0000ff01-0000-1000-8000-00805f9b34fb"

val notificationFocusLost = byteArrayOf(0x02, 0x3F, 0x00);
val notificationFocusReady = byteArrayOf(0x02, 0x3F, 0x20);
val notificationFocusBusy = byteArrayOf(0x02, 0x3F, 0x40);
val notificationShutterReady = byteArrayOf(0x02, 0xA0.toByte(), 0x00);
val notificationShutterActive = byteArrayOf(0x02, 0xA0.toByte(), 0x20);
val notificationRecordingStopped = byteArrayOf(0x02, 0xD5.toByte(), 0x00);
val notificationRecordingStarted = byteArrayOf(0x02, 0xD5.toByte(), 0x20);

var cmdFocusUp = byteArrayOf(0x01, 0x06)
var cmdFocusDown = byteArrayOf(0x01, 0x07)

var cmdShutterUp = byteArrayOf(0x01, 0x08)
var cmdShutterDown = byteArrayOf(0x01, 0x09)

var cmdC1Up = byteArrayOf(0x01, 0x20)
var cmdC1Down = byteArrayOf(0x01, 0x21)

var cmdRecordUp = byteArrayOf(0x01, 0x0e)
var cmdRecordDown = byteArrayOf(0x01, 0x0f)

var cmdAfOnUp = byteArrayOf(0x01, 0x14)
var cmdAfOnDown = byteArrayOf(0x01, 0x15)

var cmdZoomInUp = byteArrayOf(0x02, 0x44, 0x00)
var cmdZoomInDown = byteArrayOf(0x02, 0x45, 0x20)
var cmdZoomOutUp = byteArrayOf(0x02, 0x46, 0x00)
var cmdZoomOutDown = byteArrayOf(0x02, 0x47, 0x20)

var cmdFocusOutUp = byteArrayOf(0x02, 0x6a, 0x00)
var cmdFocusOutDown = byteArrayOf(0x02, 0x6b, 0x20)
var cmdFocusInUp = byteArrayOf(0x02, 0x6c, 0x00)
var cmdFocusInDown = byteArrayOf(0x02, 0x6d, 0x20)


class RemoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRemoteBinding

    private var isFocusReady = false;
    private var isShutterReady = true;
    private var isRecording = false;

    private val device: BluetoothDevice by lazy {
        intent.parcelableExtraCompat(BluetoothDevice.EXTRA_DEVICE)
            ?: error("Missing BluetoothDevice from MainActivity!")
    }

    private val notifyingCharacteristics = mutableListOf<UUID>()

    private lateinit var notificationCharacteristic: BluetoothGattCharacteristic

    private lateinit var commandCharacteristic: BluetoothGattCharacteristic

    private val characteristics by lazy {
        ConnectionManager.servicesOnDevice(device)?.flatMap { service ->
            service.characteristics ?: listOf()
        } ?: listOf()
    }

    private fun sendCommand (command: ByteArray) {
        ConnectionManager.writeCharacteristic(device, commandCharacteristic, command)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConnectionManager.registerListener(connectionEventListener)

        binding = ActivityRemoteBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = getString(R.string.title)
        }

        binding.shutterButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    sendCommand(cmdShutterUp)
                    sendCommand(cmdFocusUp)
                    false
                }
                MotionEvent.ACTION_DOWN -> {
                    sendCommand(cmdFocusDown)
                    sendCommand(cmdShutterDown)
                    false
                }
                else -> false
            }
        }

        binding.customButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    sendCommand(cmdC1Up)
                    false
                }
                MotionEvent.ACTION_DOWN -> {
                    sendCommand(cmdC1Down)
                    false
                }
                else -> false
            }
        }

        binding.zoomInButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    sendCommand(cmdZoomInUp)
                    false
                }
                MotionEvent.ACTION_DOWN -> {
                    sendCommand(cmdZoomInDown)
                    false
                }
                else -> false
            }
        }

        binding.zoomOutButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    sendCommand(cmdZoomOutUp)
                    false
                }
                MotionEvent.ACTION_DOWN -> {
                    sendCommand(cmdZoomOutDown)
                    false
                }
                else -> false
            }
        }

        binding.focusInButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    sendCommand(cmdFocusInUp)
                    false
                }
                MotionEvent.ACTION_DOWN -> {
                    sendCommand(cmdFocusInDown)
                    false
                }
                else -> false
            }
        }

        binding.focusOutButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    sendCommand(cmdFocusOutUp)
                    false
                }
                MotionEvent.ACTION_DOWN -> {
                    sendCommand(cmdFocusOutDown)
                    false
                }
                else -> false
            }
        }

        binding.afOnButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    sendCommand(cmdAfOnUp)
                    false
                }
                MotionEvent.ACTION_DOWN -> {
                    sendCommand(cmdAfOnDown)
                    false
                }
                else -> false
            }
        }

        binding.recordButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    sendCommand(cmdRecordUp)
                    false
                }
                MotionEvent.ACTION_DOWN -> {
                    sendCommand(cmdRecordDown)
                    false
                }
                else -> false
            }
        }


        initCharacteristics()
    }

    private fun initCharacteristics() {
        notificationCharacteristic = characteristics
            .find { it.uuid == UUID.fromString(notificationChrUUID) }!!
        commandCharacteristic = characteristics
            .find { it.uuid == UUID.fromString(commandChrUUID) }!!
        ConnectionManager.enableNotifications(device, notificationCharacteristic)
    }

    override fun onDestroy() {
        ConnectionManager.unregisterListener(connectionEventListener)
        ConnectionManager.teardownConnection(device)
        super.onDestroy()
    }

    private fun log(message: String) {
        Timber.d(message)
    }

    private val connectionEventListener by lazy {
        ConnectionEventListener().apply {
            onDisconnect = {
                runOnUiThread {
                    AlertDialog.Builder(this@RemoteActivity)
                        .setTitle("Disconnected")
                        .setMessage("Disconnected from device.")
                        .setPositiveButton("OK") { _, _ -> onBackPressed() }
                        .show()
                }

            }

            onCharacteristicRead = { _, characteristic, value ->
                log("Read from ${characteristic.uuid}: ${value.toHexString()}")
            }

            onCharacteristicWrite = { _, characteristic ->
                log("Wrote to ${characteristic.uuid}")
            }

            onMtuChanged = { _, mtu ->
                log("MTU updated to $mtu")
            }

            onCharacteristicChanged = { _, _, value ->
                when {
                    value.contentEquals(notificationFocusLost) -> {
                        isFocusReady = false;
                        binding.focusView.text = getString(R.string.focus_lost)
                    }

                    value.contentEquals(notificationFocusReady) -> {
                        isFocusReady = true;
                        binding.focusView.text = getString(R.string.focus_ready)
                    }

                    value.contentEquals(notificationFocusBusy) -> {
                        isFocusReady = false;
                        binding.focusView.text = getString(R.string.focus_busy)
                    }

                    value.contentEquals(notificationShutterActive) -> {
                        isShutterReady = false;
                        binding.shutterView.text = getString(R.string.shutter_active)
                    }

                    value.contentEquals(notificationShutterReady) -> {
                        isShutterReady = true;
                        binding.shutterView.text = getString(R.string.shutter_ready)
                    }

                    value.contentEquals(notificationRecordingStarted) -> {
                        isRecording = true;
                        binding.shutterView.text = getString(R.string.recording)
                    }

                    value.contentEquals(notificationRecordingStopped) -> {
                        binding.shutterView.text = ""
                    }

                    else -> {
                        log(value.toHexString());
                    }
                }
            }

            onNotificationsEnabled = { _, characteristic ->
                log("Enabled notifications on ${characteristic.uuid}")
                notifyingCharacteristics.add(characteristic.uuid)
            }

            onNotificationsDisabled = { _, characteristic ->
                log("Disabled notifications on ${characteristic.uuid}")
                notifyingCharacteristics.remove(characteristic.uuid)
            }
        }
    }
}
