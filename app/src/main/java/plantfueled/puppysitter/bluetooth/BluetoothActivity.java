package plantfueled.puppysitter.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import java.util.List;

/**
 * Created by Simon on 11/10/2017.
 */

public class BluetoothActivity extends Activity implements BluetoothAdapter.LeScanCallback {

    public enum BluetoothState {
        BLUETOOTH_UNAVAILABLE,
        BLUETOOTH_DISABLED,
        BLUETOOTH_SCANNING,
        BLUETOOTH_CONNECTED,
    }

    private static final String DEVICE_NAME = "Puppyboard";
    private static final int REQUEST_ENABLE_BT = 1337;

    private Handler scanHandler;

    private BluetoothState currentState;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice blunoBoard;
    private BluetoothGatt blunoGatt;

    private List<BluetoothGattService> services;

    public BluetoothActivity() {}

    public void bluetoothInit() {
        scanHandler = new Handler();

        // Get bluetooth adapter
        bluetoothAdapter = ((BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else {
            currentState = BluetoothState.BLUETOOTH_UNAVAILABLE;
        }
    }

    public boolean isAvailable() { return currentState != BluetoothState.BLUETOOTH_UNAVAILABLE; }
    public boolean isScanning() { return currentState == BluetoothState.BLUETOOTH_SCANNING; }
    public boolean isConnected() { return currentState == BluetoothState.BLUETOOTH_CONNECTED; }

    public List<BluetoothGattService> getGATTServices() { return services; }

    private void onError() {
        // TODO disconnect EVERYTHING
        currentState = BluetoothState.BLUETOOTH_DISABLED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Do something idk ye
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            scanForBLEDevice();
        }
        else {
            currentState = BluetoothState.BLUETOOTH_DISABLED;
        }
    }

    public void scanForBLEDevice() {
        if (currentState == BluetoothState.BLUETOOTH_DISABLED) {
            scanHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScanForBLEDevice();
                }
            }, 10000);
            currentState = BluetoothState.BLUETOOTH_SCANNING;
            bluetoothAdapter.startLeScan(this);
        }
    }

    public void stopScanForBLEDevice() {
        if (currentState == BluetoothState.BLUETOOTH_SCANNING) {
            bluetoothAdapter.stopLeScan(this);
            currentState = BluetoothState.BLUETOOTH_DISABLED;
        }
    }


    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device.getName().equals(DEVICE_NAME)) {
            blunoBoard = device;
            blunoGatt = device.connectGatt(this, true, new BluetoothDataHandler());
            blunoGatt.discoverServices();
        }
    }

    private class BluetoothDataHandler extends BluetoothGattCallback {
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                services = gatt.getServices();
                currentState = BluetoothState.BLUETOOTH_CONNECTED;
            }
            else {
                onError();
            }
        }
    }

}
