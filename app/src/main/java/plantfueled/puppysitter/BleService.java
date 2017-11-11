package plantfueled.puppysitter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

import static android.content.Context.BLUETOOTH_SERVICE;

/**
 * Created by Gabriel on 2017-10-29.
 */

public class BleService {

    private static final String TAG = "BleService";

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBtGatt;
    private Activity mActivity;
    private Context mAppContext;
    private TextView distanceText;

    private LinkedList<Integer> rssiTrend = new LinkedList<>();

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public BleService(Activity activity, Context appContext){

        mActivity = activity;
        mAppContext = appContext;

        distanceText = mActivity.findViewById(R.id.distanceText);

        BluetoothManager manager = (BluetoothManager) mActivity.getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.d(TAG, "New LE Device " + device.getName() + "@" + rssi);

            if("Puppyboard".equals(device.getName())){
                mBtGatt = device.connectGatt(mAppContext, true, mGattCallback);
            }
        }
    };

    public void scanLeDevice() {
        new Thread() {

            @Override
            public void run() {
                mBluetoothAdapter.startLeScan(mLeScanCallback);

                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }.start();
    }

    private final BluetoothGattCallback mGattCallback =
            new BluetoothGattCallback() {

                @Override
                public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status){
                    if(rssiTrend.size() > 5){
                        rssiTrend.remove();
                    }
                    rssiTrend.add(rssi);

                    int average = 0;
                    ArrayList rssiList = new ArrayList(rssiTrend);
                    for(int i = 0; i < rssiList.size(); i++){
                        average += (int)rssiList.get(i);
                    }
                    average /= rssiList.size();

                    Handler handler = new Handler(Looper.getMainLooper());
                    if(average > -58){
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

                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    String intentAction;
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        intentAction = ACTION_GATT_CONNECTED;
                        mConnectionState = STATE_CONNECTED;
                        broadcastUpdate(intentAction);
                        Log.i(TAG, "Connected to GATT server.");
                        Log.i(TAG, "Attempting to start service discovery:" +
                                mBtGatt.discoverServices());
                        rssiThread.start();

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        intentAction = ACTION_GATT_DISCONNECTED;
                        mConnectionState = STATE_DISCONNECTED;
                        Log.i(TAG, "Disconnected from GATT server.");
                        broadcastUpdate(intentAction);
                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                    } else {
                        Log.w(TAG, "onServicesDiscovered received: " + status);
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                    }
                }
            };
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        mActivity.sendBroadcast(intent);
    }
    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for(byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
                    stringBuilder.toString());
        }
        mActivity.sendBroadcast(intent);
    }

    public void btCheck(){
        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivity(enableBtIntent);
            mActivity.finish();
            return;
        }

        if(!mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mActivity, "NO LE SUPPORT", Toast.LENGTH_SHORT).show();
            mActivity.finish();
            return;
        }
    }

    Thread rssiThread = new Thread() {
        @Override
        public void run() {
            try {
                while(true) {
                    sleep(200);
                    if(!mBtGatt.readRemoteRssi()){
                        //There was a problem reading RSSI
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
}
