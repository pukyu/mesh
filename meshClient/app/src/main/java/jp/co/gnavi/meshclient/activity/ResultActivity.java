package jp.co.gnavi.meshclient.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.co.gnavi.lib.connection.GNCustomUrlReturnObject;
import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.adpter.SelectListAdapter;
import jp.co.gnavi.meshclient.common.Utility;
import jp.co.gnavi.meshclient.data.SelectListData;

/**
 * Created by kaifuku on 2016/10/18.
 */

public class ResultActivity extends BaseActivity
{
    public static final int RESULT_WIN = 0;
    public static final int RESULT_LOSE = RESULT_WIN + 1;
    public static final int RESULT_DRAW = RESULT_LOSE + 1;

    private int     miResultType;

    private static final int STAR_ANIM_NON = 0;
    private static final int STAR_ANIM_STOP = STAR_ANIM_NON  + 1;
    private static final int STAR_ANIM_RESTART = STAR_ANIM_STOP + 1;

    private int     miStarAnimationState = STAR_ANIM_NON;

    private SelectListData mTargetData;

    private Long    mBaseTime;

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

        setContentView(R.layout.result_layout);

        Intent intent = getIntent();
        mTargetData = (SelectListData) intent.getSerializableExtra("target");
        String strResult = intent.getStringExtra("result");
        mBaseTime = intent.getLongExtra("start_time", 0L);

        initalize();
//        calcResult( strResult );
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

        if ( miResultType == RESULT_WIN )
        {
            if( miStarAnimationState == STAR_ANIM_NON )
            {
                resetStar();
            }
            else if( miStarAnimationState == STAR_ANIM_STOP )
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetStar();
                    }
                }, STAR_GLITTER_DURATION);
            }
        }

