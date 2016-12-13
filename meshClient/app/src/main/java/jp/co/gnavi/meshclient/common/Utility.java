package jp.co.gnavi.meshclient.common;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import java.util.Set;

import static android.support.v7.appcompat.R.styleable.AlertDialog;

/**
 * Created by kaifuku on 2016/10/05.
 */
public class Utility {

    public static int INVALID_ID = -1;

    private static BluetoothAdapter mBluetoothAdapter;
    // todo:他の MESH 端末も同じ数値かチェック（数値の意味チェック）
    private final static String MESH_NAME = "MESH-100BU1001883";

    private final static String SharedPreferenceName = "MashUpApp";

    /**
     * 初期化
     */
    public static final void initialize() {
        // コンストラクタでやらせたら、準備が間に合わず null が確実に来たので。。。
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static final void saveStringData( Context context, String strKey, String strData )
    {
        SharedPreferences data = context.getSharedPreferences(SharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();

        editor.putString(strKey, strData);
        editor.apply();
    }

    public static final String getSavedStringData( Context context, String strKey )
    {
        SharedPreferences data = context.getSharedPreferences(SharedPreferenceName, Context.MODE_PRIVATE);
        return data.getString(strKey, "");
    }

    public static final void saveIntData( Context context, String strKey, int iValue )
    {
        SharedPreferences data = context.getSharedPreferences(SharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();

        editor.putInt(strKey, iValue);
        editor.apply();
    }

    public static final int getSavedIntData( Context context, String strKey )
    {
        SharedPreferences data = context.getSharedPreferences(SharedPreferenceName, Context.MODE_PRIVATE);
        return data.getInt(strKey, INVALID_ID);
    }

    /**
     * ログ出力
     * @param strTag
     * @param strLogMessage
     * @param iLogType
     *
     * @note    リリースビルドでログが自動で消える
     */
    public static final void customLog( String strTag, String strLogMessage, int iLogType ) {

        switch( iLogType )
        {
            case Log.DEBUG:
                Log.d( strTag, strLogMessage );
                break;
            case Log.INFO:
                Log.i( strTag, strLogMessage );
                break;
            case Log.VERBOSE:
                Log.v( strTag, strLogMessage );
                break;
            case Log.WARN:
                Log.w( strTag, strLogMessage );
                break;
            case Log.ERROR:
                Log.e( strTag, strLogMessage );
                break;
            case Log.ASSERT:
                Log.wtf( strTag, strLogMessage );
                break;
        }
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
     * @return  BluetoothDevice（失敗時 null）
     */
    public static final BluetoothDevice getPairDeviceList( Context context ) {
        if( !canUseBluetooth() ) {
            return null;
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // ペアなし
        if( pairedDevices.size() == 0 )
        {
            return null;
        }

        BluetoothDevice bDevice = null;
        for( BluetoothDevice device : pairedDevices ) {
            if( MESH_NAME.compareTo( device.getName() ) == 0 ) {
                bDevice = device;
                break;
            }
        }

        return bDevice;
    }

    /**
     * Bluetooth 設定呼び出し
     */
    public static void callBluetoothSettings( Activity activity, int iRequestCode ) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE );
        activity.startActivityForResult( intent, iRequestCode );
    }


}
