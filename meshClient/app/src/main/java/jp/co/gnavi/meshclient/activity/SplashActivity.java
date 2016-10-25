package jp.co.gnavi.meshclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import jp.co.gnavi.lib.utility.GNUtility;
import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.common.Utility;

/**
 * Created by kaifuku on 2016/10/05.
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.splash );
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

        int height = GNUtility.getDisplayHeight( this );
        int width = GNUtility.getDisplayWidth( this );

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);



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
//        Intent intent = new Intent( this, WaitActivity.class );
        Intent intent = new Intent( this, SelectAcitivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
        startActivity( intent );
        overridePendingTransition(0, 0);
    }

}
