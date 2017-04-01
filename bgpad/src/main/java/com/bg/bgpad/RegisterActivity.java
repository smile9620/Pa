package com.bg.bgpad;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bg.utils.SetTitle;

public class RegisterActivity extends BaseActivity implements
        View.OnFocusChangeListener, SetTitle.OnTitleBtClickListener {
    private EditText tel;
    private EditText password;
    private EditText passure;
    private EditText title;
    private TextView ifsame;
    private Button register;
    private String telphone;
    private String pass;
    private String passu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        View view = this.findViewById(R.id.title);
        new SetTitle(this,view, new boolean[]{true,false},
                "注册", new int[]{R.drawable.back_bt, R.drawable.turn_bt});

        tel = (EditText) this.findViewById(R.id.tel);
        password = (EditText) this.findViewById(R.id.password);
        passure = (EditText) this.findViewById(R.id.passure);
        ifsame = (TextView) this.findViewById(R.id.ifsame);
        register = (Button) this.findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telphone = tel.getText().toString().trim();
                pass = password.getText().toString().trim();
                passu = passure.getText().toString().trim();

                if (telphone == null || telphone.isEmpty()) {
                    tel.setHint("电话号码不能为空！");
                    tel.setHintTextColor(Color.RED);
                    return;
                }
                if (pass == null || pass.isEmpty()) {
                    password.setHint("密码不能为空！");
                    password.setHintTextColor(Color.RED);
                    return;
                }
                if (passu == null || passu.isEmpty()) {
                    passure.setHint("确认密码不能为空！");
                    passure.setHintTextColor(Color.RED);
                    return;
                }
                if (pass.equals(passu)) {
                    SharedPreferences share = getSharedPreferences(
                            RegisterActivity.this.getString(R.string.login),
                            MODE_PRIVATE);
                    SharedPreferences.Editor edit = share.edit();
                    edit.putString("telephone", telphone);
                    edit.putString("password", pass);
                    Boolean flag = edit.commit();
                    if (flag) {
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(RegisterActivity.this,
//                                LoginActivity.class);
//                        startActivity(intent);
                        finish();
                    }
                } else {
                    ifsame.setVisibility(View.VISIBLE);
                    return;
                }

            }
        });

    }

    private boolean matchname(String str) {
        boolean mat = false;
        if (str.matches("^[a-zA-z][a-zA-Z0-9_]{2,9}$")) { // 用户名由
            // 3-10位的字母下划线和数字组成
            mat = true;
        } else {
            // input[0].setText("");
            // input[0].setHint("用户名输入不正确");
            // input[0].setHintTextColor(Color.RED);
        }
        return mat;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.passure:
            case R.id.password:
                if (hasFocus) {
                    ifsame.setVisibility(View.INVISIBLE);
                } else {
                    pass = password.getText().toString().trim();
                    passu = passure.getText().toString().trim();

                    if (pass.length() != 0 || passu.length() != 0) {
                        if (!pass.equals(passu)) {
                            ifsame.setVisibility(View.VISIBLE);
                        }
                    } else {
                        showToast("密码或确认密码不能为空！");
                    }
                }
                break;

            default:
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
