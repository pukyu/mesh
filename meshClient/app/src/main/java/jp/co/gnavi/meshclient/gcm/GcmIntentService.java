package jp.co.gnavi.meshclient.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.activity.MainActivity;
import jp.co.gnavi.meshclient.activity.SplashActivity;

/**
 * Created by kaifuku on 2016/10/06.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;

    public GcmIntentService( String name ) {
        super( name );
    }

    public GcmIntentService() {
        super( "GcmIntentService" );
    }

    @Override
    protected void onHandleIntent( Intent intent ) {
        final Bundle extras = intent.getExtras();
        final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        final String messageType = gcm.getMessageType(intent);

        if ( !extras.isEmpty() ) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.d("LOG", "messageType(error): " + messageType + ",body:" + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.d("LOG", "messageType(deleted): " + messageType + ",body:" + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.d("LOG", "messageType(message): " + messageType + ",body:" + extras.toString());

                final String message = extras.getString("message");
                sendNotification( message );
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent( intent );
    }

    private void sendNotification( String msg ) {
        final NotificationManager notificationManager = (NotificationManager)
                this.getSystemService( Context.NOTIFICATION_SERVICE );

        final PendingIntent contentIntent = PendingIntent.getActivity( this, 0, new Intent( this, SplashActivity.class ), 0 );

        final Uri uri= RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION );

        final NotificationCompat.Builder builder = new NotificationCompat.Builder( this );
        builder.setContentTitle( getString( R.string.app_name ) );
        builder.setSmallIcon( R.drawable.ic_launcher );
        builder.setStyle( new NotificationCompat.BigTextStyle().bigText( msg ) );
        builder.setContentText( msg );
        builder.setWhen( System.currentTimeMillis() );
        builder.setSound( uri );

        // タップで通知領域から削除する
        builder.setAutoCancel( true );

        builder.setContentIntent( contentIntent );
        notificationManager.notify( NOTIFICATION_ID, builder.build() );
    }
}
