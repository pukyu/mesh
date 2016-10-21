package jp.co.gnavi.meshclient.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mlkcca.client.DataElement;
import com.mlkcca.client.DataStore;
import com.mlkcca.client.DataStoreEventListener;
import com.mlkcca.client.MilkCocoa;

import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.common.Utility;
import jp.co.gnavi.meshclient.data.SelectListData;

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

    // カウントダウン
    private int miNowCount;

    // 状態
    private static final int STATE_WAIT = 0;
    private static final int STATE_READY = STATE_WAIT + 1;
    private static final int STATE_START = STATE_READY + 1;
    private static final int STATE_WIN = STATE_START + 1;
    private static final int STATE_LOSE = STATE_WIN + 1;
    private int miState = STATE_WAIT;

    private int mBossId = 0;
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
        setContentView( R.layout.wait );

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

        miSoundId = initializeSound();
    }

    @Override
    protected void onPause() {
        super.onPause();

        clearArrawAnimation();
        releaseSound();
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

    /**
     * 初期化
     */
    private void initialize() {

        // TODO:仮
        changeStateWait();

        makeConnection();

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

        ImageView backSelectImage = (ImageView)findViewById(R.id.reselect);
        backSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( WaitActivity.this, SelectAcitivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity( intent );
                overridePendingTransition(0, 0);
            }
        });


        ImageView targetIcon = (ImageView)findViewById(R.id.user_icon);
        if( mTargetData.getIconResourceId() != Utility.INVALID_ID )
        {
            targetIcon.setImageResource(mTargetData.getIconResourceId());
        }
        else
        {
            targetIcon.setImageResource(R.drawable.user_def);
        }
        TextView group = (TextView)findViewById(R.id.group_name);
        group.setText(mTargetData.getTeam());

        TextView targetName = (TextView)findViewById(R.id.user_name);
        targetName.setText(mTargetData.getTargetName());

        TextView waitTime = (TextView)findViewById(R.id.wait_time);
        waitTime.setText("15");
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

        RelativeLayout readyLayout = (RelativeLayout)findViewById(R.id.overlay);
        readyLayout.setVisibility(View.INVISIBLE);

        RelativeLayout subTitleWaitLayout = (RelativeLayout)findViewById(R.id.wait_layout);
        subTitleWaitLayout.setVisibility(View.VISIBLE);

        RelativeLayout stateSubTitleLayout = (RelativeLayout)findViewById(R.id.state_sub_title);
        stateSubTitleLayout.setVisibility(View.INVISIBLE);

        ImageView backSelectImage = (ImageView)findViewById(R.id.reselect);
        backSelectImage.setEnabled(true);
    }

    private void changeStateReady()
    {
        if( miState >= STATE_READY )
        {
            return;
        }

        miState = STATE_READY;
        miNowCount = 11;

        setDisplayColorFilter(getResources().getColor(R.color.yellow));
        setOverlayColorFilter( STATE_READY );

        TextView stateText = (TextView)findViewById(R.id.state_title);
        stateText.setText("STANDBY");

        TextView subTitle = (TextView)findViewById(R.id.sub_title);
        subTitle.setText("準備中");

        TextView subText = (TextView)findViewById(R.id.state_sub_text);
        subText.setText("各自争奪戦準備後待機推奨");

        RelativeLayout readyLayout = (RelativeLayout)findViewById(R.id.overlay);
        readyLayout.setVisibility(View.VISIBLE);

        RelativeLayout subTitleWaitLayout = (RelativeLayout)findViewById(R.id.wait_layout);
        subTitleWaitLayout.setVisibility(View.INVISIBLE);

        RelativeLayout stateSubTitleLayout = (RelativeLayout)findViewById(R.id.state_sub_title);
        stateSubTitleLayout.setVisibility(View.VISIBLE);

        ImageView backSelectImage = (ImageView)findViewById(R.id.reselect);
        backSelectImage.setEnabled(false);


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                countDown();
            }
        });
    }

    private void countDown()
    {
        miNowCount--;

        if( miNowCount <= 0 )
        {
            changeStateStart();
            return;
        }

        ImageView overlayMainImage = (ImageView)findViewById(R.id.overlay_main_pic);
        overlayMainImage.setImageResource( getCountResource( miNowCount ) );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                countDown();
            }
        }, 1000);
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

        setDisplayColorFilter(getResources().getColor(R.color.pink));
        setOverlayColorFilter( STATE_START );

        TextView stateText = (TextView)findViewById(R.id.state_title);
        stateText.setText("START");

        TextView subTitle = (TextView)findViewById(R.id.sub_title);
        subTitle.setText("争奪戦開始");

        TextView subText = (TextView)findViewById(R.id.state_sub_text);
        subText.setText("神経集中/最速早押");

        RelativeLayout readyLayout = (RelativeLayout)findViewById(R.id.overlay);
        readyLayout.setVisibility(View.VISIBLE);

        RelativeLayout subTitleWaitLayout = (RelativeLayout)findViewById(R.id.wait_layout);
        subTitleWaitLayout.setVisibility(View.INVISIBLE);

        RelativeLayout stateSubTitleLayout = (RelativeLayout)findViewById(R.id.state_sub_title);
        stateSubTitleLayout.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent( getApplicationContext(), ResultActivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity( intent );
                overridePendingTransition(0, 0);
            }
        }, 5000);
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

        ImageView reselect = (ImageView)findViewById(R.id.reselect);
        reselect.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);
    }

    private void setOverlayColorFilter( int state ) {
        String strMainText = "";
        int iTextColor = getResources().getColor(R.color.white);
        int iAlphaColor = getResources().getColor(R.color.themeColor);
        int iLightColor = iAlphaColor;
        int iSubTitleColor = getResources().getColor(R.color.white);
        ImageView mainImage = (ImageView) findViewById(R.id.overlay_main_pic);
        switch (state) {
            case STATE_READY:
                iAlphaColor = getResources().getColor(R.color.alphaYellow);
                iLightColor = getResources().getColor(R.color.lightYellow);
                strMainText = "上司争奪戦開始迄";
                mainImage.setImageResource(R.drawable.count_10);
                iTextColor = getResources().getColor(R.color.black);
                iSubTitleColor = getResources().getColor(R.color.yellow);
                break;
            case STATE_START:
                iAlphaColor = getResources().getColor(R.color.alphaPink);
                iLightColor = getResources().getColor(R.color.lightPink);
                strMainText = "上司争奪戦開始";
                mainImage.setImageResource(R.drawable.push);
                iSubTitleColor = getResources().getColor(R.color.pink);
                break;
            case STATE_WIN:
                break;
            case STATE_LOSE:
                break;
            default:
                break;
        }

        View overlayView = (View) findViewById(R.id.overlay);
        overlayView.setBackgroundColor(iAlphaColor);

        RelativeLayout overlayInfo = (RelativeLayout) findViewById(R.id.overlay_infor);
        overlayInfo.setBackgroundColor(iLightColor);

        TextView mainTextView = (TextView) findViewById(R.id.overlay_main_text);
        mainTextView.setTextColor(iTextColor);
        mainTextView.setText(strMainText);

        TextView subTitleText = (TextView)findViewById(R.id.state_sub_text);
        subTitleText.setTextColor(iSubTitleColor);
    }

    private void makeConnection()
    {
        mBossId = getIntent().getIntExtra("id", 0);

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

                if ("notice".equals(type)) {
                    // 上司が開始ボタンを押した
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("push", "notice " + id);
                        }
                    });
                }
                else if ("start".equals(type)) {
                    // サーバ側で受付が開始された
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("push", "start " + id);
                        }
                    });
                }
                else if ("finish".equals(type)) {
                    // カウントダウンが終了した
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("push", "finish " + id);
                        }
                    });
                }
                else if ("result".equals(type)) {
                    // カウントダウンが終了した
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String result = data.getValue("result");
                            Log.d("push", "result " + id + ":" + result);
                        }
                    });
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
}
