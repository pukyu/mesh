package jp.co.gnavi.meshclient.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mlkcca.client.DataElement;
import com.mlkcca.client.DataStore;
import com.mlkcca.client.DataStoreEventListener;
import com.mlkcca.client.MilkCocoa;
import com.mlkcca.client.Streaming;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import jp.co.gnavi.lib.common.GNDefine;
import jp.co.gnavi.lib.connection.GNCustomUrlConnection;
import jp.co.gnavi.lib.connection.GNCustomUrlReturnObject;
import jp.co.gnavi.lib.connection.GNImageLoad;
import jp.co.gnavi.lib.utility.GNUtility;
import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.common.Define;
import jp.co.gnavi.meshclient.common.Utility;
import jp.co.gnavi.meshclient.data.SelectListData;

/**
 * 開始待ち画面
 *
 * Created by kaifuku on 2016/10/12.
 */
public class WaitActivity extends BaseActivity {
    // 一番外側の円アニメーション時間（ミリ秒）
    private static final int    CIRCLE4_ANIMATE_DURATION = 90000;
    // 内側から 3 番目の円アニメーション時間（ミリ秒）
    private static final int    CIRCLE3_ANIMATE_DURATION = 40000;
    // 内側から 2 番目の円アニメーション時間（ミリ秒）
    private static final int    CIRCLE2_ANIMATE_DURATION = 25000;
    // 一番内側の円アニメーション時間（ミリ秒）
    private static final int    CIRCLE1_ANIMATE_DURATION = 4000;

    // カウントダウン値
    private int miNowCount;
    // 開始までの時間（画面上部に表示される時間）
    private int miDrawTime = Utility.INVALID_ID;

    // 状態
    private static final int STATE_WAIT = 0;
    private static final int STATE_READY = STATE_WAIT + 1;
    private static final int STATE_START = STATE_READY + 1;
    // 現在の状態
    private int miState = STATE_WAIT;

    // PUSHプラットフォーム
    private MilkCocoa milkCocoa;
    private DataStore dataStore;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private SelectListData mTargetData;

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        // 戻るボタンが押されたとき
        if(e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        }

        return super.dispatchKeyEvent(e);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if( Utility.isTablet( getApplicationContext() ) )
        {
            setContentView( R.layout.wait_tablet );
        }
        else
        {
            setContentView( R.layout.wait );
        }

