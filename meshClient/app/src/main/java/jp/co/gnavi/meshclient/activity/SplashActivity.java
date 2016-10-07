package jp.co.gnavi.meshclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.common.Utility;

/**
 * Created by kaifuku on 2016/10/05.
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash );
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initialize();
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

    /**
     * 初期化
     */
    private void initialize() {
        Utility.initialize();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                goToNext();
            }
        };
        new Handler().postDelayed( runnable, 3000 );
    }

    /**
     * 次の画面へ
     */
    private void goToNext() {
        Intent intent = new Intent( this, MainActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
        startActivity( intent );
    }

}