//        initializeSound();
    }

    @Override
    protected void onPause() {
        super.onPause();

        clearStartAnimation();
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
    }

    private void initalize()
    {
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.alpha_down_layout);
        layout.setVisibility(View.VISIBLE);

        setCommon();

        getBossInformation( mTargetData.getListNo() );
    }

    private static final int START_ANIM_DELAY = 3000;
    private static final int ALPHA_ANIM_DURATION = 500;

    private void startAlphaDownAnimation()
    {
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.alpha_down_layout);
        AlphaAnimation alpha = new AlphaAnimation( 1.0f, 0.0f );
        alpha.setDuration( ALPHA_ANIM_DURATION );
        alpha.setInterpolator( new AccelerateInterpolator() );
        alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout layout = (RelativeLayout)findViewById(R.id.alpha_down_layout);
                layout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        layout.clearAnimation();
        layout.setVisibility(View.VISIBLE);
        layout.setAnimation( alpha );
    }


    private void setCommon()
    {
        TextView stateText = (TextView)findViewById(R.id.state_title);
        stateText.setText("RESULT");

        TextView subTitle = (TextView)findViewById(R.id.sub_title);
        subTitle.setText("争奪戦結果");

        setDisplayColorFilter(getResources().getColor(R.color.winYellow));

        ImageView backSelectImage = (ImageView)findViewById(R.id.reselect);
        backSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( ResultActivity.this, SelectAcitivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity( intent );
                overridePendingTransition(0, 0);
            }
        });

        Button continueButton = (Button)findViewById(R.id.btn_continue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( ResultActivity.this, SelectAcitivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity( intent );
                overridePendingTransition(0, 0);
            }
        });
    }

    private static final int RESULT_ANIMATION_DELAY = START_ANIM_DELAY + ALPHA_ANIM_DURATION - 100;
    private static final int RESULT_ANIMATION_DURATION = 250;
    private void setWin()
    {
        View    backView = (View)findViewById(R.id.back_color);
        backView.setBackgroundColor(getResources().getColor(R.color.winColor));

        RelativeLayout starLayout = (RelativeLayout)findViewById(R.id.star_layout);
        starLayout.setVisibility(View.VISIBLE);

        RelativeLayout resultImageLayout =(RelativeLayout)findViewById(R.id.result_image_layout);
        resultImageLayout.setVisibility(View.INVISIBLE);

        ImageView winImage = (ImageView)findViewById(R.id.main_image);
        winImage.setImageResource(R.drawable.win);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.result_image_layout);
                ScaleAnimation scale = new ScaleAnimation(2.0f, 1.0f, 2.0f, 1.0f, layout.getWidth()/2, layout.getHeight()/2);
                scale.setDuration( RESULT_ANIMATION_DURATION );
                scale.setInterpolator( new AccelerateInterpolator() );
                layout.clearAnimation();
                layout.setVisibility(View.VISIBLE);
                layout.setAnimation(scale);

                playSound(SOUND_WIN);
            }
        },  RESULT_ANIMATION_DELAY );

        Button continueButton = (Button)findViewById(R.id.btn_continue);
        continueButton.setVisibility(View.GONE);

        RelativeLayout resultLayout = (RelativeLayout)findViewById(R.id.result_data);
        resultLayout.setVisibility(View.VISIBLE);
    }

    private void setLose()
    {
        View    backView = (View)findViewById(R.id.back_color);
        backView.setBackgroundColor(getResources().getColor(R.color.loseColor));

        RelativeLayout starLayout = (RelativeLayout)findViewById(R.id.star_layout);
        starLayout.setVisibility(View.GONE);

        RelativeLayout resultImageLayout =(RelativeLayout)findViewById(R.id.result_image_layout);
        resultImageLayout.setVisibility(View.INVISIBLE);

        ImageView loseImage = (ImageView)findViewById(R.id.main_image);
        loseImage.setImageResource(R.drawable.lose);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.result_image_layout);
                AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
                alpha.setDuration( RESULT_ANIMATION_DURATION );
                alpha.setInterpolator( new AccelerateInterpolator() );
                layout.clearAnimation();
                layout.setVisibility(View.VISIBLE);
                layout.setAnimation(alpha);

                playSound(SOUND_LOSE);
            }
        },  RESULT_ANIMATION_DELAY );

        Button continueButton = (Button)findViewById(R.id.btn_continue);
        continueButton.setVisibility(View.VISIBLE);

        RelativeLayout resultLayout = (RelativeLayout)findViewById(R.id.result_data);
        resultLayout.setVisibility(View.VISIBLE);

        TextView resultDiscription = (TextView)findViewById(R.id.reuslt_description);
    }

    private void setDraw()
    {
        View    backView = (View)findViewById(R.id.back_color);
        backView.setBackgroundColor(getResources().getColor(R.color.drawColor));

        RelativeLayout starLayout = (RelativeLayout)findViewById(R.id.star_layout);
        starLayout.setVisibility(View.GONE);

        RelativeLayout resultImageLayout =(RelativeLayout)findViewById(R.id.result_image_layout);
        resultImageLayout.setVisibility(View.INVISIBLE);

        ImageView drawImage = (ImageView)findViewById(R.id.main_image);
        drawImage.setImageResource(R.drawable.draw);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.result_image_layout);
                AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
                alpha.setDuration( RESULT_ANIMATION_DURATION );
                alpha.setInterpolator( new AccelerateInterpolator() );
                layout.clearAnimation();
                layout.setVisibility(View.VISIBLE);
                layout.setAnimation(alpha);

                playSound(SOUND_LOSE);
            }
        },  RESULT_ANIMATION_DELAY );

        Button continueButton = (Button)findViewById(R.id.btn_continue);
        continueButton.setVisibility(View.VISIBLE);

        RelativeLayout resultLayout = (RelativeLayout)findViewById(R.id.result_data);
        resultLayout.setVisibility(View.INVISIBLE);

        TextView resultDiscription = (TextView)findViewById(R.id.reuslt_description);
        resultDiscription.setText("諸事情により実行されませんでした");
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

        TextView targetTitle = (TextView)findViewById(R.id.result_infor_title);
        targetTitle.setTextColor(iColor);

        ImageView reselect = (ImageView)findViewById(R.id.reselect);
        reselect.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);
    }

    private void clearStartAnimation()
    {
        miStarAnimationState = STAR_ANIM_STOP;

        // 時計回り順
        final ImageView imgStarLTop = (ImageView)findViewById(R.id.star_l_top);
        imgStarLTop.clearAnimation();
        imgStarLTop.setVisibility(View.INVISIBLE);

        final ImageView imgStarSTop = (ImageView)findViewById(R.id.star_s_top);
        imgStarSTop.clearAnimation();
        imgStarSTop.setVisibility(View.INVISIBLE);

        final ImageView imgStarMRight = (ImageView)findViewById(R.id.star_m_right);
        imgStarMRight.clearAnimation();
        imgStarMRight.setVisibility(View.INVISIBLE);

        final ImageView imgStarSRight = (ImageView)findViewById(R.id.star_s_right);
        imgStarSRight.clearAnimation();
        imgStarSRight.setVisibility(View.INVISIBLE);

        final ImageView imgStarLRight = (ImageView)findViewById(R.id.star_l_right);
        imgStarLRight.clearAnimation();
        imgStarLRight.setVisibility(View.INVISIBLE);

        final ImageView imgStarSBottomRight = (ImageView)findViewById(R.id.star_s_bottom_right);
        imgStarSBottomRight.clearAnimation();
        imgStarSBottomRight.setVisibility(View.INVISIBLE);

        final ImageView imgStarSBottom = (ImageView)findViewById(R.id.star_s_bottom);
        imgStarSBottom.clearAnimation();
        imgStarSBottom.setVisibility(View.INVISIBLE);

        final ImageView imgStarMBottom = (ImageView)findViewById(R.id.star_m_bottom);
        imgStarMBottom.clearAnimation();
        imgStarMBottom.setVisibility(View.INVISIBLE);

        final ImageView imgStarSBottomLeft = (ImageView)findViewById(R.id.star_s_bottom_left);
        imgStarSBottomLeft.clearAnimation();
        imgStarSBottomLeft.setVisibility(View.INVISIBLE);

        final ImageView imgStarSLeft = (ImageView)findViewById(R.id.star_s_left);
        imgStarSLeft.clearAnimation();
        imgStarSLeft.setVisibility(View.INVISIBLE);

        final ImageView imgStarLLeft = (ImageView)findViewById(R.id.star_l_left);
        imgStarLLeft.clearAnimation();
        imgStarLLeft.setVisibility(View.INVISIBLE);

        final ImageView imgStarSTopLeft = (ImageView)findViewById(R.id.star_s_top_left);
        imgStarSTopLeft.clearAnimation();
        imgStarSTopLeft.setVisibility(View.INVISIBLE);
    }

    private final static int STAR_DEFAULT_ANIM_DELAY = 1000;
    private void resetStar()
    {
        miStarAnimationState = STAR_ANIM_RESTART;

        int iCount = RESULT_ANIMATION_DELAY + RESULT_ANIMATION_DURATION + 1000;
        int iLargeStartCount = RESULT_ANIMATION_DELAY;

        // 時計回り順
        final ImageView imgStarLTop = (ImageView)findViewById(R.id.star_l_top);
        imgStarLTop.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setDefaultStartAnimation(imgStarLTop, STAR_GLITTER_TYPE_ON );
//                setStarAppearanceAnimation(imgStarLTop);
            }
        }, iLargeStartCount);
        iLargeStartCount += STAR_DEFAULT_ANIM_DELAY;
