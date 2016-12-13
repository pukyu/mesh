package jp.co.gnavi.meshclient.Application;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import jp.co.gnavi.meshclient.activity.BaseActivity;

/**
 * Created by kaifuku on 2016/10/21.
 */

public class CustomApplication extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        BaseActivity.initializeSound( getApplicationContext() );
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();

        BaseActivity.releaseSound();
    }
}

