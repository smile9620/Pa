package com.bg.utils;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.bg.bgpad.AppContext;
import com.bg.bgpad.R;

import java.util.Calendar;

/**
 * Created by zjy on 2017-03-24.
 */

public class SelectionUser implements View.OnClickListener {

    EditText input;
    ImageButton more;
    private ImageButton search;
    private Button add;
    private SetOnSelectoinUser onSelectoinUser;
    private String select_name = "user_name"; // 1、根据姓名进行查询
    private String select_number = "user_number";// 2、根据编码进行查询
    private String select_date = "createDate";// 2、根据编码进行查询
    private String select;
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
                onSelectoinUser.onSearch(select, str);
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
            }
        });
        number.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                inputSelect(popWindow, "number");
            }
        });
        date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                inputSelect(popWindow, "date");
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

                if (str.equals("from")) {
                    from = year + "-" + (month + 1) + "-" + dayOfMonth;
                    showDialog("to");
                } else {
                    to = year + "-" + (month + 1) + "-" + dayOfMonth;
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

    public interface SetOnSelectoinUser {
        void onSearch(String type, String selectStr);

        void onAdd();
    }
}
