package jp.co.gnavi.meshclient.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by kaifuku on 2016/10/06.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive( Context context, Intent intent ) {
        //受け取ったインテントの処理をGcmIntentServiceで行う
        final ComponentName comp = new ComponentName( context.getPackageName(), GcmIntentService.class.getName() );

        //サービスの起動。処理中スリープを制御
        startWakefulService( context, ( intent.setComponent( comp ) ) );
        setResultCode( Activity.RESULT_OK );
    }
}