        Intent intent = getIntent();
        mTargetData = (SelectListData) intent.getSerializableExtra("target");

        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if( !mbStateArrawAnimation )
        {
            mbStateArrawAnimation = true;
            startArrowAnimation();
        }

//        initializeSound();
    }

    @Override
    protected void onPause() {
        super.onPause();

        clearArrawAnimation();
//        releaseSound();
        mbStateArrawAnimation = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if( dataStore != null )
        {
            dataStore.removeDataStoreEventListener();
        }
        dataStore = null;
        milkCocoa = null;
    }


    /**
     * 初期化
     */
    private void initialize() {
        sendRegist();

        // TODO:仮
        changeStateWait();

        makeConnection();

        TextView inforTitle = (TextView)findViewById(R.id.infor_text);
        inforTitle.setVisibility(View.INVISIBLE);

        final ImageView circle4 = (ImageView)findViewById(R.id.circle_4);
        final ViewTreeObserver observer4 = circle4.getViewTreeObserver();
        observer4.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int point = circle4.getWidth()/2;
                setRoopRotateAnimation(circle4, 0, 360, point, point, CIRCLE4_ANIMATE_DURATION);

                circle4.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        final ImageView circle3 = (ImageView)findViewById(R.id.circle_3);
        final ViewTreeObserver observer3 = circle3.getViewTreeObserver();
        observer3.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int point = circle3.getWidth()/2;
                setRoopRotateAnimation(circle3, 360, 0, point, point, CIRCLE3_ANIMATE_DURATION);

                circle3.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        final ImageView circle2 = (ImageView)findViewById(R.id.circle_2);
        final ViewTreeObserver observer2 = circle2.getViewTreeObserver();
        observer2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int point = circle2.getWidth()/2;
                setRoopRotateAnimation(circle2, 0, 360, point, point, CIRCLE2_ANIMATE_DURATION);

                circle2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        final ImageView circle1 = (ImageView)findViewById(R.id.circle_1);
        final ViewTreeObserver observer1 = circle1.getViewTreeObserver();
        observer1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int point = circle1.getWidth()/2;
                RelativeLayout layout = (RelativeLayout)findViewById(R.id.user_layout);
                int pointy = layout.getHeight()/2;
                setRoopScaleAlphaAnimation( circle1, point, pointy, CIRCLE1_ANIMATE_DURATION );

                circle1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        ImageView backSelectImage = (ImageView)findViewById(R.id.reselect);
        backSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUnRegist();

                Intent intent = new Intent( WaitActivity.this, SelectAcitivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity( intent );
                overridePendingTransition(0, 0);
            }
        });


        final ImageView targetIcon = (ImageView)findViewById(R.id.user_icon);
        if( mTargetData.getIconImageUrl() != null )
        {
            Handler handler = new Handler()
            {
                public void handleMessage(Message msg) {
                    targetIcon.setVisibility(View.VISIBLE);
                }
            };
            targetIcon.setVisibility(View.INVISIBLE);

            GNImageLoad imgLoad = new GNImageLoad(handler, mTargetData.getIconImageUrl(), targetIcon, GNDefine.CONNECTION_GET, null, null, null, getApplicationContext());
            imgLoad.start();
        }
        else
        {
            if( mTargetData.getIconResourceId() != Utility.INVALID_ID )
            {
                targetIcon.setImageResource(mTargetData.getIconResourceId());
            }
            else
            {
                targetIcon.setImageResource(R.drawable.user_def);
            }

            targetIcon.setVisibility(View.VISIBLE);
        }

        TextView group = (TextView)findViewById(R.id.group_name);
        group.setText(mTargetData.getTeam());

        TextView targetName = (TextView)findViewById(R.id.user_name);
        targetName.setText(mTargetData.getTargetName());

        calcTimer();
    }

    private void calcTimer()
    {
        TextView waitTime = (TextView)findViewById(R.id.wait_time);
        if( mTargetData.getStartTime() != null )
        {
            try{
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                Calendar startTime = Calendar.getInstance();
                Date startDate = new Date( format.parse(mTargetData.getStartTime()).getTime() );
                startTime.setTime(startDate);

                Date date = new Date();
                Calendar nowTime = Calendar.getInstance();
                nowTime.setTime(date);

                if( startTime.get(Calendar.YEAR) != nowTime.get(Calendar.YEAR)
                        || startTime.get(Calendar.MONTH) != nowTime.get(Calendar.MONTH)
                        || startTime.get(Calendar.DAY_OF_MONTH) != nowTime.get(Calendar.DAY_OF_MONTH))
                {
                    waitTime.setText("∞");
                }
                else
                {
                    int iSubHour = startTime.get(Calendar.HOUR_OF_DAY) - nowTime.get(Calendar.HOUR_OF_DAY);
                    int iSubMinute = startTime.get(Calendar.MINUTE) - nowTime.get(Calendar.MINUTE);

                    miDrawTime = iSubHour * 60 + iSubMinute;

                    if( miDrawTime > 999 )
                    {
                        waitTime.setText("∞");
                    }
                    else if( miDrawTime < 0 )
                    {
                        waitTime.setText( "0" );
                    }
                    else
                    {
                        waitTime.setText(String.valueOf(miDrawTime));
                    }

                    if( miDrawTime > 0 )
                    {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateTimer();
                            }
                        }, 60 * 1000);
                    }
                }
            }catch (ParseException e) {
                waitTime.setText("？？？");
            }
        }
        else
        {
            waitTime.setText("？？？");
        }
    }

    private void updateTimer()
    {
        if( miDrawTime == Utility.INVALID_ID || miDrawTime <= 0)
        {
            return;
        }

        TextView waitTime = (TextView)findViewById(R.id.wait_time);
        miDrawTime--;
        if( waitTime != null )
        {
            if( waitTime != null )
            {
                waitTime.setText(miDrawTime);
            }
        }

        if( miDrawTime > 0 && waitTime != null )
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateTimer();
                }
            }, 60 * 1000);
        }
    }

    private void setRoopRotateAnimation(View view, float fStart, float fEnd, int iCenterX, int iCenterY, int iDuration )
    {
        RotateAnimation rotate = new RotateAnimation( fStart, fEnd, iCenterX, iCenterY );
        rotate.setDuration( iDuration );
        rotate.setInterpolator( new LinearInterpolator() );
        rotate.setRepeatCount( -1 );

        view.clearAnimation();
        view.setAnimation( rotate );
    }

    private void setRoopScaleAlphaAnimation( View view, int iCenterX, int iCenterY, int iDuration )
    {
        ScaleAnimation scale = new ScaleAnimation( 1.0f, 2.3f, 1.0f, 2.3f, iCenterX, iCenterY );
        scale.setRepeatCount( -1 );
        AlphaAnimation alpha = new AlphaAnimation( 1.0f, 0.0f );
        alpha.setRepeatCount( -1 );

        AnimationSet animSet = new AnimationSet( true );
        animSet.addAnimation( scale );
        animSet.addAnimation( alpha );
        animSet.setDuration( iDuration );
        animSet.setRepeatCount( -1 );
        animSet.setInterpolator( new LinearInterpolator() );

        view.clearAnimation();
        view.setAnimation( animSet );
    }

    private void setCloseRotateAnimation(View view, float fStart, float fEnd, int iCenterX, int iCenterY, int iDuration )
    {
        ScaleAnimation scale = new ScaleAnimation( 1.0f, 0.0f, 1.0f, 0.0f, iCenterX, iCenterY );
        RotateAnimation rotate = new RotateAnimation( fStart, fEnd, iCenterX, iCenterY );

        AnimationSet animSet = new AnimationSet( true );
        animSet.addAnimation( scale );
        animSet.addAnimation( rotate );
        animSet.setDuration( iDuration );
        animSet.setInterpolator( new AccelerateInterpolator() );

        view.clearAnimation();
        view.setAnimation( animSet );
    }

    private void setOpenRotateAnimation(final View view, final float fStart, final float fEnd, final int iCenterX, final int iCenterY, final int iOpenDuration, final int iRoopDuration )
    {
        float fSub = ( Math.abs( fStart - fEnd ) / ( iRoopDuration / 1000.0f ) ) * ( iOpenDuration / 1000.0f );
        final float fOpenAnimEnd = fStart < fEnd ? fSub : fEnd - fSub;
        final float fRoopAnimEnd = fStart < fEnd ? fOpenAnimEnd + 360.0f : fOpenAnimEnd - 360.0f;

        ScaleAnimation scale = new ScaleAnimation( 0.0f, 1.0f, 0.0f, 1.0f, iCenterX, iCenterY );
        RotateAnimation rotate = new RotateAnimation( fStart, fOpenAnimEnd, iCenterX, iCenterY );

        AnimationSet animSet = new AnimationSet( true );
        animSet.addAnimation( scale );
        animSet.addAnimation( rotate );
        animSet.setDuration( iOpenDuration );
        animSet.setInterpolator( new AccelerateInterpolator() );
        animSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setRoopRotateAnimation( view, fOpenAnimEnd, fRoopAnimEnd, iCenterX, iCenterY, iRoopDuration );
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.clearAnimation();
        view.setAnimation( animSet );
    }

    private void changeStateWait()
    {
        setDisplayColorFilter(getResources().getColor(R.color.themeColorNonAlpha));

        TextView stateText = (TextView)findViewById(R.id.state_title);
        stateText.setText("WAIT");

        TextView subTitle = (TextView)findViewById(R.id.sub_title);
        subTitle.setText("上司着席待");

        RelativeLayout readyLayout = (RelativeLayout)findViewById(R.id.overlay_layout);
        readyLayout.setVisibility(View.GONE);

        RelativeLayout subTitleWaitLayout = (RelativeLayout)findViewById(R.id.wait_layout);
        subTitleWaitLayout.setVisibility(View.VISIBLE);

        RelativeLayout stateSubTitleLayout = (RelativeLayout)findViewById(R.id.state_sub_title);
        stateSubTitleLayout.setVisibility(View.INVISIBLE);

        ImageView backSelectImage = (ImageView)findViewById(R.id.reselect);
        backSelectImage.setEnabled(true);
        backSelectImage.setVisibility(View.VISIBLE);
    }

    private void setFlashAnimation()
    {
        final RelativeLayout layout = (RelativeLayout)findViewById(R.id.overlay_layout);

        AlphaAnimation alpha = new AlphaAnimation( 1.0f, 0.0f );
        alpha.setDuration( 500 );
        alpha.setInterpolator( new LinearInterpolator() );
        alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layout.setVisibility(View.GONE);
                layout.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        layout.clearAnimation();
        layout.setVisibility(View.VISIBLE);
        layout.setAnimation( alpha );
    }

    private final static int CLOSE_ANIM_DURATION = 2000;
    private final static int OPEN_ANIM_DURATION = 2000;
    private void readyCountDown()
    {
        getBossInformation(mTargetData.getListNo());

        setOverlayColorFilter( STATE_WAIT );
        setFlashAnimation();

        playSound(SOUND_PREPARE);

        final ImageView circle4 = (ImageView)findViewById(R.id.circle_4);
        final int iPoint4 = circle4.getWidth()/2;
        float fEndRotate4 = ( 360.0f / ( CIRCLE4_ANIMATE_DURATION / 1000.0f ) ) * (CLOSE_ANIM_DURATION / 1000.0f);
        setCloseRotateAnimation(circle4, 0.0f, 360.0f, iPoint4, iPoint4, CLOSE_ANIM_DURATION);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setCircleColorFilter(getResources().getColor(R.color.red));
                setOpenRotateAnimation(circle4, 0.0f, 360.0f, iPoint4, iPoint4, OPEN_ANIM_DURATION, CIRCLE4_ANIMATE_DURATION / 4 );
            }
        }, CLOSE_ANIM_DURATION - 200 );

        final ImageView circle3 = (ImageView)findViewById(R.id.circle_3);
        final int iPoint3 = circle3.getWidth()/2;
        float fEndRotate3 = ( 360.0f / ( CIRCLE3_ANIMATE_DURATION / 1000.0f ) ) * (CLOSE_ANIM_DURATION / 1000.0f);
        setCloseRotateAnimation(circle3, 360.0f, 0.0f, iPoint3, iPoint3, CLOSE_ANIM_DURATION);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setOpenRotateAnimation(circle3, 360.0f, 0.0f, iPoint3, iPoint3, OPEN_ANIM_DURATION, CIRCLE3_ANIMATE_DURATION / 4 );
            }
        }, CLOSE_ANIM_DURATION - 200 );

        final ImageView circle2 = (ImageView)findViewById(R.id.circle_2);
        final int iPoint2 = circle2.getWidth()/2;
        float fEndRotate2 = ( 360.0f / ( CIRCLE2_ANIMATE_DURATION / 1000.0f ) ) * (CLOSE_ANIM_DURATION / 1000.0f);
        setCloseRotateAnimation(circle2, 0.0f, 360.0f, iPoint2, iPoint2, CLOSE_ANIM_DURATION);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setOpenRotateAnimation(circle2, 0.0f, 360.0f, iPoint2, iPoint2, OPEN_ANIM_DURATION, CIRCLE2_ANIMATE_DURATION / 4 );
            }
        }, CLOSE_ANIM_DURATION - 200 );
    }

    private void changeStateReady()
    {
        if( miState >= STATE_READY )
        {
            return;
        }

        miState = STATE_READY;
        miNowCount = 11;
        mbRestFiveCountDownStart = false;

        playSound(SOUND_WARNING);

        setDisplayColorFilter(getResources().getColor(R.color.yellow));
        setOverlayColorFilter( STATE_READY );

        TextView stateText = (TextView)findViewById(R.id.state_title);
        stateText.setText("STANDBY");

        TextView subTitle = (TextView)findViewById(R.id.sub_title);
        subTitle.setText("準備中");

        TextView subText = (TextView)findViewById(R.id.state_sub_text);
        subText.setText("各自争奪戦準備後待機推奨");

        RelativeLayout readyLayout = (RelativeLayout)findViewById(R.id.overlay_layout);
        readyLayout.setVisibility(View.VISIBLE);

        RelativeLayout subTitleWaitLayout = (RelativeLayout)findViewById(R.id.wait_layout);
        subTitleWaitLayout.setVisibility(View.INVISIBLE);

        RelativeLayout stateSubTitleLayout = (RelativeLayout)findViewById(R.id.state_sub_title);
        stateSubTitleLayout.setVisibility(View.VISIBLE);

        ImageView backSelectImage = (ImageView)findViewById(R.id.reselect);
//        backSelectImage.setEnabled(false);
        backSelectImage.setVisibility(View.INVISIBLE);


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if( miState != STATE_READY )
                {
                    return;
                }

                countDown();
            }
        });
    }

    private void countDown()
    {
        if( mbRestFiveCountDownStart )
        {
            return;
        }

        miNowCount--;

        if( miNowCount <= 0 )
        {
//            changeStateStart();
            return;
        }

        if( miState != STATE_READY )
        {
            return;
        }

        ImageView overlayMainImage = (ImageView)findViewById(R.id.overlay_main_pic);
        overlayMainImage.setImageResource( getCountResource( miNowCount ) );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                countDown();
            }
        }, 950);
    }

    private Boolean mbRestFiveCountDownStart = false;
    private void restFiveCountDown()
    {
        if( !mbRestFiveCountDownStart )
        {
            miNowCount = 5;
            mbRestFiveCountDownStart = true;
        }
        else
        {
            miNowCount--;
        }

        if( miNowCount <= 0 )
        {
//            changeStateStart();
            return;
        }

        if( miState != STATE_READY )
        {
            return;
        }

        ImageView overlayMainImage = (ImageView)findViewById(R.id.overlay_main_pic);
        overlayMainImage.setImageResource( getCountResource( miNowCount ) );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                restFiveCountDown();
            }
        }, 950);
    }


    private int getCountResource( int iCount )
    {
        switch( iCount )
        {
            case 1:
                return R.drawable.count_01;
            case 2:
                return R.drawable.count_02;
            case 3:
                return R.drawable.count_03;
            case 4:
                return R.drawable.count_04;
            case 5:
                return R.drawable.count_05;
            case 6:
                return R.drawable.count_06;
            case 7:
                return R.drawable.count_07;
            case 8:
                return R.drawable.count_08;
            case 9:
                return R.drawable.count_09;
            case 10:
            default:
                return R.drawable.count_10;
        }
    }

    private void changeStateStart()
    {
        if( miState == STATE_START )
        {
            return;
        }

        miState = STATE_START;

        stopSound();
        playSound(SOUND_START);

        setDisplayColorFilter(getResources().getColor(R.color.pink));
        setOverlayColorFilter( STATE_START );

        TextView stateText = (TextView)findViewById(R.id.state_title);
        stateText.setText("START");

        TextView subTitle = (TextView)findViewById(R.id.sub_title);
        subTitle.setText("争奪戦開始");

        TextView subText = (TextView)findViewById(R.id.state_sub_text);
        subText.setText("神経集中/最速早押");

        RelativeLayout readyLayout = (RelativeLayout)findViewById(R.id.overlay_layout);
        readyLayout.setVisibility(View.VISIBLE);

        RelativeLayout subTitleWaitLayout = (RelativeLayout)findViewById(R.id.wait_layout);
        subTitleWaitLayout.setVisibility(View.INVISIBLE);

        RelativeLayout stateSubTitleLayout = (RelativeLayout)findViewById(R.id.state_sub_title);
        stateSubTitleLayout.setVisibility(View.VISIBLE);
/*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent( getApplicationContext(), ResultActivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity( intent );
                overridePendingTransition(0, 0);
            }
        }, 5000);
*/
    }

    private void setDisplayColorFilter( int iColor )
    {
        ImageView rightArrow1 = (ImageView)findViewById(R.id.right_arrow_1);
        rightArrow1.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);

        ImageView rightArrow2 = (ImageView)findViewById(R.id.right_arrow_2);
        rightArrow2.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);

        ImageView rightArrow3 = (ImageView)findViewById(R.id.right_arrow_3);
        rightArrow3.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);

        ImageView leftArrow1 = (ImageView)findViewById(R.id.left_arrow_1);
        leftArrow1.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);

        ImageView leftArrow2 = (ImageView)findViewById(R.id.left_arrow_2);
        leftArrow2.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);

        ImageView leftArrow3 = (ImageView)findViewById(R.id.left_arrow_3);
        leftArrow3.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);

        View leftLine = (View)findViewById(R.id.line_view_left);
        leftLine.setBackgroundColor(iColor);

        View rightLine = (View)findViewById(R.id.line_view_right);
        rightLine.setBackgroundColor(iColor);

        TextView stateText = (TextView)findViewById(R.id.state_title);
        stateText.setTextColor(iColor);

        TextView targetTitle = (TextView)findViewById(R.id.target_title);
        targetTitle.setTextColor(iColor);

        TextView inforTitle = (TextView)findViewById(R.id.infor_title);
        inforTitle.setTextColor(iColor);

        setCircleColorFilter(iColor);

        ImageView reselect = (ImageView)findViewById(R.id.reselect);
        reselect.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);
    }

    private void setCircleColorFilter( int iColor )
    {
        ImageView circle1 = (ImageView)findViewById(R.id.circle_1);
        circle1.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);

        ImageView circle2 = (ImageView)findViewById(R.id.circle_2);
        circle2.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);

        ImageView circle3 = (ImageView)findViewById(R.id.circle_3);
        circle3.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);

        ImageView circle4 = (ImageView)findViewById(R.id.circle_4);
        circle4.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);
    }

    private void setOverlayColorFilter( int state ) {
        String strMainText = "";
        int iTextColor = getResources().getColor(R.color.white);
        int iAlphaColor = getResources().getColor(R.color.themeColor);
        int iLightColor = iAlphaColor;
        int iSubTitleColor = getResources().getColor(R.color.gray);
        ImageView mainImage = (ImageView) findViewById(R.id.overlay_main_pic);
        ImageView pushImage = (ImageView) findViewById(R.id.push_img);
        RelativeLayout overlayInfor = (RelativeLayout)findViewById(R.id.overlay_infor);
        switch (state) {
            case STATE_READY:
                iAlphaColor = getResources().getColor(R.color.alphaYellow);
                iLightColor = getResources().getColor(R.color.lightYellow);
                strMainText = "上司争奪戦開始迄";
                mainImage.setImageResource(R.drawable.count_10);
                pushImage.setVisibility(View.INVISIBLE);
                iTextColor = getResources().getColor(R.color.black);
                iSubTitleColor = getResources().getColor(R.color.yellow);
                overlayInfor.setVisibility(View.VISIBLE);
                break;
            case STATE_START:
                iAlphaColor = getResources().getColor(R.color.alphaPink);
                iLightColor = getResources().getColor(R.color.lightPink);
                strMainText = "上司争奪戦開始";
                mainImage.setVisibility(View.INVISIBLE);
                pushImage.setVisibility(View.VISIBLE);
                iSubTitleColor = getResources().getColor(R.color.pink);
                overlayInfor.setVisibility(View.VISIBLE);
                break;
            case STATE_WAIT:
                strMainText = "上司着席待";
                overlayInfor.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }

        View overlayView = (View) findViewById(R.id.overlay);
        overlayView.setBackgroundColor(iAlphaColor);

        overlayInfor.setBackgroundColor(iLightColor);

        TextView mainTextView = (TextView) findViewById(R.id.overlay_main_text);
        mainTextView.setTextColor(iTextColor);
        mainTextView.setText(strMainText);

        TextView subTitleText = (TextView)findViewById(R.id.state_sub_text);
        subTitleText.setTextColor(iSubTitleColor);
    }

    private static final int GO_TO_RESULT_DELAY = 2000;
    private void makeConnection()
    {
        if( !GNUtility.isConnection( getApplicationContext() ) )
        {
            drawError(getResources().getString(R.string.alart_title), getResources().getString(R.string.network_error));
            return;
        }

        milkCocoa = new MilkCocoa("guitariu6e7lgx.mlkcca.com");
        dataStore = milkCocoa.dataStore("messages");
        dataStore.addDataStoreEventListener(new DataStoreEventListener() {
            @Override
            public void onPushed(DataElement dataElement) {
                Log.d("Milkcocoa", "onPushed");
                if (dataElement == null) {
                    Log.e("Milkcocoa", "data is null");
                    return;
                }

                final DataElement data = dataElement;
                final String type = data.getValue("type");
                final String id = data.getValue("id");

                if( !id.equals(mTargetData.getListNo()) )
                {
                    return;
                }

                if ("notice".equals(type)) {
                    // 上司が開始ボタンを押した（カウントダウン開始時刻通知）
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Utility.customLog("push", "notice " + id, Log.DEBUG);

                            // 時間更新
                            String strDate = data.getValue("datetime");
                            mTargetData.setStartTime(strDate);
                            calcTimer();

                            readyCountDown();
                        }
                    });
                }
                else if("pre_start".equals(type)) {
                    // カウントダウン開始5秒前
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Utility.customLog("push", "pre_start " + id, Log.DEBUG);
                        }
                    });
                }
                else if ("start".equals(type)) {
                    // サーバ側で受付が開始された（カウントダウン開始通知：黄）
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Utility.customLog("push", "start " + id, Log.DEBUG);
                            changeStateReady();
                        }
                    });
                }
                else if("pre_finish".equals(type)) {
                    // 開始5秒前
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Utility.customLog("push", "pre_finish " + id, Log.DEBUG);
//                            restFiveCountDown();
                        }
                    });
                }
                else if ("finish".equals(type)) {
                    // カウントダウンが終了した（早押しスタート：赤）
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Utility.customLog("push", "finish " + id, Log.DEBUG);

                            changeStateStart();
                        }
                    });
                }
                else if ("result".equals(type)) {
                    // finish が終了した（finish 5 秒後）
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String result = data.getValue("result");
                            Utility.customLog("push", "result " + id, Log.DEBUG);

                            Long  lStartBaseTime = 0L;
                            try {
                                String strTime = data.getValue("datetime");
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Calendar startTime = Calendar.getInstance();
                                Date startDate = null;
                                startDate = new Date( format.parse(strTime).getTime() );
                                startTime.setTime(startDate);
                                lStartBaseTime = startTime.getTimeInMillis() + 15000;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            Intent intent = new Intent( getApplicationContext(), ResultActivity.class );
                            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                            intent.putExtra("target", mTargetData);
                            intent.putExtra("result", result.substring(1, result.length()-1) );
                            intent.putExtra("start_time", lStartBaseTime);
                            startActivity( intent );
                            overridePendingTransition(0, 0);


                            miDrawTime = 0;
                            dataStore.removeDataStoreEventListener();
                        }
                    }, GO_TO_RESULT_DELAY);
                }

            }

            @Override
            public void onSetted(DataElement dataElement) {
                Log.d("Milkcocoa", "onSetted");
            }

            @Override
            public void onSended(DataElement dataElement) {
                Log.d("Milkcocoa", "onSended");
                if (dataElement == null) {
                    Log.e("Milkcocoa", "data is null");
                    return;
                }
            }

            @Override
            public void onRemoved(DataElement dataElement) {
                Log.d("Milkcocoa", "onremoved");
            }
        });
        dataStore.on("push");
        dataStore.on("send");
        Log.d("Milkcocoa", "setuped");
    }

    private void sendRegist()
    {
        if( !GNUtility.isConnection( getApplicationContext() ) )
        {
            drawError(getResources().getString(R.string.alart_title), getResources().getString(R.string.network_error));
            return;
        }


        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg == null || msg.obj == null) {
                    return;
                }

                GNCustomUrlReturnObject obj = (GNCustomUrlReturnObject) msg.obj;
                Object ret = obj.getMessageObject();

                if( ret == null )
                {
                    return;
                }

                String strData = new String( (byte[])ret, Charset.forName( "UTF-8" ) );

                try {
                    JSONObject json = new JSONObject(strData);
                    JSONArray arrayData = json.getJSONArray("data");
                    int iCount = arrayData.length();

                    TextView inforTitle = (TextView)findViewById(R.id.infor_text);
                    inforTitle.setText("他部下" + String.valueOf(iCount) + "名待機中...");
                    inforTitle.setVisibility(View.VISIBLE);

                    Utility.customLog("WAIT", "regist:?" +  "boss id:" + mTargetData.getListNo(), Log.DEBUG);
                } catch (JSONException e) {
                    e.printStackTrace();
                    drawError(getResources().getString(R.string.alart_title), getResources().getString(R.string.data_error));
                }
            }
        };

        String strUrl = Define.BASE_URL +  "api/boss/" + mTargetData.getListNo() + "/regist/";
        String strParam = "code=" + Utility.getSavedStringData(getApplicationContext(), "code");
        GNCustomUrlConnection connection = new GNCustomUrlConnection(handler, strUrl, GNDefine.CONNECTION_POST, null, strParam, null, getApplicationContext());
        connection.start();
    }

    private void sendUnRegist()
    {
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg == null || msg.obj == null) {
                    return;
                }

                GNCustomUrlReturnObject obj = (GNCustomUrlReturnObject) msg.obj;
                Object ret = obj.getMessageObject();

                if( ret == null )
                {
                    return;
                }

                String strDummy = new String( (byte[])ret, Charset.forName( "UTF-8" ) );

                try {
                    JSONObject json = new JSONObject(strDummy);

                    Utility.customLog("WAIT", "unregist", Log.DEBUG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        String strUrl = Define.BASE_URL +  "api/boss/" + mTargetData.getListNo() + "/unregist/";
        String strParam = "code=" + Utility.getSavedStringData(getApplicationContext(), "code");
        GNCustomUrlConnection connection = new GNCustomUrlConnection(handler, strUrl, GNDefine.CONNECTION_POST, null, strParam, null, getApplicationContext());
        connection.start();
    }

    @Override
    protected void callbackBossInformation(Message msg)
    {
        if (msg == null || msg.obj == null) {

            return;
        }

        GNCustomUrlReturnObject obj = (GNCustomUrlReturnObject) msg.obj;
        Object ret = obj.getMessageObject();

        if( ret == null )
        {
            return;
        }

        String strData = new String( (byte[])ret, Charset.forName( "UTF-8" ) );

        try {
            JSONObject json = new JSONObject(strData);

            JSONObject object = json.getJSONObject("data");
            JSONArray waitArray = object.getJSONArray("register");
            int iCount = waitArray.length();

            TextView inforTitle = (TextView)findViewById(R.id.infor_text);
            inforTitle.setText("他部下" + String.valueOf(iCount) + "名待機中...");
            inforTitle.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
