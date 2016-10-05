package jp.co.gnavi.meshclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.bluetooth.BluetoothClientThread;
import jp.co.gnavi.meshclient.common.Utility;

/**
 * Created by kaifuku on 2016/10/05.
 */
public class MainActivity extends BaseActivity {

    private static final int  CALL_BLUETOOTH_SETTINGS = 1;

    private Boolean mbCanUseBluetooth = true;
    private String  mstrMeshMacAddress = null;

    private BluetoothClientThread   mClientThread;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent( intent );

        String strData = intent.getStringExtra( "mesh_data" );
        TextView text = (TextView)findViewById( R.id.mesh_data );
        text.setText( strData );
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main );

        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if( !mbCanUseBluetooth ) {
            return;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onActivityResult( int requestCode, int resultCode, Intent data ) {
        switch ( requestCode )
        {
            case CALL_BLUETOOTH_SETTINGS:
                if( resultCode == Activity.RESULT_OK ) {
                    setMeshMacAddress();
                }
                else {
                    Toast.makeText( getApplicationContext(), getResources().getString( R.string.bluetooth_off ), Toast.LENGTH_LONG ).show();
                    Utility.callBluetoothSettings( this, CALL_BLUETOOTH_SETTINGS );
                }
                break;
            default:
                break;
        }
    }

    /**
     * 初期化処理
     */
    private void initialize() {
        // Bluetooth が使用できる端末かチェック（OS ver の方が良いかも）
        if( !Utility.canUseBluetooth() ) {
            Toast.makeText( getApplicationContext(), getResources().getString( R.string.cannot_bluetooth_error ), Toast.LENGTH_LONG ).show();
            mbCanUseBluetooth = false;
            return;
        }

        if( !Utility.isBluetoothOn() ) {
            Utility.callBluetoothSettings( this, CALL_BLUETOOTH_SETTINGS );
        }
    }

    /**
     * MESH の MacAddress 取得・設定
     */
    private void setMeshMacAddress() {
        mstrMeshMacAddress = Utility.getPairDeviceList( getApplicationContext() );

        if( mstrMeshMacAddress == null ) {
            Toast.makeText( getApplicationContext(), getResources().getString( R.string.mesh_unknown ), Toast.LENGTH_LONG ).show();
            return;
        }

        TextView text = (TextView)findViewById(R.id.mesh_data);
        text.setText(mstrMeshMacAddress);

/*
        mClientThread = new BluetoothClientThread( getApplicationContext(), "", , null );
        mClientThread.start();
*/
    }
}
