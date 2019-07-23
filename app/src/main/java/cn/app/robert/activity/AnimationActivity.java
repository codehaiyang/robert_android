package cn.app.robert.activity;

import android.util.Log;

import java.io.IOException;

import butterknife.BindView;
import cn.app.robert.R;
import cn.app.robert.base.BaseActivity;
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
            gifDrawable = new GifDrawable(getResources(), R.drawable.robert_ani);
            mIvGif.setImageDrawable(gifDrawable);
            gifDrawable.setLoopCount(1);
            // 设置动画完成监听器
            gifDrawable.addAnimationListener(loopNumber -> {
                Log.d(TAG, "onAnimationCompleted: over");
                finish();
                if (page.equals("remote")){
                    openActivity(RemoteActivity.class);
                }else {
                    openActivity(ProgramActivity.class);
                }
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
