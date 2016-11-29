package jp.co.gnavi.meshclient.adpter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import jp.co.gnavi.lib.common.GNDefine;
import jp.co.gnavi.lib.connection.GNImageLoad;
import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.common.Utility;
import jp.co.gnavi.meshclient.data.SelectListData;

/**
 * Created by cheshirecat on 2016/10/16.
 */

public class SelectListAdapter extends ArrayAdapter<SelectListData> {
    LayoutInflater  mLayoutInflater = null;
    Context         mContext;

    public SelectListAdapter(Context context) {
        super(context, 0);
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.select_item, parent, false);
        }

        SelectListData data = getItem(position);

        int iMax = getCount() - 1;
        View topLineView = (View)convertView.findViewById(R.id.list_top_line);
        if( position == 0 )
        {
            topLineView.setVisibility(View.VISIBLE);
        }
        else
        {
            topLineView.setVisibility(View.GONE);
        }

        final ImageView   iconFrame = (ImageView)convertView.findViewById(R.id.icon_frame);
        final ImageView   iconImage = (ImageView)convertView.findViewById(R.id.target_icon);
        if( data.getIconImageUrl() != null )
        {
            Handler handler = new Handler()
            {
                public void handleMessage(Message msg) {
                    iconImage.setVisibility(View.VISIBLE);
                    iconFrame.setVisibility(View.VISIBLE);
                }
            };
            iconImage.setVisibility(View.INVISIBLE);
            iconFrame.setVisibility(View.INVISIBLE);

            GNImageLoad imgLoad = new GNImageLoad(handler, data.getIconImageUrl(), iconImage, GNDefine.CONNECTION_GET, null, null, null, mContext);
            imgLoad.start();
        }
        else
        {
            if( data.getIconResourceId() == Utility.INVALID_ID )
            {
                iconImage.setImageResource(R.drawable.user_def);
            }
            else
            {
                iconImage.setImageResource(data.getIconResourceId());
            }

            iconImage.setVisibility(View.VISIBLE);
            iconFrame.setVisibility(View.VISIBLE);
        }


        TextView selectNoText = (TextView)convertView.findViewById(R.id.select_no);
        selectNoText.setText("SELECT:" + data.getListNo());

        TextView teamName = (TextView)convertView.findViewById(R.id.team);
        teamName.setText(data.getTeam());

        TextView text = (TextView)convertView.findViewById(R.id.target_name);
        text.setText( data.getTargetName() );

        return convertView;
    }
}
