package com.bg.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bg.bgpad.R;

/**
 * Created by zjy on 2017-04-05.
 */

public class MyDialog {
    private Dialog myialog;
    private Context context;
    private TextView msg;

    public MyDialog(Context context) {
        this.context = context;
    }

    public Dialog setDialog(String message, boolean showCancel, boolean showConfirm, final DialogConfirm dialogConfirm) {
        myialog = new Dialog(context, R.style.myDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.error_tip, null);
        msg = (TextView) view.findViewById(R.id.msg);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        TextView confirm = (TextView) view.findViewById(R.id.confirm);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        msg.setText(newLine(message));
        if (!showConfirm) {
            confirm.setVisibility(View.GONE);
        }
        if (!showCancel) {
            cancel.setVisibility(View.GONE);
        }
        if (!showConfirm && !showCancel) {
            progressBar.setVisibility(View.VISIBLE);
        }
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirm.dialogConfirm();
                myialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myialog.dismiss();
            }
        });
        myialog.setContentView(view);
        myialog.setCancelable(false);
        myialog.getWindow().setGravity(Gravity.CENTER);
        return myialog;
    }

    public void setMsg(String str) {

        if (myialog != null) {
            msg.setText(newLine(str));
        }
    }

    private String newLine(String aimStr) {
        //要返回的结果
        StringBuffer result = new StringBuffer();
        //中、英文标点符号数组
        String[] punctuations = {"，", "。", "！", ",", ".", "!"};
        //换行符
        String newLine = "\n";
        //中断特殊字符,可以是 , ; . 之类的
        //字符串不为空且长度大于10
        if (aimStr != null && aimStr.length() > 10) {
            int i = 10;
            while (i < aimStr.length() + 10) {
                if (i >= aimStr.length()) {
                    result.append(aimStr.substring(i - 10, aimStr.length()));
                } else {
                    result.append(aimStr.substring(i - 10, i));
                    for (String punctuation : punctuations) {
                        if (aimStr.substring(i, i + 1).equals(punctuation)) {
                            result.append(punctuation);
                            i += 1;
                            break;
                        }
                    }
                    result.append(newLine);
                }
                i += 10;
            }
        } else {
            result.append(aimStr);
        }
        return result.toString();
    }

    public interface DialogConfirm {
        void dialogConfirm();
    }
}
