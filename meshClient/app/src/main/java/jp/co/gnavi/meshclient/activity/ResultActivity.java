package jp.co.gnavi.meshclient.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;

import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.adpter.SelectListAdapter;
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
        miResultType = intent.getIntExtra("type", RESULT_WIN);

        initalize();
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
    }

    @Override
    protected void onPause() {
        super.onPause();

        clearArrawAnimation();
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startAlphaDownAnimation();
            }
        }, START_ANIM_DELAY);

        setCommon();

        switch (miResultType)
        {
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
    }

    private static final int WIN_ANIMATION_DELAY = START_ANIM_DELAY + ALPHA_ANIM_DURATION - 100;
    private static final int WIN_ANIMATION_DURATION = 250;
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
                scale.setDuration( WIN_ANIMATION_DURATION );
                scale.setInterpolator( new AccelerateInterpolator() );
                layout.clearAnimation();
                layout.setVisibility(View.VISIBLE);
                layout.setAnimation(scale);
            }
        },  WIN_ANIMATION_DELAY );

        Button continueButton = (Button)findViewById(R.id.btn_continue);
        continueButton.setVisibility(View.GONE);

        RelativeLayout resultLayout = (RelativeLayout)findViewById(R.id.result_data);
        resultLayout.setVisibility(View.VISIBLE);

        TextView resultDiscription = (TextView)findViewById(R.id.reuslt_description);
    }

    private void setLose()
    {
        View    backView = (View)findViewById(R.id.back_color);
        backView.setBackgroundColor(getResources().getColor(R.color.loseColor));

        RelativeLayout starLayout = (RelativeLayout)findViewById(R.id.star_layout);
        starLayout.setVisibility(View.GONE);

        RelativeLayout resultImageLayout =(RelativeLayout)findViewById(R.id.result_image_layout);
        resultImageLayout.setVisibility(View.VISIBLE);

        ImageView loseImage = (ImageView)findViewById(R.id.main_image);
        loseImage.setImageResource(R.drawable.lose);

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
        resultImageLayout.setVisibility(View.VISIBLE);

        ImageView drawImage = (ImageView)findViewById(R.id.main_image);
        drawImage.setImageResource(R.drawable.draw);

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

}
