package cn.app.robert.base;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ComponentActivity;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.app.robert.MyApplication;

public abstract class BaseActivity extends ComponentActivity {


    private Unbinder mBind;
    private MyApplication mContext;
    private BaseActivity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mBind = ButterKnife.bind(this);
        mContext = MyApplication.getInstance();
        mActivity = this;
        initView();
        initData();

//        BleManager.getInstance().init(getApplication());
//        BluetoothUtils.getInstance(this).scan();
    }

    /**
     * 初始化view
     */
    protected abstract void initView();

    /**
     * 初始化data
     */
    protected abstract void initData();

    /**
     * 获取layout
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 跳转activity
     * @param cls
     */
    public void openActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    public void openActivity(String key,String param,Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(key,param);
        startActivity(intent);
    }

}