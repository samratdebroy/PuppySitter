package plantfueled.puppysitter.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import plantfueled.puppysitter.R;

/**
 * Created by Simon on 11/10/2017.
 */

public abstract class BluetoothActivity extends AppCompatActivity implements BluetoothAdapter.LeScanCallback {

    public enum BluetoothState {
        BLUETOOTH_UNAVAILABLE,
        BLUETOOTH_DISABLED,
        BLUETOOTH_SCANNING,
        BLUETOOTH_CONNECTED,
    }

    private static final String SerialPortUUID="0000dfb1-0000-1000-8000-00805f9b34fb";
    private static final String TAG = "BT-ACT";
    private static final String DEVICE_NAME = "Puppyboard";
    private static final int REQUEST_ENABLE_BT = 1337;

    private TextView distanceText;

    private Handler scanHandler;

    private BluetoothState currentState = BluetoothState.BLUETOOTH_DISABLED;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice blunoBoard;
    private BluetoothGatt blunoGatt;
    private BluetoothGattCharacteristic serialCharacteristic;

    private List<BluetoothGattService> services;

    private LinkedList<Integer> RSSITrend = new LinkedList<>();
    private static final int trendSize = 5;
    private static final int averageTargetRssi = -58;


    public BluetoothActivity() {}

    public void bluetoothInit() {
        scanHandler = new Handler();
        setupUI();

        // Get bluetooth adapter
        bluetoothAdapter = ((BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else {
            scanForBLEDevice();
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

    private void setupUI() {
        distanceText = (TextView) findViewById(R.id.distanceText);
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
            scanHandler.removeCallbacksAndMessages(null);
            bluetoothAdapter.stopLeScan(this);
            currentState = BluetoothState.BLUETOOTH_DISABLED;
        }
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (DEVICE_NAME.equals(device.getName())) {
            //Found the Puppboard and attempt connection
            stopScanForBLEDevice();
            blunoBoard = device;
            blunoGatt = device.connectGatt(this, true, new BluetoothDataHandler());
        }
    }

    private class BluetoothDataHandler extends BluetoothGattCallback {
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                services = gatt.getServices();
                currentState = BluetoothState.BLUETOOTH_CONNECTED;

                serviceLoop:
                for (BluetoothGattService service : services) {
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        if (SerialPortUUID.equals(characteristic.getUuid().toString())) {
                            serialCharacteristic = characteristic;
                            blunoGatt.setCharacteristicNotification(serialCharacteristic, true);
                            onBluetoothSuccess();
                            break serviceLoop;
                        }
                    }
                }
            }
            else {
                onError();
            }
            rssiThread.start();
        }
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                currentState = BluetoothState.BLUETOOTH_CONNECTED;
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" + blunoGatt.discoverServices());

            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                currentState = BluetoothState.BLUETOOTH_DISABLED;
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                onSoundReceived();
            }
        }
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if(SerialPortUUID.equals(characteristic.getUuid().toString())){
                blunoGatt.readCharacteristic(characteristic);
            }
        }
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status){
            if(RSSITrend.size() > trendSize){
                RSSITrend.remove();
            }
            RSSITrend.add(rssi);

            //RSSI and Distance Algorithm
            int average = 0;
            for(int i = 0; i < RSSITrend.size(); i++){
                average += RSSITrend.get(i);
            }
            average /= RSSITrend.size();

            Handler handler = new Handler(Looper.getMainLooper());
            if(average > averageTargetRssi){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        distanceText.setText("NEAR");
                    }
                });

            }
            else{
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        distanceText.setText("FAR");
                    }
                });
            }
        }
    }

    Thread rssiThread = new Thread() {
        @Override
        public void run() {
            try {
                while(true) {
                    sleep(200);
                    if(!blunoGatt.readRemoteRssi()){
                        //There was a problem reading RSSI
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    public abstract void onBluetoothSuccess();
    public abstract void onSoundReceived();
    public abstract void onBluetoothFailure();

}
