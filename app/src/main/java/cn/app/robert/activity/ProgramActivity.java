package cn.app.robert.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import cn.app.robert.R;
import cn.app.robert.adapter.PlayerActionAdapter;
import cn.app.robert.base.BaseActivity;
import cn.app.robert.data.DataFrame;
import cn.app.robert.data.DataPackage;
import cn.app.robert.data.DataPackageType;
import cn.app.robert.db.OpenHelper;
import cn.app.robert.entity.ActionBean;
import cn.app.robert.entity.Actions;
import cn.app.robert.entity.BlueStatusMessage;
import cn.app.robert.entity.Song;
import cn.app.robert.utils.BluetoothUtils;
import cn.app.robert.utils.ByteUtils;
import cn.app.robert.utils.LightColor;
import cn.app.robert.utils.LightMode;
import cn.app.robert.utils.LocalMusicUtils;
import cn.app.robert.utils.RobotBleListener;
import cn.app.robert.view.ActionsDialog;
import cn.app.robert.view.CustomSeekbar;
import cn.app.robert.view.MusicListDialog;
import cn.app.robert.view.SaveActionsDialog;

/**
 * 控制页面
 * @author daxiong
 */
public class ProgramActivity extends BaseActivity implements View.OnClickListener,RobotBleListener {

    private static final String TAG = "ProgramActivity";
    private int dataPackageType = DataPackageType.DataPackageTypeRobort;
    private int tapCount = 8; //拍数
    private int speed = 0; // 最快
    private int color = 2;
    private int colorMode = 0; //常亮
    private boolean presentionStatus  = false; //时长收集状态
    //private boolean playStatus = false; //播放状态

    @BindView(R.id.tv_save)
    TextView mTvSave;
    @BindView(R.id.tv_list)
    TextView mTvList;
    @BindView(R.id.ib_add_actions)
    ImageView mIbAddActions;
    @BindView(R.id.ib_beat)
    ImageView mIbBeat;
    @BindView(R.id.ll_top_contro)
    LinearLayout mLlTopContro;
    @BindView(R.id.ib_light)
    ImageView mIbLight;
    @BindView(R.id.ib_speed)
    ImageView mIbSpeed;
    @BindView(R.id.rv_actions_list)
    RecyclerView mRvActionsList;
    @BindView(R.id.ib_back)
    Button mIbBack;
    @BindView(R.id.tv_add_music)
    TextView mTvAddMusic;
    @BindView(R.id.tv_music_name)
    TextView mTvMusicName;
    @BindView(R.id.iv_music_album)
    ImageView mIvMusicAlbum;
    @BindView(R.id.iv_needle)
    ImageView mIvNeedle;
    @BindView(R.id.ll_play)
    LinearLayout mLlPlay;
    @BindView(R.id.ll_presentation)
    LinearLayout mLlPresentation;
    @BindView(R.id.ll_light)
    LinearLayout mlLlight;
    @BindView(R.id.ib_play)
    ImageButton mIbPlay;
    @BindView(R.id.ib_presentation)
    ImageButton mIbPresentation;
    @BindView(R.id.tv_status)
    TextView mTvStatus;
    @BindView(R.id.tv_music_time)
    TextView mTvMusicTime;
    @BindView(R.id.iv_status)
    ImageView mIbStatus;
    @BindView(R.id.rl_head)
    RelativeLayout mRlHead;

    private ProgramActivity mContext;

    private View mViewBeat;
    private PopupWindow mPopBeat;
    private View mViewLight;
    private PopupWindow mPopLight;
    private View mViewSpeed;
    private PopupWindow mPopSpeed;

    private ArrayList<ActionBean> actions;

    private PlayerActionAdapter playerActionAdapter;

    private ImageButton mIbLightWhite;
    private ImageButton mIbLightDarkBlue;
    private ImageButton mIbLightBlue;
    private ImageButton mIbLightGreen;
    private ImageButton mIbLightYellow;
    private ImageButton mIbLightRed;
    private ImageButton mIbLightPurple;
    private LinearLayout mLlBeat2;
    private LinearLayout mLlBeat4;
    private LinearLayout mLlBeat8;
    private LinearLayout mLlBeat12;
    private LinearLayout mLlBeat16;
    private ImageView mIvBeat2;
    private ImageView mIvBeat4;
    private ImageView mIvBeat8;
    private ImageView mIvBeat12;
    private ImageView mIvBeat16;
    private Switch mSwEye;

    /**
     * 动作
     */
    private ActionBean actionBean;

    /**
     * 修改动作标记
     */
    private int flag = 999;

    /**
     * 播放指针动画
     */
    private ObjectAnimator needleRotation;
    /**
     * 播放专辑图片动画
     */
    private ObjectAnimator albumRotation;

    private MediaPlayer mediaPlayer;

    /**
     * 播放状态
     */
    private boolean isPlaying = false;
    private Song song;
    private int lastPosition = 0;
    private OpenHelper helper;
    //选中颜色的id
    private int choseColorId = 0;

    private int chooseFlag = 1;
    private Thread thread;
    private boolean isStop = true;
    //当前音乐播放状态
    private boolean currentMusicStatus = true;
    // 上一个动作的时长
    private int afterActionPosition = 0;

    // 每个动作记录的时长
    private int actionPosition;
    // 动作序列信息
    private Actions actionsDetail;
    //所选择动作序列信息
    private Actions choseActions;
    private Actions actionsBean;

    // 动作保存状态
    private boolean saveActionStatus = false;

