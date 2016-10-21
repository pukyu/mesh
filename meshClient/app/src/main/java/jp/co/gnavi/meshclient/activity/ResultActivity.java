package jp.co.gnavi.meshclient.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.adpter.SelectListAdapter;
import jp.co.gnavi.meshclient.data.SelectListData;

/**
 * Created by kaifuku on 2016/10/18.
 */

public class ResultActivity extends BaseActivity
{
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
        setWin();
    }

    private void setWin()
    {
        setDisplayColorFilter(getResources().getColor(R.color.winYellow));

        TextView stateText = (TextView)findViewById(R.id.state_title);
        stateText.setText("RESULT");

        TextView subTitle = (TextView)findViewById(R.id.sub_title);
        subTitle.setText("争奪戦結果");

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
