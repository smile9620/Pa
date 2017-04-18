package com.bg.bgpad;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.bg.constant.Constant;
import com.bg.utils.SetTitle;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InBodySetActivity extends BaseActivity implements SetTitle.OnTitleBtClickListener {

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
    @BindView(R.id.confirm)
    Button confirm;
    private SharedPreferences companyShare;
    private SharedPreferences printShare;
    private Map<String, ?> companymap;
    private Map<String, ?> printmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_body_set);
        ButterKnife.bind(this);

        new SetTitle(this, view, new boolean[]{true, false},
                "设置", new int[]{R.drawable.back_bt, R.drawable.ble_bt});

        printShare = getSharedPreferences(
                InBodySetActivity.this.getString(R.string.printshare),
                MODE_PRIVATE);
        printmap = printShare.getAll();
        if (printShare != null && printmap.size() != 0) {
            printset.setChecked(printmap.get("print").equals("wifi") ? true : false);
        } else {
            printset.setChecked(false);
        }
        printset.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor edit = printShare.edit();

                if (isChecked) {
//                    showToast("wifi");
                    edit.putString("print", "wifi");
                } else {
//                    showToast("usb");
                    edit.putString("print", "usb");
                }
                edit.commit();
            }
        });
        companyShare = getSharedPreferences(
                InBodySetActivity.this.getString(R.string.companyshare),
                MODE_PRIVATE);
        companymap = companyShare.getAll();
        if (companyShare != null && companymap.size() != 0) {
            confirm.setText(getResources().getString(R.string.modify));
            company.setFocusable(false);
            address.setFocusable(false);
            telephone.setFocusable(false);
            company.setText(companymap.get("company").toString());
            address.setText(companymap.get("address").toString());
            telephone.setText(companymap.get("telephone").toString());
        } else {
            confirm.setText(getResources().getString(R.string.confirm));
        }
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirm.getText().toString().equals(getResources().getString(R.string.confirm))) {
                    String comp = company.getText().toString();
                    String addr = address.getText().toString();
                    String tele = telephone.getText().toString();

                    if (checkEdit(comp, addr, tele)) {
                        SharedPreferences.Editor edit = companyShare.edit();
                        edit.putString("company", comp);
                        edit.putString("address", addr);
                        edit.putString("telephone", tele);

                        if (edit.commit()) {
                            confirm.setText(getResources().getString(R.string.modify));
                            company.setFocusable(false);
                            address.setFocusable(false);
                            telephone.setFocusable(false);
                        }
                    }
                } else {
                    getFocusable(telephone);
                    getFocusable(address);
                    getFocusable(company);
                    confirm.setText(getResources().getString(R.string.confirm));

                }

            }
        });
    }

    private void getFocusable(EditText edit) {
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
    }

    private boolean checkEdit(String comp, String addr, String tele) {
        if (comp == null || comp.isEmpty()) {
            company.setHint("公司名称不能为空！");
            company.setHintTextColor(Color.RED);
            return false;
        }
        if (addr == null || addr.isEmpty()) {
            address.setHint("公司地址不能为空！");
            address.setHintTextColor(Color.RED);
            return false;
        }
        if (tele == null || tele.isEmpty()) {
            telephone.setHint("公司电话不能为空！");
            telephone.setHintTextColor(Color.RED);
            return false;
        }
        return true;
    }

    @Override
    public void leftBt(ImageButton left) {
        finish();
    }

    @Override
    public void rightBt(ImageButton right) {

    }
}
