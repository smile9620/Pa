package com.bg.bgpad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by Administrator on 2017-01-09.
 */

public class WelcomeActivity extends Activity {
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Intent intent = new Intent(WelcomeActivity.this,
                    LoginActivity.class);

            startActivity(intent);

            finish();
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome_activity);
        handler.sendEmptyMessageDelayed(0, 1500);
    }
}
