package com.bg.bgpad;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import com.bg.utils.SetTitle;

public class NewbabyVisitActivity extends BaseActivity implements SetTitle.OnTitleBtClickListener {

    private Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newbaby_home_visit);

        View view = this.findViewById(R.id.title);
        new SetTitle(this, view, new boolean[]{true, false},
                "新生儿家庭访视记录表", new int[]{R.drawable.back_bt, R.drawable.ble_bt});
    }

    @Override
    public void leftBt(ImageButton left) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(left.getWindowToken(), 0); //强制隐藏键盘
        handler.sendEmptyMessageDelayed(0,100);
    }

    @Override
    public void rightBt(ImageButton right) {

    }
}
