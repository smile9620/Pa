package com.bg.bgpad;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import com.bg.constant.Constant;
import com.bg.constant.DeviceName;
import com.bg.constant.InBodyBluetooth;
import com.bg.model.InBodyData;
import com.bg.model.User;
import com.bg.utils.SelectionUser;
import com.bg.utils.SetTitle;
import com.bg.utils.UserSelectaDapter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserSelectActivity extends BleActivityResult implements SetTitle.OnTitleBtClickListener,
        AdapterView.OnItemClickListener, SelectionUser.SetOnSelectoinUser {
    @BindView(R.id.title)
    View view;
    @BindView(R.id.selection)
    View selection;
    @BindView(R.id.listview)
    ListView listview;

    private List<User> users = new ArrayList<User>();
    private List<Map<String, String>> list_maps = new ArrayList<>();
    //    private SimpleAdapter simpleAdapter;
    private boolean bt_enable;
    private boolean column = true;  //是否有立柱，默认为有
    private UserSelectaDapter selectaDapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);
        ButterKnife.bind(this);
        new SetTitle(this, view, new boolean[]{true, true}, "用户信息",
                new int[]{R.drawable.back_bt, R.drawable.ble_bt});
        new SelectionUser(this, this, selection, true);

//        getBle(DeviceName.InBody); //启动蓝牙
        getData();
//        simpleAdapter = new SimpleAdapter(this, list_maps, R.layout.userlist_item,
//                new String[]{"user_number", "user_name", "sex", "strDate"},
//                new int[]{R.id.usernumber, R.id.username, R.id.sex, R.id.testdate});
        selectaDapter = new UserSelectaDapter(this, list_maps);
        listview.setAdapter(selectaDapter);
        listview.setOnItemClickListener(this);

    }

    private void getData() {
//        users = DataSupport.order("createDate desc").limit(10).find(User.class);
        Map<String, String> map_title = new HashMap<>();
        map_title.put("user_number", "编号");
        map_title.put("user_name", "姓名");
        map_title.put("sex", "性别");
        map_title.put("strDate", "测试日期");
        list_maps.add(map_title); // 添加表头
//        for (int i = 0; i < users.size(); i++) {
        for (int i = 0; i < 30; i++) {
            Map<String, String> map = new HashMap<>();
//            map.put("user_number", users.get(i).getCreateDate().toString());
//            map.put("user_name", users.get(i).getUser_name());
//            map.put("sex", users.get(i).getSex() + "");
//            map.put("strDate", users.get(i).getStrDate().toString());
            map.put("user_number", "1234567890");
            map.put("user_name", "张三" + i);
            map.put("sex", "男");
            map.put("strDate", "2017-04-01");
            list_maps.add(map);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectaDapter.notifyDataSetChanged();
    }

    @Override
    protected void updateState(boolean bool) {
        bt_enable = bool;
        if (bool) {
            writeBle(InBodyBluetooth.send1);
        }
    }

    @Override
    protected void updateData(String str) {

        showToast("UserSelectActivity" + str);
//        FA 4A 0A 30 38 38 38 38 38 38 38 38 38 01 FF
        String[] data = str.split(" ");
        column = data[data.length - 3].equals("0") ? false : true;
        showToast(column == true ? "有立柱" : "没有立柱");

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
        String number = list_maps.get(position).get("user_number");
        Intent intent = new Intent(this, UserInformationActivity.class);
        intent.putExtra("user_number", number);
        intent.putExtra("column", column);
        startActivity(intent);
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
    public void onSearch(String type, String selectStr) {
        list_maps.clear();
        users.clear();
        if (selectStr == null || selectStr.isEmpty()) {
            showToast("请输入查询条件！");
        } else {
            if (type.equals(getResources().getString(R.string.search_date))) {
                List<InBodyData> data = DataSupport.select().where("strDate = ?", selectStr).
                        order("testDate desc").limit(15).find(InBodyData.class, true);
//                List<InBodyData> data =  DataSupport.select("user").where("strDate = ?", info).
//                        order("testDate desc").find(InBodyData.class,true);
                for (int i = 0; i < data.size(); i++) {
                    users.add(data.get(i).getUser());
                }
            } else {
                users = DataSupport.where(type + " like ?", "%" + selectStr + "%").
                        order("createDate desc").find(User.class);
            }
        }
        for (int i = 0; i < users.size(); i++) {
            Map<String, String> map = new HashMap<>();
            map.put("user_name", users.get(i).getUser_name());
            map.put("user_number", users.get(i).getCreateDate().toString());
            list_maps.add(map);
        }

    }

    @Override
    public void onAdd() {
        Intent intent = new Intent(this, UserInformationActivity.class);
        startActivity(intent);
    }
}
