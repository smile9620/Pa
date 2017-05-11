package com.bg.utils;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bg.bgpad.AppContext;
import com.bg.bgpad.BaseActivity;
import com.bg.bgpad.R;
import com.bg.model.InBodyData;
import com.bg.model.User;

import org.litepal.crud.DataSupport;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.type;

/**
 * Created by zjy on 2017-03-24.
 */

public class SelectionUser implements View.OnClickListener {

    static EditText input;
    ImageButton more;
    private ImageButton search;
    private Button add;
    private SetOnSelectoinUser onSelectoinUser;
    private static String select_name = "user_name"; // 1、根据姓名进行查询
    private static String select_number = "user_number";// 2、根据编码进行查询
    public static String select_date = "testDate";// 2、根据测试时间进行查询
    private String select = select_name;
    private PopupWindow pop;
    private Context context;
    private String from;
    private String to;

    public SelectionUser(Context context, SetOnSelectoinUser onSelectoinUser, View view, boolean addShow) {
        this.context = context;
        this.onSelectoinUser = onSelectoinUser;
        input = (EditText) view.findViewById(R.id.input);
        more = (ImageButton) view.findViewById(R.id.more);
        search = (ImageButton) view.findViewById(R.id.search);
        add = (Button) view.findViewById(R.id.add);
        if (!addShow) {
            add.setVisibility(View.GONE);
        }
        input.setOnClickListener(this);
        more.setOnClickListener(this);
        search.setOnClickListener(this);
        add.setOnClickListener(this);
        int popWidth = (int) (AppContext.getContext().getResources().getDimension(R.dimen.selectuser_edit_width)
                + AppContext.getContext().getResources().getDimension(R.dimen.selectuser_edit_height));

        pop = showPopupWindow(popWidth);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                rotate(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.input:
                if (input.getHint().equals(context.getResources().getString(R.string.search_date))) {
                    showDialog("from");
                }
                break;
            case R.id.search:
                String str = input.getText().toString();
                List<Map<String, String>> datalist = new ArrayList<Map<String, String>>();
                if (str == null || str.isEmpty()) {
                    Toast toast = Toast.makeText(AppContext.getContext(), "请输入查询条件！", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    if (select.equals(SelectionUser.select_date)) {
                        String[] dates = dateFormat(str);
                        List<InBodyData> list = DataSupport.where(" date between ? and ? ",
                                dates[0], dates[1]).find(InBodyData.class);
                        datalist = getData(list);
                    } else {
                        List<User> users = DataSupport.select("user_number", "user_name","sex").where(select + " = ?", str).find(User.class);
                        for (int i = 0; i < users.size(); i++) {
                            List<InBodyData> list = DataSupport.where("user_number = ? ", users.get(i).getUser_number().
                                    toString()).find(InBodyData.class);
                            if (list.size() != 0) {
                                datalist = getData(list);
                            } else {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("data_id", null);
                                map.put("user_number", users.get(i).getUser_number());
                                map.put("user_name", users.get(i).getUser_name());
                                map.put("sex", users.get(i).getSex() == 0 ? "女" : "男");
                                map.put("strDate", "暂无");
                                datalist.add(map);
                            }
                        }
                    }
                    onSelectoinUser.sendList(datalist);
                }

                break;
            case R.id.add:
                onSelectoinUser.onAdd();
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
        }
    }

    private PopupWindow showPopupWindow(int width) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.pop_window, null);
        // 设置按钮的点击事件
        TextView name = (TextView) contentView.findViewById(R.id.name);
        TextView number = (TextView) contentView.findViewById(R.id.number);
        TextView date = (TextView) contentView.findViewById(R.id.date);

        final PopupWindow popWindow = new PopupWindow(contentView,
                width, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        name.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                inputSelect(popWindow, "name");
                fresh();
            }
        });
        number.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                inputSelect(popWindow, "number");
                fresh();
            }
        });
        date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                inputSelect(popWindow, "date");
                fresh();
            }
        });
        // 需要设置一下此参数，点击外边可消失
        popWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popWindow.getBackground().setAlpha(100);
        //設置動畫
        popWindow.setAnimationStyle(android.R.style.Animation_Activity);
        //设置点击窗口外边窗口消失
        popWindow.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        popWindow.setFocusable(true);
        //防止虛擬鍵盤被彈出菜單遮住
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return popWindow;
    }

    private void showDialog(final String str) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String mon = month + 1 + "";
                String day = dayOfMonth + "";
                if ((month + 1) < 10) {
                    mon = "0" + (month + 1);
                }
                if (dayOfMonth < 10) {
                    day = "0" + dayOfMonth;
                }
                if (str.equals("from")) {
                    from = year + "-" + mon + "-" + day;
                    showDialog("to");
                } else {
                    to = year + "-" + mon + "-" + day;
                    input.setText(from + "/" + to);
                }
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void inputSelect(PopupWindow popWindow, String str) {
        popWindow.dismiss();
        switch (str) {
            case "name":
                select = select_name;  //根据姓名进行查询
                input.setHint(context.getResources().getString(R.string.search_name));
                break;
            case "number":
                select = select_number;  //根据编号进行查询
                input.setHint(context.getResources().getString(R.string.search_number));
                break;
            case "date":
                select = select_date;  //根据体检日期进行查询
                input.setHint(context.getResources().getString(R.string.search_date));
                showDialog("from");
                break;
        }
        if (str.equals("date")) {
            if (input.hasFocus()) {
                input.setFocusable(false);
            }
        } else {
            if (!input.hasFocus()) {
                input.setFocusable(true);
                input.setFocusableInTouchMode(true);
                input.requestFocus();
            }
        }
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

    private String[] dateFormat(String date) {
        String[] dates = date.split("/");
        String[] date1 = dates[0].split("-");
        String[] date2 = dates[1].split("-");
        dates[0] = date1[0] + date1[1] + date1[2];
        dates[1] = date2[0] + date2[1] + date2[2];
        return dates;
    }

    private List<Map<String, String>> getData(List<InBodyData> list) {
        List<Map<String, String>> datalist = new ArrayList<Map<String, String>>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("data_id", String.valueOf(list.get(i).getId()));
            map.put("user_number", list.get(i).getUser_number());
            map.put("user_name", list.get(i).getUser().getUser_name());
            map.put("sex", list.get(i).getUser().getSex() == 0 ? "女" : "男");
            map.put("strDate", list.get(i).getStrDate());
            datalist.add(map);
        }
        return datalist;
    }

    public static void fresh() {
        input.setText("");
    }

    public interface SetOnSelectoinUser {
        void sendList(List<Map<String, String>> list);

        void onAdd();
    }
}
