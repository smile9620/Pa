package com.bg.bgpad;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import com.bg.constant.Constant;
import com.bg.constant.DeviceName;
import com.bg.constant.InBodyBluetooth;
import com.bg.model.User;
import com.bg.utils.SetTitle;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InBodyActivity extends BleActivityResult implements SetTitle.OnTitleBtClickListener,
        AdapterView.OnItemClickListener {
    @BindView(R.id.title)
    View view;
    @BindView(R.id.input)
    TextInputLayout input;
    @BindView(R.id.search)
    Button search;
    @BindView(R.id.add)
    Button add;
    @BindView(R.id.more)
    ImageButton more;
    @BindView(R.id.listview)
    ListView listview;

    private String select_name = "user_name"; // 1、根据姓名进行查询
    private String select_number = "user_number";// 2、根据编码进行查询
    private String select = "user_name";     // 默认情况下，根据姓名进行查询;
    private PopupWindow pop;
    private EditText input_text;
    private List<User> users;
    private List<Map<String, String>> list_maps = new ArrayList<>();
    private SimpleAdapter simpleAdapter;
    private boolean bt_enable;
    public boolean column = true;  //是否有立柱，默认为有

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_body);
        ButterKnife.bind(this);
        new SetTitle(this).setTitleBar(view, new boolean[]{false, false}, "用户信息",
                new int[]{R.drawable.back_bt,
                        R.drawable.ble_bt});

        getBle(DeviceName.InBody);
        input_text = input.getEditText();

        simpleAdapter = new SimpleAdapter(this, list_maps, R.layout.listitem_device,
                new String[]{"user_name", "user_number"},
                new int[]{R.id.device_name, R.id.device_address});
        listview.setAdapter(simpleAdapter);
        listview.setOnItemClickListener(this);
        pop = showPopupWindow(600);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                rotate(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
        simpleAdapter.notifyDataSetChanged();
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

        showToast("InBodyActivity"+str);
//        FA 4A 0A 30 38 38 38 38 38 38 38 38 38 01 FF
        String[] data = str.split(" ");
        column = data[data.length - 3].equals("0") ? false : true;
        showToast(column == true?"有立柱":"没有立柱");

    }

    @OnClick({R.id.search, R.id.add, R.id.more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search:
                getData();
                simpleAdapter.notifyDataSetChanged();
                break;
            case R.id.add:
                if (bt_enable) {
                    Intent intent = new Intent(this, UserInformationActivity.class);
                    intent.putExtra("column",column);
                    startActivity(intent);
                } else {
                    showToast("蓝牙连接失败，请重新连接！");
                }

                break;
            case R.id.more:
                if (!pop.isShowing()) {
                    rotate(true);
                    //正下方無偏移
                    //popWindow.showAsDropDown(v);
                    //相對某個控件的位置（正下方）有偏移
                    //popWindow.showAsDropDown(v, 50, 50);
                    //相對于父控件的位置，無偏移
                    //popWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                    //相對于父控件的位置，有偏移
                    //pop.showAtLocation(input, 0, 0, 100);
                    pop.showAsDropDown(input);
                } else {
                    rotate(false);
                }
                break;
        }
    }

    private PopupWindow showPopupWindow(int width) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.pop_window, null);
        // 设置按钮的点击事件
        Button name = (Button) contentView.findViewById(R.id.name);
        Button number = (Button) contentView.findViewById(R.id.number);

//        final PopupWindow popWindow = new PopupWindow(contentView,
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        final PopupWindow popWindow = new PopupWindow(contentView,
                width, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        name.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                select = select_name;  //根据姓名进行查询
                input.setHint(getResources().getString(R.string.search_name));
                popWindow.dismiss();
            }
        });
        number.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                select = select_number;  //根据年龄进行查询
                input.setHint(getResources().getString(R.string.search_number));
                popWindow.dismiss();
            }
        });
        // 需要设置一下此参数，点击外边可消失
        popWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popWindow.getBackground().setAlpha(100);
        //設置動畫
        popWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        //设置点击窗口外边窗口消失
        popWindow.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        popWindow.setFocusable(true);
        //防止虛擬鍵盤被彈出菜單遮住
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return popWindow;
    }

    @Override
    public void leftBt(Button left) {
        finish();
    }

    @Override
    public void rightBt(Button right) {
        searchBtClick();
    }

    private void rotate(boolean bool) {
        ObjectAnimator animator;
        if (bool) {
            animator = ObjectAnimator.ofFloat(more,
                    "rotation", 0F, 90F).setDuration(50);
        } else {
            animator = ObjectAnimator.ofFloat(more,
                    "rotation", 90F, 0F).setDuration(50);
        }
        animator.start();
    }

    private void getData() {
        list_maps.clear();
        String info = input_text.getText().toString();
        if (info == null || info.isEmpty()) {
            users = DataSupport.order("createDate desc").limit(10).find(User.class);
        } else {
            users = DataSupport.where(select + " like ?", "%" + info + "%").
                    order("createDate desc").find(User.class);
        }
        for (int i = 0; i < users.size(); i++) {
            Map<String, String> map = new HashMap<>();
            map.put("user_name", users.get(i).getUser_name());
            map.put("user_number", users.get(i).getUser_number());
            list_maps.add(map);
        }

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
}
