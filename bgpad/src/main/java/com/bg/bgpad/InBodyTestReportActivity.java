package com.bg.bgpad;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.bg.utils.SetTitle;

public class InBodyTestReportActivity extends BleActivityResult implements SetTitle.OnTitleBtClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_body_test_report);

        View view = this.findViewById(R.id.title);
        new SetTitle(this, view, new boolean[]{true, false},
                "测试报告", new int[]{R.drawable.back_bt, R.drawable.ble_bt});
    }

    @Override
    protected void updateState(boolean bool) {

    }

    @Override
    protected void updateData(String str) {

    }

    @Override
    public void leftBt(ImageButton left) {
        finish();
    }

    @Override
    public void rightBt(ImageButton right) {

    }
}