//        iCount += STAR_DURAION;

        final ImageView imgStarSTop = (ImageView)findViewById(R.id.star_s_top);
        imgStarSTop.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setStarAppearanceAnimation(imgStarSTop);
            }
        }, iCount);
        iCount += STAR_DURAION;

        final ImageView imgStarMRight = (ImageView)findViewById(R.id.star_m_right);
        imgStarMRight.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setStarAppearanceAnimation(imgStarMRight);
            }
        }, iCount);
        iCount += STAR_DURAION;

        final ImageView imgStarSRight = (ImageView)findViewById(R.id.star_s_right);
        imgStarSRight.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setStarAppearanceAnimation(imgStarSRight);
            }
        }, iCount);
        iCount += STAR_DURAION;

        final ImageView imgStarLRight = (ImageView)findViewById(R.id.star_l_right);
        imgStarLRight.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setDefaultStartAnimation(imgStarLRight, STAR_GLITTER_TYPE_ON );
//                setStarAppearanceAnimation(imgStarLRight);
            }
        }, iLargeStartCount);
        iLargeStartCount += STAR_DEFAULT_ANIM_DELAY;
//        iCount += STAR_DURAION;

        final ImageView imgStarSBottomRight = (ImageView)findViewById(R.id.star_s_bottom_right);
        imgStarSBottomRight.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setStarAppearanceAnimation(imgStarSBottomRight);
            }
        }, iCount);
        iCount += STAR_DURAION;

        final ImageView imgStarSBottom = (ImageView)findViewById(R.id.star_s_bottom);
        imgStarSBottom.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setStarAppearanceAnimation(imgStarSBottom);
            }
        }, iCount);
        iCount += STAR_DURAION;

        final ImageView imgStarMBottom = (ImageView)findViewById(R.id.star_m_bottom);
        imgStarMBottom.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setStarAppearanceAnimation(imgStarMBottom);
            }
        }, iCount);
        iCount += STAR_DURAION;

        final ImageView imgStarSBottomLeft = (ImageView)findViewById(R.id.star_s_bottom_left);
        imgStarSBottomLeft.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setStarAppearanceAnimation(imgStarSBottomLeft);
            }
        }, iCount);
        iCount += STAR_DURAION;

        final ImageView imgStarSLeft = (ImageView)findViewById(R.id.star_s_left);
        imgStarSLeft.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setDefaultStartAnimation(imgStarSLeft, STAR_GLITTER_TYPE_ON );
