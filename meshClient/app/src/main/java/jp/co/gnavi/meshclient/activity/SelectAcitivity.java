package jp.co.gnavi.meshclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
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
 * 上司選択画面
 *
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
            // この前はログイン画面。ログアウト機能はないので戻せない。
            return true;
        }

        return super.dispatchKeyEvent(e);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if( Utility.isTablet( getApplicationContext() ) )
        {
            setContentView(R.layout.select_tablet_list);
        }
        else {
            setContentView(R.layout.select_list);
        }

        initalize();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 上司データのロード。セレクト画面にはあちこちから戻ってこれるので、ここで呼んで都度最新データを取り直す。
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
            // ここでヘッダ矢印アニメーション開始。
            // ここでやらないと、画面ロック→解除とやられると、全矢印が同じタイミングで点滅してしまう。
            startArrowAnimation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 矢印アニメーションを解除。これにより再度 onResume になった時に綺麗にずれて点滅するようになる。
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

    /**
     * 初期化
     */
    private void initalize()
    {
        ListView list = (ListView)findViewById(R.id.select_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

    /**
     * 上司データのロード
     */
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
                    drawError(getResources().getString(R.string.alart_title), getResources().getString(R.string.network_error));
                    return;
                }

                GNCustomUrlReturnObject obj = (GNCustomUrlReturnObject) msg.obj;
                Object ret = obj.getMessageObject();
                String strObject = new String( (byte[])ret, Charset.forName( "UTF-8" ) );

                try {
                    JSONObject json = new JSONObject(strObject);
                    JSONArray arrayData = json.getJSONArray("data");
                    makeList( arrayData );
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

    /**
     * 上司リスト生成
     *
     * @param data      上司リストの json
     */
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

                if( strIconUrl != null && !strIconUrl.equals("null") )
                {
                    listData.setIconImageUrl(strIconUrl);
                }

                mTargetData.add(listData);

            } catch (JSONException e) {
                e.printStackTrace();
                drawError(getResources().getString(R.string.alart_title), getResources().getString(R.string.data_error));
            }

        }

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
