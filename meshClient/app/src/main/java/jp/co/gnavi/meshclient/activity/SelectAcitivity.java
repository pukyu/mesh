package jp.co.gnavi.meshclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.adpter.SelectListAdapter;
import jp.co.gnavi.meshclient.common.Utility;
import jp.co.gnavi.meshclient.data.SelectListData;

/**
 * Created by cheshirecat on 2016/10/16.
 */

public class SelectAcitivity extends BaseActivity
{
    private int miSoundId;
    private ArrayList<SelectListData>   mTargetData = new ArrayList<>();

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

        setContentView(R.layout.select_list);

        initalize();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        // Pause で落ちてると思うけど念のため。
        mbStateArrawAnimation = false;
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

    private void initalize()
    {
        SelectListAdapter adapter = new SelectListAdapter(getApplicationContext());
/*
        for( int i = 0 ; i < 5 ; i++ )
        {
            SelectListData data = new SelectListData();
            if( i*0.1 < 1)
            {
                data.setListNo("SELECT:0" + i);
            }
            else
            {
                data.setListNo("SELECT:" + i);
            }
            data.setTeam("○○部");
            data.setTargetName("？？？？？");

            adapter.add(data);
        }
*/
        SelectListData dataTaki = new SelectListData();
        dataTaki.setListNo("SELECT:01");
        dataTaki.setTeam("Z14 推進期間");
        dataTaki.setTargetName("滝 久雄");

        SelectListData dataKubo = new SelectListData();
        dataKubo.setListNo("SELECT:02");
        dataKubo.setTeam("Z14 推進期間");
        dataKubo.setTargetName("久保 征一郎");

        SelectListData dataMatsu = new SelectListData();
        dataMatsu.setListNo("SELECT:03");
        dataMatsu.setTeam("Z14 推進期間 第一開発部 主任");
        dataMatsu.setTargetName("松村 翔子");
        dataMatsu.setIconResourceId(R.drawable.matsumura);

        SelectListData dataYama = new SelectListData();
        dataYama.setListNo("SELECT:04");
        dataYama.setTeam("Z14 推進期間 第一開発部 主任");
        dataYama.setTargetName("山田 太郎");

        mTargetData.add(dataTaki);
        mTargetData.add(dataKubo);
        mTargetData.add(dataMatsu);
        mTargetData.add(dataYama);

        for( int i = 0 ; i < mTargetData.size() ; i++ )
        {
            adapter.add(mTargetData.get(i));
        }



        ListView list = (ListView)findViewById(R.id.select_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playSound( miSoundId );

                Intent intent = new Intent( getApplicationContext(), WaitActivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                intent.putExtra("id", position);
                intent.putExtra("target", (Serializable) mTargetData.get(position));
                startActivity( intent );
                overridePendingTransition(0, 0);
            }
        });

        TextView title = (TextView)findViewById(R.id.state_title);
        title.setText("SELECT");

    }
}
