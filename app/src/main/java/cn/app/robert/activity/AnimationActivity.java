package cn.robert.app.activity;

import android.util.Log;

import java.io.IOException;

import butterknife.BindView;
import cn.robert.app.R;
import cn.robert.app.base.BaseActivity;
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
        String page = getIntent().getStringExtra("page");
        GifDrawable gifDrawable = null;
        try {
            if ("0".equals(page)){
                gifDrawable = new GifDrawable(getResources(), R.drawable.big_robot);
            }else {
                gifDrawable = new GifDrawable(getResources(), R.drawable.little_robot);
            }
            mIvGif.setImageDrawable(gifDrawable);
            gifDrawable.setLoopCount(1);
            // 设置动画完成监听器
            gifDrawable.addAnimationListener(loopNumber -> {
                Log.d(TAG, "onAnimationCompleted: over");
                finish();
                openActivity("page",page,HomeActivity.class);
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
