package com.smartinhaler.blefinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.samsung.android.sdk.bt.gatt.BluetoothGatt;
import com.samsung.android.sdk.bt.gatt.BluetoothGattAdapter;
import com.samsung.android.sdk.bt.gatt.BluetoothGattCallback;
import com.samsung.android.sdk.bt.gatt.BluetoothGattCharacteristic;
import com.samsung.android.sdk.bt.gatt.BluetoothGattDescriptor;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class LocalBLEService extends Service {

	private static final String TAG = "LocalBLEService";
	private final IBinder mBinder = new MyBinder();
	private ArrayList<String> list = new ArrayList<String>();

	public BluetoothGatt mBluetoothGatt = null;
	private BluetoothAdapter mBtAdapter = null;
      
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Random random = new Random();
		if (random.nextBoolean()) {
			list.add("Linux");
		}
		if (random.nextBoolean()) {
			list.add("Apple");
		}
		if (random.nextBoolean()) {
			list.add("PC");
		}
		
		if (list.size() >=20 ) {
			list.remove(0);
		}
		
		return Service.START_NOT_STICKY;
		
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public class MyBinder extends Binder { 
		LocalBLEService getService () {
			return LocalBLEService.this;
		}
	}
	
	public List<String> getWordList() {
		return list;
	}
	
	public void scan(boolean start) {
		//Make sure everything is going.
		init();
		if (mBluetoothGatt == null)
            return;
        if (start) {
        	Log.d(TAG, "scan has been called");
            mBluetoothGatt.startScan();
        } else {
        	Log.d(TAG, "stop has been called");
            mBluetoothGatt.stopScan();
        }
    }
	/**
	 * Init - sets up the bluetooth listener. Should be called to get the thing started
	 * TODO: Need to tear down again when not being used
	 */
	private void init() {
		//Want to set everything up first....
        if (mBtAdapter == null) {
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBtAdapter == null)
                return;
        }

        if (mBluetoothGatt == null) {
            BluetoothGattAdapter.getProfileProxy(this, mProfileServiceListener, BluetoothGattAdapter.GATT);
        }
	}
	
	/** Connect to a specific device **/
	public void connect(BluetoothDevice device, boolean autoconnect) {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.connect(device, autoconnect);
        }
    }
	
	
	/*****
	 * Bluetooth Stuff HERE
	 */
	private BluetoothProfile.ServiceListener mProfileServiceListener = new BluetoothProfile.ServiceListener() {
        @SuppressLint("NewApi")
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothGattAdapter.GATT) {
                mBluetoothGatt = (BluetoothGatt) proxy;
                mBluetoothGatt.registerApp(mGattCallbacks);
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothGattAdapter.GATT) {
                if (mBluetoothGatt != null)
                    mBluetoothGatt.unregisterApp();

                mBluetoothGatt = null;
            }
        }
    };

    
    /** 
     * GATT Callback listener..
     */
   
    private BluetoothGattCallback mGattCallbacks = new BluetoothGattCallback() {

        @Override
        public void onScanResult(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.d(TAG, "onScanResult() - device=" + device + ", rssi=" + rssi);
            /*
            if (!checkIfBroadcastMode(scanRecord)) {
            	
            		
                Bundle mBundle = new Bundle();
                Message msg = Message.obtain(mDeviceListHandler, GATT_DEVICE_FOUND_MSG);
                mBundle.putParcelable(BluetoothDevice.EXTRA_DEVICE, device);
                mBundle.putInt(EXTRA_RSSI, rssi);
                mBundle.putInt(EXTRA_SOURCE, DEVICE_SOURCE_SCAN);
                msg.setData(mBundle);
                msg.sendToTarget();
            } else
                Log.i(TAG, "device =" + device + " is in Brodacast mode, hence not displaying");
                
                */
        }

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            Log.d(TAG, "onConnectionStateChange (" + device.getAddress() + ")");
            /*
             
            if (newState == BluetoothProfile.STATE_CONNECTED && mBluetoothGatt != null) {
                Bundle mBundle = new Bundle();
                Message msg = Message.obtain(mActivityHandler, HRP_CONNECT_MSG);
                mBundle.putString(BluetoothDevice.EXTRA_DEVICE, device.getAddress());
                msg.setData(mBundle);
                msg.sendToTarget();
                mBluetoothGatt.discoverServices(device);
            }
            if (newState == BluetoothProfile.STATE_DISCONNECTED && mBluetoothGatt != null) {
                Message msg = Message.obtain(mActivityHandler, HRP_DISCONNECT_MSG);
                msg.sendToTarget();
            } 
            */
            
        }
        

        @Override
        public void onServicesDiscovered(BluetoothDevice device, int status) {
           /* Message msg = Message.obtain(mActivityHandler, HRP_READY_MSG); */
          /*
            msg.sendToTarget();
            DummyReadForSecLevelCheck(device);
            */
        }

        public void onCharacteristicChanged(BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "onCharacteristicChanged");
            /*
             
            Bundle mBundle = new Bundle();
            Message msg = Message.obtain(mActivityHandler, HRP_VALUE_MSG);
            int hrmval = 0;
            int eeval = -1;
            ArrayList<Integer> rrinterval = new ArrayList<Integer>();
            int length = characteristic.getValue().length;
            if (isHeartRateInUINT16(characteristic.getValue()[0])) {
                Log.d(TAG, "HeartRateInUINT16");
                hrmval = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1);
            } else {
                Log.d(TAG, "HeartRateInUINT8");
                hrmval = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
            }
            Log.i(TAG, "checking eeval and rr int");
            if (isEEpresent(characteristic.getValue()[0])) {
                if (isHeartRateInUINT16(characteristic.getValue()[0])) {
                    eeval = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 3);
                    if (isRRintpresent(characteristic.getValue()[0])) {
                        int startoffset = 5;
                        for (int i = startoffset; i < length; i += 2) {
                            rrinterval.add(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, i));
                        }
                    }
                } else {
                    eeval = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 2);
                    if (isRRintpresent(characteristic.getValue()[0])) {
                        int startoffset = 4;
                        for (int i = startoffset; i < length; i += 2) {
                            rrinterval.add(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, i));
                        }
                    }
                }
            } else {
                if (isHeartRateInUINT16(characteristic.getValue()[0])) {
                    if (isRRintpresent(characteristic.getValue()[0])) {
                        int startoffset = 3;
                        for (int i = startoffset; i < length; i += 2) {
                            rrinterval.add(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, i));
                        }
                    }
                } else {
                    if (isRRintpresent(characteristic.getValue()[0])) {
                        int startoffset = 2;
                        for (int i = startoffset; i < length; i += 2) {
                            rrinterval.add(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, i));
                        }
                    }
                }

            }
            mBundle.putInt(HRM_VALUE, hrmval);
            mBundle.putInt(HRM_EEVALUE, eeval);
            mBundle.putIntegerArrayList(HRM_RRVALUE, rrinterval);
            msg.setData(mBundle);
            msg.sendToTarget();
            * 
             */
        }

        public void onCharacteristicRead(BluetoothGattCharacteristic charac, int status) {
            UUID charUuid = charac.getUuid();
            /*
            if (charUuid.equals(FIRMWARE_REVISON_UUID))
                return;
            Bundle mBundle = new Bundle();
            Message msg = Message.obtain(mActivityHandler, HRP_VALUE_MSG);
            Log.i(TAG, "onCharacteristicRead");
            if (charUuid.equals(BODY_SENSOR_LOCATION))
                mBundle.putByteArray(BSL_VALUE, charac.getValue());
            if (charUuid.equals(SERIAL_NUMBER_STRING))
                mBundle.putString(SERIAL_STRING, charac.getStringValue(0));
            if (charUuid.equals(MANUFATURE_NAME_STRING))
                mBundle.putByteArray(MANF_NAME, charac.getValue());
            if (charUuid.equals(ICDL))
                mBundle.putByteArray(ICDL_VALUE, charac.getValue());
            msg.setData(mBundle);
            msg.sendToTarget();
            */
        }

        public void onDescriptorRead(BluetoothGattDescriptor descriptor, int status) {
            Log.i(TAG, "onDescriptorRead");
            /*
             
            BluetoothGattCharacteristic mHRMcharac = descriptor.getCharacteristic();
            enableNotification(true, mHRMcharac);
             */
        }
        
    };
}
