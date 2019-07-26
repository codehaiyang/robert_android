package cn.app.robert.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.io.IOException;
import java.util.List;
import butterknife.BindView;
import cn.app.robert.MyApplication;
import cn.app.robert.R;
import cn.app.robert.base.BaseActivity;
import cn.app.robert.entity.BlueStatusMessage;
import cn.app.robert.services.BlueToothConnectReceiver;
import cn.app.robert.utils.BluetoothUtils;
import cn.app.robert.utils.RobotBleListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

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
    @BindView(R.id.ll_connect)
    LinearLayout mLlConnect;
    @BindView(R.id.ll_jump)
    LinearLayout mLlJump;
    @BindView(R.id.iv_gif)
    GifImageView mIvGif;

    private static final int PERMISSION_REQUEST_LOCATION = 99;
    private MainActivity mContext;
    private BlueToothConnectReceiver connectReceiver;

    @Override
    protected void initView() {
        mContext = this;

        BleManager.getInstance().init(MyApplication.getInstance());
        requirePermission();
        mLlConnect.setOnClickListener(this);
        mIbToRemote.setOnClickListener(this);
        mIbToProgram.setOnClickListener(this);
        mIbSeeting.setOnClickListener(this);
        boolean bleConnected = checkConnectDevice();
        if (bleConnected){
            //注册蓝牙事件
            initBlue();
        }
        GifDrawable gifDrawable = null;
        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.robert_ani);
            mIvGif.setImageDrawable(gifDrawable);
            gifDrawable.setLoopCount(1);
            // 设置动画完成监听器
            gifDrawable.addAnimationListener(loopNumber -> {
                Log.d(TAG, "onAnimationCompleted: over");
                mIvGif.setVisibility(View.GONE);
                mRlHead.setVisibility(View.VISIBLE);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        //BleManager.getInstance().init(getApplication());
        //BluetoothUtils.getInstance(0).scan();
        List<BleDevice> allConnectedDevice = BleManager.getInstance().getAllConnectedDevice();
        if (allConnectedDevice != null){
            for (BleDevice device: allConnectedDevice) {
                boolean connected = BleManager.getInstance().isConnected(device);
                if (connected){
                    mTvStatus.setText(R.string.connected);
                    mTvStatus.setTextColor(getResources().getColor(R.color.textBg));
                    mIbStatus.setImageResource(R.mipmap.ic_blue);
                    mRlHead.setBackgroundResource(R.mipmap.head);
                    mLlJump.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
    }

    public void initBlue(){
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
            case R.id.ll_connect:
                if (!checkConnectDevice()) {
                    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivityForResult(intent, 0);
                    connectReceiver = new BlueToothConnectReceiver();
                    IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
                    registerReceiver(connectReceiver,intentFilter);
                    connectReceiver.setOnBleConnectListener(new BlueToothConnectReceiver.OnBleConnectListener() {
                        @Override
                        public void onConnect(BluetoothDevice device) {
                            if (device.getName().equals("Robert")){
                                finish();
                                initBlue();
                                openActivity(MainActivity.class);
                            }
                            mLlJump.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onDisConnect(BluetoothDevice device) {
                        }
                    });
                }else{
                    //注册蓝牙事件
                    initBlue();
                }
                break;
            case R.id.ib_to_remote:
                openActivity(RemoteActivity.class);
                break;
            case R.id.ib_to_program:
                openActivity(ProgramActivity.class);
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
            mLlJump.setVisibility(View.GONE);
        } else if(status == RobotBleListener.ConnectStatusSuccess) {
            // 连接成功
            Log.d(TAG, "blueStautsChange: -------------success");
            mTvStatus.setText(R.string.connected);
            mTvStatus.setTextColor(getResources().getColor(R.color.textBg));
            mIbStatus.setImageResource(R.mipmap.ic_blue);
            mRlHead.setBackgroundResource(R.mipmap.head);
            mLlJump.setVisibility(View.VISIBLE);
            mRlHead.setVisibility(View.VISIBLE);
        } else if(status == RobotBleListener.ConnectStatusFail) {
            // 连接失败
            Log.d(TAG, "blueStautsChange: -------------fail");
            mTvStatus.setText(R.string.unconnected);
            mTvStatus.setTextColor(getResources().getColor(R.color.unTextBg));
            mIbStatus.setImageResource(R.mipmap.ic_un_blue);
            mRlHead.setBackgroundResource(R.mipmap.unhead);
            mLlJump.setVisibility(View.GONE);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectReceiver != null){
            unregisterReceiver(connectReceiver);
        }
        EventBus.getDefault().unregister(this);
    }
}
