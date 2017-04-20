package com.bg.bgpad;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.bg.model.InBodyData;
import com.bg.model.User;
import com.bg.utils.MyDialog;
import com.bg.utils.SetTitle;

import java.io.File;
import java.util.Map;

public class InBodyTestReportActivity extends BleActivityResult implements SetTitle.OnTitleBtClickListener {

    private User user;
    private boolean is_save = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_body_test_report);

        View view = this.findViewById(R.id.title);
        new SetTitle(this, view, new boolean[]{true, false},
                "测试报告", new int[]{R.drawable.back_bt, R.drawable.ble_bt});
        Button save = (Button) this.findViewById(R.id.save);
        Button print = (Button) this.findViewById(R.id.print);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();

//                for (int i = 0; i < 10; i++) {
//                    User user = new User();
//                    user.setUser_number("" + i + i);
//                    user.setUser_name("张" + i);
//                    user.setBirthday("20120305");
//                    user.setSex(0);
//                    for (int j = 0; j < 5; j++) {
//                        InBodyData inBodyData = new InBodyData();
//                        inBodyData.setHeight(Float.parseFloat("45.5"));
//                        inBodyData.setUser_number("" + i + i);
//                        inBodyData.save();
//                    }
//                    user.save();
//                }
            }
        });
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences printShare = getSharedPreferences(InBodyTestReportActivity.this.getString(R.string.printshare), MODE_PRIVATE);
                Map<String, ?> printmap = printShare.getAll();
                if (printShare != null && printmap.size() != 0) {
                    if (printmap.get("print").equals("wifi")) {
                        Intent intent = new Intent();
                        ComponentName componentName = new ComponentName("com.lenovo.vop", "com.lenovo.vop.StartActivity");
                        if (componentName != null) {
                            intent.setComponent(componentName);
                            startActivity(intent);
                        } else {
                            showToast("请安装打印机！");
                        }

                    } else {
                        showToast("usb 打印");
                    }
                }else{
                    showToast("usb 打印");  //默认情况下为USB打印
                }
            }
        });
    }

    @Override
    protected void updateState(boolean bool) {

    }

    @Override
    protected void updateData(String str) {

    }

    @Override
    public void leftBt(ImageButton left) {
        back();
    }

    @Override
    public void rightBt(ImageButton right) {

    }

    private void finishActivity() {
        UserInformationActivity.instance.finish();
        finish();
    }

    private void saveUser() {
        if (user != null) {
            InBodyData inBodyData = new InBodyData();
            inBodyData.setHeight(45.5f);
            inBodyData.setUser_number(user.getUser_number());
            if (user.save() && inBodyData.save()) {
                showToast("保存成功！");
                is_save = true;
            } else {
                showToast("保存失败！");
            }
        } else {
            showToast("user 为空");
        }
    }

    private void back() {
        if (!is_save) {
            new MyDialog(this).setDialog("该测试报告尚未保存，您确定要关闭当前页面吗？", true, true, new MyDialog.DialogConfirm() {
                @Override
                public void dialogConfirm() {
                    if (user.getImage_path() != null) {  //删除头像
                        File path1 = new File(getSDPath() + "/Image");
                        File file = new File(path1, user.getImage_path());
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    finishActivity();
                }
            }).show();
        } else {
            finishActivity();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
        }
        return super.onKeyDown(keyCode, event);
    }
}
