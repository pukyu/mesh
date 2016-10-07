package jp.co.gnavi.meshclient.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import jp.co.gnavi.meshclient.activity.MainActivity;

/**
 * Created by kaifuku on 2016/10/05.
 */
public class BluetoothConnectThread extends Thread {
    //ソケットに対するI/O処理

    public static InputStream in;
    public static OutputStream out;
    private String sendNumber;
    private Context mContext;

    private Boolean     mbRunnning = true;


    //コンストラクタの定義
    public BluetoothConnectThread(Context context, BluetoothSocket socket, String string){
        sendNumber = string;
        mContext = context;

        try {
            //接続済みソケットからI/Oストリームをそれぞれ取得
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public void write(byte[] buf){
        //Outputストリームへのデータ書き込み
        try {
            out.write(buf);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void run() {
        byte[] buf = new byte[1024];
        String rcvNum = null;
        int tmpBuf = 0;
/*
        try {
            write(sendNumber.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/
        while(mbRunnning){
            try {
                tmpBuf = in.read(buf);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(tmpBuf!=0){
                try {
                    rcvNum = new String(buf, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            Intent i = new Intent(mContext, MainActivity.class);
            i.putExtra("mesh_data", rcvNum);
            mContext.startActivity(i);
        }
    }

    public void stopRunnning()
    {
        mbRunnning = false;
    }

}
