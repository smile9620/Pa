package com.bg.bgpad;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bg.utils.SetTitle;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestReportActivity extends BaseActivity implements SetTitle.OnTitleBtClickListener {

    @BindView(R.id.title)
    View view;
    @BindView(R.id.usernumber)
    TextView usernumber;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.sex)
    TextView sex;
    @BindView(R.id.testdate)
    TextView testdate;
    @BindView(R.id.advice)
    TextView advice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_report);
        ButterKnife.bind(this);

        new SetTitle(this, view, new boolean[]{true, false},
                "专家建议", new int[]{R.drawable.back_bt, R.drawable.ble_bt});
    }

    @Override
    public void leftBt(ImageButton left) {
        finish();
    }

    @Override
    public void rightBt(ImageButton right) {

    }


}
