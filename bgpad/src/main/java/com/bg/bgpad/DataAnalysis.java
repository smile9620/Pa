package com.bg.bgpad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bg.constant.Constant;
import com.bg.constant.DeviceName;
import com.bg.utils.SetTitle;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DataAnalysis extends BleActivityResult implements SetTitle.OnTitleBtClickListener {

    @BindView(R.id.textView)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_analysis);
        ButterKnife.bind(this);

        View view = this.findViewById(R.id.title);
        new SetTitle(this,view, new boolean[]{true, false},
                "数据分析", new int[]{R.drawable.back_bt, R.drawable.ble_bt});

        getBle(DeviceName.InBody);
        if (!startBle()) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Constant.mBluetoothLeService != null) {
            Constant.mBluetoothLeService.close();
            Constant.mBluetoothLeService = null;
        }
    }

    @Override
    protected void updateState(boolean bool) {

    }

    @Override
    protected void updateData(String str) {
        textView.setText(str);
    }

    @Override
    public void leftBt(ImageButton left) {
        finish();
    }

    @Override
    public void rightBt(ImageButton right) {
        searchBtClick();
    }

}
