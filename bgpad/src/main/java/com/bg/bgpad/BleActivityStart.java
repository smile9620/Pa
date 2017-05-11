package com.bg.bgpad;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bg.constant.Constant;
import com.bg.utils.LeDeviceListAdapter;
import com.bg.utils.MyDialog;
import com.zxing.activity.CaptureActivity;

/**
 * 蓝牙功能调用getBle() 进行搜索，searchBtClick() 进行改变状态
 * 二维码扫描功能调用 requestPermission("Camrea"); 即可
 */
public abstract class BleActivityStart extends BaseActivity {

    protected boolean firstIn = false; // 是否第一次进入主界面
    private boolean if_supported; //该设备是否支持蓝牙
    private BluetoothAdapter mBluetoothAdapter;

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private final long SCAN_PERIOD = 3000;
    protected Dialog pogressDialog = null;
    protected MyDialog myDialog = null;
    private AlertDialog devicesDialog = null;
    private boolean is_ble = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getBle(); //第一次进入界面自动搜索，在需要搜索的界面的此处添加即可
    }

    protected abstract void select_ble();

    protected void searchBtClick() {
        if (is_ble) {
            if (!Constant.mScanning) {//蓝牙是否正在搜索
                isavailable();
            } else {
                scanLeDevice(false);
            }
        } else {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        scanLeDevice(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mBluetoothAdapter.disable();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void getBle(String deviceName) {
        if_supported = getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE);
        if (if_supported) {
            // 初始化 Bluetooth adapter,
            // 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上版本)
            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            // 获取Adapter
            if_supported = mBluetoothAdapter != null ? true : false;
            if (if_supported) {
                myDialog = new MyDialog(this);
                mLeDeviceListAdapter = new LeDeviceListAdapter(this, deviceName);
                if (!firstIn) {
                    // 如果蓝牙已开启第一次进入，自动搜索
                    firstIn = true;
                    isavailable();
                }
            }
        } else {
            is_ble = false;
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void scanLeDevice(final boolean enable) {
        if (Constant.mBluetoothLeService != null) {
            showToast("设备已连接！");
            return;
        } else {
            if (enable) {
                // Stops scanning after a pre-defined scan period.
                mHandler.postDelayed(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override
                    public void run() {
                        if (Constant.mScanning) {
                            Constant.mScanning = false;
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            invalidateButton(Constant.mScanning);
                            mHandler.sendEmptyMessage(2);
                        }
                    }
                }, SCAN_PERIOD);

                Constant.mScanning = true;
                mLeDeviceListAdapter.clear();
                mHandler.sendEmptyMessage(1);
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                Constant.mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
            invalidateButton(Constant.mScanning);
        }
    }

    private void invalidateButton(Boolean scan) {
        if (!scan) {
            pogressDialog.dismiss();
        } else {
            pogressDialog = myDialog.setDialog(getResources().getString(R.string.ble_search), false, false, null);
            pogressDialog.show();
        }
    }

    private void isavailable() {
        if (!mBluetoothAdapter.isEnabled()) { // 设备是否已开启
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Constant.BT_REQ);
        } else {
            requestPermission("Ble");
        }
    }

    protected void requestPermission(String permisName) {

        if (Build.VERSION.SDK_INT >= 23) { //6.0以上特性，权限申请
            if (permisName.equals("Ble")) { //蓝牙权限
                int checkAccessFinePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (checkAccessFinePermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            Constant.REQUEST_ACCESS_LOCATION);
//                LogUtil.d(TAG, "没有权限，请求权限");
                    return;
                } else {
//            LogUtil.d(TAG, "已有定位权限");
//              做下面该做的事
                    scanLeDevice(true);
                    return;
                }
            } else { //相机权限
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请CAMERAE权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.
                            permission.CAMERA}, Constant.REQUEST_GRANTED_ACCESS);
                    return;
                } else {
                    startQR();
                    return;
                }
            }
        }
        if (permisName.equals("Ble")) {
            scanLeDevice(true);
        } else {
            startQR();
        }
    }

    private void startQR() {
        Intent intent = new Intent(BleActivityStart.this,
                CaptureActivity.class);
        startActivityForResult(intent, Constant.QR_REQ);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // 开启权限permission granted
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case Constant.REQUEST_ACCESS_LOCATION://蓝牙
                    scanLeDevice(true);// 自动搜索
                    break;
                case Constant.REQUEST_GRANTED_ACCESS://相机
                    startQR();
                    break;
                default:
                    break;
            }
        } else {
            showToast("请在应用管理中打开访问权限！");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: // Notify change
                    mLeDeviceListAdapter.notifyDataSetChanged();
                    break;
                case 2: // Notify change
                    //搜索到设备名后，只有一个的自动连接，有两个的就用弹出框让用户选择
                    if (mLeDeviceListAdapter.mLeDevices.size() == 0) {
                        showToast("当前无可用设备!");
                    } else if (mLeDeviceListAdapter.mLeDevices.size() == 1) {
                        Constant.mDeviceAddress = mLeDeviceListAdapter.mLeDevices.get(0).getAddress();
                        select_ble();
                    } else {
                        devicesDialog = devicesDialog();
                        devicesDialog.show();
                    }
                    break;
            }
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(device);
                    mHandler.sendEmptyMessage(1);
                }
            });
        }
    };

    private AlertDialog devicesDialog() {
        AlertDialog.Builder dia = new AlertDialog.Builder(BleActivityStart.this);
        AlertDialog dialog = null;
        dia.setTitle("发现可用设备是否连接？");
        View view = LayoutInflater.from(BleActivityStart.this).inflate(
                R.layout.bluetooth_dialog, null);

        ListView list_item = (ListView) view.findViewById(R.id.listView);
        list_item.setAdapter(mLeDeviceListAdapter);
        list_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                BluetoothDevice device = mLeDeviceListAdapter
                        .getDevice(position);
                if (device == null) {
                    return;
                } else {
                    Constant.mDeviceAddress = device.getAddress();
                    select_ble();
                    if (Constant.mScanning) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        Constant.mScanning = false;
                    }
                    devicesDialog.cancel();

                }
            }
        });
        dia.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.cancel();
                    scanLeDevice(false);
                }
            }
        });
        dia.setView(view);
        dialog = dia.create();
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.BT_REQ:
                    requestPermission("Ble");
                    break;
                case Constant.QR_REQ:
                    Bundle bundle = data.getExtras();
                    String scanResult = bundle.getString("result"); // 这就获取了扫描的内容了
                    char[] mydata = null;
                    if (scanResult != null) {
                        mydata = scanResult.toCharArray();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
