package jp.co.gnavi.meshclient.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.UUID;

import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.bluetooth.BluetoothClientThread;
import jp.co.gnavi.meshclient.bluetooth.BluetoothServerThread;
import jp.co.gnavi.meshclient.common.Utility;

/**
 * Created by kaifuku on 2016/10/05.
 */
public class MainActivity extends BaseActivity {

    private static final int  CALL_BLUETOOTH_SETTINGS = 1;

    private Boolean mbCanUseBluetooth = true;
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

        mClientThread.cancel();
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

        // Bluetooth が OFF の場合は ON にさせる
        if( !Utility.isBluetoothOn() ) {
            Utility.callBluetoothSettings( this, CALL_BLUETOOTH_SETTINGS );
        }
        // Bluetooth が ON の場合は connection を張る
        else {
            setMeshMacAddress();
        }
    }

    /**
     * MESH の MacAddress 取得・設定
     */
    private void setMeshMacAddress() {
        BluetoothDevice bDevice = Utility.getPairDeviceList( getApplicationContext() );

        if( bDevice == null ) {
            Toast.makeText( getApplicationContext(), getResources().getString( R.string.mesh_unknown ), Toast.LENGTH_LONG ).show();
            return;
        }

        ParcelUuid[] uuids =  bDevice.getUuids();
        String strDebugString = "Name:" + bDevice.getName() + " \nMacAddress:" + bDevice.getAddress() + " \nUUID" + bDevice.getUuids();
        TextView text = (TextView)findViewById(R.id.mesh_data);
        text.setText( strDebugString );

/*
        mClientThread = new BluetoothClientThread( getApplicationContext(), "", bDevice, null );
        mClientThread.start();
*/
/*
        BluetoothAdapter    adapter = new BluetoothAdapter.getDefaultAdapter();
        adapter.getRemoteDevice(bDevice.getAddress());
        BluetoothServerThread serverThread = new BluetoothServerThread( getApplicationContext(), "", adapter);
*/
        bDevice.connectGatt(getApplicationContext(), false, callback);
    }

    BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if( status == BluetoothGatt.GATT_SUCCESS ) {
                BluetoothGattService service = gatt.getService( UUID.fromString("72C90001-57A9-4D40-B746-534E22EC9F9E") );
                if( service == null ) {

                } else {
                    notificationEnableDescriptor( service, gatt, UUID.fromString("72C90005-57A9-4D40-B746-534E22EC9F9E") );
                }
            }
//            throw new RuntimeException("Stub!");
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if( newState == BluetoothProfile.STATE_CONNECTED ) {
                gatt.discoverServices();
            } else if( newState == BluetoothProfile.STATE_DISCONNECTED ) {

            }

//            throw new RuntimeException("Stub!");
        }

        private void notificationEnableDescriptor( BluetoothGattService service, BluetoothGatt gatt, UUID uuid )
        {
            BluetoothGattCharacteristic characterictic = service.getCharacteristic( uuid );
            if( characterictic == null ) {

            } else {
                boolean registered = gatt.setCharacteristicNotification(characterictic, true);

                BluetoothGattDescriptor descriptor = characterictic.getDescriptor( UUID.fromString("00002902-0000-1000-8000-00805f9b34fb") );
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
        }

        private void writeHexDescriptor( BluetoothGattService service, BluetoothGatt gatt, UUID uuid, byte[] data) {
            BluetoothGattCharacteristic characterictic = service.getCharacteristic( uuid );
            if( characterictic == null ) {

            } else {
                boolean registered = gatt.setCharacteristicNotification(characterictic, true);

                characterictic.setValue( data );
                boolean bResult = gatt.writeCharacteristic(characterictic);
                if( !bResult ) {
                    Utility.customLog("log", "error", Log.DEBUG);
                }
            }
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt,characteristic,status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            if (bFlag == 1) {
                bFlag++;
                BluetoothGattService service = gatt.getService(UUID.fromString("72C90001-57A9-4D40-B746-534E22EC9F9E"));
                notificationEnableDescriptor(service, gatt, UUID.fromString("72C90003-57A9-4D40-B746-534E22EC9F9E"));
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt,characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt,descriptor,status);
        }

        private int bFlag = 0;
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt,descriptor,status);

            if( bFlag == 0) {
                bFlag++;
                int test = 0x00020103;
                byte[] bytes = ByteBuffer.allocate( Integer.SIZE/Byte.SIZE ).putInt(test).array();
                BluetoothGattService service = gatt.getService( UUID.fromString("72C90001-57A9-4D40-B746-534E22EC9F9E") );
                writeHexDescriptor( service, gatt, UUID.fromString("72C90004-57A9-4D40-B746-534E22EC9F9E"), bytes );
/*
                BluetoothGattService service = gatt.getService( UUID.fromString("72C90001-57A9-4D40-B746-534E22EC9F9E") );
                BluetoothGattCharacteristic read = service.getCharacteristic( UUID.fromString("72C90004-57A9-4D40-B746-534E22EC9F9E"));
                gatt.readCharacteristic(read);
*/
            }
            else if( bFlag == 1) {
                bFlag++;
                BluetoothGattService service = gatt.getService( UUID.fromString("72C90001-57A9-4D40-B746-534E22EC9F9E") );
                notificationEnableDescriptor( service, gatt, UUID.fromString("72C90003-57A9-4D40-B746-534E22EC9F9E") );
            }

/*
            if( status == BluetoothGatt.GATT_SUCCESS ) {
                BluetoothGattService service = gatt.getService( UUID.fromString("72C90001-57A9-4D40-B746-534E22EC9F9E") );
                if( service == null ) {

                } else {
//                    notificationEnableDescriptor( service, gatt, UUID.fromString("72C90005-57A9-4D40-B746-534E22EC9F9E") );

//                    byte[] bytes = {0x00020103};
                    byte[] bytes = {0x0,0x0,0x0,0x2,0x0,0x1,0x0,0x3};
                    writeHexDescriptor( service, gatt, UUID.fromString("72C90004-57A9-4D40-B746-534E22EC9F9E"), bytes );

//                   notificationEnableDescriptor( service, gatt, UUID.fromString("72C90003-57A9-4D40-B746-534E22EC9F9E") );
                }
            }
*/
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt,status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt,mtu,status);
        }
    };
}
