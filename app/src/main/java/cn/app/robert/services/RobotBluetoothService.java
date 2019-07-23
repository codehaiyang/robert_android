package cn.app.robert.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;

import androidx.annotation.Nullable;

//import android.bluetooth.IBluetoothManager;


public class RobotBluetoothService  extends Service {

    private static String TAG = "RobotBluetoothService";
    private RobotBluetoothServiceBinder binder;
    private RobotBluetoothThread bluethoothThread;
    private static BluetoothManager myManager;
    private static BluetoothAdapter myAdapter;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"create");
        bluetoothInit();
        if(bluethoothThread==null){
            bluethoothThread = new RobotBluetoothThread();
            Log.i(TAG,"create bluetoothThread" );
        }
        bluethoothThread.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"destory");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"bind");

        if(binder==null){
            binder = new RobotBluetoothServiceBinder();
            Log.i(TAG,"create RobotBluetoothServiceBinder");
        }
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG,"unbind");
        return super.onUnbind(intent);

    }

    public class RobotBluetoothServiceBinder extends Binder {
        @Override
        public void attachInterface(@Nullable IInterface owner, @Nullable String descriptor) {
            super.attachInterface(owner, descriptor);
            Log.i(TAG,"attachInterface");
        }

    }

    //发送数据的线程
    class RobotBluetoothThread extends Thread {
        @Override
        public void run(){
            while(true){
                //Log.i(TAG,"RobotBluetoothThread running");
                try {
                    Thread.sleep(100);
                }catch (InterruptedException e) {

                }

            }

        }

    }


    //扫描回调
    private ScanCallback mLeScanCallback  = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i(TAG,result.toString());
        }
    };


    private void bluetoothInit() {

        if(myManager==null) {
            myManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        }
        if(myAdapter == null) {
            myAdapter = myManager.getAdapter();
        }

        //myAdapter.getBluetoothLeScanner().startScan(mLeScanCallback);

        myAdapter.startDiscovery();
        myAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
                                          @Override
                                          public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                                              Log.d(TAG,"bluetooth name =" +device.getName() +" address= " + device.getAddress() + " scanRecord = " + byteToStr(scanRecord) );
                                          }
                                          private String byteToStr(byte[] datas){
                                              StringBuffer sb = new StringBuffer();
                                              for(byte i : datas){
                                                  sb.append("|");
                                                  String str = String.format("%02X",i&0xFF);
                                                  sb.append(str);
                                              }
                                              return sb.toString();
                                          };
                                      });



    }


}
