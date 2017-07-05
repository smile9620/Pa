package com.bg.bgpad;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.widget.Toast;

import com.bg.constant.Constant;
import com.bg.constant.DeviceName;
import com.bg.utils.MyDialog;

import java.lang.ref.WeakReference;

public class BabyWeighingActivity extends BleActivityResult {
    private boolean ble_enable;
    private Toast toast;

    private MyHandler handler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<BabyWeighingActivity> mActivity;

        public MyHandler(BabyWeighingActivity activity) {
            mActivity = new WeakReference<BabyWeighingActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BabyWeighingActivity act = mActivity.get();
            act.toast = act.showToast(null, msg.obj.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_weighing);

        getBle(DeviceName.BabyWeighing); //启动蓝牙
    }

    @Override
    protected void updateState(boolean bool) {
        if (bool) {
            writeData(null, new byte[]{(byte) 0xEA, (byte) 0x12, (byte) 0x02, (byte) 0x2B, (byte) 0xFF});
        } else {
            ble_enable = bool;
            new MyDialog(this).setDialog("蓝牙已断开，请重新连接！", true, false, null).show();
        }
    }

    @Override
    protected void updateData(String str) {
        String[] datas = str.toString().split(" ");
        Message message = Message.obtain();
        message.obj = str;
        handler.sendMessage(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Constant.mBluetoothLeService != null) {
            Constant.mBluetoothLeService.close();
            Constant.mBluetoothLeService = null;
            showToast(null, "设备已断开！");
        }
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}
