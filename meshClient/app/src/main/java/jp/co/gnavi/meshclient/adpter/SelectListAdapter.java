package jp.co.gnavi.meshclient.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.common.Utility;
import jp.co.gnavi.meshclient.data.SelectListData;

/**
 * Created by cheshirecat on 2016/10/16.
 */

public class SelectListAdapter extends ArrayAdapter<SelectListData> {
    LayoutInflater mLayoutInflater = null;

    public SelectListAdapter(Context context) {
        super(context, 0);
        mLayoutInflater = LayoutInflater.from(context);
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


        ImageView   iconImage = (ImageView)convertView.findViewById(R.id.target_icon);
//        if( data.getIconImageUrl() == null )
        if( data.getIconResourceId() == Utility.INVALID_ID )
        {
            iconImage.setImageResource(R.drawable.user_def);
        }
        else
        {
            // TODO:ロード？
            iconImage.setImageResource(data.getIconResourceId());
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
