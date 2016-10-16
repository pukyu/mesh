package jp.co.gnavi.meshclient.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import jp.co.gnavi.meshclient.R;
import jp.co.gnavi.meshclient.data.SelectListData;

/**
 * Created by cheshirecat on 2016/10/16.
 */

public class SelectListAdapter extends ArrayAdapter<SelectListData> {
    LayoutInflater mLayoutInflater = null;
    ArrayList<SelectListData> mDataList;

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
        TextView text = (TextView)convertView.findViewById(R.id.target_name);
        text.setText( data.getTargetName() );

        return convertView;
    }
}
