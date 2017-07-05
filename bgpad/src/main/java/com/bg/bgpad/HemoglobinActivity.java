package com.bg.bgpad;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;

import com.bg.constant.Constant;
import com.bg.constant.DeviceName;
import com.bg.utils.MyDialog;
import com.bg.utils.SetTitle;

import java.lang.ref.WeakReference;

public class HemoglobinActivity extends BleActivityResult implements SetTitle.OnTitleBtClickListener {
    private boolean ble_enable;
    private int time = 0;

    private MyHandler handler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<HemoglobinActivity> mActivity;

        public MyHandler(HemoglobinActivity activity) {
            mActivity = new WeakReference<HemoglobinActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HemoglobinActivity act = mActivity.get();
            act.showToast(null,msg.obj.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hemoglobin);
        View view = this.findViewById(R.id.title);
        new SetTitle(this, view, new boolean[]{true, true}, "血红蛋白",
                new int[]{R.drawable.back_bt, R.drawable.ble_bt});
        getBle(DeviceName.Hemoglobin); //启动蓝牙
    }

    @Override
    protected void updateState(boolean bool) {
        if (bool) {
            writeData(null, new byte[]{(byte) 0xEA, (byte) 0x21, (byte) 0x02, (byte) 0x02, (byte) 0xFF});
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
//        if ((datas[0] + datas[1]).toString().equals(DeviceName.Hemoglobin_Head) && datas[3].equals("11") &&
//                Integer.parseInt(datas[2], 16) == (datas.length - 3) && datas[datas.length - 1].equals("FF")) {
//
//            ble_enable = true;
//        } else {
//            if (time < 3) {
//                writeData(null, new byte[]{(byte) 0xEA, (byte) 0x21, (byte) 0x03, (byte) 0x02, (byte) 0xFF});
//            }
//            time++;
//        }
    }

    @Override
    public void leftBt(ImageButton left) {
        finish();
    }

    @Override
    public void rightBt(ImageButton right) {
        searchBtClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Constant.mBluetoothLeService != null) {
            Constant.mBluetoothLeService.close();
            Constant.mBluetoothLeService = null;
            showToast(null,"设备已断开！");
        }
        handler.removeCallbacksAndMessages(null);
    }
}
