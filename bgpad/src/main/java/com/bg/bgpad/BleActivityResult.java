package com.bg.bgpad;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.bg.constant.Constant;

public abstract class BleActivityResult extends BleActivityStart {

    private ServiceConnection mServiceConnection = null;
    private BroadcastReceiver mGattUpdateReceiver = null;
    private boolean is_change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Receiver();
    }

    protected boolean writeBle(byte[] byt) {  //分包向蓝牙写数据
        boolean result = false;
        int len = byt.length;
        int flag = 0;
        byte[] tmp;
        do {
            if (len <= 20) {
                tmp = new byte[len];
                System.arraycopy(byt, flag, tmp, 0, len);
                result = Constant.mBluetoothLeService.WriteValue(tmp);
                len -= 20;
            } else {
                tmp = new byte[20];
                System.arraycopy(byt, flag, tmp, 0, 20);
                if (Constant.mBluetoothLeService.WriteValue(tmp)) {
                    flag += 20;
                    len -= 20;
                    try {
                        Thread.sleep(6);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } while (len > 0);
        return result;
    }

    protected boolean startBle() {
        if (Constant.mDeviceAddress != null) {
            BleConnected();
            Receiver();
            return true;
        }
        return false;
    }

    @Override
    protected void select_ble() {
        pogressDialog.show();
        pogressDialog.setMessage("蓝牙正在连接中，请稍后...");
        startBle();
    }

    //1分钟之后，如何还是显示连接中，则认为连接不成功，则断开
    CountDownTimer countDownTimer = new CountDownTimer(10000, 1) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            if (pogressDialog != null && pogressDialog.isShowing()) {
                pogressDialog.dismiss();
                if (Constant.mBluetoothLeService != null) {
                    Constant.mBluetoothLeService.close();
                    Constant.mBluetoothLeService = null;
                }
                new AlertDialog.Builder(BleActivityResult.this).
                        setTitle("提示：").setIcon(android.R.drawable.ic_dialog_info).
                        setMessage("蓝牙连接失败，请重试！")
                        .setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setCancelable(false).show();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        is_change = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        is_change = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
        if (mGattUpdateReceiver != null) {
            unregisterReceiver(mGattUpdateReceiver);
        }
    }

    protected abstract void updateState(boolean bool);

    protected abstract void updateData(String str);

    private void Receiver() {

        if (mGattUpdateReceiver == null) {
            mGattUpdateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (is_change) {
                        if (pogressDialog != null) {
                            pogressDialog.dismiss();
                        }
                        final String action = intent.getAction();
                        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) { // 连接成功
                            // Log.e(TAG, "Only gatt, just wait");
                        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
                                .equals(action)) { // 断开连接
                            updateState(false);
                            new AlertDialog.Builder(BleActivityResult.this).
                                    setTitle("提示：").setIcon(android.R.drawable.ic_dialog_info).
                                    setMessage("蓝牙已断开，请重新连接！")
                                    .setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).setCancelable(false).show();
                            if (Constant.mBluetoothLeService != null) {
                                Constant.mBluetoothLeService.close();
                                Constant.mBluetoothLeService = null;
                            }
                        } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                                .equals(action)) // 可以开始干活了
                        {
                            updateState(true);
                            showToast("连接成功，现在可以正常通信！");

                        } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) { // 收到数据
                            String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                            updateData(data);
                        }
                    }
                }
            };
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }
    }

    private void BleConnected() {
        if (Constant.mBluetoothLeService == null) {
            mServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName,
                                               IBinder service) {
                    Constant.mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                            .getService();
                    if (Constant.mBluetoothLeService.initialize()) {
                        Constant.mBluetoothLeService.connect(Constant.mDeviceAddress);
                        countDownTimer.start();
                    } else {
                        if (pogressDialog != null && pogressDialog.isShowing()) {
                            pogressDialog.dismiss();
                            showToast("蓝牙连接失败！");
                        }
                        showToast("设备获取失败!");
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    Constant.mBluetoothLeService = null;
                }

            };

            Intent gattServiceIntent = new Intent(this,
                    BluetoothLeService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() { // 注册接收的事件
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }

}
