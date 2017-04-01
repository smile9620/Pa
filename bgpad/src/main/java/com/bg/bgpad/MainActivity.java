package com.bg.bgpad;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bg.constant.Constant;
import com.bg.utils.ActivityCollector;
import com.bg.utils.SetTitle;

import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BleActivityResult implements SetTitle.OnTitleBtClickListener,
        AdapterView.OnItemClickListener {

    private ViewFlipper mViewFlipper;
    private GridView mGridView;
    private ProgressBar progressBar;
    private int[] res = {R.drawable.ppt1, R.drawable.ppt2, R.drawable.ppt3};
    private int[] icon = {R.drawable.gridview, R.drawable.gridview,
            R.drawable.gridview, R.drawable.gridview};
    private String[] icontext = {"生长发育", "血红蛋白", "人体成分", "母乳分析"};
    private List<BaseActivity> pages = new ArrayList<BaseActivity>();
    private SimpleAdapter simple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = this.findViewById(R.id.title);
        new SetTitle(this,view, new boolean[]{true,false},
                "贝高医疗", new int[]{R.drawable.qr_bt, R.drawable.ble_bt});

        mViewFlipper = (ViewFlipper) this.findViewById(R.id.viewFlipper);
        mGridView = (GridView) this.findViewById(R.id.gridView);
        progressBar = (ProgressBar) this.findViewById(R.id.progressBar);

        for (int i = 0; i < res.length; i++) {
            mViewFlipper.addView(getImage(res[i]));
        }

        simple = new SimpleAdapter(this, getdata(), R.layout.gridview_item,
                new String[]{"imageView", "textView"}, new int[]{
                R.id.imageView, R.id.textView});
        mGridView.setAdapter(simple);
        mGridView.setOnItemClickListener(this);

        mViewFlipper.setInAnimation(this, R.anim.push_right_in);
        mViewFlipper.setOutAnimation(this, R.anim.push_left_out);
        mViewFlipper.setFlipInterval(3000);
        mViewFlipper.startFlipping();
        getPages();
    }

    @Override
    protected void updateState(boolean bool) {

    }

    @Override
    protected void updateData(String str) {

    }

    private ImageView getImage(int resid) {
        ImageView image = new ImageView(this);
        image.setBackgroundResource(resid);
        return image;
    }

    private List<Map<String, Object>> getdata() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;
        for (int i = 0; i < icon.length; i++) {
            map = new HashMap<String, Object>();
            map.put("imageView", icon[i]);
            map.put("textView", icontext[i]);
            list.add(map);
        }
        return list;
    }

    private List<BaseActivity> getPages() {
        UserSelectActivity userSelectActivity = new UserSelectActivity();
        UserManagementActivity userManag = new UserManagementActivity();
        pages.add(userSelectActivity);
        pages.add(userManag);
        pages.add(userSelectActivity);
        pages.add(userSelectActivity);
        return pages;
    }

    @Override
    public void leftBt(ImageButton left) {
        requestPermission("Camrea");
    }

    @Override
    public void rightBt(ImageButton right) {

//        if (!Constant.mScanning) {
//            isavailable();
//        } else {
//            scanLeDevice(false);
//        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, pages.get(position).getClass());
        startActivity(intent);
    }

    /**
     * 捕获手机物理菜单键
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                ActivityCollector.finishAll();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}