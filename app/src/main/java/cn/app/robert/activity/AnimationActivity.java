package cn.app.robert.activity;

import android.util.Log;

import com.clj.fastble.BleManager;

import java.io.IOException;

import butterknife.BindView;
import cn.app.robert.R;
import cn.app.robert.base.BaseActivity;
import cn.app.robert.utils.BluetoothUtils;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * 开机动画页面
 * @author daxiong
 */
public class AnimationActivity extends BaseActivity {

    private static final String TAG = "AnimationActivity";

    @BindView(R.id.iv_gif)
    GifImageView mIvGif;

    @Override
    protected void initView() {
        GifDrawable gifDrawable = null;
        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.robert_ani);
            mIvGif.setImageDrawable(gifDrawable);
            gifDrawable.setLoopCount(1);
            // 设置动画完成监听器
            gifDrawable.addAnimationListener(loopNumber -> {
                Log.d(TAG, "onAnimationCompleted: over");
                finish();
                openActivity(MainActivity.class);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_animation;
    }
}
