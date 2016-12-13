package jp.co.gnavi.meshclient.activity;

import android.app.Activity;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import jp.co.gnavi.lib.common.GNDefine;
import jp.co.gnavi.lib.connection.GNCustomUrlConnection;
import jp.co.gnavi.lib.utility.GNUtility;
import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.common.Define;
import jp.co.gnavi.meshclient.common.Utility;

/**
 * Created by kaifuku on 2016/10/05.
 */
public class BaseActivity extends Activity {
    static private SoundPool mSoundPool = null;
    static private int mLoopStremId = Utility.INVALID_ID;

    // 上部矢印アニメーションのそれぞれの開始間隔
    protected static final int ARROW_DELAY = 400;

    // アルファダウンアニメーションリクエスト番号
    protected static final int ALPHA_DOWN_ANIM = 0;
    // アルファアップアニメーションリクエスト番号
    protected static final int ALPHA_UP_ANIM = ALPHA_DOWN_ANIM + 1;

    // 矢印アルファダウンアニメーション時間
    protected static final int FLOW_ALPHA_ANIM_DOWN_DURATION = 1000;
    // 矢印アルファアップアニメーション時間
    protected static final int FLOW_ALPHA_ANIM_UP_DURATION = 700;
    // エラーアニメーション時間
    private static final int ERROR_ANIM_DURATION = 2000;

    // 矢印アニメーションの状態     true:アニメーション中   false:アニメーション停止中
    protected Boolean mbStateArrawAnimation = false;

    static private int miSoundResouceID[] = {
        Utility.INVALID_ID, Utility.INVALID_ID, Utility.INVALID_ID, Utility.INVALID_ID, Utility.INVALID_ID};

    final static protected int SOUND_PREPARE = 0;
    final static protected int SOUND_WARNING = SOUND_PREPARE + 1;
    final static protected int SOUND_START = SOUND_WARNING + 1;
    final static protected int SOUND_WIN = SOUND_START + 1;
    final static protected int SOUND_LOSE = SOUND_WIN + 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // フルスクリーン化
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // エラービュー準備
        View errorLayout = (View)findViewById(R.id.error_root);
        if( errorLayout != null )
        {
            errorLayout.setVisibility(View.GONE);
        }
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
    static public void initializeSound( Context context )
    {
        if( mSoundPool != null )
        {
            return;
        }

        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        miSoundResouceID[SOUND_PREPARE] = mSoundPool.load(context, R.raw.prepare, 0);
        miSoundResouceID[SOUND_WARNING] = mSoundPool.load(context, R.raw.warning, 0);
        miSoundResouceID[SOUND_START] = mSoundPool.load(context, R.raw.start, 0);
        miSoundResouceID[SOUND_WIN] = mSoundPool.load(context, R.raw.win, 0);
        miSoundResouceID[SOUND_LOSE] = mSoundPool.load(context, R.raw.lose, 0);
    }

    static public void releaseSound()
    {
        if(mSoundPool == null)
        {
            return;
        }

        mSoundPool.release();
        mSoundPool = null;
    }

    protected void playSound( int iSoundType )
    {
        if( mSoundPool == null || miSoundResouceID.length <= iSoundType || iSoundType < 0 )
        {
            return;
        }

        int iLoop = 0;
        if( iSoundType == SOUND_WARNING )
        {
            iLoop = -1;
        }

        stopSound();
        mLoopStremId = mSoundPool.play(miSoundResouceID[iSoundType], 1.0f, 1.0f, 0, iLoop, 1.0f);
    }

    protected void stopSound()
    {
        if( mSoundPool == null || mLoopStremId == Utility.INVALID_ID )
        {
            return;
        }

        mSoundPool.stop(mLoopStremId);
        mLoopStremId = Utility.INVALID_ID;
    }

    /**
     * 矢印アルファアニメーション開始
     *
     * @note    矢印が画面にある場合のみ使用すること
     */
    protected void startArrowAnimation()
    {
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
    }

    /**
     * 矢印アルファアニメーションクリア
     *
     * @note    矢印が画面にある場合のみ使用すること
     */
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

    /**
     * アルファアニメーションループリクエスト処理
     *
     * @param view              アニメーション対象ビュー
     * @param iAnimType         アニメーションタイプ（ALPHA_DOWN_ANIM or ALPHA_UP_ANIM）
     */
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


    /**
     * 指定上司の情報取得
     *
     * @param strBossID     上司 ID
     *
     * @note 結果は callbackBossInformation メソッドで返却されるので override して処理を記述すること
     */
    protected void getBossInformation( final String strBossID )
    {
        if( !GNUtility.isConnection( getApplicationContext() ) )
        {
            drawError(getResources().getString(R.string.alart_title), getResources().getString(R.string.network_error));
            return;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                callbackBossInformation( msg );
            }
        };

        String strUrl = Define.BASE_URL +  "api/boss/" + strBossID;
        GNCustomUrlConnection connection = new GNCustomUrlConnection(handler, strUrl, GNDefine.CONNECTION_GET, null, null, null, getApplicationContext());
        connection.start();
    }

    /**
     * 上司情報取得 API コールバック
     *
     * @param msg       取得データ
     *
     * @note getBossInformation 使用時は override してデータ取得後の処理を記述すること
     */
    protected void callbackBossInformation( Message msg )
    {
    }

    /**
     * 汎用エラー表示
     *
     * @param strTitle          タイトル
     * @param strDetail         詳細
     *
     * @return  表示の成否
     */
    protected Boolean drawError( String strTitle, String strDetail )
    {
        final RelativeLayout errorLayout = (RelativeLayout)findViewById(R.id.error_root);
        final View animView = (View)findViewById(R.id.error_color_view);

        if( errorLayout == null )
        {
            return false;
        }

        TextView    errorTitle = (TextView)findViewById(R.id.error_title);
        errorTitle.setText(strTitle);

        TextView    detailText = (TextView)findViewById(R.id.error_detail);
        detailText.setText(strDetail);


        errorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                animView.clearAnimation();

                stopSound();
            }
        });

        animView.clearAnimation();
        AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
        alpha.setDuration(ERROR_ANIM_DURATION);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setRepeatCount(-1);
        animView.setAnimation(alpha);

        errorLayout.setVisibility(View.VISIBLE);

        playSound(SOUND_WARNING);

        return true;
    }
}
