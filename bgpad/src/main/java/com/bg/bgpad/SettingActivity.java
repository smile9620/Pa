package com.bg.bgpad;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.bg.utils.SetTitle;
public class SettingActivity extends BaseActivity implements SetTitle.OnTitleBtClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        View view = this.findViewById(R.id.title);
        new SetTitle(this, view, new boolean[]{false, false},
                "设置", new int[]{R.drawable.back_bt, R.drawable.ble_bt});
    }

    @Override
    public void leftBt(ImageButton left) {

    }

    @Override
    public void rightBt(ImageButton right) {

    }
}