//                setStarAppearanceAnimation(imgStarSLeft);
            }
        }, iLargeStartCount);
//        iCount += STAR_DURAION;

        final ImageView imgStarLLeft = (ImageView)findViewById(R.id.star_l_left);
        imgStarLLeft.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                setDefaultStartAnimation(imgStarLLeft, STAR_GLITTER_TYPE_ON );
                setStarAppearanceAnimation(imgStarLLeft);
            }
        }, iCount);
        iCount += STAR_DURAION;

        final ImageView imgStarSTopLeft = (ImageView)findViewById(R.id.star_s_top_left);
        imgStarSTopLeft.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setStarAppearanceAnimation(imgStarSTopLeft);
            }
        }, iCount);
    }

    private static final int STAR_APPEAR_ANIM_DURATION = 500;
    private void setStarAppearanceAnimation( final ImageView view )
    {
        final int iCenterX = view.getWidth()/2;
        final int iCenterY = view.getHeight()/2;

        AlphaAnimation alpha = new AlphaAnimation( 0.0f, 1.0f );
        ScaleAnimation scale = new ScaleAnimation( 0.0f, 1.0f, 0.0f, 1.0f, iCenterX, iCenterY );

        AnimationSet animSet = new AnimationSet(true);
        animSet.addAnimation(alpha);
        animSet.addAnimation(scale);
        animSet.setDuration( STAR_APPEAR_ANIM_DURATION );
        animSet.setInterpolator( new LinearInterpolator() );
        animSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setStarGlitterAnimation( view, STAR_GLITTER_TYPE_OFF, iCenterX, iCenterY );
                    }
                }, STAR_GLITTER_DURATION);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.clearAnimation();
        view.setVisibility(View.VISIBLE);
        view.setAnimation( animSet );
    }

    // 星きらめきαアニメ 1 → 0
    private static final int STAR_GLITTER_TYPE_OFF = 0;
    // 星きらめきαアニメ 0 → 1
    private static final int STAR_GLITTER_TYPE_ON = STAR_GLITTER_TYPE_OFF + 1;
    // 星きらめき間の時間
    private static final int STAR_GLITTER_DURATION = 5000;
    // 星間のきらめき開始ディレイ
    private static final int STAR_DURAION = 50;

    private void setStarGlitterAnimation( final View view, final int iType, final int iCenterX, final int iCenterY )
    {
        AlphaAnimation alpha;
        ScaleAnimation scale;
        if( iType == STAR_GLITTER_TYPE_OFF )
        {
            alpha = new AlphaAnimation( 1.0f, 0.0f );
            scale = new ScaleAnimation( 1.0f, 0.0f, 1.0f, 0.0f, iCenterX, iCenterY );
        }
        else
        {
            alpha = new AlphaAnimation( 0.0f, 1.0f );
            scale = new ScaleAnimation( 0.0f, 1.0f, 0.0f, 1.0f, iCenterX, iCenterY );
        }

        AnimationSet animSet = new AnimationSet(true);
        animSet.addAnimation(alpha);
        animSet.addAnimation(scale);
        animSet.setDuration( STAR_APPEAR_ANIM_DURATION );
        animSet.setInterpolator( new LinearInterpolator() );
        animSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if( miStarAnimationState != STAR_ANIM_RESTART )
                {
                    return;
                }

                if( iType == STAR_GLITTER_TYPE_OFF )
                {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            setStarGlitterAnimation( view, STAR_GLITTER_TYPE_ON, iCenterX, iCenterY );
                        }
                    });
                }
                else
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setStarGlitterAnimation( view, STAR_GLITTER_TYPE_OFF, iCenterX, iCenterY );
                        }
                    }, STAR_GLITTER_DURATION );
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.clearAnimation();
        view.setVisibility(View.VISIBLE);
        view.setAnimation( animSet );
    }

    private final static int STAR_DEFAULT_ANIM_DURATION = 2000;
    private void setDefaultStartAnimation( final ImageView view, final int iType )
    {
        final int iCenterX = view.getWidth()/2;
        final int iCenterY = view.getHeight()/2;

        AlphaAnimation alpha;
        ScaleAnimation scale;
        if( iType == STAR_GLITTER_TYPE_OFF )
        {
            alpha = new AlphaAnimation( 1.0f, 0.0f );
            scale = new ScaleAnimation( 1.0f, 0.0f, 1.0f, 0.0f, iCenterX, iCenterY );
        }
        else
        {
            alpha = new AlphaAnimation( 0.0f, 1.0f );
            scale = new ScaleAnimation( 0.0f, 1.0f, 0.0f, 1.0f, iCenterX, iCenterY );
        }

        AnimationSet animSet = new AnimationSet(true);
        animSet.addAnimation(alpha);
        animSet.addAnimation(scale);
        animSet.setDuration( STAR_DEFAULT_ANIM_DURATION );
        animSet.setInterpolator( new LinearInterpolator() );
        animSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if( miStarAnimationState != STAR_ANIM_RESTART )
                {
                    return;
                }

                if( iType == STAR_GLITTER_TYPE_OFF )
                {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            setDefaultStartAnimation( view, STAR_GLITTER_TYPE_ON );
                        }
                    });
                }
                else
                {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            setDefaultStartAnimation( view, STAR_GLITTER_TYPE_OFF );
                        }
                    });
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.clearAnimation();
        view.setVisibility(View.VISIBLE);
        view.setAnimation( animSet );
    }

    private int[] mResultTextIDs = {R.id.name_1st, R.id.name_2nd, R.id.name_3rd};
    private int[] mResultTimeIDs = {R.id.time_1st, R.id.time_2nd, R.id.time_3rd};

    @Override
    protected void callbackBossInformation( Message msg )
    {
        if (msg == null || msg.obj == null) {
            setDraw();
            return;
        }

        GNCustomUrlReturnObject obj = (GNCustomUrlReturnObject) msg.obj;
        Object ret = obj.getMessageObject();

        if( ret == null )
        {
            setDraw();
            return;
        }

        String strData = new String( (byte[])ret, Charset.forName( "UTF-8" ) );

        try {
            JSONObject json = new JSONObject(strData);
            JSONObject data = json.getJSONObject("data");

            JSONArray array = data.getJSONArray("push_list");

            calcResult( array );
            startResult();
        } catch (JSONException e) {
            e.printStackTrace();

            for (int i = 0; i < 3; i++) {
                TextView view = (TextView) findViewById(mResultTextIDs[i]);
                view.setText("---");
            }
            // TODO 本当は DRAW
            miResultType = RESULT_DRAW;
            setDraw();
        }
    }

    private void startResult()
    {
        // 描画アニメーション開始
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startAlphaDownAnimation();
            }
        }, START_ANIM_DELAY);

        switch (miResultType) {
            case RESULT_WIN:
                setWin();
                break;
            case RESULT_DRAW:
                setDraw();
                break;
            default:
            case RESULT_LOSE:
                setLose();
                break;
        }
    }

    private void calcResult( JSONArray array )
    {
        if( array == null || array.length() == 0 )
        {
            for( int i = 0 ; i < 3 ; i++ )
            {
                TextView view = (TextView)findViewById(mResultTextIDs[i]);
                view.setText( "---" );
            }
            miResultType = RESULT_DRAW;
            return;
        }
        else
        {
            int iMax = array.length() > 3 ? array.length() - 3 : 0;
            int iResourceIndex = 0;
            long lBaseTime = Utility.INVALID_ID;
            for( int i = array.length() - 1; i >= iMax ; i--, iResourceIndex++ )
            {
                TextView name = (TextView)findViewById(mResultTextIDs[iResourceIndex]);
                TextView time = (TextView)findViewById(mResultTimeIDs[iResourceIndex]);

                try {
                    JSONObject object = array.getJSONObject(i);
/*
                    if( i == array.length() - 1 )
                    {
                        lBaseTime = Long.valueOf(object.getString("datetime"));
                        time.setText( "00:00:000" );
                    }
                    else {
                        long lTime = Long.valueOf(object.getString("datetime"));
                        Date date = new Date( lTime - lBaseTime );
                        SimpleDateFormat format = new SimpleDateFormat("mm:ss:SSS");
                        time.setText(format.format(date));
                    }
*/
                    if( mBaseTime == 0L )
                    {
                        mBaseTime = Long.valueOf(object.getString("datetime"));
                        time.setText( "00:00:000" );
                    }
                    else
                    {
                        long lTime = Long.valueOf(object.getString("datetime"));

                        // フライングが起きる可能性有
                        if( mBaseTime - lTime > 0 )
                        {
                            time.setText( "00:00:000" );
                        }
                        else {
                            Date date = new Date( lTime - mBaseTime );
                            SimpleDateFormat format = new SimpleDateFormat("mm:ss:SSS");
                            time.setText(format.format(date));
                        }
                    }

                    JSONObject ParsonData = object.getJSONObject("subordinate");
                    String strName = ParsonData.getString("name");
                    name.setText(strName);
                } catch (JSONException e) {
                    e.printStackTrace();
                    miResultType = RESULT_DRAW;
                    return;
                }
            }

            //  押した人が 3 人に満たなかった場合用
            for( int i = iResourceIndex ; i < 3 ; i++ )
            {
                TextView view = (TextView)findViewById(mResultTextIDs[i]);
                view.setText( "---" );

                TextView time = (TextView)findViewById(mResultTimeIDs[i]);
                time.setText( "--:--:---" );
            }


            miResultType = RESULT_LOSE;
            int iNumber = Utility.INVALID_ID;
            // 自分の順位
            for( int i = array.length() - 1 ; i >= 0 ; i-- )
            {
                try {
                    JSONObject object = array.getJSONObject(i);

                    JSONObject ParsonData = object.getJSONObject("subordinate");
                    String strCode = ParsonData.getString("code");

                    String strOwnCode = Utility.getSavedStringData(getApplicationContext(), "code");
                    if( strOwnCode.contentEquals(strCode) )
                    {
                        iNumber = array.length() - i;
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    miResultType = RESULT_DRAW;
                    return;
                }
            }

            // 1 位のみ勝利にする
            if( iNumber == 1 )
            {
                miResultType = RESULT_WIN;
            }

            TextView rankingText = (TextView)findViewById(R.id.reuslt_description);
            rankingText.setText( String.valueOf( array.length() ) + "人中" + String.valueOf(iNumber) + "番目にボタンを押しました");
        }
    }
}