    @Override
    protected void initView() {
        mContext = this;
        needleRotation = ObjectAnimator.ofFloat(mIvNeedle, "rotation", 0, -50);
        mIvNeedle.setX(0);
        mIvNeedle.setY(0);
        needleRotation.setInterpolator(new LinearInterpolator());
        needleRotation.start();

        // 添加动作的列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        //添加Android自带的分割线
        mRvActionsList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL));
        mRvActionsList.setLayoutManager(linearLayoutManager);

        mTvList.setOnClickListener(this);
        mIbAddActions.setOnClickListener(this);
        mIbBeat.setOnClickListener(this);
        mIbLight.setOnClickListener(this);
        mIbSpeed.setOnClickListener(this);
        mTvSave.setOnClickListener(this);
        mIbBack.setOnClickListener(this);
        mTvAddMusic.setOnClickListener(this);
        mIvMusicAlbum.setOnClickListener(this);
        mIvNeedle.setOnClickListener(this);
        mLlPlay.setOnClickListener(this);
        mIbPlay.setOnClickListener(this);
        mLlPresentation.setOnClickListener(this);
        mIbPresentation.setOnClickListener(this);

        //注册蓝牙事件
        BluetoothUtils.getInstance(0).onBlueStautsChange(this);
        EventBus.getDefault().register(this);
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
    protected void initData() {
        mediaPlayer = new MediaPlayer();
        // 设置音乐的播放的时候界面长亮
        mediaPlayer.setScreenOnWhilePlaying(true);
        
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion: ---------------播放完毕--------------------");
                // 修改收集状态为停止
                if (presentionStatus){
                    presentionStatus = false;
                    mIbPresentation.setBackgroundResource(R.mipmap.ib_icon_start);
                }
                //播放状态
                if (isPlaying){
                    isPlaying = false;
                    mIbPlay.setBackgroundResource(R.mipmap.ib_icon_start);
                }
                currentMusicStatus = false;
                if (needleRotation != null) {
                    needleRotation.start();
                }
                if (albumRotation != null && albumRotation.isRunning()) {
                    albumRotation.cancel();
                    float valueAvatar = (float) albumRotation.getAnimatedValue();
                    albumRotation.setFloatValues(valueAvatar, 360f + valueAvatar);
                }
                // 播放完成设置最后动作的时长
                actionBean.setMusicPosition(mp.getCurrentPosition() - afterActionPosition);
                // 设置最后的时长
                lastPosition = mp.getCurrentPosition();
                isStop = !isStop;
                sendCommand((byte)77,(byte)8);
            }
        });
        
        helper = new OpenHelper(mContext);

        actions = new ArrayList<>();

        actionsBean = new Actions();

        playerActionAdapter = new PlayerActionAdapter(mContext,actions);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_program;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_beat:
                // 显示节拍弹框
                if (presentionStatus || isPlaying){
                    return;
                }
                showBeatPopWindow();
                break;
            case R.id.ib_light:
                //显示灯光弹框
                if (presentionStatus || isPlaying){
                    return;
                }
                showLightPopWindow();
                break;
            case R.id.ib_speed:
                //显示速度弹框
                if (presentionStatus || isPlaying){
                    return;
                }
                showSpeedPopWindow();
                break;
            case R.id.tv_list:
                //显示保存的列表
                showSaveList();
                break;
            case R.id.ib_add_actions:
                //选择动作放到动作列表
                if (presentionStatus || isPlaying){
                    return;
                }
                showPalyerActions();
                break;
            case R.id.tv_save:
                //保存动作队列
                saveActions();
                break;
            case R.id.iv_music_album:
            case R.id.tv_add_music:
                // 弹出本地音乐列表
                showMusicDialog();
                break;
            case R.id.ll_play:
            case R.id.ib_play:
                // 播放音乐
                playing();
                break;
            case R.id.ll_presentation:
            case R.id.ib_presentation:
                presentation();
                break;
            case R.id.ib_back:
                // 推出需要提示是否保存当前的动作序列 以保存则直接推出
                if (saveActionStatus || actions.size() == 0){
                    finish();
                }
                showIsSaveDialog(0,null);
                break;
            case R.id.ib_light_white:
            case R.id.ib_light_blue:
            case R.id.ib_light_darkblue:
            case R.id.ib_light_green:
            case R.id.ib_light_yellow:
            case R.id.ib_light_red:
            case R.id.ib_light_purple:
                changeColor(view);
                mPopLight.dismiss();
                break;
            case R.id.ll_beat2:
            case R.id.ll_beat4:
            case R.id.ll_beat8:
            case R.id.ll_beat12:
            case R.id.ll_beat16:
                changeBeatStatus(view);
                mPopBeat.dismiss();
                break;
                default:
        }
    }

    /**
     * 保存动作队列
     */
    private void saveActions() {
        if (actions == null || actions.size() == 0){
            //Toast.makeText(mContext,"请添加动作！",Toast.LENGTH_LONG).show();
            return;
        }

        //删除
        if(actionsBean != null){
            helper.delete(actionsBean.getName()+"",OpenHelper.TABLE_ACTIONS);
            helper.deleteByNumber(actionsBean.getNumber(),OpenHelper.TABLE_ACTION_DETAIL);
        }
        // 保存最后一个动作的时间
        savePresentationTime();
        actionsBean = new Actions();
        actionsBean.setName(song.getName());
        actionsBean.setDuration(lastPosition);
        actionsBean.setPath(song.getPath());
        actionsBean.setNumber(helper.getNumber());
        ContentValues contentValuesAction = helper.actionToContentValues(actionsBean);
        helper.insert(contentValuesAction,OpenHelper.TABLE_ACTIONS);
        for (int i = 0; i < this.actions.size(); i++){
            ActionBean action = this.actions.get(i);
            ContentValues contentValues = helper.actionBeanToContentValues(action,actionsBean.getNumber());
            helper.insert(contentValues,OpenHelper.TABLE_ACTION_DETAIL);
        }
        //Toast.makeText(this,"保存成功！",Toast.LENGTH_LONG).show();
        // 当前动作的保存状态
        saveActionStatus = true;
    }

    /**
     * 获取保存的动作队列列表并显示
     */
    private void showSaveList() {
        // 获取保存的动作序列列表
        List<Actions> actions = helper.getActions();
        if (actions.size() == 0){
            //Toast.makeText(this,"没有保存动作队列!",Toast.LENGTH_LONG).show();
            return;
        }
        SaveActionsDialog saveActionsDialog = new SaveActionsDialog(this,actions);
        // 回调选择的队列
        saveActionsDialog.setOnItemClickListener(new SaveActionsDialog.OnItemClickListener() {
            @Override
            public void click(Actions actionsDetail, int position) {
                mContext.actionsDetail = actionsDetail;
                String number = actionsDetail.getNumber();
                // 获取当前序列下的所有动作 并替换当前的动作列表
                List<ActionBean> actionDetail = helper.getActionDetail(number);
                mContext.actions.clear();
                mContext.actions.addAll(actionDetail);
                playerActionAdapter = new PlayerActionAdapter(mContext, mContext.actions);
                mRvActionsList.setAdapter(playerActionAdapter);
                // 获取当前序列的最后一个动作
                ActionBean actionBean = actionDetail.get(actionDetail.size() - 1);
                mContext.actionBean = actionBean;
                mContext.actionsBean = actionsDetail;
                // 所选择的动作队列信息
                mContext.choseActions = actions.get(position);
                // 回显歌曲信息
                Song song = new Song();
                //song.setDuration(formatTime(choseActions.getDuration()));
                song.setPath(choseActions.getPath());
                song.setName(choseActions.getName());
                mContext.song = song;
                //mContext.lastPosition = choseActions.getDuration();
                mTvMusicName.setText(song.getName());
                initMediaPlayer(song);
            }

            @Override
            public void del(Actions actions) {
                helper.delete(actions.getName(),OpenHelper.TABLE_ACTIONS);
                helper.deleteByNumber(actions.getNumber(),OpenHelper.TABLE_ACTION_DETAIL);
            }
        });
        saveActionsDialog.show();
    }

    //改变颜色
    private void changeColor(View view) {

        if(dataPackageType==DataPackageType.DataPackageTypeOur){
            return;
        }
        Log.i(TAG, "change color");
        mIbLightWhite.setActivated(false);
        mIbLightBlue.setActivated(false);
        mIbLightDarkBlue.setActivated(false);
        mIbLightGreen.setActivated(false);
        mIbLightYellow.setActivated(false);
        mIbLightRed.setActivated(false);
        mIbLightPurple.setActivated(false);
        (view).setActivated(true);

        //byte color = LightColor.White;

        switch (view.getId()) {
            case R.id.ib_light_white:
                choseColorId = view.getId();
                color = LightColor.White;
                break;
            case R.id.ib_light_blue:
                choseColorId = view.getId();
                color = LightColor.Blue;
                break;
            case R.id.ib_light_darkblue:
                choseColorId = view.getId();
                color = LightColor.BlueDark;
                break;
            case R.id.ib_light_green:
                choseColorId = view.getId();
                color = LightColor.Green;
                break;
            case R.id.ib_light_yellow:
                choseColorId = view.getId();
                color = LightColor.Yellow;
                break;
            case R.id.ib_light_red:
                choseColorId = view.getId();
                color = LightColor.Red;
                break;
            case R.id.ib_light_purple:
                choseColorId = view.getId();
                color = LightColor.RedPin;
                
                break;
            default:
        }
        sendCommand((byte)77,(byte)8);
    }

    /**
     * 显示是否保存动作弹框
     */
    private void showIsSaveDialog(int flag,Song song) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.savetips);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 切换歌曲的时候
                if(flag == 1){
                    saveActions();
                    actions.clear();
                    playerActionAdapter.notifyDataSetChanged();
                    if (song != null){
                        mContext.song = song;
                        mIvMusicAlbum.setImageResource(R.mipmap.plate);
                        mContext.currentMusicStatus = true;
                        mTvMusicName.setText(song.getName());
                        initMediaPlayer(song);
                    }
                    mTvMusicTime.setText("");
                    actionsBean = new Actions();
                }else {
                    //保存动作 停止音乐  返回主界面
                    saveActions();
                    isStop = true;
                    finish();
                }
            }
        });
        builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 切换歌曲的时候
                if(flag == 1){
                    actions.clear();
                    playerActionAdapter.notifyDataSetChanged();
                    if (song != null){
                        mContext.song = song;
                        mIvMusicAlbum.setImageResource(R.mipmap.plate);
                        mContext.currentMusicStatus = true;
                        mTvMusicName.setText(song.getName());
                        initMediaPlayer(song);
                    }
                    mTvMusicTime.setText("");
                    actionsBean = new Actions();
                }else {
                    // 停止音乐跟动作 返回主页面
                    isStop = true;
                    finish();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (actions != null && actions.size() > 0 && currentMusicStatus){
            showIsSaveDialog(0,null);
        }else {
            finish();
        }
    }

    /**
     * 时长收集
     */
    public void presentation() {
        if (isPlaying){
            return;
        }
        if (song == null){
            return;
        }
        if (actions.size() == 0){
            return;
        }
        // 判断当前音乐播放完成 不可以再继续收集
        if(!currentMusicStatus){
            // 播放完成替换最后一个动作继续采集
            if (actionBean.getMusicPosition() != 0){
                return;
            }
        }
        //收集动作属性
        if(actionBean != null){
            actionBean.setColor(color);
            actionBean.setColorMode(colorMode);
            actionBean.setSpeed(speed);
            actionBean.setTapCount(tapCount);
        }
        presentionStatus  = !presentionStatus;
        if(presentionStatus) {
            //Last Action 最后一包的动作
            sendCommand((byte)actionBean.getAction(),(byte)255);
            mIbPresentation.setBackgroundResource(R.mipmap.ic_pause);
            // 跳转到当前保存队列的最后结束时间
            if (mContext.choseActions != null){
                int duration = choseActions.getDuration();
                mediaPlayer.seekTo(duration);
            }
            if (actionBean.getMusicPosition() == 0){
                int musicPosition = actionBean.getMusicPosition();
                mediaPlayer.seekTo(musicPosition);
                //获取上一个动作是否有采集
                if (mContext.actions.size() > 1){
                    ActionBean actionBean = mContext.actions.get(mContext.actions.size() - 2);
                    if(actionBean.getMusicPosition() != 0){
                        mediaPlayer.seekTo(lastPosition);
                        ActionBean cuttentAction = mContext.actions.get(mContext.actions.size() - 1);
                        if (cuttentAction.getMusicPosition() == 0 && actionBean.getMusicPosition() != 0){
                            mediaPlayer.seekTo(actionBean.getLastPosition());
                        }
                    }
                }
            }
            // 选择队列继续采集
            if (actionBean != null){
                if(actionBean.getMusicPosition() != 0){
                    // 当所选择的动作序列继续采集 当前的歌曲时间为上一个动作的结束时间
                    if (actionsDetail != null){
                        if (actions.size() > 1) {
                            ActionBean actionBean = actions.get(actions.size() - 2);
                            mediaPlayer.seekTo(actionBean.getLastPosition());
                        }
                    }
                }
            }
            // 只有一个动作的话从头开始
            if (actions.size() == 1){
                ActionBean oneAction = actions.get(0);
                mediaPlayer.seekTo(0);
            }
            isStop = !isStop;
            playMusic();
            playerActionAdapter.setPresenterStatus(false);
        }else {
            sendCommandPresentStop();
            sendCommand((byte)77,(byte)8);
            mIbPresentation.setBackgroundResource(R.mipmap.ib_icon_start);
            isStop = !isStop;
            // 暂停
            stopMusic("presentation");
            playerActionAdapter.setPresenterStatus(true);
        }
    }

    /**
     * 演示
     */
    private void playing() {
        // 时长收集中 不允许播放完整歌曲
        if(presentionStatus) {
            return;
        }
        if (actions.size() == 0){
            return;
        }
        // 没有采集不能播放
        ActionBean lastAction = actions.get(actions.size() - 1);
        if (lastAction.getLastPosition() == 0){
            return;
        }
        isPlaying =  !isPlaying;
        if (isPlaying){
            isStop = !isStop;
            sendWholeCommand(actions);
            // 设置所选择队列的最后播放时间
            if (choseActions != null){
                lastPosition = choseActions.getDuration();
            }
            mIbPlay.setBackgroundResource(R.mipmap.ic_pause);
            mediaPlayer.seekTo(0);
            playMusic();
            playerActionAdapter.setPresenterStatus(false);
        }else {
            isStop = !isStop;
            mIbPlay.setBackgroundResource(R.mipmap.ib_icon_start);
            stopMusic("playing");
            playerActionAdapter.setPresenterStatus(true);
        }
    }

    /**
     * 暂停音乐
     */
    private void stopMusic(String flag) {
        //sendCommandPresentStop();
        lastPosition = mediaPlayer.getCurrentPosition();
        if (mContext.actions.size() > 0){
           mContext.actions.get(mContext.actions.size() - 1).setLastPosition(lastPosition);
        }
        //采集
//        if(flag.equals("presentation")){
//            lastPosition = mediaPlayer.getCurrentPosition();
//            //收集当前动作的时长
//            if(actionBean != null){
//                // 记录每个动作所播放的时长
//                actionPosition= lastPosition - afterActionPosition;
//                actionBean.setMusicPosition(actionPosition);
//                // 记录上一个动作的时长
//                afterActionPosition = actionBean.getMusicPosition();
//            }
//        }
        if (flag.equals("onStop")){
            if (isPlaying){
                isPlaying = !isPlaying;
                mIbPlay.setBackgroundResource(R.mipmap.ib_icon_start);
            }
            if (presentionStatus){
                presentionStatus = !presentionStatus;
                mIbPresentation.setBackgroundResource(R.mipmap.ib_icon_start);
            }
        }
        if (needleRotation != null) {
            needleRotation.start();
        }
        if (albumRotation != null && albumRotation.isRunning()) {
            albumRotation.cancel();
            float valueAvatar = (float) albumRotation.getAnimatedValue();
            albumRotation.setFloatValues(valueAvatar, 360f + valueAvatar);
        }
        currentMusicStatus = true;
        mediaPlayer.pause();
    }

    /**
     * 保存上一个动作的时长
     */
    private void savePresentationTime() {
        lastPosition = mediaPlayer.getCurrentPosition();
        //收集当前动作的时长
        if(actionBean != null){
            // 记录每个动作所播放的时长
            if(actionBean.getMusicPosition() == 0){
                actionPosition= lastPosition - afterActionPosition;
                actionBean.setMusicPosition(actionPosition);
            }
            if(actionBean.getLastPosition() == 0){
                actionBean.setLastPosition(lastPosition);
            }
            // 记录上一个动作的时长
            afterActionPosition = actionBean.getLastPosition();
            // 记录动作序列的总时长
            actionsBean.setDuration(lastPosition);
        }
    }

    /**
     * 播放音乐
     */
    private void playMusic() {
        if(song == null){
            return;
        }
        //播放音乐
        mediaPlayer.start();
        currentMusicStatus = true;

        thread = new Thread(new PlayMusicThread());
        thread.start();
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                int currentPosition = mediaPlayer.getCurrentPosition();
//                //mPbduration.setProgress(currentPosition);
//                handler.sendEmptyMessage(currentPosition);
//                // 根据收集的时间暂停
////                if (lastPosition != 0){
////                    //Log.d(TAG, "playMusic: " + mediaPlayer.getCurrentPosition() + "lastposition---" + lastPosition);
////                    int duration = currentPosition - lastPosition;
////                    if (!isPlaying){
////                        if(duration  > 1){
////                            //Log.d(TAG, "timer: ----------======================停止");
////                            isPlaying = false;
////                            timer.cancel();
////                            mContext.runOnUiThread(new Runnable() {
////                                @Override
////                                public void run() {
////                                    mIbPlay.setBackgroundResource(R.mipmap.ib_icon_start);
////                                    stopMusic("handler");
////                                }
////                            });
////                        }
////                    }
////                }
//
//            }
//        },0,50);

        albumRotation.start();
        needleRotation.reverse();
    }

    class PlayMusicThread implements Runnable {
        @Override
        public void run() {
            while (!isStop && mediaPlayer != null ) {
                // 将SeekBar位置设置到当前播放位置
                handler.sendEmptyMessage(mediaPlayer.getCurrentPosition());
                try {
                    // 每100毫秒更新一次位置
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int position = msg.what;
            mTvMusicTime.setText(formatTime(mediaPlayer.getDuration() - position));
            // 采集时45s暂停
            if (presentionStatus){
                ActionBean afterAction = null;
                int lastActionIndex = actions.indexOf(actionBean);
                if (lastActionIndex > 0){
                    afterAction = actions.get(lastActionIndex - 1);
                }else {
                    afterAction = actions.get(lastActionIndex);
                }
                int stopPosition = afterAction.getLastPosition() + 45000;
                //Log.d(TAG, "handleMessage: " + position + "," + stopPosition);
                if(position - stopPosition > 1){
                    isStop = !isStop;
                    presentionStatus = !presentionStatus;
                    mIbPresentation.setBackgroundResource(R.mipmap.ib_icon_start);
                    stopMusic("handler");
                    playerActionAdapter.setPresenterStatus(true);
                    sendCommand((byte)77,(byte)8);
                }
            }
            // 根据收集的时间暂停
            int stopPosition = 0;
            ActionBean lastAction = actions.get(actions.size() - 1);
            // 采集立马演示需要根据最后暂停的时间
            if (lastAction.getLastPosition() == 0){
                stopPosition = lastPosition;
            }else {
                stopPosition = lastAction.getLastPosition();
            }
            if (stopPosition != 0){
                if (isPlaying){
                    int duration = position - stopPosition;
                    if(duration > 1){
                        isStop = !isStop;
                        isPlaying = false;
                        mTvMusicTime.setText("");
                        mIbPlay.setBackgroundResource(R.mipmap.ib_icon_start);
                        stopMusic("handler");
                        playerActionAdapter.setPresenterStatus(true);
                        //sendCommand((byte)77,(byte)8);
                    }
                }
            }
        }
    };

    private String formatTime(int length) {
        Date date = new Date(length);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        String TotalTime = simpleDateFormat.format(date);
        return TotalTime;
    }

    /**
     * 显示音乐弹框
     */
    private void showMusicDialog() {
        // 获取音乐弹框
        List<Song> musics = LocalMusicUtils.getMusics(this);
        List<Actions> actions = helper.getActions();
        for (Actions a : actions) {
            for (Song s : musics) {
                if (a.getName().equals(s.getName())){
                    s.setSaveStaus(true);
                    s.setSaveDuration(LocalMusicUtils.formatTime(a.getDuration()));
                }
            }
        }
        MusicListDialog musicListDialog = new MusicListDialog(this,musics);
        musicListDialog.show();
        // 选择音乐回调
        musicListDialog.setOnItemClickListener(new MusicListDialog.OnItemClickListener() {
            @Override
            public void click(Song song) {
                // 选择音乐的时候 检查是否有采集动作
                if(mContext.actions.size() != 0 && !saveActionStatus){
                    showIsSaveDialog(1,song);
                    return;
                }

                //  如果当前的歌曲采集了动作 需要展示
                List<Actions> actions = helper.getActions(song.getName());
                if (actions.size() > 0){
                    Actions action = actions.get(0);
                    if (action.getName().equals(song.getName())){
                        // 获取当前序列下的所有动作 并替换当前的动作列表
                        List<ActionBean> actionDetail = helper.getActionDetail(action.getNumber());
                        mContext.actions.clear();
                        mContext.actions.addAll(actionDetail);
                        playerActionAdapter = new PlayerActionAdapter(mContext, mContext.actions);
                        mRvActionsList.setAdapter(playerActionAdapter);
                        // 获取当前序列的最后一个动作
                        ActionBean actionBean = actionDetail.get(actionDetail.size() - 1);
                        mContext.actionBean = actionBean;
                        mContext.actionsBean = action;
                        // 所选择的动作队列信息
                        choseActions = action;
                        // 回显歌曲信息
                        song.setPath(choseActions.getPath());
                        song.setName(choseActions.getName());
                        mContext.song = song;
                        mTvMusicName.setText(song.getName());
                        initMediaPlayer(song);
                        return;
                    }
                }

                mContext.actions.clear();
                playerActionAdapter.notifyDataSetChanged();
                isPlaying = false;
                mContext.song = song;
                mIvMusicAlbum.setImageResource(R.mipmap.plate);
                mContext.currentMusicStatus = true;
                mTvMusicName.setText(song.getName());
                mTvAddMusic.setVisibility(View.GONE);
                mIbPlay.setBackgroundResource(R.mipmap.ib_icon_start);
                if (needleRotation != null) {
                    needleRotation.start();
                }
                if (albumRotation != null && albumRotation.isRunning()) {
                    albumRotation.cancel();
                    float valueAvatar = (float) albumRotation.getAnimatedValue();
                    albumRotation.setFloatValues(valueAvatar, 360f + valueAvatar);
                }
                // 播放音乐
                initMediaPlayer(song);
                //播放动画
                setAnimator();
            }

            @Override
            public void del(Song song, int position) {
                List<Actions> actions = helper.getActions(song.getName());
                Actions action = actions.get(0);
                helper.delete(action.getName(),OpenHelper.TABLE_ACTIONS);
                helper.deleteByNumber(action.getNumber(),OpenHelper.TABLE_ACTION_DETAIL);
                song.setSaveDuration("");
                song.setSaveStaus(false);
                musicListDialog.updateSong(song,position);
            }
        });
    }

    /**
     * 选择音乐
     * @param song
     */
    private void initMediaPlayer(Song song) {
        //播放动画
        setAnimator();
        if (needleRotation != null) {
            needleRotation.start();
        }
        if (albumRotation != null && albumRotation.isRunning()) {
            albumRotation.cancel();
            float valueAvatar = (float) albumRotation.getAnimatedValue();
            albumRotation.setFloatValues(valueAvatar, 360f + valueAvatar);
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(song.getPath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放动画
     */
    private void setAnimator(){
//        needleRotation = ObjectAnimator.ofFloat(mIvNeedle, "rotation", 0, 0);
//        mIvNeedle.setX(0);
//        mIvNeedle.setY(0);
//        needleRotation.setDuration(800);
//        needleRotation.setInterpolator(new LinearInterpolator());

        albumRotation = ObjectAnimator.ofFloat(mIvMusicAlbum, "rotation", 0, 360);
        albumRotation.setDuration(20000);
        albumRotation.setInterpolator(new LinearInterpolator());
        albumRotation.setRepeatCount(ValueAnimator.INFINITE);
    }

    /**
     * 修改节奏状态
     */
    private void changeBeatStatus(View view) {
        mIvBeat2.setVisibility(View.INVISIBLE);
        mIvBeat4.setVisibility(View.INVISIBLE);
        mIvBeat8.setVisibility(View.INVISIBLE);
        mIvBeat12.setVisibility(View.INVISIBLE);
        mIvBeat16.setVisibility(View.INVISIBLE);
        view.setVisibility(View.VISIBLE);
        switch (view.getId()) {
            case R.id.ll_beat2:
                tapCount = 2;
                mIvBeat2.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_beat4:
                tapCount = 4;
                mIvBeat4.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_beat8:
                tapCount = 8;
                mIvBeat8.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_beat12:
                tapCount = 12;
                mIvBeat12.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_beat16:
                tapCount = 16;
                mIvBeat16.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }

    /**
     * 显示以添加的动作列表
     */
    private void showPalyerActions() {
        if (song == null){
            return;
        }
        ActionsDialog actionsDialog = new ActionsDialog(this,"",chooseFlag);
        actionsDialog.setOnActionClickListener(new ActionsDialog.OnActionClickListener() {
            @Override
            public void onCheckAction(ActionBean actionBean) {
                //设置当前动作序列的保存状态
                saveActionStatus = false;

                // 选择动作的时候保存上一个动作的采集时长
                if(mContext.actionBean != null && lastPosition != 0){
                    savePresentationTime();
                }

                // 当最后一个动作没有采集时长 在选择动作的时候直接替换  音乐播放完成 替换当前动作
                if(actions.size() > 0){
                    if(mContext.actionBean.getMusicPosition() == 0){
                        //修改动作
                        int lastIndex = actions.indexOf(mContext.actionBean);
                        playerActionAdapter.updateAction(actionBean,lastIndex);
                        flag = 999;
                        return;
                    }
                    if(!currentMusicStatus){
                        //修改动作
                        int lastIndex = actions.indexOf(mContext.actionBean);
                        playerActionAdapter.updateAction(actionBean,lastIndex);
                        mContext.actions.remove(lastIndex);
                        mContext.actions.add(actionBean);
                        if(lastIndex > 0){
                            ActionBean actionBean1 = mContext.actions.get(lastIndex - 1);
                            mediaPlayer.seekTo(actionBean1.getLastPosition());
                        }else {
                            ActionBean actionBean1 = mContext.actions.get(lastIndex);
                            mediaPlayer.seekTo(actionBean1.getLastPosition());
                        }
                        //记录当前动作
                        mContext.actionBean  = actionBean;
                        flag = 999;
                        return;
                    }
                }
                //记录当前选择的动作
                mContext.actionBean  = actionBean;
                //发送命令
                sendCommand((byte)actionBean.getAction());

                // 将动作添加到预览列表显示
                if(flag == 999){
                    actions.add(actionBean);
                    mRvActionsList.setAdapter(playerActionAdapter);
                }else{
                    //修改动作
                    playerActionAdapter.updateAction(actionBean,flag);

                    mContext.actions.remove(flag);
                    mContext.actions.add(actionBean);
                    flag = 999;
                }
                // 以添加的动作列表点击修改回调
                playerActionAdapter.setOnPalyerActionsItemClickListener(new PlayerActionAdapter.OnPalyerActionsItemClickListener() {
                    @Override
                    public void edit(int position) {
                        flag = position;
                        actionsDialog.show();
                    }

                    @Override
                    public void del(List<ActionBean> actions) {
                        if(mContext.actions.size() == 0){
                            mediaPlayer.seekTo(0);
                        }else {
                            //删除动作时需要删除记录的时长
                            ActionBean lastActionBean = mContext.actions.get(mContext.actions.size() - 1);
                            int lastPosition = lastActionBean.getLastPosition();
                            mediaPlayer.seekTo(lastPosition);
                            // 设置当前的actionBean
                            mContext.actionBean = lastActionBean;
                            //Log.d(TAG, "del:------------------ " + mContext.actionBean);
                        }
                    }
                });
            }

            @Override
            public void chooseActionType(int flag) {
                //Log.d(TAG, "chooseActionType: "  + flag);
                mContext.chooseFlag = flag;
            }
        });
        actionsDialog.show();
    }


    /**
     * 弹出选择节奏框
     */
    private void showBeatPopWindow() {
        mViewBeat = LinearLayout.inflate(mContext, R.layout.pop_beat, null);
        mPopBeat = new PopupWindow(mViewBeat, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopBeat.setTouchable(true);
        mPopBeat.setOutsideTouchable(true);
        mPopBeat.getContentView().setFocusableInTouchMode(true);
        mPopBeat.setFocusable(true);

        mPopBeat.getContentView().measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        int x0ff;
        int viewWidth = mLlTopContro.getWidth();
        int popWindowWidth = mViewBeat.getMeasuredWidth();
        x0ff = viewWidth  / 2 - popWindowWidth / 2;
        mPopBeat.showAsDropDown(mIbBeat,x0ff - 20,0);

        mLlBeat2 = mViewBeat.findViewById(R.id.ll_beat2);
        mLlBeat4 = mViewBeat.findViewById(R.id.ll_beat4);
        mLlBeat8 = mViewBeat.findViewById(R.id.ll_beat8);
        mLlBeat12 = mViewBeat.findViewById(R.id.ll_beat12);
        mLlBeat16 = mViewBeat.findViewById(R.id.ll_beat16);
        mIvBeat2 = mViewBeat.findViewById(R.id.iv_beat2);
        mIvBeat4 = mViewBeat.findViewById(R.id.iv_beat4);
        mIvBeat8 = mViewBeat.findViewById(R.id.iv_beat8);
        mIvBeat12 = mViewBeat.findViewById(R.id.iv_beat12);
        mIvBeat16 = mViewBeat.findViewById(R.id.iv_beat16);

        mLlBeat2.setOnClickListener(this);
        mLlBeat4.setOnClickListener(this);
        mLlBeat8.setOnClickListener(this);
        mLlBeat12.setOnClickListener(this);
        mLlBeat16.setOnClickListener(this);

        switch (tapCount){
            case 2:
                mIvBeat2.setVisibility(View.VISIBLE);
                break;
            case 4:
                mIvBeat4.setVisibility(View.VISIBLE);
                break;
            case 8:
                mIvBeat8.setVisibility(View.VISIBLE);
                break;
            case 12:
                mIvBeat12.setVisibility(View.VISIBLE);
                break;
            case 16:
                mIvBeat16.setVisibility(View.VISIBLE);
                break;
                default:
        }
    }

    /**
     * 弹出灯光颜色选择
     */
    private void showLightPopWindow() {
        mViewLight = LinearLayout.inflate(mContext, R.layout.pop_light, null);
        mPopLight = new PopupWindow(mViewLight, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopLight.setTouchable(true);
        mPopLight.setOutsideTouchable(true);
        mPopLight.getContentView().setFocusableInTouchMode(true);
        mPopLight.setFocusable(true);

        mPopLight.getContentView().measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        int x0ff;
        int viewWidth = mLlTopContro.getWidth();
        int popWindowWidth = mViewLight.getMeasuredWidth();
        x0ff = viewWidth  / 2 - popWindowWidth / 2;
        mPopLight.showAsDropDown(mIbLight,x0ff-20,0);

        mIbLightWhite = mViewLight.findViewById(R.id.ib_light_white);
        mIbLightDarkBlue = mViewLight.findViewById(R.id.ib_light_darkblue);
        mIbLightBlue = mViewLight.findViewById(R.id.ib_light_blue);
        mIbLightGreen = mViewLight.findViewById(R.id.ib_light_green);
        mIbLightYellow = mViewLight.findViewById(R.id.ib_light_yellow);
        mIbLightRed = mViewLight.findViewById(R.id.ib_light_red);
        mIbLightPurple = mViewLight.findViewById(R.id.ib_light_purple);
        mSwEye = mViewLight.findViewById(R.id.sw_eye);

        mIbLightWhite.setOnClickListener(this);
        mIbLightBlue.setOnClickListener(this);
        mIbLightDarkBlue.setOnClickListener(this);
        mIbLightGreen.setOnClickListener(this);
        mIbLightYellow.setOnClickListener(this);
        mIbLightRed.setOnClickListener(this);
        mIbLightPurple.setOnClickListener(this);

        mSwEye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mSwEye.setThumbResource(R.mipmap.ic_thumb);
                    colorMode = LightMode.Light;
                }else {
                    mSwEye.setThumbResource(R.mipmap.ic_thumb_close);
                    colorMode = LightMode.Splash;
                }
                sendCommand((byte)77,(byte)8);
            }
        });
        if (choseColorId != 0){
            switch (choseColorId) {
                case R.id.ib_light_white:
                    mIbLightWhite.setActivated(true);
                    break;
                case R.id.ib_light_blue:
                    mIbLightBlue.setActivated(true);
                    break;
                case R.id.ib_light_darkblue:
                    mIbLightDarkBlue.setActivated(true);
                    break;
                case R.id.ib_light_green:
                    mIbLightGreen.setActivated(true);
                    break;
                case R.id.ib_light_yellow:
                    mIbLightYellow.setActivated(true);
                    break;
                case R.id.ib_light_red:
                    mIbLightRed.setActivated(true);
                    break;
                case R.id.ib_light_purple:
                    mIbLightPurple.setActivated(true);
                    break;
                default:
            }
        }else {
            mIbLightBlue.setActivated(true);
        }

    }

    /**
     * 弹出调整速度框
     */
    private void showSpeedPopWindow() {
        mViewSpeed = LinearLayout.inflate(mContext, R.layout.pop_speed, null);
        mPopSpeed = new PopupWindow(mViewSpeed, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopSpeed.setTouchable(true);
        mPopSpeed.setOutsideTouchable(true);
        mPopSpeed.getContentView().setFocusableInTouchMode(true);
        mPopSpeed.setFocusable(true);
        mPopSpeed.getContentView().measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        int x0ff;
        int viewWidth = mLlTopContro.getWidth();
        int popWindowWidth = mViewSpeed.getMeasuredWidth();
        x0ff = viewWidth  / 2 - popWindowWidth / 2;
        mPopSpeed.showAsDropDown(mIbSpeed,x0ff-20,0);
        CustomSeekbar scSpeed = mViewSpeed.findViewById(R.id.cs_speed);
        scSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = Math.abs(progress - 3);
                speed = progress;
             }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        sendCommand((byte)77,(byte)8);
        isStop = true;
        stopMusic("onStop");
    }

    @Override
    protected void onDestroy() {
        sendCommand((byte)77,(byte)8);
        isStop = true;
        mediaPlayer.release();
        EventBus.getDefault().unregister(this);
        BluetoothUtils.cleanEvent();
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    private void sendCommandPresentStop(){
        Log.d(TAG, "sendCommandPresentStop: ------------发送暂停指令");
        DataPackage datapackage = new DataPackage(DataPackageType.DataPackageTypeStop);
        byte[]  data  = datapackage.toBytes();
        BluetoothUtils.getInstance(0).sendData(data);
        //Toast.makeText(mContext,"stop:"+ByteUtils.byteToStr(data),Toast.LENGTH_LONG).show();
    }


    /**
     * 播放动作队列命令
     * @param actionBean
     */
    private void sendWholeCommand(List<ActionBean> actionBean){
        DataPackage datapackage = new DataPackage(dataPackageType);

        //loop add action
        for (ActionBean action : actionBean){
            Log.d(TAG, "sendWholeCommand: " + action.getAction());
            DataFrame dataFrame = datapackage.createDataFrame();
            //动作
            dataFrame.setActionIndex((byte) action.getAction());
            dataFrame.setHandActionIndex((byte) action.getAction());
            dataFrame.setFootActionIndex((byte) action.getAction());

            //动作拍数d
            dataFrame.setActionCount((byte)action.getTapCount()); //8拍

            //速度
            dataFrame.setActionSpeed((byte)action.getSpeed()); //= (Byte)(3-self.speed);

            //灯的颜色
            dataFrame.setLightColor((byte)action.getColor());

            //灯的模式
            dataFrame.setLightModle((byte)action.getColorMode());
            //呼吸时长
            dataFrame.setBreathInterval((byte)0x02);
            dataFrame.setBreathSleepInterval((byte)0x02);


            datapackage.appendFrame(dataFrame);

        }

        byte[]  data  = datapackage.toBytes();
        BluetoothUtils.getInstance(0).sendData(data);

    }
    //发送命令
    private void sendCommand(byte action) {
        sendCommand(action,(byte)this.tapCount);
    }

    private void sendCommand(byte action, byte tapCount){

        DataPackage datapackage = new DataPackage(dataPackageType);

        DataFrame dataFrame = datapackage.createDataFrame();

        //动作
        dataFrame.setActionIndex(action);
        dataFrame.setHandActionIndex(action);
        dataFrame.setFootActionIndex(action);

        //动作拍数d
        dataFrame.setActionCount((byte)tapCount); //8拍

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

    @Override
    public void blueStautsChange(int status) {
//        if(status== RobotBleListener.ConnectStatusBreak) {
//            Log.d(TAG, "blueStautsChange: -------------break");
//            mTvStatus.setText(R.string.unconnected);
//            mTvStatus.setBackgroundResource(R.mipmap.ic_ble_unconn);
//        } else if(status == RobotBleListener.ConnectStatusSuccess) {
//            // 连接成功
//            Log.d(TAG, "blueStautsChange: -------------success");
//            mTvStatus.setText(R.string.connected);
//            mTvStatus.setBackgroundResource(R.mipmap.icon_blu_bg);
//        } else if(status == RobotBleListener.ConnectStatusFail) {
//            // 连接失败
//            Log.d(TAG, "blueStautsChange: -------------fail");
//            mTvStatus.setText(R.string.unconnected);
//            mTvStatus.setBackgroundResource(R.mipmap.ic_ble_unconn);
//        }
    }

    @Override
    public void blueValueChange(byte[] value) {

        //记录最后一帧的拍数 tapcount value[5];
        String s = ByteUtils.byteToStr(value);
        Log.d(TAG, "blueValueChange: " + s);
        Log.d(TAG, "blueValueChange: --------------" + value[5]);
        if(actionBean != null){
            actionBean.setTapCount(value[5]);
        }
    }
}
