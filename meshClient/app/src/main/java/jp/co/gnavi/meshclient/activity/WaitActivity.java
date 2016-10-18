package jp.co.gnavi.meshclient.activity;

import android.animation.AnimatorSet;
import android.graphics.PorterDuff;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Image;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
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

import jp.co.gnavi.lib.utility.GNUtility;
import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.common.Utility;

/**
 * Created by kaifuku on 2016/10/12.
 */
public class WaitActivity extends BaseActivity {
    private int miSoundId;

    // 一番外側の円アニメーション時間（ミリ秒）
    private static final int    CIRCLE4_ANIMATE_DURATION = 90000;
    // 内側から 3 番目の円アニメーション時間（ミリ秒）
    private static final int    CIRCLE3_ANIMATE_DURATION = 40000;
    // 内側から 2 番目の円アニメーション時間（ミリ秒）
    private static final int    CIRCLE2_ANIMATE_DURATION = 25000;
    // 一番内側の円アニメーション時間（ミリ秒）
    private static final int    CIRCLE1_ANIMATE_DURATION = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.wait );

        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if( !mbStateArrawAnimation )
        {
            mbStateArrawAnimation = true;
            startArrowAnimation();
        }

        miSoundId = initializeSound();
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

        mbStateArrawAnimation = false;
        releaseSound();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初期化
     */
    private void initialize() {

        // TODO:仮
        changeStateWait();

        final ImageView circle4 = (ImageView)findViewById(R.id.circle_4);
        ViewTreeObserver observer4 = circle4.getViewTreeObserver();
        observer4.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int point = circle4.getWidth()/2;
                setRoopRotateAnimation(circle4, 0, 360, point, point, CIRCLE4_ANIMATE_DURATION);
            }
        });

        final ImageView circle3 = (ImageView)findViewById(R.id.circle_3);
        ViewTreeObserver observer3 = circle3.getViewTreeObserver();
        observer3.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int point = circle3.getWidth()/2;
                setRoopRotateAnimation(circle3, 360, 0, point, point, CIRCLE3_ANIMATE_DURATION);
            }
        });

        final ImageView circle2 = (ImageView)findViewById(R.id.circle_2);
        ViewTreeObserver observer2 = circle2.getViewTreeObserver();
        observer2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int point = circle2.getWidth()/2;
                setRoopRotateAnimation(circle2, 0, 360, point, point, CIRCLE2_ANIMATE_DURATION);
            }
        });

        final ImageView circle1 = (ImageView)findViewById(R.id.circle_1);
        ViewTreeObserver observer1 = circle1.getViewTreeObserver();
        observer1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int point = circle1.getWidth()/2;
                RelativeLayout layout = (RelativeLayout)findViewById(R.id.user_layout);
                int pointy = layout.getHeight()/2;
                setRoopScaleAlphaAnimation( circle1, point, pointy, CIRCLE1_ANIMATE_DURATION );
            }
        });

        ImageView iconImage = (ImageView)findViewById(R.id.user_icon);
        iconImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound( miSoundId );
                changeStateReady();
            }
        });
    }

    private void setRoopRotateAnimation(View view, int iStart, int iEnd, int iCenterX, int iCenterY, int iDuration )
    {
        RotateAnimation rotate = new RotateAnimation( iStart, iEnd, iCenterX, iCenterY );
        rotate.setDuration( iDuration );
        rotate.setInterpolator( new LinearInterpolator() );
        rotate.setRepeatCount( -1 );
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

        view.setAnimation( animSet );
    }

    private void changeStateWait()
    {
        setDisplayColorFilter(getResources().getColor(R.color.themeColor));

        TextView stateText = (TextView)findViewById(R.id.state_title);
        stateText.setText("WAIT");

        TextView subTitle = (TextView)findViewById(R.id.sub_title);
        subTitle.setText("上司着席待");

        RelativeLayout readyLayout = (RelativeLayout)findViewById(R.id.ready_overlay);
        readyLayout.setVisibility(View.INVISIBLE);

        RelativeLayout subTitleWaitLayout = (RelativeLayout)findViewById(R.id.wait_layout);
        subTitleWaitLayout.setVisibility(View.VISIBLE);
    }

    private void changeStateReady()
    {
        setDisplayColorFilter(getResources().getColor(R.color.yellow));

        TextView stateText = (TextView)findViewById(R.id.state_title);
        stateText.setText("STANDBY");

        TextView subTitle = (TextView)findViewById(R.id.sub_title);
        subTitle.setText("準備中");

        RelativeLayout readyLayout = (RelativeLayout)findViewById(R.id.ready_overlay);
        readyLayout.setVisibility(View.VISIBLE);

        RelativeLayout subTitleWaitLayout = (RelativeLayout)findViewById(R.id.wait_layout);
        subTitleWaitLayout.setVisibility(View.INVISIBLE);


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

        ImageView circle1 = (ImageView)findViewById(R.id.circle_1);
        circle1.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);

        ImageView circle2 = (ImageView)findViewById(R.id.circle_2);
        circle2.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);

        ImageView circle3 = (ImageView)findViewById(R.id.circle_3);
        circle3.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);

        ImageView circle4 = (ImageView)findViewById(R.id.circle_4);
        circle4.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);
    }



}
