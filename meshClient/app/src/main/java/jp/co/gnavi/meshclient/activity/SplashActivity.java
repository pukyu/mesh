package jp.co.gnavi.meshclient.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.places.UserDataType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;

import jp.co.gnavi.lib.common.GNDefine;
import jp.co.gnavi.lib.connection.GNCustomUrlConnection;
import jp.co.gnavi.lib.connection.GNCustomUrlReturnObject;
import jp.co.gnavi.lib.utility.GNUtility;
import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.common.Define;
import jp.co.gnavi.meshclient.common.Utility;
import jp.co.gnavi.meshclient.data.UserData;
import jp.co.gnavi.meshclient.gcm.GcmRegistration;

/**
 * スプラッシュ＆ログイン画面
 *
 * Created by kaifuku on 2016/10/05.
 */
public class SplashActivity extends BaseActivity implements TextWatcher {

    private ArrayList<UserData> mUserDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.splash );

        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // ユーザー情報読み込み。initialize でやっても良いけど、自動ログイン時、遷移が早すぎた。。。
        // delay 入れるのもめんどいので、ロードをここにずらす。
        loadUsers();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /////// TextWatcher メソッド
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        ImageView sendImage = (ImageView)findViewById(R.id.send_img);
        if( s.length() == 0 )
        {
            sendImage.setVisibility(View.INVISIBLE);
        }
        else
        {
            sendImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
    /////// TextWatcher

    /**
     * 初期化
     */
    private void initialize() {
        // utility シングルトンの初期化
        Utility.initialize();

        // テスト用 push トークン取得処理
        if( Define.PUSH_SERVICE )
        {
            final String strRegId = Utility.getSavedStringData( getApplicationContext(), "regist_id" );
            if( strRegId == null || strRegId.length() == 0 )
            {
                startPushService();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkRegId();
                    }
                }, 1 * 1000);
            }
            else
            {
                TextView regText = (TextView)findViewById(R.id.registration_id_test);
                regText.setText(strRegId);
                regText.setVisibility(View.VISIBLE);
                regText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callMailer(strRegId);
                    }
                });
            }
        }
/*
        int height = GNUtility.getDisplayHeight( this );
        int width = GNUtility.getDisplayWidth( this );

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
*/
        RelativeLayout userLayout = (RelativeLayout)findViewById(R.id.user_layout);
        userLayout.setVisibility(View.INVISIBLE);

        ImageView sendImage = (ImageView)findViewById(R.id.send_img);
        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText)findViewById(R.id.code_box);
                String strCode = editText.getText().toString();
                if( checkUser( strCode ) )
                {
                    goToNext();
                }
                else
                {
                    drawError(getResources().getString(R.string.alart_title), getResources().getString(R.string.code_error));
                }
            }
        });
        sendImage.setVisibility(View.INVISIBLE);

        EditText editText = (EditText)findViewById(R.id.code_box);
        editText.addTextChangedListener(this);
    }

    /**
     * 次の画面へ
     */
    private void goToNext() {
        Intent intent = new Intent( this, SelectAcitivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
        startActivity( intent );
        overridePendingTransition(0, 0);
    }

    /**
     * ユーザー情報読み込み
     */
    private void loadUsers()
    {
        if( !GNUtility.isConnection( getApplicationContext() ) )
        {
            drawError(getResources().getString(R.string.alart_title), getResources().getString(R.string.network_error));
            return;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {

                mUserDataList.clear();

                if (msg == null || msg.obj == null) {
                    drawError(getResources().getString(R.string.alart_title), getResources().getString(R.string.network_error));
                    return;
                }

                GNCustomUrlReturnObject obj = (GNCustomUrlReturnObject) msg.obj;
                Object ret = obj.getMessageObject();

                if( ret == null )
                {
                    drawError(getResources().getString(R.string.alart_title), getResources().getString(R.string.network_error));
                    return;
                }

                String strData = new String( (byte[])ret, Charset.forName( "UTF-8" ) );

                try {
                    JSONObject json = new JSONObject(strData);
                    JSONArray array = json.getJSONArray("data");

                    for( int i = 0 ; i < array.length() ; i++ )
                    {
                        JSONObject object = array.getJSONObject(i);

                        int id = object.getInt("id");
                        String strCode = object.getString("code");
                        String strName = object.getString("name");
                        String strOrg = object.getString("organization");

                        UserData user = new UserData(id, strCode, strName, strOrg);
                        mUserDataList.add(user);
                    }

                    // 既に一度でもログインしていた場合、そのユーザーがユーザーリストにあるかチェックし、あれば自動ログイン
                    String strOwnUser = Utility.getSavedStringData(getApplicationContext(), "code");
                    if( checkUser( strOwnUser ) )
                    {
                        goToNext();
                    }
                    else
                    {
                        RelativeLayout userLayout = (RelativeLayout)findViewById(R.id.user_layout);
                        userLayout.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    drawError(getResources().getString(R.string.alart_title), getResources().getString(R.string.data_error));
                }
            }
        };

        String strUrl = Define.BASE_URL +  "api/subordinate/";
        GNCustomUrlConnection connection = new GNCustomUrlConnection(handler, strUrl, GNDefine.CONNECTION_GET, null, null, null, getApplicationContext());
        connection.start();
    }

    /**
     * 指定ユーザーがユーザーリストに存在するかどうかチェック
     *
     * @param strUser       ユーザー名
     *
     * @return      true：存在     false：存在しない
     */
    private Boolean checkUser( String strUser )
    {
        if( strUser == null || strUser.length() == 0 )
        {
            return false;
        }

        for( int i = 0 ; i < mUserDataList.size(); i++ )
        {
            UserData data = mUserDataList.get(i);
            if( strUser.contentEquals( data.getCode() ) )
            {
                Utility.saveIntData(getApplicationContext(), "id", data.getId());
                Utility.saveStringData(getApplicationContext(), "code", data.getCode());
                Utility.saveStringData(getApplicationContext(), "name", data.getName());
                Utility.saveStringData(getApplicationContext(), "organization", data.getOrganization());

                return true;
            }
        }

        return false;
    }

    /**
     * テスト用 push トークン取得サービス開始
     */
    private void startPushService()
    {
        Intent intent = new Intent(this, GcmRegistration.class);
        startService(intent);
    }

    /**
     * テスト用 push トークンメール飛ばし
     *
     * @param strRegId      registration ID
     */
    private void callMailer(String strRegId){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SENDTO);

        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "REG ID");
        intent.putExtra(Intent.EXTRA_TEXT, strRegId);

        startActivity(Intent.createChooser(intent, null));
    }

    /**
     * テスト用 push トークン取得待ち・トークン表示処理
     */
    private void checkRegId()
    {
        final String getRegId = Utility.getSavedStringData(getApplicationContext(), "regist_id");
        if( getRegId == null || getRegId.length() == 0 )
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkRegId();
                }
            }, 1 * 1000);
        }
        else
        {
            TextView regText = (TextView)findViewById(R.id.registration_id_test);
            regText.setText(getRegId);
            regText.setVisibility(View.VISIBLE);
            regText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callMailer(getRegId);
                }
            });
        }
    }
}

