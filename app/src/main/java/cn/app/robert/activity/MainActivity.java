package cn.app.robert.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.clj.fastble.BleManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import androidx.annotation.NonNull;
import butterknife.BindView;
import cn.app.robert.MyApplication;
import cn.app.robert.R;
import cn.app.robert.base.BaseActivity;
import cn.app.robert.entity.BlueStatusMessage;
import cn.app.robert.services.BlueToothConnectReceiver;
import cn.app.robert.utils.BluetoothUtils;
import cn.app.robert.utils.RobotBleListener;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    @BindView(R.id.ib_to_remote)
    ImageView mIbToRemote;
    @BindView(R.id.ib_to_program)
    ImageView mIbToProgram;
    @BindView(R.id.ib_setting)
    ImageView mIbSeeting;
    @BindView(R.id.tv_status)
    TextView mTvStatus;
    @BindView(R.id.iv_status)
    ImageView mIbStatus;
    @BindView(R.id.rl_head)
    RelativeLayout mRlHead;

    private static final int PERMISSION_REQUEST_LOCATION = 99;
    private MainActivity mContext;

    @Override
    protected void initView() {
        requirePermission();

        mIbToRemote.setOnClickListener(this);
        mIbToProgram.setOnClickListener(this);
        mIbSeeting.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mContext = this;
        if (!checkConnectDevice()) {
            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivityForResult(intent, 0);
            BlueToothConnectReceiver connectReceiver = new BlueToothConnectReceiver();
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
            registerReceiver(connectReceiver,intentFilter);
            connectReceiver.setOnBleConnectListener(new BlueToothConnectReceiver.OnBleConnectListener() {
                @Override
                public void onConnect(BluetoothDevice device) {
                    Log.d(TAG, "onConnect:  -------------" + device.getName());
                    openActivity(MainActivity.class);
                    initBlue();
                }

                @Override
                public void onDisConnect(BluetoothDevice device) {
                }
            });
        }else{
            //注册蓝牙事件
            initBlue();
        }
        EventBus.getDefault().register(this);
    }

    public void initBlue(){
        BleManager.getInstance().init(MyApplication.getInstance());
        BluetoothUtils.getInstance(0).scan();
    }

    private void requirePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    //Manifest.permission.READ_PHONE_STATE,
                    //Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST_LOCATION);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_to_remote:
                openActivity("page","remote",AnimationActivity.class);
                break;
            case R.id.ib_to_program:
                openActivity("page","program",AnimationActivity.class);
                break;
            case R.id.ib_setting:
                initBlue();
                break;
                default:
        }
    }

    @Subscribe
    public void onEventMainThread(BlueStatusMessage msg){
        int status = msg.getStatus();
        Log.d(TAG, "onEventMainThread: -------------------");
        if(status == RobotBleListener.ConnectStatusBreak) {
            Log.d(TAG, "blueStautsChange: -------------break");
            mTvStatus.setText(R.string.unconnected);
            mTvStatus.setTextColor(getResources().getColor(R.color.unTextBg));
            mIbStatus.setImageResource(R.mipmap.ic_un_blue);
            mRlHead.setBackgroundResource(R.mipmap.unhead);
        } else if(status == RobotBleListener.ConnectStatusSuccess) {
            // 连接成功
            Log.d(TAG, "blueStautsChange: -------------success");
            mTvStatus.setText(R.string.connected);
            mTvStatus.setTextColor(getResources().getColor(R.color.textBg));
            mIbStatus.setImageResource(R.mipmap.ic_blue);
            mRlHead.setBackgroundResource(R.mipmap.head);
        } else if(status == RobotBleListener.ConnectStatusFail) {
            // 连接失败
            Log.d(TAG, "blueStautsChange: -------------fail");
            mTvStatus.setText(R.string.unconnected);
            mTvStatus.setTextColor(getResources().getColor(R.color.unTextBg));
            mIbStatus.setImageResource(R.mipmap.ic_un_blue);
            mRlHead.setBackgroundResource(R.mipmap.unhead);
        }
    }

    public boolean checkConnectDevice() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        int a2dp = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
        int headset = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
        int health = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEALTH);
        int flag = -1;
        if (a2dp == BluetoothProfile.STATE_CONNECTED) {
            flag = a2dp;
        } else if (headset == BluetoothProfile.STATE_CONNECTED) {
            flag = headset;
        } else if (health == BluetoothProfile.STATE_CONNECTED) {
            flag = health;
        }
        if (flag == -1){
            return false;
        }
        return true;
    }
}
