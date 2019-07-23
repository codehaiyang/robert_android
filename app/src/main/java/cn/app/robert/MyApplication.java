package cn.app.robert;

import android.app.Application;
import android.content.Context;

import org.greenrobot.eventbus.Subscribe;

import cn.app.robert.entity.BlueStatusMessage;

public class MyApplication extends Application {

    private static MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        //BleManager.getInstance().init(this);
    }

    public static synchronized MyApplication getInstance(){
        return myApplication;
    }

    public static Context getContext(){
        return myApplication;
    }

    @Subscribe
    public void onEventMainThread(BlueStatusMessage msg){
    }
}
