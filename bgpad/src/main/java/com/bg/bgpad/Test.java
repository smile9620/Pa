package com.bg.bgpad;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bg.model.User;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Test extends BleActivityResult {

    @BindView(R.id.username)
    TextInputLayout username;
    @BindView(R.id.userid)
    TextInputLayout userid;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.show)
    Button show;
    @BindView(R.id.user_show)
    TextView user_show;

    private EditText user_text;
    private EditText id_text;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String str = bundle.get("str").toString();
            user_show.setText(str);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        //通过getEditText()方法来获取EditText控件
        user_text = username.getEditText();
        id_text = userid.getEditText();
        user_show.setText("hello");
    }

    @OnClick({R.id.save, R.id.show})
    public void onClick(View view) {
        Connector.getDatabase();
        switch (view.getId()) {
            case R.id.save:
                User user = new User();
                user.setUser_name(user_text.getText().toString());
                user.setUser_number(id_text.getText().toString());
                Boolean bool = user.save();
                Toast.makeText(this, bool.toString(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.show:
                List<User> listUser = DataSupport.findAll(User.class);
                StringBuilder builder = new StringBuilder();
                for (User user1 : listUser) {
                    builder.append(user1.getUser_name());
                    builder.append(user1.getUser_number() + "");
                }
                Toast.makeText(this, builder.toString(), Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        user_show.setText("onDestroy");
    }

    @Override
    protected void updateState(boolean bool) {

    }

    @Override
    protected void updateData(String str) {
        user_show.setText(str);
    }
}
