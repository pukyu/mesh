package jp.co.gnavi.meshclient.activity;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import jp.co.gnavi.meshclient.R;

/**
 * Created by kaifuku on 2016/10/05.
 */
public class BaseActivity extends Activity {
    private SoundPool mSoundPool = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    // とりあえずの固定サウンド。。。もっと使う時に汎用的に。。。
    protected int initializeSound()
    {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        return mSoundPool.load(getApplicationContext(), R.raw.decision13, 0);
    }

    protected void releaseSound()
    {
        if(mSoundPool == null)
        {
            return;
        }

        mSoundPool.release();
        mSoundPool = null;
    }

    protected void playSound( int iSoundId )
    {
        if( mSoundPool == null )
        {
            return;
        }

        mSoundPool.play(iSoundId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

}
