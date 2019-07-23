package cn.app.robert.services;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BlueToothConnectReceiver extends BroadcastReceiver {

    private static final String TAG = "BlueToothConnectReceive";

    private OnBleConnectListener onBleConnectListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        switch (action) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                if (onBleConnectListener != null) {
                    onBleConnectListener.onConnect(device);
                }
                Log.d(TAG, "onReceive: 蓝牙已连接:" + device.getName());
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                if (onBleConnectListener != null) {
                    onBleConnectListener.onDisConnect(device);
                }
                Log.d(TAG,"onReceive:蓝牙已断开：" + device.getName());
                break;
                default:
        }
    }

    public interface OnBleConnectListener {
        void onConnect(BluetoothDevice device);

        void onDisConnect(BluetoothDevice device);
    }

    public void setOnBleConnectListener(OnBleConnectListener onBleConnectListener) {
        this.onBleConnectListener = onBleConnectListener;
    }
}