package com.bg.bgpad;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bg.constant.Constant;
import com.bg.constant.DeviceName;
import com.bg.constant.InBodyBluetooth;
import com.bg.model.InBodyData;
import com.bg.model.User;
import com.bg.utils.SelectionUser;
import com.bg.utils.SetTitle;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserSelectActivity extends BleActivityResult implements SetTitle.OnTitleBtClickListener,
        AdapterView.OnItemClickListener, SelectionUser.SetOnSelectoinUser {
    @BindView(R.id.title)
    View view;
    @BindView(R.id.selection)
    View selection;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.datatip)
    TextView datatip;

    //    private List<User> userlist = new ArrayList<User>();
    private List<Map<String, String>> list_maps = new ArrayList<>();
    private boolean bt_enable;
    private boolean column = true;  //是否有立柱，默认为有
    private SimpleAdapter simpleAdapter;
    private int[] colors = new int[]{0xFFFFFFFF, 0xFFEFEFEF};
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            simpleAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);
        ButterKnife.bind(this);
        new SetTitle(this, view, new boolean[]{true, true}, "用户信息",
                new int[]{R.drawable.back_bt, R.drawable.ble_bt});
        new SelectionUser(this, this, selection, true);

        getBle(DeviceName.InBody); //启动蓝牙
        getData();
        simpleAdapter = new SimpleAdapter(this, list_maps, R.layout.userlist_item,
                new String[]{"user_number", "user_name", "sex", "strDate"},
                new int[]{R.id.usernumber, R.id.username, R.id.sex, R.id.testdate}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View adapterView = super.getView(position, convertView, parent);
                int colorPos = position % colors.length;
                adapterView.setBackgroundColor(colors[colorPos]);
                return adapterView;
            }
        };

        listview.setAdapter(simpleAdapter);
        listview.setOnItemClickListener(this);
    }

    private void getData() {
        List<InBodyData> inbodylist = DataSupport.order("testDate desc").limit(15).find(InBodyData.class);
        addData(inbodylist);
    }

    private void addData(List<InBodyData> list) {
        list_maps.clear();
        if (list.size() != 0) {
            datatip.setVisibility(View.GONE);
            for (int i = 0; i < list.size(); i++) {
                User user = list.get(i).getUser();
                Map<String, String> map = new HashMap<>();
                map.put("user_number", user.getUser_number().toString());
                map.put("user_name", user.getUser_name());
                map.put("sex", user.getSex() == 0 ? "男" : "女");
                map.put("strDate", list.get(i).getStrDate());
                list_maps.add(map);
            }
        } else {
            datatip.setVisibility(View.VISIBLE);
        }
        handler.sendEmptyMessage(0);
    }

    @Override
    protected void updateState(boolean bool) {
        bt_enable = bool;
        if (bool) {
            writeBle("66666666");
        }
    }

    @Override
    protected void updateData(String str) {

        showToast("UserSelectActivity" + str);

//        column = data[data.length - 3].equals("0") ? false : true;
//        showToast(column == true ? "有立柱" : "没有立柱");


    }

    @Override
    public void leftBt(ImageButton left) {
        finish();
    }

    @Override
    public void rightBt(ImageButton right) {
        searchBtClick();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.title_bar));
        String number = list_maps.get(position).get("user_number");
        Intent intent = new Intent(this, UserInformationActivity.class);
        intent.putExtra("user_number", number);
        intent.putExtra("column", column);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Constant.mBluetoothLeService != null) {
            Constant.mBluetoothLeService.close();
            Constant.mBluetoothLeService = null;
            showToast("设备已断开！");
        }
    }

    @Override
    public void sendList(List<InBodyData> list) {
        addData(list);
    }

    @Override
    public void onAdd() {
        Intent intent = new Intent(this, UserInformationActivity.class);
        startActivity(intent);
    }
}
