package jp.co.gnavi.meshclient.activity;

import android.os.Bundle;

import jp.co.gnavi.meshclient.R;

/**
 * Created by kaifuku on 2016/10/12.
 */
public class WaitActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait );
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
    }

}
