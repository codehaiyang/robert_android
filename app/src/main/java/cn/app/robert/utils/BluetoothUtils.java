package cn.app.robert.utils;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;

import cn.app.robert.MyApplication;
import cn.app.robert.entity.BlueStatusMessage;


/**
 * 蓝牙工具包
 */

public class BluetoothUtils {
    //public static final String Action_Ble_Connect_Status = "cn.robert.app.ble.Status";
    //public static final String Action_Ble_Value_Change = "cn.robert.app.ble.ChangeValue";

    private static final String TAG = "BluetoothUtils";
    private static BluetoothUtils instance;
    private static boolean runState;
    private static int PACKAGE_LEN = 18;

    private static final String UuidServcie ="0000ffc0-0000-1000-8000-00805f9b34fb";
    private static final String UuidChanacterNoticy ="0000ffc2-0000-1000-8000-00805f9b34fb";
    private static final String UuidChanacterWrite ="0000ffc1-0000-1000-8000-00805f9b34fb";
    private static final String BluetoothName ="Robert";

    private static final String UuidServcieOur ="000000ff-0000-1000-8000-00805f9b34fb";
    private static final String UuidChanacterNoticyOur ="0000ff01-0000-1000-8000-00805f9b34fb";
    private static final String UuidChanacterWriteOur ="0000ff01-0000-1000-8000-00805f9b34fb";
    private static final String BluetoothNameOur ="Our";

    private BluetoothThread bluetoothThread;
    private List<byte[]> commandArr;
    private int currentPackageIndex;

    private BleDevice connectDevice;
    private String uuidServcie;
    private String uuidChanacterNoticy;
    private String uuidChanacterWrite;
    private String bluetoothName;
    private RobotBleListener listener;
    private int rebotType;

    public static BluetoothUtils  getInstance(int type){
        if(instance == null) {
            instance = new BluetoothUtils(type);
        }
        if(instance!=null && instance.rebotType!=type) {
            Log.d(TAG, "getInstance: -----------------");
            instance.stop();
            instance =  new BluetoothUtils(type);
            EventBus.getDefault().register(MyApplication.getInstance());
        }
        return instance;
    }

    private BluetoothUtils(int type){
        Log.i(TAG,"BluetoothUtils contruct => " + type);
        this.rebotType = type;
        if(type==1) {
            uuidServcie = UuidServcieOur;
            uuidChanacterNoticy = UuidChanacterNoticyOur;
            uuidChanacterWrite  = UuidChanacterWriteOur;
            bluetoothName = BluetoothNameOur;
        }else {
            uuidServcie = UuidServcie;
            uuidChanacterNoticy = UuidChanacterNoticy;
            uuidChanacterWrite  = UuidChanacterWrite;
            bluetoothName = BluetoothName;
        }
        commandArr = new ArrayList<>();
        start();
    }

    public void connectBluetooth(Context context) {
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        context.startActivity(intent);
    }

    public void onBlueStautsChange(RobotBleListener listener ){
        this.listener = listener;
    }



    /**
     * 发送数据到蓝牙
     * @param bytes
     */
    public void sendData(byte[] bytes) {
        Log.i(TAG,ByteUtils.byteToStr(bytes));
        commandArr.add(bytes);
    }

