package com.bg.bgpad;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.bg.utils.ActivityCollector;
public class BaseActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Drawable drawable = getResources().getDrawable(R.color.share_view,null);
        this.getWindow().setBackgroundDrawable(drawable);

//       Toast.makeText(this, "添加", Toast.LENGTH_SHORT).show();
        ActivityCollector.addActivity(this);

        setContentView(R.layout.activity_main);

}
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    protected void showToast(String str){
        Toast toast = Toast.makeText(BaseActivity.this,str,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

}