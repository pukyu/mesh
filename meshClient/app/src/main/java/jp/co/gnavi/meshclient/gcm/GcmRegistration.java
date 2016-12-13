package jp.co.gnavi.meshclient.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.common.Utility;

/**
 * Created by kaifuku on 2016/10/06.
 */
public class GcmRegistration extends IntentService{

    public GcmRegistration()
    {
        super(GcmRegistration.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String regId = instanceID.getToken(getResources().getString(R.string.push_project_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Utility.customLog("push", "regId = " + regId, Log.DEBUG);

            Utility.saveStringData( getApplicationContext(), "regist_id", regId );

        } catch (Exception e) {
        }
    }
}
