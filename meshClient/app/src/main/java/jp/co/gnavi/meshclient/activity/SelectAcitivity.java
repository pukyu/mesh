package jp.co.gnavi.meshclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.adpter.SelectListAdapter;
import jp.co.gnavi.meshclient.data.SelectListData;

/**
 * Created by cheshirecat on 2016/10/16.
 */

public class SelectAcitivity extends BaseActivity
{
    private int miSoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_list);

        initalize();
    }

    @Override
    protected void onStart() {
        super.onStart();

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
        releaseSound();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initalize()
    {
        SelectListAdapter adapter = new SelectListAdapter(getApplicationContext());
        for( int i = 0 ; i < 5 ; i++ )
        {
            SelectListData data = new SelectListData();
            data.setTargetName("？？？？？");
            adapter.add(data);
        }
        ListView list = (ListView)findViewById(R.id.select_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playSound( miSoundId );

                Intent intent = new Intent( getApplicationContext(), WaitActivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity( intent );
                overridePendingTransition(0, 0);
            }
        });
    }
}
