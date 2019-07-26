package cn.app.robert.activity;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.Random;
import butterknife.BindView;
import cn.app.robert.R;
import cn.app.robert.base.BaseActivity;
import cn.app.robert.data.DataFrame;
import cn.app.robert.data.DataPackage;
import cn.app.robert.data.DataPackageType;
import cn.app.robert.entity.BlueStatusMessage;
import cn.app.robert.utils.BluetoothUtils;
import cn.app.robert.utils.LightColor;
import cn.app.robert.utils.LightMode;
import cn.app.robert.utils.RobotBleListener;
import cn.app.robert.view.CustomSeekbar;

public class RemoteActivity extends BaseActivity implements View.OnClickListener,View.OnTouchListener {

    private static final String TAG  = "RemoteActivity";

    private int dataPackageType = DataPackageType.DataPackageTypeRobort;
    private int tapCount = 8; //拍数
    private int speed = 0; // 最快
    private int color = 2;
    private int colorMode = 0; //常亮

    @BindView(R.id.ib_back)
    Button mIbBack;
    @BindView(R.id.ib_light_white)
    ImageView mIbLightWhite;
    @BindView(R.id.ib_light_blue)
    ImageView mIbLightBlue;
    @BindView(R.id.ib_light_darkblue)
    ImageView mIbLightDarkBlue;
    @BindView(R.id.ib_light_green)
    ImageView mIbLightGreen;
    @BindView(R.id.ib_light_yellow)
    ImageView mIbLightYellow;
    @BindView(R.id.ib_light_red)
    ImageView mIbLightRed;
    @BindView(R.id.ib_light_purple)
    ImageView mIbLightPurple;
    @BindView(R.id.sw_eye)
    Switch mSwEye;
    @BindView(R.id.ll_hand)
    LinearLayout mLlHand;
    @BindView(R.id.ll_leg)
    LinearLayout mLlLeg;
    @BindView(R.id.ll_combined)
    LinearLayout mLlCombined;
    @BindView(R.id.ll_lighting)
    LinearLayout mLlLighting;
    @BindView(R.id.ib_up)
    ImageView mIbUp;
    @BindView(R.id.ib_down)
    ImageView mIbdown;
    @BindView(R.id.ib_left)
    ImageView mIbLeft;
    @BindView(R.id.ib_right)
    ImageView mIbRight;

    @BindView(R.id.rl_up)
    RelativeLayout mRlUp;
    @BindView(R.id.rl_down)
    RelativeLayout mRldown;
    @BindView(R.id.rl_left)
    RelativeLayout mRlLeft;
    @BindView(R.id.rl_right)
    RelativeLayout mRlRight;

    @BindView(R.id.ib_hand)
    ImageView mIbHand;
    @BindView(R.id.ib_leg)
    ImageView mIbLeg;
    @BindView(R.id.ib_combined)
    ImageView mIbCombined;
    @BindView(R.id.sc_speed)
    CustomSeekbar mScSpeed;
    @BindView(R.id.tv_status)
    TextView mTvStatus;
    @BindView(R.id.iv_status)
    ImageView mIbStatus;
    @BindView(R.id.rl_head)
    RelativeLayout mRlHead;

    private RemoteActivity mContext;

