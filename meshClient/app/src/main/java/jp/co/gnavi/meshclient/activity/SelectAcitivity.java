package jp.co.gnavi.meshclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import jp.co.gnavi.lib.common.GNDefine;
import jp.co.gnavi.lib.connection.GNCustomUrlConnection;
import jp.co.gnavi.lib.connection.GNCustomUrlReturnObject;
import jp.co.gnavi.lib.utility.GNUtility;
import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.adpter.SelectListAdapter;
import jp.co.gnavi.meshclient.common.Define;
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

        load();
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
        ListView list = (ListView)findViewById(R.id.select_list);
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

        TextView nowLoading = (TextView)findViewById(R.id.now_loading);
        nowLoading.setVisibility(View.VISIBLE);
    }

    private void load()
    {
        if( !GNUtility.isConnection( getApplicationContext() ) )
        {
            drawError(getResources().getString(R.string.alart_title), getResources().getString(R.string.network_error));
            return;
        }


        final ListView selectList = (ListView)findViewById(R.id.select_list);
        selectList.setVisibility(View.INVISIBLE);

        final TextView nowLoading = (TextView)findViewById(R.id.now_loading);
        nowLoading.setVisibility(View.VISIBLE);

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg == null || msg.obj == null) {
                    return;
                }

                GNCustomUrlReturnObject obj = (GNCustomUrlReturnObject) msg.obj;
                Object ret = obj.getMessageObject();
                String strObject = new String( (byte[])ret, Charset.forName( "UTF-8" ) );

                try {
                    JSONObject json = new JSONObject(strObject);
                    JSONArray arrayData = json.getJSONArray("data");
                    makeList( arrayData );
/*
                    JSONObject data = json.getJSONObject("data");
                    JSONObject listUser = data.getJSONObject("0");
                    String strName = listUser.getString("name");
*/

                } catch (JSONException e) {
                    e.printStackTrace();
                    drawError(getResources().getString(R.string.alart_title), getResources().getString(R.string.network_error));
                }
            }
        };

        String strUrl = Define.BASE_URL +  "api/boss/";
        GNCustomUrlConnection connection = new GNCustomUrlConnection(handler, strUrl, GNDefine.CONNECTION_GET, null, null, null, getApplicationContext());
        connection.start();
    }

    private void makeList( JSONArray data )
    {
        mTargetData.clear();

        SelectListAdapter adapter = new SelectListAdapter(getApplicationContext());

        for( int i = 0 ; i < data.length() ; i++ )
        {
            try {
                JSONObject object = data.getJSONObject(i);

                SelectListData listData = new SelectListData();

                String strNumber = object.getString("id");
                String strName = object.getString("name");
                String strTeam = object.getString("organization");
                String strTime = object.getString("start_datetime");
                String strIconUrl = object.getString("icon");

                listData.setListNo(strNumber);
                listData.setTeam(strTeam);
                listData.setTargetName(strName);
                listData.setStartTime(strTime);

                if( strIconUrl == null || strIconUrl.equals("null") )
                {
                    if( strNumber.contentEquals("1") )
                    {
                        listData.setIconResourceId(R.drawable.matsumura);
                    }
                    else if( strNumber.contentEquals("2") )
                    {
                        listData.setIconResourceId(R.drawable.seki);
                    }
                }
                else
                {
                    listData.setIconImageUrl(strIconUrl);
                }

                mTargetData.add(listData);

            } catch (JSONException e) {
                e.printStackTrace();
                drawError(getResources().getString(R.string.alart_title), getResources().getString(R.string.data_error));
            }

        }



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
/*
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
*/
        for( int i = 0 ; i < mTargetData.size() ; i++ )
        {
            adapter.add(mTargetData.get(i));
        }

        ListView list = (ListView)findViewById(R.id.select_list);
        list.setAdapter(adapter);
        list.invalidate();

        TextView nowLoading = (TextView)findViewById(R.id.now_loading);
        nowLoading.setVisibility(View.GONE);

        list.setVisibility(View.VISIBLE);
    }

}
