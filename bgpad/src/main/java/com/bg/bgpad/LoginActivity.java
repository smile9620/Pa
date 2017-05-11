package com.bg.bgpad;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText username;
    private EditText password;
    private Button login;
    private Button reset;

    private LocationClient mLocationClient; // baidu 地图
    public final int REQUEST_PERMISSION_ACCESS_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        username = (EditText) this.findViewById(R.id.username);
        password = (EditText) this.findViewById(R.id.password);
        login = (Button) this.findViewById(R.id.login);
        reset = (Button) this.findViewById(R.id.register);

        mLocationClient = new LocationClient(this.getApplicationContext()); // baidu
        // 地图
        mLocationClient.registerLocationListener(new MyLocationListener()); // baidu
        // 地图
        InitLocation(); // baidu 地图
        requestPermission(); // baidu 地图

        login.setOnClickListener(this);
        reset.setOnClickListener(this);

    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        mLocationClient.stop();
        super.onStop();
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) { //6.0以上特性，权限申请
            int checkAccessFinePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkAccessFinePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_ACCESS_LOCATION);
//                Log.d(TAG, "没有权限，请求权限");
                return;
            }
//            Log.d(TAG, "已有定位权限");
        }
        //做下面该做的事
        mLocationClient.start(); // baidu 地图
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.d(TAG, "开启权限permission granted!");
                    //做下面该做的
                    mLocationClient.start(); // baidu 地图
                } else {
                    Toast.makeText(this, "读取位置信息被禁止", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "没有定位权限，请先开启!");
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.login:
                String myname = username.getText().toString().trim();
                String mypassword = password.getText().toString().trim();
                if (myname == null || myname.isEmpty()) {
                    Toast.makeText(this, "用户名为空，不能登录！", Toast.LENGTH_SHORT).show();
                } else if (mypassword == null || mypassword.isEmpty()) {
                    Toast.makeText(this, "密码为空，不能登录！", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences share = getSharedPreferences(
                            LoginActivity.this.getString(R.string.log),
                            MODE_PRIVATE);
                    Map<String, ?> map = share.getAll();

                    if (share != null && map.size() != 0) {
                        if ((map.get("telephone").equals(myname) && map.get(
                                "password").equals(mypassword))) {
                            intent.setClass(this, MainActivity.class);
//                            overridePendingTransition(R.anim.push_left_out,
//                                    R.anim.push_left_out);
                            startActivity(intent);
                            finish();
                        } else {
                            showToast("用户名或密码错误，不能登录！");
                        }

                    } else {
                        showToast("用户名或密码错误，不能登录！");
                    }
                }
                break;
            case R.id.register:
                intent.setClass(this, RegisterActivity.class);
//                overridePendingTransition(R.anim.push_left_out,
//                        R.anim.push_left_out);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void InitLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        // option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
        int span = 2000;
        option.setScanSpan(span);// 设置发起定位请求的间隔时间为2000ms
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // Receive Location
            // StringBuffer sb = new StringBuffer(256);
            // sb.append("time : ");
            // sb.append(location.getTime());
            // sb.append("\nerror code : ");
            // sb.append(location.getLocType());
            // sb.append("\nlatitude : ");
            // sb.append(location.getLatitude());
            // sb.append("\nlontitude : ");
            // sb.append(location.getLongitude());
            // sb.append("\nradius : ");
            // sb.append(location.getRadius());
            // if (location.getLocType() == BDLocation.TypeGpsLocation) {
            // sb.append("\nspeed : ");
            // sb.append(location.getSpeed());
            // sb.append("\nsatellite : ");
            // sb.append(location.getSatelliteNumber());
            // sb.append("\ndirection : ");
            // sb.append("\naddr : ");
            // sb.append(location.getAddrStr());
            // sb.append(location.getDirection());
            // } else if (location.getLocType() ==
            // BDLocation.TypeNetWorkLocation) {
            // sb.append("\naddr : ");
            // sb.append(location.getAddrStr());
            // // 运营商信息
            // sb.append("\noperationers : ");
            // sb.append(location.getOperators());
            // }

            if (location.getAddrStr() != null) {
                mLocationClient.stop();
                Toast.makeText(LoginActivity.this,
                        "当前位置：" + location.getAddrStr(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
