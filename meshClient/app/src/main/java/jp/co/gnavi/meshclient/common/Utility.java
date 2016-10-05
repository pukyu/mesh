package jp.co.gnavi.meshclient.common;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by kaifuku on 2016/10/05.
 */
public class Utility {
    private static BluetoothAdapter mBluetoothAdapter;

    public Utility() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
    * Bluetooth 対応端末かチェック
    * @return true:対応  false:非対応
    */
    public static final Boolean canUseBluetooth() {

        if( mBluetoothAdapter == null ) {
            return false;
        }

        return true;
    }

    /**
     * Bluetooth が ON になっているかチェック
     * @return  true:ON     false:OFF
     * @note    canUseBluetooth で対応端末かチェックしてから呼ぶこと
     */
    public static final Boolean isBluetoothOn() {

        if( !canUseBluetooth() ) {
            return false;
        }

        return mBluetoothAdapter.isEnabled();
    }

    /**
     * Bluetooth でペアとなっているデバイスを返す
     * @return  MacAddress（失敗時 null）
     */
    public static final String getPairDeviceList( Context context ) {
        if( !canUseBluetooth() ) {
            return null;
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // ペアなし
        if( pairedDevices.size() == 0 )
        {
            return null;
        }

        // TODO:MESH を探して MacAddress を返却する
        for( BluetoothDevice device : pairedDevices ) {
            String strName = device.getName();
            String strAddress = device.getAddress();
            Toast.makeText( context, "Name:" + strName + " address:" + strAddress, Toast.LENGTH_LONG ).show();
        }

        return "12345";
    }

    /**
     * Bluetooth 設定呼び出し
     */
    public static void callBluetoothSettings( Activity activity, int iRequestCode ) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE );
        activity.startActivityForResult( intent, iRequestCode );
    }

}
