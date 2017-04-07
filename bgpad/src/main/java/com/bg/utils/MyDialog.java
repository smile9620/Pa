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
    private DialogConfirm dialogConfirm;

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
        if (message.length() > 10) {
            StringBuilder bu = new StringBuilder(message);
            bu.insert(9, "\n"); //换行
            message = bu.toString();
        }
        msg.setText(message);
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
        StringBuilder bu = new StringBuilder(str);
        if (myialog != null) {
            if (str.length() > 10) {
                bu.insert(9, "\n"); //换行
            }
            msg.setText(bu.toString());
        }
    }

    public interface DialogConfirm {
        public void dialogConfirm();
    }
}
