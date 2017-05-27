package com.bg.bgpad;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.bg.constant.Constant;
import com.bg.utils.MyDialog;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public abstract class BleActivityResult extends BleActivityStart {

    private ServiceConnection mServiceConnection = null;
    private BroadcastReceiver mGattUpdateReceiver = null;
    private boolean is_change;
    private Timer timer1;
    private Timer timer2;//检测蓝牙数据是否发完
    private String data = "";
    private MyHandler handler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<BleActivityResult> mActivity;

        public MyHandler(BleActivityResult activity) {
            mActivity = new WeakReference<BleActivityResult>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BleActivityResult act = mActivity.get();
            if (mActivity.get() != null) {
                new MyDialog(act).setDialog("下位机在忙或蓝牙故障无法接收数据！",
                        true, false, null).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Receiver();
    }

    private int time = 0; //计数用，发三次则认为下位机忙或蓝牙故障
    private byte[] head;

    protected void writeData(String data, byte[] by) {
        this.data = data;
        this.head = by;
        timer1 = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (time < 3) {
                    time = time + 1;
                    writeBle();
                } else {
                    time = 0;
                    timer1.cancel();
                    handler.sendEmptyMessage(0);
                }
            }
        };
        timer1.schedule(task, 0, 1000);//1秒发一次，3次之后未收到数据则下位机忙或蓝牙故障
    }

    private boolean writeBle() {  //分包向蓝牙写数据
        byte[] data3 = head;
        if (data != null) {
            // 将字符串转换成16进制的字节数组，并插入包头
            byte[] content = new byte[0]; //字符串转字节数组
            try {
                content = data.getBytes("gbk");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            data3 = new byte[head.length + content.length];
            System.arraycopy(head, 0, data3, 0, head.length - 1);
            System.arraycopy(content, 0, data3, 4, content.length);
            System.arraycopy(head, 4, data3, content.length + 4, 1);
        }
        // 将字节数组写入蓝牙
        boolean result = false;
        int len = data3.length;
        int flag = 0;
        byte[] tmp;
        do {
            if (len <= 20) {
                tmp = new byte[len];
                System.arraycopy(data3, flag, tmp, 0, len);
                if (Constant.mBluetoothLeService.WriteValue(tmp)) {
                    result = true;
                } else {
                    result = false;
                    break;
                }
                len -= 20;
            } else {
                tmp = new byte[20];
                System.arraycopy(data3, flag, tmp, 0, 20);

                if (Constant.mBluetoothLeService.WriteValue(tmp)) {
                    flag += 20;
                    len -= 20;
                    try {
                        Thread.sleep(6);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    result = true;
                } else {
                    result = false;
                    break;
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
//        pogressDialog.show();
//        myDialog.setMsg(getResources().getString(R.string.ble_connect));
        startBle();
    }

    //1分钟之后，如果还是显示连接中，则认为连接不成功，则断开
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
                myDialog.setDialog("蓝牙连接失败，请重试！"
                        , true, false, null).show();
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
        if (timer1 != null) {
            timer1.cancel();
        }
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
        if (mGattUpdateReceiver != null) {
            unregisterReceiver(mGattUpdateReceiver);
        }
        handler.removeCallbacksAndMessages(null);
    }

    protected abstract void updateState(boolean bool);

    protected abstract void updateData(String str);

    private StringBuilder builder = new StringBuilder();

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
//                            new MyDialog(AppContext.getContext()).setDialog("蓝牙已断开，请重新连接！"
//                                    , true, false, null).show();
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
                            time = 0;
                            if (timer1 != null) {
                                timer1.cancel();
                            }
                            if (timer2 != null) {
                                timer2.cancel();
                            }
                            String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                            builder.append(data);
                            String[] da = data.split(" ");
                            if (da[da.length - 1].equals("FF")) {  //判断蓝牙数据是否结束
                                if (da.length != 1) {
                                    timer2 = new Timer();
                                    timer2.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            updateData(builder.toString());
                                            builder.delete(0, builder.length());
                                        }
                                    }, 50);
                                } else {
                                    updateState(false);//"FF" 设备断开
                                    if (Constant.mBluetoothLeService != null) {
                                        Constant.mBluetoothLeService.close();
                                        Constant.mBluetoothLeService = null;
                                    }
                                }

                            }
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
