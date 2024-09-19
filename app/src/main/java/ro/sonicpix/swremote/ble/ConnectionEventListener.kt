package ro.sonicpix.swremote.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor

/** A listener containing callback methods to be registered with [ConnectionManager].*/
class ConnectionEventListener {
    var onConnectionSetupComplete: ((gatt: BluetoothGatt) -> Unit)? = null

    var onDisconnect: ((device: BluetoothDevice) -> Unit)? = null

    var onDescriptorRead: (
        (
        device: BluetoothDevice,
        descriptor: BluetoothGattDescriptor,
        value: ByteArray
    ) -> Unit
    )? = null

    var onDescriptorWrite: (
        (
        device: BluetoothDevice,
        descriptor: BluetoothGattDescriptor
    ) -> Unit
    )? = null

    var onCharacteristicChanged: (
        (
        device: BluetoothDevice,
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray
    ) -> Unit
    )? = null

    var onCharacteristicRead: (
        (
        device: BluetoothDevice,
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray
    ) -> Unit
    )? = null

    var onCharacteristicWrite: (
        (
        device: BluetoothDevice,
        characteristic: BluetoothGattCharacteristic
    ) -> Unit
    )? = null

    var onNotificationsEnabled: (
        (
        device: BluetoothDevice,
        characteristic: BluetoothGattCharacteristic
    ) -> Unit
    )? = null

    var onNotificationsDisabled: (
        (
        device: BluetoothDevice,
        characteristic: BluetoothGattCharacteristic
    ) -> Unit
    )? = null

    var onMtuChanged: ((device: BluetoothDevice, newMtu: Int) -> Unit)? = null
}
