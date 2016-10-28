package jp.co.gnavi.meshclient.activity;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

import jp.co.gnavi.lib.common.GNDefine;
import jp.co.gnavi.lib.connection.GNCustomUrlConnection;
import jp.co.gnavi.lib.connection.GNCustomUrlReturnObject;
import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.common.Define;
import jp.co.gnavi.meshclient.common.Utility;

/**
 * Created by kaifuku on 2016/10/05.
 */
public class BaseActivity extends Activity {
    private SoundPool mSoundPool = null;

    // 上部矢印アニメーションのそれぞれの開始間隔
    protected static final int ARROW_DELAY = 400;


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
/*
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        return mSoundPool.load(getApplicationContext(), R.raw.decision13, 0);
*/
        return 1;
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

//        mSoundPool.play(iSoundId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    /**
     * 矢印がある画面でのみ使うこと
     */
    protected void startArrowAnimation()
    {
/*
        ImageView leftArrow = (ImageView)findViewById(R.id.left_arrow_3);
        setRoopFlowAlphaAnimation(leftArrow, ALPHA_DOWN_ANIM);
*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView leftArrow = (ImageView)findViewById(R.id.left_arrow_2);
                setRoopFlowAlphaAnimation(leftArrow, ALPHA_DOWN_ANIM);
            }
        }, ARROW_DELAY );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView leftArrow = (ImageView)findViewById(R.id.left_arrow_1);
                setRoopFlowAlphaAnimation(leftArrow, ALPHA_DOWN_ANIM);
            }
        }, ARROW_DELAY*2 );

/*
        ImageView rightArrow = (ImageView)findViewById(R.id.right_arrow_3);
        setRoopFlowAlphaAnimation(rightArrow, ALPHA_DOWN_ANIM);
*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView rightArrow = (ImageView)findViewById(R.id.right_arrow_2);
                setRoopFlowAlphaAnimation(rightArrow, ALPHA_DOWN_ANIM);
            }
        }, ARROW_DELAY );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView rightArrow = (ImageView)findViewById(R.id.right_arrow_1);
                setRoopFlowAlphaAnimation(rightArrow, ALPHA_DOWN_ANIM);
            }
        }, ARROW_DELAY*2 );

        View lineViewLeft = (View)findViewById(R.id.line_view_left);
        setRoopLineAlphaAnimation(lineViewLeft, ALPHA_DOWN_ANIM);

        View lineViewRight = (View)findViewById(R.id.line_view_right);
        setRoopLineAlphaAnimation(lineViewRight, ALPHA_DOWN_ANIM);
    }

    protected static final int ALPHA_DOWN_ANIM = 0;
    protected static final int ALPHA_UP_ANIM = ALPHA_DOWN_ANIM + 1;

    protected static final int LINE_ALPHA_ANIM_DURATION = 500;

    protected void setRoopLineAlphaAnimation(final View view, final int iAnimType )
    {
        AlphaAnimation alpha;
        if( iAnimType == ALPHA_DOWN_ANIM )
        {
            alpha = new AlphaAnimation( 1.0f, 0.9f );
            alpha.setDuration( LINE_ALPHA_ANIM_DURATION );
        }
        else if( iAnimType == ALPHA_UP_ANIM )
        {
            alpha = new AlphaAnimation( 0.9f, 1.0f );
            alpha.setDuration( LINE_ALPHA_ANIM_DURATION );
        }
        else
        {
            return;
        }
        alpha.setInterpolator( new LinearInterpolator() );
        alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if( iAnimType == ALPHA_DOWN_ANIM )
                {
                    setRoopFlowAlphaAnimation( view, ALPHA_UP_ANIM );
                }
                else if( iAnimType == ALPHA_UP_ANIM )
                {
                    setRoopFlowAlphaAnimation( view, ALPHA_DOWN_ANIM );
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.clearAnimation();
        view.setAnimation( alpha );
    }

    protected static final int FLOW_ALPHA_ANIM_DOWN_DURATION = 1000;
    protected static final int FLOW_ALPHA_ANIM_UP_DURATION = 700;

    protected void setRoopFlowAlphaAnimation( final View view, final int iAnimType )
    {
        AlphaAnimation alpha;
        if( iAnimType == ALPHA_DOWN_ANIM )
        {
            alpha = new AlphaAnimation( 1.0f, 0.0f );
            alpha.setDuration( FLOW_ALPHA_ANIM_DOWN_DURATION );
        }
        else if( iAnimType == ALPHA_UP_ANIM )
        {
            alpha = new AlphaAnimation( 0.0f, 1.0f );
            alpha.setDuration( FLOW_ALPHA_ANIM_UP_DURATION );
        }
        else
        {
            return;
        }
        alpha.setInterpolator( new LinearInterpolator() );
        alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if( !mbStateArrawAnimation )
                {
                    return;
                }


                if( iAnimType == ALPHA_DOWN_ANIM )
                {
                    setRoopFlowAlphaAnimation( view, ALPHA_UP_ANIM );
                }
                else if( iAnimType == ALPHA_UP_ANIM )
                {
                    setRoopFlowAlphaAnimation( view, ALPHA_DOWN_ANIM );
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.clearAnimation();
        view.setAnimation( alpha );
    }

    protected void clearArrawAnimation()
    {
        ImageView leftArraw3 = (ImageView)findViewById(R.id.left_arrow_3);
        leftArraw3.clearAnimation();

        ImageView leftArraw2 = (ImageView)findViewById(R.id.left_arrow_2);
        leftArraw2.clearAnimation();

        ImageView leftArraw1 = (ImageView)findViewById(R.id.left_arrow_1);
        leftArraw1.clearAnimation();

        ImageView rightArraw3 = (ImageView)findViewById(R.id.right_arrow_3);
        rightArraw3.clearAnimation();

        ImageView rightArraw2 = (ImageView)findViewById(R.id.right_arrow_2);
        rightArraw2.clearAnimation();

        ImageView rightArraw1 = (ImageView)findViewById(R.id.right_arrow_1);
        rightArraw1.clearAnimation();
    }


    protected Boolean mbStateArrawAnimation = false;


    protected void getBossInformation( final String strBossID )
    {
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                callbackBossInformation( msg );
            }
        };

        String strUrl = Define.BASE_URL +  "api/boss/" + strBossID;
        GNCustomUrlConnection connection = new GNCustomUrlConnection(handler, strUrl, GNDefine.CONNECTION_GET, null, null, null, getApplicationContext());
        connection.start();
    }


    protected void callbackBossInformation( Message msg )
    {
    }
}
