package jp.co.gnavi.meshclient.activity;

import android.animation.AnimatorSet;
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

import jp.co.gnavi.lib.utility.GNUtility;
import jp.co.gnavi.meshclient.R;

/**
 * Created by kaifuku on 2016/10/12.
 */
public class WaitActivity extends BaseActivity {
    private int miSoundId;

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

    // 一番外側の円アニメーション時間（ミリ秒）
    private static final int    CIRCLE4_ANIMATE_DURATION = 90000;
    // 内側から 3 番目の円アニメーション時間（ミリ秒）
    private static final int    CIRCLE3_ANIMATE_DURATION = 40000;
    // 内側から 2 番目の円アニメーション時間（ミリ秒）
    private static final int    CIRCLE2_ANIMATE_DURATION = 25000;
    // 一番内側の円アニメーション時間（ミリ秒）
    private static final int    CIRCLE1_ANIMATE_DURATION = 4000;

    private static Boolean      mbStateArrawAnimation = false;

    /**
     * 初期化
     */
    private void initialize() {
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
            }
        });
    }

    private void startArrowAnimation()
    {
        ImageView leftArrow = (ImageView)findViewById(R.id.left_arrow_3);
        setRoopFlowAlphaAnimation(leftArrow, ALPHA_DOWN_ANIM);

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

        ImageView rightArrow = (ImageView)findViewById(R.id.right_arrow_3);
        setRoopFlowAlphaAnimation(rightArrow, ALPHA_DOWN_ANIM);

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

        View lineView = (View)findViewById(R.id.line_view);
        setRoopLineAlphaAnimation(lineView, ALPHA_DOWN_ANIM);
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

    private static final int ALPHA_DOWN_ANIM = 0;
    private static final int ALPHA_UP_ANIM = ALPHA_DOWN_ANIM + 1;

    private static final int FLOW_ALPHA_ANIM_DOWN_DURATION = 1000;
    private static final int FLOW_ALPHA_ANIM_UP_DURATION = 700;

    private static final int ARROW_DELAY = 400;

    private void setRoopFlowAlphaAnimation( final View view, final int iAnimType )
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

        view.setAnimation( alpha );
    }

    private static final int LINE_ALPHA_ANIM_DURATION = 500;

    private void setRoopLineAlphaAnimation( final View view, final int iAnimType )
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

        view.setAnimation( alpha );
    }
}
