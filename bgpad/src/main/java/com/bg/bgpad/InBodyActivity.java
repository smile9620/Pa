package com.bg.bgpad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.bg.utils.SetTitle;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InBodyActivity extends BaseActivity implements SetTitle.OnTitleBtClickListener {

    @BindView(R.id.title)
    View view;
    @BindView(R.id.test)
    Button test;
    @BindView(R.id.dataset)
    Button dataset;
    @BindView(R.id.set)
    Button set;
    @BindView(R.id.help)
    Button help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_body);
        ButterKnife.bind(this);
        new SetTitle(this, view, new boolean[]{true, false},
                "贝高", new int[]{R.drawable.back_bt, R.drawable.ble_bt});
    }

    @OnClick({R.id.test, R.id.dataset, R.id.set, R.id.help})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.test:
                intent.setClass(this, UserSelectActivity.class);
                startActivity(intent);
                break;
            case R.id.dataset:
                intent.setClass(this, UserManagementActivity.class);
                startActivity(intent);
                break;
            case R.id.set:
                intent.setClass(this, InBodySetActivity.class);
                startActivity(intent);
                break;
            case R.id.help:
                showToast("该功能暂未开启");
                break;
        }
    }

    @Override
    public void leftBt(ImageButton left) {
        finish();
    }

    @Override
    public void rightBt(ImageButton right) {

    }

}
