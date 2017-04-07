package com.bg.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bg.bgpad.R;

import java.util.List;
import java.util.Map;

/**
 * Created by zjy on 2017-04-01.
 */

public class UserSelectaDapter extends BaseAdapter {

    private List<Map<String, String>> list_maps;
    private Context context;
//    private Color color = ContextCompat.getColor(context,R.color.table_row);

    public UserSelectaDapter(Context context, List<Map<String, String>> list_maps) {
        this.context = context;
        this.list_maps = list_maps;
    }

    @Override
    public int getCount() {
        return list_maps.size();
    }

    @Override
    public Object getItem(int position) {
        return list_maps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            // 初始化holder
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.userlist_item, null);
            viewHolder.usernumber = (TextView) convertView.findViewById(R.id.usernumber);
            viewHolder.username = (TextView) convertView.findViewById(R.id.username);
            viewHolder.sex = (TextView) convertView.findViewById(R.id.sex);
            viewHolder.testdate = (TextView) convertView.findViewById(R.id.testdate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.parseColor("#efefef"));
        }
        parent.getChildAt(position);
        viewHolder.usernumber.setText(list_maps.get(position).get("user_number").toString());
        viewHolder.username.setText(list_maps.get(position).get("user_name").toString());
        viewHolder.sex.setText(list_maps.get(position).get("sex").toString());
        viewHolder.testdate.setText(list_maps.get(position).get("strDate").toString());

        return convertView;
    }


    class ViewHolder {
        TextView usernumber;
        TextView username;
        TextView sex;
        TextView testdate;
    }
}
