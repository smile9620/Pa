package com.bg.bgpad;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import com.bg.utils.SetTitle;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InBodySetActivity extends BaseActivity implements SetTitle.OnTitleBtClickListener{

    @BindView(R.id.title)
    View view;
    @BindView(R.id.printset)
    Switch printset;
    @BindView(R.id.company)
    EditText company;
    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.telephone)
    EditText telephone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_body_set);
        ButterKnife.bind(this);

        new SetTitle(this, view, new boolean[]{true, false},
                "设置", new int[]{R.drawable.back_bt, R.drawable.ble_bt});
    }

    @Override
    public void leftBt(ImageButton left) {
        finish();
    }

    @Override
    public void rightBt(ImageButton right) {

    }
}