    public void scan() {
        BleManager.getInstance().enableBluetooth();
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanFinished(List<BleDevice> list) {
                Log.i(TAG,"Finish Scan");
            }

            @Override
            public void onScanStarted(boolean b) {
                Log.i(TAG,"Start Scan");
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                Log.i(TAG, "Find name = " + bleDevice.getName());
                String bluename= bleDevice.getName();
                if(bluename!=null && bluename.startsWith(bluetoothName)){
                    connect(bleDevice);
                    BleManager.getInstance().cancelScan();
                }

            }
        });
    }

    private void connect(BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                Log.i(TAG,"Start Connect");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                Log.i(TAG,"Connect Fail");
                EventBus.getDefault().post(new BlueStatusMessage(RobotBleListener.ConnectStatusFail));
                if(listener!=null) {
                    listener.blueStautsChange(RobotBleListener.ConnectStatusFail);
                }
                connect(bleDevice);
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                Log.i(TAG,"Connect Success" );
                EventBus.getDefault().post(new BlueStatusMessage(RobotBleListener.ConnectStatusSuccess));
                if(listener!=null) {
                    listener.blueStautsChange(RobotBleListener.ConnectStatusSuccess);
                }

                connectDevice = bleDevice;
                notice();
                List<BluetoothGattService> services =  gatt.getServices();
                for(BluetoothGattService service :services) {
                    String serviceUuid = service.getUuid().toString();
                    Log.i(TAG,"service = " + serviceUuid);

                    if(UuidServcieOur.equals(serviceUuid) || UuidServcie.equals(serviceUuid)){
                        changePropertiesByServiceName(serviceUuid);
                        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                        for(BluetoothGattCharacteristic  characteristic : characteristics) {
                            Log.i(TAG,"service characteristic = " + characteristic.getUuid() );
                        }

                    }

                }

            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                Log.i(TAG,"Connect Break");
                EventBus.getDefault().post(new BlueStatusMessage(RobotBleListener.ConnectStatusBreak));
                if (listener != null){
                    listener.blueStautsChange(RobotBleListener.ConnectStatusBreak);
                }
            }
        });
    }

    private void changePropertiesByServiceName (String serviceName){
        if(connectDevice!=null && connectDevice.getName()!=null) {

            if(serviceName.equals(UuidServcieOur)) {
                uuidServcie = UuidServcieOur;
                uuidChanacterNoticy = UuidChanacterNoticyOur;
                uuidChanacterWrite  = UuidChanacterWriteOur;
                bluetoothName = BluetoothNameOur;
            }else {
                uuidServcie = UuidServcie;
                uuidChanacterNoticy = UuidChanacterNoticy;
                uuidChanacterWrite  = UuidChanacterWrite;
                bluetoothName = BluetoothName;
            }
        }

    }

    private void notice(){
        BleManager.getInstance().notify(connectDevice, uuidServcie, uuidChanacterNoticy, new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {
                Log.i(TAG,"onNotifySuccess");
            }

            @Override
            public void onNotifyFailure(BleException exception) {

            }

            @Override
            public void onCharacteristicChanged(byte[] data) {

                if(listener!=null) {
                    listener.blueValueChange(data);
                }

            }
        });
    }

    public void write(byte[] data) {

        if(connectDevice!=null) {

            BleManager.getInstance().write(connectDevice, uuidServcie, uuidChanacterWrite, data, new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int i, int i1, byte[] bytes) {
                    Log.i(TAG,"Write data success");
                }

                @Override
                public void onWriteFailure(BleException e) {
                    Log.i(TAG,"Write data failuer");
                }
            });

        }

    }

    public void start(){
        Log.i(TAG,"start");
        if(bluetoothThread==null) {
            runState = true;
            bluetoothThread = new BluetoothThread();
            bluetoothThread.setName("bluetoothThread");
            bluetoothThread.start();
        }

    }

    public synchronized  void stop() {
        runState = false;
        bluetoothThread = null;
    }

    private synchronized byte[] getNextPackage() {
        byte[] result= null;
        //Log.i(TAG,"getNextPackage");

        if(commandArr.size()>0){
            byte[] data  = commandArr.get(0);
            int offset = currentPackageIndex*PACKAGE_LEN;
            if(data.length> offset){
                int length = Math.min(PACKAGE_LEN, data.length - offset);
                result = new byte[length];
                System.arraycopy(data,offset,result,0,length);
                currentPackageIndex++;
                if(length<PACKAGE_LEN) {
                    commandArr.remove(0);
                    currentPackageIndex = 0;
                }
            }else {
                commandArr.remove(0);
                currentPackageIndex = 0;
            }
        }else {
            currentPackageIndex  = 0;
        }
        return result;

    }


    ////////////////
    // 蓝牙线程
    class  BluetoothThread extends Thread {
        @Override
        public void run() {

            while(true) {
                try {
                    Thread.sleep(1);
                }catch (InterruptedException ie){

                }
                if(!runState){
                    Log.i(TAG," Thread id =" +getId() + " quit... ");
                    break;
                }

                byte[] data = getNextPackage();
                if(data!=null && data.length>0){
                    Log.i(TAG, "Thread "+ ByteUtils.byteToStr(data));
                    write(data);
                    // TODO 写入蓝牙数据
                    //[self.connectedPeripheral writeValue:data forCharacteristic:self.acharacteristic type:CBCharacteristicWriteWithResponse];
                }

            }
        }
    }


    @Subscribe
    public void onEventMainThread(BlueStatusMessage msg){
        Log.d(TAG, "onEventMainThread: " + msg.getStatus());
    }

    public static void cleanEvent(){
        EventBus.getDefault().unregister(MyApplication.getInstance());
    }
}