package com.bg.bgpad;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bg.constant.Constant;
import com.bg.constant.DeviceName;
import com.bg.model.InBodyData;
import com.bg.model.User;
import com.bg.utils.FormatString;
import com.bg.utils.MyDialog;
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
    @BindView(R.id.total)
    TextView total;

    private List<Map<String, String>> list_maps = new ArrayList<>();
    private boolean ble_enable;
    private boolean column = true;  //是否有立柱，默认为有
    private SimpleAdapter simpleAdapter;
    private int[] colors = new int[]{0xFFFFFFFF, 0xFFEFEFEF};
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    simpleAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    showToast(column == true ? "有立柱" : "无立柱");
                    break;
            }
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
        List<Map<String, String>> datalist = new ArrayList<Map<String, String>>();
        for (int i = 0; i < inbodylist.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("user_number", inbodylist.get(i).getUser_number());
            map.put("user_name", inbodylist.get(i).getUser().getUser_name());
            map.put("sex", inbodylist.get(i).getUser().getSex() == 0 ? "女" : "男");//性别 0 代表女生，1 代表男生
            map.put("strDate", inbodylist.get(i).getStrDate());
            datalist.add(map);
        }
        total.setText("前 " + datalist.size() + " 条数据");
        addData(datalist);
    }

    private void addData(List<Map<String, String>> list) {
        list_maps.clear();
        if (list.size() != 0) {
            datatip.setVisibility(View.GONE);
            for (int i = 0; i < list.size(); i++) {
                Map<String, String> map = new HashMap<>();
                map.put("user_number", list.get(i).get("user_number"));
                map.put("user_name", list.get(i).get("user_name"));
                map.put("sex", list.get(i).get("sex"));
                map.put("strDate", list.get(i).get("strDate"));
                list_maps.add(map);
            }
        } else {
            datatip.setVisibility(View.VISIBLE);
        }
        handler.sendEmptyMessage(0);
    }

    @Override
    protected void updateState(boolean bool) {
        if (bool) {
            writeData(null, new byte[]{(byte) 0xEA, (byte) 0x52, (byte) 0x02, (byte) 0x20, (byte) 0xFF});
        } else {
            ble_enable = bool;
            new MyDialog(this).setDialog("蓝牙已断开，请重新连接！", true, false, null).show();
        }
    }

    private int time = 0;

    @Override
    protected void updateData(String str) {
        String[] datas = str.toString().split(" ");
        if ((datas[0] + datas[1]).toString().equals(DeviceName.InBody_Head) && datas[3].equals("11") &&
                Integer.parseInt(datas[2], 16) == (datas.length - 3) && datas[datas.length - 1].equals("FF")) {
            //立柱信息
            String allData = FormatString.formateData(datas, new int[]{4, 4});
            column = allData.equals("0") ? false : true;
            ble_enable = true;
            handler.sendEmptyMessage(1);
        } else {
            if (time < 3) {
                writeData(null, new byte[]{(byte) 0xEA, (byte) 0x52, (byte) 0x02, (byte) 0x20, (byte) 0xFF});
            }
            time++;
        }
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
        if (Constant.mBluetoothLeService != null) {
            if (ble_enable) {
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.title_bar));
                String number = list_maps.get(position).get("user_number");
                Intent intent = new Intent(this, UserInformationActivity.class);
                intent.putExtra("user_number", number);
                intent.putExtra("column", column);
                startActivity(intent);
            } else {
                showToast("蓝牙初始化中，请稍后...");
            }
        } else {
            showToast("蓝牙未连接!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
        SelectionUser.fresh();
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
    public void sendList(List<Map<String, String>> list) {
        total.setText("共 " + list.size() + " 条数据");
        addData(list);
    }

    @Override
    public void onAdd() {
        if (Constant.mBluetoothLeService != null) {
            if (ble_enable) {
                Intent intent = new Intent(this, UserInformationActivity.class);
                startActivity(intent);
            } else {
                showToast("蓝牙初始化中，请稍后...");
            }
        } else {
            showToast("蓝牙未连接!");
        }
    }

}
