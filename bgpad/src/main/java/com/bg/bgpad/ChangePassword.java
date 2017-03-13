package com.bg.bgpad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bg.utils.SetTitle;

public class ChangePassword extends BaseActivity implements SetTitle.OnTitleBtClickListener,
        View.OnFocusChangeListener {

    private TextView oldpassword;
    private TextView newpassword;
    private TextView passure;
    private Button change;
    private Button back;
    private TextView ifsame;

    private String newpass;
    private String passu;
    private String oldpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        View view = this.findViewById(R.id.title);
        new SetTitle(this).setTitleBar(view,new boolean[]{false,true}, "密码修改", new int[]{R.drawable.back_bt,
                R.drawable.turn_bt});
        findView();

        newpassword.setOnFocusChangeListener(this);
        passure.setOnFocusChangeListener(this);

        change.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            oldpass = oldpassword.getText().toString().trim();
            newpass = newpassword.getText().toString().trim();
            passu = passure.getText().toString().trim();

            if (oldpass == null || oldpass.isEmpty()) {
                oldpassword.setHint("请输入旧密码！");
                oldpassword.setHintTextColor(Color.RED);
                return;
            }
            if (newpass == null || newpass.isEmpty()) {
                newpassword.setHint("新密码不能为空！");
                newpassword.setHintTextColor(Color.RED);
                return;
            }
            if (passu == null || passu.isEmpty()) {
                passure.setHint("确认密码不能为空！");
                passure.setHintTextColor(Color.RED);
                return;
            }

            if (newpass.equals(passu)) {
                SharedPreferences share = getSharedPreferences(
                        ChangePassword.this.getString(R.string.login),
                        MODE_PRIVATE);
                String tel = share.getString("telephone", "");
                String pass = share.getString("password", "");

                if (pass.equals(oldpassword.getText().toString().trim())) {
                    SharedPreferences.Editor edit = share.edit();
                    edit.putString("telephone", tel);
                    edit.putString("password", newpass);
                    Boolean flag = edit.commit();
                    if (flag) {
                        Toast.makeText(ChangePassword.this, "修改成功！", Toast.LENGTH_SHORT)
                                .show();
                        Intent intent = new Intent(ChangePassword.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    showToast("旧密码输入错误，请重试！");
                }
            } else {
                ifsame.setVisibility(View.VISIBLE);
                return;
            }
        }
    };

    public void findView() {
        oldpassword = (TextView) this.findViewById(R.id.oldpassword);
        newpassword = (TextView) this.findViewById(R.id.newpassword);
        passure = (TextView) this.findViewById(R.id.passure);
        ifsame = (TextView) this.findViewById(R.id.ifsame);
        change = (Button) this.findViewById(R.id.change);
    }


    @Override
    public void leftBt(Button left) {
        finish();
    }

    @Override
    public void rightBt(Button right) {
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.passure:
            case R.id.newpassword:
                if (hasFocus) {
                    ifsame.setVisibility(View.INVISIBLE);
                } else {
                    newpass = newpassword.getText().toString().trim();
                    passu = passure.getText().toString().trim();

                    if (newpass.length() != 0 || passu.length() != 0) {
                        if (!newpass.equals(passu)) {
                            ifsame.setVisibility(View.VISIBLE);
                        }
                    } else {
                        showToast("新密码或确认密码不能为空！");
                    }
                }
                break;

            default:
                break;
        }
    }
}