    @Override
    protected void initView() {
        mContext = this;
        mIbLightBlue.setActivated(true);
        mIbLightWhite.setOnClickListener(this);
        mIbLightBlue.setOnClickListener(this);
        mIbLightDarkBlue.setOnClickListener(this);
        mIbLightGreen.setOnClickListener(this);
        mIbLightYellow.setOnClickListener(this);
        mIbLightRed.setOnClickListener(this);
        mIbLightPurple.setOnClickListener(this);
        mIbBack.setOnClickListener(this);
        mIbHand.setOnClickListener(this);
        mIbLeg.setOnClickListener(this);
        mIbCombined.setOnClickListener(this);

        mIbUp.setOnTouchListener(this);
        mIbdown.setOnTouchListener(this);
        mIbLeft.setOnTouchListener(this);
        mIbRight.setOnTouchListener(this);

        mRlUp.setOnTouchListener(this);
        mRldown.setOnTouchListener(this);
        mRlLeft.setOnTouchListener(this);
        mRlRight.setOnTouchListener(this);

        // 速度切换监听
        mScSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged:------- " + progress);
                progress = Math.abs(progress - 3);
                Log.d(TAG, "onProgressChanged: " + progress);
                speed = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mSwEye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSwEye.setThumbResource(R.mipmap.ic_thumb);
                } else {
                    mSwEye.setThumbResource(R.mipmap.ic_thumb_close);
                }
            }
        });


        mSwEye.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                mSwEye.setThumbResource(R.mipmap.ic_thumb);
                colorMode = LightMode.Light;
            }else {
                mSwEye.setThumbResource(R.mipmap.ic_thumb_close);
                colorMode = LightMode.Splash;
            }
            sendCommand((byte)77,(byte)8);
        });
    }

    @Override
    protected void initData() {
        //BleManager.getInstance().init(getApplication());
       // BluetoothUtils.getInstance(0).scan();
        EventBus.getDefault().register(this);
//        BlueToothConnectReceiver connectReceiver = new BlueToothConnectReceiver();
//        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
//        registerReceiver(connectReceiver,intentFilter);
//        connectReceiver.setOnBleConnectListener(new BlueToothConnectReceiver.OnBleConnectListener() {
//            @Override
//            public void onConnect(BluetoothDevice device) {
//                // 连接成功
//                Log.d(TAG, "blueStautsChange: -------------success");
//                mTvStatus.setText(R.string.connected);
//                mTvStatus.setTextColor(getResources().getColor(R.color.textBg));
//                mIbStatus.setImageResource(R.mipmap.ic_blue);
//                mRlHead.setBackgroundResource(R.mipmap.head);
//            }
//
//            @Override
//            public void onDisConnect(BluetoothDevice device) {
//                Log.d(TAG, "blueStautsChange: -------------break");
//                mTvStatus.setText(R.string.unconnected);
//                mIbStatus.setImageResource(R.mipmap.ic_un_blue);
//                mRlHead.setBackgroundResource(R.mipmap.unhead);
//                finish();
//            }
//        });
    }

    @Subscribe
    public void onEventMainThread(BlueStatusMessage msg){
        int status = msg.getStatus();
        if(status == RobotBleListener.ConnectStatusBreak) {
            Log.d(TAG, "blueStautsChange: -------------break");
            mTvStatus.setText(R.string.unconnected);
            mTvStatus.setTextColor(getResources().getColor(R.color.unTextBg));
            mIbStatus.setImageResource(R.mipmap.ic_un_blue);
            mRlHead.setBackgroundResource(R.mipmap.unhead);
            finish();
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
            finish();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_remote;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_back:
                finish();
                break;
            case R.id.ib_hand:
                // 手的动作
                randomAction(100,11);
                break;
            case R.id.ib_leg:
                // 脚的动作
                randomAction(200,36);
                break;
            case R.id.ib_combined:
                // 手脚组合动作
                randomAction(1,93);
                break;

            case R.id.ib_light_white:
            case R.id.ib_light_blue:
            case R.id.ib_light_darkblue:
            case R.id.ib_light_green:
            case R.id.ib_light_yellow:
            case R.id.ib_light_red:
            case R.id.ib_light_purple:
                changeColor(view);
                break;
                default:
        }
    }

    //随机发送一个动作
    private void randomAction(int base, int range) {

        Random random = new Random();
        int r = random.nextInt(range) + base;
        if(r==77) {
            r = 78;
        }
        sendCommand((byte)r,(byte)tapCount);


    }

    private void changeColor(View view) {

        Log.i(TAG, "change color");
        mIbLightWhite.setActivated(false);
        mIbLightBlue.setActivated(false);
        mIbLightDarkBlue.setActivated(false);
        mIbLightGreen.setActivated(false);
        mIbLightYellow.setActivated(false);
        mIbLightRed.setActivated(false);
        mIbLightPurple.setActivated(false);
        ((ImageButton ) view).setActivated(true);

        //byte color = LightColor.White;

        switch (view.getId()) {
            case R.id.ib_light_white:
                color = LightColor.White;
                break;
            case R.id.ib_light_blue:
                color = LightColor.Blue;
                break;
            case R.id.ib_light_darkblue:
                color = LightColor.BlueDark;
                break;
            case R.id.ib_light_green:
                color = LightColor.Green;
                break;
            case R.id.ib_light_yellow:
                color = LightColor.Yellow;
                break;
            case R.id.ib_light_red:
                color = LightColor.Red;
                break;
            case R.id.ib_light_purple:
                color = LightColor.RedPin;
                break;
                default:
        }
        sendCommand((byte)77,(byte)8);

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean result = false;

        if(event.getAction()==MotionEvent.ACTION_UP){
            sendCommand((byte)77,(byte)8);
            result = true;
        }else if(event.getAction()==MotionEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.rl_up:
                case R.id.ib_up:
                    Log.d(TAG, "onTouch: --------------up");
                    sendCommand((byte) 1, (byte) 0xFF);
                    break;
                case R.id.rl_down:
                case R.id.ib_down:
                    Log.d(TAG, "onTouch: --------------down");
                    sendCommand((byte) 2, (byte) 0xFF);
                    break;
                case R.id.rl_left:
                case R.id.ib_left:
                    Log.d(TAG, "onTouch: --------------left");
                    sendCommand((byte) 3, (byte) 0xFF);
                    break;
                case R.id.rl_right:
                case R.id.ib_right:
                    Log.d(TAG, "onTouch: --------------right");
                    sendCommand((byte) 4, (byte) 0xFF);
                    break;
                    default:
            }
        }

        return result;
    }


    private void sendCommand(byte action, byte tapCount) {

        DataPackage datapackage = new DataPackage(dataPackageType);

        DataFrame dataFrame = datapackage.createDataFrame();

        //动作
        dataFrame.setActionIndex(action);
        dataFrame.setHandActionIndex(action);
        dataFrame.setFootActionIndex(action);

        //动作拍数d
        dataFrame.setActionCount(tapCount); //8拍

        //速度
        dataFrame.setActionSpeed((byte)speed); //= (Byte)(3-self.speed);

        //灯的颜色
        dataFrame.setLightColor((byte)color);

        //灯的模式
        dataFrame.setLightModle((byte)colorMode);
        //呼吸时长
        dataFrame.setBreathInterval((byte)0x02);
        dataFrame.setBreathSleepInterval((byte)0x02);


        datapackage.appendFrame(dataFrame);
        byte[]  data  = datapackage.toBytes();

        BluetoothUtils.getInstance(0).sendData(data);

    }


}
