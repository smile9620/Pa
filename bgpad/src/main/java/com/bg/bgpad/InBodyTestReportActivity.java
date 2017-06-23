package com.bg.bgpad;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bg.constant.DeviceName;
import com.bg.model.InBodyData;
import com.bg.model.User;
import com.bg.utils.DrawTestReport;
import com.bg.utils.FormatString;
import com.bg.utils.MyDialog;
import com.bg.utils.SetTitle;

import org.litepal.crud.DataSupport;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InBodyTestReportActivity extends BleActivityResult implements SetTitle.OnTitleBtClickListener {

    @BindViews({R.id.inliquid, R.id.outliquid, R.id.totalprotein, R.id.totalinorganicsalt, R.id.bodyfat,
            R.id.totalwater, R.id.muscle, R.id.fatfree, R.id.totalweight, R.id.normalrange0,
            R.id.normalrange1, R.id.normalrange2, R.id.normalrange3, R.id.normalrange4})
    TextView[] body; //身体成分分析
    @BindViews({R.id.weight, R.id.bones, R.id.musclefat, R.id.weightrange, R.id.bonesrange,
            R.id.fatrange, R.id.normalrange5, R.id.normalrange6, R.id.normalrange7})
    TextView[] muscleFat; //肌肉脂肪分析
    @BindViews({R.id.weightprogress, R.id.bonesprogress, R.id.fatprogress})
    ProgressBar[] muscleProgress; //肌肉脂肪分析进度条
    @BindViews({R.id.bmi, R.id.fatrate, R.id.waistrate, R.id.bmirange, R.id.fatraterange, R.id.waistraterange, R.id.normalrange8, R.id.normalrange9, R.id.normalrange10})
    TextView[] fatAnalysis; // 肥胖分析
    @BindViews({R.id.bmiprogress, R.id.fatrateprogress, R.id.waistrateprogress})
    ProgressBar[] fatProgress; //肥胖分析进度条
    @BindViews({R.id.leftarm, R.id.rightarm, R.id.trunk, R.id.leftleg, R.id.rightleg,
            R.id.leftarmrange, R.id.rightarmrange, R.id.trunkrange, R.id.leftlegrange, R.id.rightlegrange,
            R.id.normalrange11, R.id.normalrange12, R.id.normalrange13, R.id.normalrange14, R.id.normalrange15})
    TextView[] segmentalMuscle; //节段肌肉分析
    @BindViews({R.id.leftarmprogress, R.id.rightarmprogress, R.id.trunkprogress, R.id.leftlegprogress, R.id.rightlegprogress})
    ProgressBar[] segmentalProgress; //节段肌肉分析进度条
    @BindViews({R.id.standardweight, R.id.standardheight, R.id.musclecontrol, R.id.weightcontrol,
            R.id.fatcontrol, R.id.basalmetabolism})
    TextView[] weightHeight;//体重身高分析
    @BindViews({R.id.waternormal, R.id.waterlack})
    TextView[] healthWater;//健康诊断身体水分
    @BindViews({R.id.edemanormal, R.id.slightedema, R.id.edema})
    TextView[] healthEdema;//健康诊断浮肿
    @BindViews({R.id.proteinlack, R.id.proteinnormal})
    TextView[] nutritionProtein;//营养评估蛋白质
    @BindViews({R.id.inorganicsaltlack, R.id.inorganicsaltnormal, R.id.inorganicsaltoverdose})
    TextView[] nutritionSalt;//营养评估无机盐
    @BindViews({R.id.fatlack, R.id.fatnormal, R.id.fatoverdose})
    TextView[] nutritionFat;//营养评估体脂肪
    @BindViews({R.id.upunbalanced, R.id.upbalanced})
    TextView[] muscleUp; //肌肉评估上肢
    @BindViews({R.id.downunbalanced, R.id.downbalanced})
    TextView[] muscleDown; //肌肉评估下肢
    @BindViews({R.id.ra0, R.id.ra1, R.id.ra2, R.id.la0, R.id.la1, R.id.la2, R.id.tr0, R.id.tr1, R.id.tr2,
            R.id.rl0, R.id.rl1, R.id.rl2, R.id.ll0, R.id.ll1, R.id.ll2})
    TextView[] biologyImpedance;//生物电阻抗
    @BindViews({R.id.shape0, R.id.shape1, R.id.shape2, R.id.shape3, R.id.shape4, R.id.shape5, R.id.shape6,
            R.id.shape7, R.id.shape8, R.id.shape9, R.id.shape10})
    TextView[] shapeJudgment; //体型判断
    @BindView(R.id.edemavalue)
    TextView edemaValue;//浮肿分析值
    @BindView(R.id.edemaprogess)
    ProgressBar edemaProgess;//浮肿分析进度条
    @BindView(R.id.title)
    View view;//抬头栏
    @BindView(R.id.save)
    Button save;//保存按钮
    @BindView(R.id.print)
    Button print;//打印按钮
    @BindView(R.id.score)
    TextView score;//身体总评分
    @BindView(R.id.usernumber)
    TextView usernumber;//被测量者编号
    @BindView(R.id.username)
    TextView username;//被测量者姓名
    @BindView(R.id.sex)
    TextView sex;//被测量者性别
    @BindView(R.id.testheight)
    TextView testHeight;//身高
    @BindView(R.id.testweight)
    TextView testWeight;//体重

    private User user;
    private boolean is_save = false;
    private boolean ble_enable = true;
    private Dialog dialog = null;
    private MyDialog myDialog = new MyDialog(this);
    private String[] datas;
    private InBodyData inBodyData;
    private MyHandler handler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<InBodyTestReportActivity> mActivity;

        public MyHandler(InBodyTestReportActivity activity) {
            mActivity = new WeakReference<InBodyTestReportActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            InBodyTestReportActivity act = mActivity.get();
            if (act != null) {
                switch (msg.what) {
                    case 0:
                        act.showDialog();
                        break;
                    case 1:
                        act.writeData(null, new byte[]{(byte) 0xEA, (byte) 0x52, (byte) 0x02, (byte) 0x26, (byte) 0xFF});
                        act.changeData();
                        break;
                    case 2:
                        act.showToast(msg.obj.toString());
                        break;
                }
            }
        }
    }

    private void showDialog() {
        if (dialog.isShowing()) { //50秒钟后未获取到蓝牙数据
            dialog.dismiss();
            myDialog.setDialog("未接收到蓝牙数据，请检查蓝牙是否故障！", false, true, new MyDialog.DialogConfirm() {
                @Override
                public void dialogConfirm() {
                    finish();
                }
            }).show();
        }
    }
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    if (dialog.isShowing()) { //50秒钟后未获取到蓝牙数据
//                        dialog.dismiss();
//                        myDialog.setDialog("未接收到蓝牙数据，请检查蓝牙是否故障！", false, true, new MyDialog.DialogConfirm() {
//                            @Override
//                            public void dialogConfirm() {
//                                finish();
//                            }
//                        }).show();
//                    }
//                    break;
//                case 1:
//                    writeData(null, new byte[]{(byte) 0xEA, (byte) 0x52, (byte) 0x02, (byte) 0x26, (byte) 0xFF});
//                    changeData();
//                    break;
//                case 2:
//                    showToast(msg.obj.toString());
//                    break;
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_body_test_report);
        ButterKnife.bind(this);
        new SetTitle(this, view, new boolean[]{true, false},
                "测试报告", new int[]{R.drawable.back_bt, R.drawable.ble_bt});
        Intent intent = getIntent();
//        user = DataSupport.where(" user_number = ? ", intent.getStringExtra("user_number")).find(User.class).get(0);
        user = (User) intent.getSerializableExtra("user");
        if (user != null) {
            usernumber.setText(user.getUser_number());
            username.setText(user.getUser_name());
            sex.setText((user.getSex() == 0 ? "女" : "男"));
        }
        dialog = myDialog.setDialog("测试中，请稍后...", false, false, null);
        dialog.show();
        handler.sendEmptyMessageDelayed(0, 50 * 1000);//50秒钟之后，未收到测试数据则认为蓝牙故障
    }

    @OnClick({R.id.save, R.id.print})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                saveUser();
                break;
            case R.id.print:
                SharedPreferences printShare = getSharedPreferences(InBodyTestReportActivity.this.getString(R.string.printshare), MODE_PRIVATE);
                Map<String, ?> printmap = printShare.getAll();
                if (printShare != null && printmap.size() != 0) {
                    if (printmap.get("print").equals("wifi")) {
                        try {
                            Intent intent = new Intent();
                            ComponentName componentName = new ComponentName("com.lenovo.vop", "com.lenovo.vop.StartActivity");
                            intent.setComponent(componentName);
                            startActivity(intent);
                            new MyThread().start();
                        } catch (Exception e) {
                            e.printStackTrace();
                            showToast("请安装wifi打印机！");
                        }
                    } else {
                        showToast("usb 打印");
                    }
                } else {
                    showToast("usb 打印");  //默认情况下为USB打印
                }
                break;
        }
    }

    @Override
    protected void updateState(boolean bool) {
        ble_enable = bool;
        if (!bool) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            new MyDialog(this).setDialog("蓝牙已断开，请重新连接！", false, true, new MyDialog.DialogConfirm() {
                @Override
                public void dialogConfirm() {
                    finishActivity();
                }
            }).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void updateData(String str) {
        if (dialog != null) {
            dialog.dismiss();
        }
        String[] data = str.split(" ");
        if ((data[0] + data[1]).toString().equals(DeviceName.InBody_Head) && data[3].equals("15") &&
                Integer.parseInt(data[2], 16) == (data.length - 3) && data[data.length - 1].equals("FF")) {
            datas = new String[data.length];
            datas = data;
            handler.sendEmptyMessage(1);
        } else if ((data[0] + data[1]).toString().equals(DeviceName.InBody_Head) && data[3].equals("17") &&
                Integer.parseInt(data[2], 16) == (data.length - 3) && data[data.length - 1].equals("FF")) {
            Message message = Message.obtain();
            message.obj = "本次测试已完成！";
            message.what = 2;
            handler.sendMessage(message);
        } else {
            Message mesg = Message.obtain();
            mesg.what = 2;
            mesg.obj = "数据解析错误！";
            handler.sendMessage(mesg);
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void leftBt(ImageButton left) {
        back();
    }

    @Override
    public void rightBt(ImageButton right) {

    }

    private void finishActivity() {
        UserInformationActivity.instance.finish();
        finish();
    }

    private void back() {
        if (!is_save) {
            new MyDialog(this).setDialog("该测试报告尚未保存，您确定要关闭当前页面吗？", true, true, new MyDialog.DialogConfirm() {
                @Override
                public void dialogConfirm() {
                    List<User> us = DataSupport.select("user_number").where("user_number = ? ", user.getUser_number()).find(User.class);
                    if (us.size() == 0 && user.getImage_path() != null) {
                        deleteImage(user.getImage_path());
                    }
                    finishActivity();
                }
            }).show();
        } else {
            finishActivity();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void saveUser() {
        Message message = Message.obtain();
        message.what = 2;
        if (user != null) {
            // body 身体成分分析
            inBodyData.setHeight(Float.parseFloat(testHeight.getText().toString()));//体重
            inBodyData.setWeight(Float.parseFloat(testWeight.getText().toString()));
            inBodyData.setUser_number(user.getUser_number());
            inBodyData.setInliquid(Float.parseFloat(body[0].getText().toString()));//细胞内液
            inBodyData.setOutliquid(Float.parseFloat(body[1].getText().toString()));//细胞外液
            inBodyData.setTotalprotein(Float.parseFloat(body[2].getText().toString()));//蛋白质
            inBodyData.setTotalinorganicsalt(Float.parseFloat(body[3].getText().toString()));//无机盐
            inBodyData.setBodyfat(Float.parseFloat(body[4].getText().toString()));//体脂肪
            inBodyData.setTotalwater(Float.parseFloat(body[5].getText().toString()));//总水分
            inBodyData.setMuscle(Float.parseFloat(body[6].getText().toString()));//肌肉量
            inBodyData.setFatfree(Float.parseFloat(body[7].getText().toString()));//去脂体重
            inBodyData.setNormalrange0(body[9].getText().toString());//细胞内液正常范围
            inBodyData.setNormalrange1(body[10].getText().toString());//细胞外液正常范围
            inBodyData.setNormalrange2(body[11].getText().toString());//蛋白质正常范围
            inBodyData.setNormalrange3(body[12].getText().toString());//无机盐正常范围
            inBodyData.setNormalrange4(body[13].getText().toString());//体脂肪正常范围
            //肌肉脂肪分析
            inBodyData.setBones(Float.parseFloat(muscleFat[1].getText().toString()));//骨骼肌
            inBodyData.setMusclefat(Float.parseFloat(muscleFat[2].getText().toString()));//肌肉脂肪分析体脂肪
            inBodyData.setWeightrange(muscleFat[3].getText().toString());//体重分析
            inBodyData.setWeightvalue(muscleProgress[0].getProgress());//体重分析进度条
            inBodyData.setBonesrange(muscleFat[4].getText().toString());//骨骼肌分析
            inBodyData.setBonesevalue(muscleProgress[1].getProgress());//骨骼肌分析进度条
            inBodyData.setFatrange(muscleFat[5].getText().toString());//体脂肪分析
            inBodyData.setFatvalue(muscleProgress[2].getProgress()); //体脂肪分析进度条
            inBodyData.setNormalrange5(muscleFat[6].getText().toString());//体重正常范围
            inBodyData.setNormalrange6(muscleFat[7].getText().toString());//骨骼肌正常范围
            inBodyData.setNormalrange7(muscleFat[8].getText().toString());//体脂肪正常范围
            // 肥胖分析
            inBodyData.setBmi(Float.parseFloat(fatAnalysis[0].getText().toString()));//bmi
            inBodyData.setFatrate(Float.parseFloat(fatAnalysis[1].getText().toString()));//体脂率%
            inBodyData.setWaistrate(Float.parseFloat(fatAnalysis[2].getText().toString()));//腰臀比
            inBodyData.setBmirange(fatAnalysis[3].getText().toString());//bmi分析
            inBodyData.setBmivalue(fatProgress[0].getProgress());//bmi分析进度条
            inBodyData.setFatraterange(fatAnalysis[4].getText().toString());//体脂率%分析
            inBodyData.setFatratevalue(fatProgress[1].getProgress());//体脂率%分析进度条
            inBodyData.setWaistraterange(fatAnalysis[5].getText().toString());//腰臀比分析
            inBodyData.setWaistratevalue(fatProgress[2].getProgress());//腰臀比分析进度条
            inBodyData.setNormalrange8(fatAnalysis[6].getText().toString());//bmi正常范围
            inBodyData.setNormalrange9(fatAnalysis[7].getText().toString());//体脂率%正常范围
            inBodyData.setNormalrange10(fatAnalysis[8].getText().toString());//腰臀比正常范围
            //节段肌肉分析
            inBodyData.setLeftarm(Float.parseFloat(segmentalMuscle[0].getText().toString()));//左臂
            inBodyData.setRightarm(Float.parseFloat(segmentalMuscle[1].getText().toString()));//右臂
            inBodyData.setTrunk(Float.parseFloat(segmentalMuscle[2].getText().toString()));//躯干
            inBodyData.setLeftleg(Float.parseFloat(segmentalMuscle[3].getText().toString()));//左腿
            inBodyData.setRightleg(Float.parseFloat(segmentalMuscle[4].getText().toString()));//右腿
            inBodyData.setLeftarmrange(segmentalMuscle[5].getText().toString());//左臂分析
            inBodyData.setLeftarmvalue(segmentalProgress[0].getProgress());//左臂分析进度条
            inBodyData.setRightarmrange(segmentalMuscle[6].getText().toString());//右臂分析
            inBodyData.setRightarmvalue(segmentalProgress[1].getProgress());//右臂分析进度条
            inBodyData.setTrunkrange(segmentalMuscle[7].getText().toString());//躯干分析
            inBodyData.setTrunkvalue(segmentalProgress[2].getProgress());//躯干分析进度条
            inBodyData.setLeftlegrange(segmentalMuscle[8].getText().toString());//左腿分析
            inBodyData.setLeftlegvalue(segmentalProgress[3].getProgress());//左腿分析进度条
            inBodyData.setRightlegrange(segmentalMuscle[9].getText().toString());//右腿分析
            inBodyData.setRightlegvalue(segmentalProgress[4].getProgress());//右腿分析进度条
            inBodyData.setNormalrange11(segmentalMuscle[10].getText().toString());//左臂正常范围
            inBodyData.setNormalrange12(segmentalMuscle[11].getText().toString());//右臂正常范围
            inBodyData.setNormalrange13(segmentalMuscle[12].getText().toString());//躯干正常范围
            inBodyData.setNormalrange14(segmentalMuscle[13].getText().toString());//左腿正常范围
            inBodyData.setNormalrange15(segmentalMuscle[14].getText().toString());//右腿正常范围
            //体重身高分析
            inBodyData.setStandardweight(Float.parseFloat(weightHeight[0].getText().toString()));//标准体重
            inBodyData.setStandardheight(Float.parseFloat(weightHeight[1].getText().toString()));//标准身高
            inBodyData.setMusclecontrol(Float.parseFloat(weightHeight[2].getText().toString()));//肌肉控制
            inBodyData.setWeightcontrol(Float.parseFloat(weightHeight[3].getText().toString()));//体重控制
            inBodyData.setFatcontrol(Float.parseFloat(weightHeight[4].getText().toString()));//脂肪控制
            inBodyData.setBasalmetabolism(Float.parseFloat(weightHeight[5].getText().toString()));//基础代谢量
            //健康诊断
            inBodyData.setWater(healthWater[Integer.parseInt(datas[114], 16)].getText().toString());//身体水分
            inBodyData.setWaterint(Integer.parseInt(datas[114], 16));
            inBodyData.setEdema(healthEdema[Integer.parseInt(datas[115], 16)].getText().toString());////浮肿
            inBodyData.setEdemaint(Integer.parseInt(datas[115], 16));
            //营养评估
            inBodyData.setProtein(nutritionProtein[Integer.parseInt(datas[118], 16)].getText().toString());//蛋白质
            inBodyData.setProteinint(Integer.parseInt(datas[118], 16));
            inBodyData.setInorganicsalt(nutritionSalt[Integer.parseInt(datas[119], 16)].getText().toString());//无机盐
            inBodyData.setSaltint(Integer.parseInt(datas[119], 16));
            inBodyData.setFat(nutritionFat[Integer.parseInt(datas[120], 16)].getText().toString());//体脂肪
            inBodyData.setFatint(Integer.parseInt(datas[120], 16));
            //肌肉评估
            inBodyData.setUpbalanced(muscleUp[Integer.parseInt(datas[121], 16)].getText().toString());//上肢均衡
            inBodyData.setUpint(Integer.parseInt(datas[121], 16));
            inBodyData.setDownbalanced(muscleDown[Integer.parseInt(datas[122], 16)].getText().toString());//下肢均衡
            inBodyData.setDownint(Integer.parseInt(datas[122], 16));
            //体型判断
            inBodyData.setShape(shapeJudgment[Integer.parseInt(datas[117], 16)].getText().toString());//浮肿分析值
            inBodyData.setShapeint(Integer.parseInt(datas[117], 16));
            //生物电阻抗
            inBodyData.setRa0(Float.parseFloat(biologyImpedance[0].getText().toString()));//RA 5kHz
            inBodyData.setRa1(Float.parseFloat(biologyImpedance[1].getText().toString()));//RA 50kHz
            inBodyData.setRa2(Float.parseFloat(biologyImpedance[2].getText().toString()));//RA 250kHz
            inBodyData.setLa0(Float.parseFloat(biologyImpedance[3].getText().toString()));//LA 5kHz
            inBodyData.setLa1(Float.parseFloat(biologyImpedance[4].getText().toString()));//LA 50kHz
            inBodyData.setLa2(Float.parseFloat(biologyImpedance[5].getText().toString()));//LA 250kHz
            inBodyData.setTr0(Float.parseFloat(biologyImpedance[6].getText().toString()));//TR 5kHz
            inBodyData.setTr1(Float.parseFloat(biologyImpedance[7].getText().toString()));//TR 50kHz
            inBodyData.setTr2(Float.parseFloat(biologyImpedance[8].getText().toString()));//TR 250kHz
            inBodyData.setRl0(Float.parseFloat(biologyImpedance[9].getText().toString()));//RL 5kHz
            inBodyData.setRl1(Float.parseFloat(biologyImpedance[10].getText().toString()));//RL 50kHz
            inBodyData.setRl2(Float.parseFloat(biologyImpedance[11].getText().toString()));//RL 250kHz
            inBodyData.setLl0(Float.parseFloat(biologyImpedance[12].getText().toString()));//LL 5kHz
            inBodyData.setLl1(Float.parseFloat(biologyImpedance[13].getText().toString()));//LL 50kHz
            inBodyData.setLl2(Float.parseFloat(biologyImpedance[14].getText().toString()));//LL 250kHz
            //浮肿分析
            inBodyData.setEdemavalue(Float.parseFloat(edemaValue.getText().toString()));//浮肿分析值
            inBodyData.setEdemarange(edemaProgess.getProgress());//浮肿分析结果进度条
            //总得分
            inBodyData.setScore(Integer.parseInt(datas[116], 16));
            if (!is_save) {
                if (inBodyData.save()) {
                    List<User> us = DataSupport.select("id", "image_path").where("user_number = ? ", user.getUser_number()).find(User.class);
                    if (us.size() == 0) {
                        user.save();
                    }
                    message.obj = "保存成功！";
                    handler.sendMessage(message);
                    is_save = true;
                } else {
                    message.obj = "保存失败！";
                    handler.sendMessage(message);
                }
            } else {
                message.obj = "数据已经保存！";
                handler.sendMessage(message);
            }
        } else {
            message.obj = "user 为空！";
            handler.sendMessage(message);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void changeData() {
        inBodyData = new InBodyData();
        inBodyData.setAllDate();
        //身体成分分析
        testHeight.setText(FormatString.formatResult(datas[4], datas[5], 1));//身高
        testWeight.setText(FormatString.formatResult(datas[6], datas[7], 1));//体重
        body[0].setText(FormatString.formatResult(datas[12], datas[13], 1));//细胞内液
        body[1].setText(FormatString.formatResult(datas[18], datas[19], 1));//细胞外液
        body[2].setText(FormatString.formatResult(datas[26], datas[27], 1));//蛋白质
        body[3].setText(FormatString.formatResult(datas[32], datas[33], 2));//无机盐
        body[4].setText(FormatString.formatResult(datas[48], datas[49], 1));//体脂肪
        body[5].setText(FormatString.formatResult(datas[24], datas[25], 1));//总水分
        body[6].setText(FormatString.formatResult(datas[38], datas[39], 1));//肌肉量
        body[7].setText(FormatString.formatResult(datas[40], datas[41], 1));//去脂体重
        body[8].setText(FormatString.formatResult(datas[6], datas[7], 1));//总体重
        body[9].setText(FormatString.formatResult(datas[16], datas[17], 1) + "-" +
                FormatString.formatResult(datas[14], datas[15], 1));//细胞内液正常范围
        body[10].setText(FormatString.formatResult(datas[22], datas[23], 1) + "-" +
                FormatString.formatResult(datas[20], datas[21], 1)
        );//细胞外液正常范围
        body[11].setText(FormatString.formatResult(datas[30], datas[31], 1) + "-" +
                FormatString.formatResult(datas[28], datas[29], 1));//蛋白质正常范围
        body[12].setText(FormatString.formatResult(datas[36], datas[37], 1) + "-" +
                FormatString.formatResult(datas[34], datas[35], 1));//无机盐正常范围
        body[13].setText(FormatString.formatResult(datas[52], datas[53], 1) + "-" +
                FormatString.formatResult(datas[50], datas[51], 1));//体脂肪正常范围
        //肌肉脂肪分析
        muscleFat[0].setText(FormatString.formatResult(datas[6], datas[7], 1));//体重
        muscleFat[1].setText(FormatString.formatResult(datas[42], datas[43], 1));//骨骼肌
        muscleFat[2].setText(FormatString.formatResult(datas[48], datas[49], 1));//体脂肪
        muscleFat[6].setText(FormatString.formatResult(datas[10], datas[11], 1) + "-" +
                FormatString.formatResult(datas[8], datas[9], 1));//体重正常范围
        muscleFat[7].setText(FormatString.formatResult(datas[46], datas[47], 1) + "-" +
                FormatString.formatResult(datas[44], datas[45], 1));//骨骼肌正常范围
        muscleFat[8].setText(FormatString.formatResult(datas[52], datas[53], 1) + "-" +
                FormatString.formatResult(datas[50], datas[51], 1));//体脂肪正常范围
        String[] progress0 = FormatString.formatStandard(FormatString.formatResult(datas[10], datas[11], 1),
                FormatString.formatResult(datas[8], datas[9], 1), muscleFat[0].getText().toString(), 0);
        String[] progress1 = FormatString.formatStandard(FormatString.formatResult(datas[46], datas[47], 1),
                FormatString.formatResult(datas[44], datas[45], 1), muscleFat[1].getText().toString(), 1);
        String[] progress2 = FormatString.formatStandard(FormatString.formatResult(datas[52], datas[53], 1),
                FormatString.formatResult(datas[50], datas[51], 1), muscleFat[2].getText().toString(), 2);
        muscleFat[3].setText(progress0[0]);
        muscleFat[4].setText(progress1[0]);
        muscleFat[5].setText(progress2[0]);
        muscleProgress[0].setProgress(Integer.parseInt(progress0[1]));//体重进度条
        muscleProgress[1].setProgress(Integer.parseInt(progress1[1]));//骨骼肌进度条
        muscleProgress[2].setProgress(Integer.parseInt(progress2[1]));//体脂肪进度条
        // 肥胖分析
        fatAnalysis[0].setText(FormatString.formatResult(datas[54], datas[55], 1));//BMI
        fatAnalysis[1].setText(FormatString.formatResult(datas[60], datas[61], 1));//体脂率
        fatAnalysis[2].setText(FormatString.formatResult(datas[66], datas[67], 2));//腰臀比
        fatAnalysis[6].setText(FormatString.formatResult(datas[58], datas[59], 1) + "-" +
                FormatString.formatResult(datas[56], datas[57], 1));//BMI正常范围
        fatAnalysis[7].setText(FormatString.formatResult(datas[64], datas[65], 1) + "-" +
                FormatString.formatResult(datas[62], datas[63], 1));//体脂率正常范围
        fatAnalysis[8].setText(FormatString.formatResult(datas[70], datas[71], 2) + "-" +
                FormatString.formatResult(datas[68], datas[69], 2));//腰臀比正常范围
        String[] progress3 = FormatString.formatStandard(FormatString.formatResult(datas[58], datas[59], 1),
                FormatString.formatResult(datas[56], datas[57], 1), fatAnalysis[0].getText().toString(), 0);
        String[] progress4 = FormatString.formatStandard(FormatString.formatResult(datas[64], datas[65], 1),
                FormatString.formatResult(datas[62], datas[63], 1), fatAnalysis[1].getText().toString(), 1);
        String[] progress5 = FormatString.formatStandard(FormatString.formatResult(datas[71], datas[70], 1),
                FormatString.formatResult(datas[68], datas[69], 1), fatAnalysis[2].getText().toString(), 2);
        fatAnalysis[3].setText(progress3[0]);
        fatAnalysis[4].setText(progress4[0]);
        fatAnalysis[5].setText(progress5[0]);
        fatProgress[0].setProgress(Integer.parseInt(progress3[1]));//BMI进度条
        fatProgress[1].setProgress(Integer.parseInt(progress4[1]));//体脂率进度条
        fatProgress[2].setProgress(Integer.parseInt(progress5[1]));//腰臀比进度条
        //节段肌肉分析
        segmentalMuscle[0].setText(FormatString.formatResult(datas[72], datas[73], 2));//左臂
        segmentalMuscle[1].setText(FormatString.formatResult(datas[78], datas[79], 2));//右臂
        segmentalMuscle[2].setText(FormatString.formatResult(datas[84], datas[85], 2));//躯干
        segmentalMuscle[3].setText(FormatString.formatResult(datas[90], datas[91], 2));//左腿
        segmentalMuscle[4].setText(FormatString.formatResult(datas[96], datas[97], 2));//右腿
        segmentalMuscle[10].setText(FormatString.formatResult(datas[76], datas[77], 2) + "-" +
                FormatString.formatResult(datas[74], datas[75], 2));//左臂正常范围
        segmentalMuscle[11].setText(FormatString.formatResult(datas[82], datas[83], 2) + "-" +
                FormatString.formatResult(datas[80], datas[81], 2));//右臂正常范围
        segmentalMuscle[12].setText(FormatString.formatResult(datas[88], datas[89], 2) + "-" +
                FormatString.formatResult(datas[86], datas[87], 2));//躯干正常范围
        segmentalMuscle[13].setText(FormatString.formatResult(datas[94], datas[95], 2) + "-" +
                FormatString.formatResult(datas[92], datas[93], 2));//左腿正常范围
        segmentalMuscle[14].setText(FormatString.formatResult(datas[100], datas[101], 2) + "-" +
                FormatString.formatResult(datas[98], datas[99], 2));//右腿正常范围
        String[] progress6 = FormatString.formatStandard(FormatString.formatResult(datas[76], datas[77], 2),
                FormatString.formatResult(datas[74], datas[75], 2), segmentalMuscle[0].getText().toString(), 0);
        String[] progress7 = FormatString.formatStandard(FormatString.formatResult(datas[82], datas[83], 2),
                FormatString.formatResult(datas[80], datas[81], 2), segmentalMuscle[1].getText().toString(), 1);
        String[] progress8 = FormatString.formatStandard(FormatString.formatResult(datas[88], datas[89], 2),
                FormatString.formatResult(datas[86], datas[87], 2), segmentalMuscle[2].getText().toString(), 2);
        String[] progress9 = FormatString.formatStandard(FormatString.formatResult(datas[94], datas[95], 2),
                FormatString.formatResult(datas[92], datas[93], 2), segmentalMuscle[3].getText().toString(), 3);
        String[] progress10 = FormatString.formatStandard(FormatString.formatResult(datas[100], datas[101], 2),
                FormatString.formatResult(datas[98], datas[99], 2), segmentalMuscle[4].getText().toString(), 4);
        segmentalMuscle[5].setText(progress6[0]);
        segmentalMuscle[6].setText(progress7[0]);
        segmentalMuscle[7].setText(progress8[0]);
        segmentalMuscle[8].setText(progress9[0]);
        segmentalMuscle[9].setText(progress10[0]);
        segmentalProgress[0].setProgress(Integer.parseInt(progress6[1]));//左臂进度条
        segmentalProgress[1].setProgress(Integer.parseInt(progress7[1]));//右臂进度条
        segmentalProgress[2].setProgress(Integer.parseInt(progress8[1]));//躯干进度条
        segmentalProgress[3].setProgress(Integer.parseInt(progress9[1]));//左腿进度条
        segmentalProgress[4].setProgress(Integer.parseInt(progress10[1]));//右腿进度条
        //体重身高分析
        weightHeight[0].setText(FormatString.formatResult(datas[102], datas[103], 1));//标准体重
        weightHeight[1].setText(FormatString.formatResult(datas[106], datas[107], 1));//标准身高
        weightHeight[2].setText(FormatString.formatResult(datas[108], datas[109], 1));//肌肉控制
        weightHeight[3].setText(FormatString.formatResult(datas[104], datas[105], 1));//体重控制
        weightHeight[4].setText(FormatString.formatResult(datas[110], datas[111], 1));//脂肪控制
        weightHeight[5].setText(FormatString.formatResult(datas[112], datas[113], 3));//基础代谢量
        //生物电阻抗 biologyImpedance
        biologyImpedance[0].setText(FormatString.formatResult(datas[125], datas[126], 1));//RA 5kHz
        biologyImpedance[1].setText(FormatString.formatResult(datas[135], datas[136], 1));//RA 50kHz
        biologyImpedance[2].setText(FormatString.formatResult(datas[145], datas[146], 1));//RA 2505kHz
        biologyImpedance[3].setText(FormatString.formatResult(datas[127], datas[128], 1));//LA 5kHz
        biologyImpedance[4].setText(FormatString.formatResult(datas[137], datas[138], 1));//LA 50kHz
        biologyImpedance[5].setText(FormatString.formatResult(datas[147], datas[148], 1));//LA 250kHz
        biologyImpedance[6].setText(FormatString.formatResult(datas[129], datas[130], 1));//TR 5kHz
        biologyImpedance[7].setText(FormatString.formatResult(datas[139], datas[140], 1));//TR 50kHz
        biologyImpedance[8].setText(FormatString.formatResult(datas[149], datas[150], 1));//TR 250kHz
        biologyImpedance[9].setText(FormatString.formatResult(datas[131], datas[132], 1));//RL 5kHz
        biologyImpedance[10].setText(FormatString.formatResult(datas[141], datas[142], 1));//RL 50kHz
        biologyImpedance[11].setText(FormatString.formatResult(datas[151], datas[152], 1));//RL 250kHz
        biologyImpedance[12].setText(FormatString.formatResult(datas[133], datas[134], 1));//LL 5kHz
        biologyImpedance[13].setText(FormatString.formatResult(datas[143], datas[144], 1));//LL 50kHz
        biologyImpedance[14].setText(FormatString.formatResult(datas[153], datas[154], 1));//LL 250kHz
        //身体x总评分 score
        score.setText(String.valueOf(Integer.parseInt(datas[116], 16)));
        //健康诊断  身体水分 healthWater 健康诊断浮肿 healthEdema
        Drawable drawable1 = getResources().getDrawable(R.drawable.check_down, null);
        drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
        healthWater[Integer.parseInt(datas[114], 16)].setCompoundDrawables(drawable1, null, null, null);
        healthEdema[Integer.parseInt(datas[115], 16)].setCompoundDrawables(drawable1, null, null, null);
        //营养评估蛋白质 nutritionProtein 营养评估无机盐 nutritionSalt 营养评估体脂肪 nutritionFat;
        nutritionProtein[Integer.parseInt(datas[118], 16)].setCompoundDrawables(drawable1, null, null, null);
        nutritionSalt[Integer.parseInt(datas[119], 16)].setCompoundDrawables(drawable1, null, null, null);
        nutritionFat[Integer.parseInt(datas[120], 16)].setCompoundDrawables(drawable1, null, null, null);
        //肌肉评估上肢 muscleUp 肌肉评估下肢 muscleDown;
        muscleUp[Integer.parseInt(datas[121], 16)].setCompoundDrawables(drawable1, null, null, null);
        muscleDown[Integer.parseInt(datas[122], 16)].setCompoundDrawables(drawable1, null, null, null);
        //体型判断 shapeJudgment;
        Drawable drawable2 = getResources().getDrawable(R.drawable.current, null);
        drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
        shapeJudgment[Integer.parseInt(datas[117], 16)].setCompoundDrawables(drawable2, null, null, null);
        //浮肿分析值 edemaValue 浮肿分析进度条 edemaProgess
        edemaValue.setText(FormatString.formatResult(datas[123], datas[124], 1));
        int value1 = Integer.parseInt(datas[115], 16);
        int value2 = 13;
        if (value1 == 0) {
            edemaProgess.setProgress(value2 + 25);
        } else if (value1 == 1) {
            edemaProgess.setProgress(value2 * 3 + 25);
        } else {
            edemaProgess.setProgress(value2 * 5 + 25);
        }
    }

    private Map<String, String> getMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("score", score.getText().toString());//得分
        map.put("usernumber", usernumber.getText().toString());//编号
        map.put("username", username.getText().toString());//姓名
        map.put("sex", sex.getText().toString());//性别
        map.put("age", String.valueOf(user.getAge()));//年龄
        map.put("testHeight", testHeight.getText().toString());//身高
        map.put("testWeight", testWeight.getText().toString());//体重
        map.put("strDate", inBodyData.getStrDate());//测试日期
        map.put("inliquid", body[0].getText().toString());//细胞内液
        map.put("outliquid", body[1].getText().toString());//细胞外液
        map.put("totalprotein", body[2].getText().toString());//蛋白质
        map.put("totalinorganicsalt", body[3].getText().toString());//无机盐
        map.put("bodyfat", body[4].getText().toString());//体脂肪
        map.put("totalwater", body[5].getText().toString());//总水分
        map.put("muscle", body[6].getText().toString());//肌肉量
        map.put("fatfree", body[7].getText().toString());//去脂体重
        map.put("weight", body[8].getText().toString());//总体重
        map.put("normalrange0", body[9].getText().toString());//细胞内液正常范围
        map.put("normalrange1", body[10].getText().toString());//细胞外液正常范围
        map.put("normalrange2", body[11].getText().toString());//蛋白质正常范围
        map.put("normalrange3", body[12].getText().toString());//无机盐正常范围
        map.put("normalrange4", body[13].getText().toString());//体脂肪正常范围
        map.put("bones", muscleFat[1].getText().toString());//骨骼肌
        map.put("musclefat", muscleFat[2].getText().toString());//体脂肪
        map.put("normalrange5", muscleFat[6].getText().toString());//体重正常范围
        map.put("normalrange6", muscleFat[7].getText().toString());//骨骼肌正常范围
        map.put("normalrange7", muscleFat[8].getText().toString());//体脂肪正常范围
        map.put("weightrange", muscleFat[3].getText().toString());//体重进度条
        map.put("boneserange", muscleFat[4].getText().toString());//骨骼肌进度条
        map.put("fatrange", muscleFat[5].getText().toString());//体脂肪进度条
        map.put("bmi", fatAnalysis[0].getText().toString());//BMI
        map.put("fatrate", fatAnalysis[1].getText().toString());//体脂率
        map.put("waistrate", fatAnalysis[2].getText().toString());//腰臀比
        map.put("normalrange8", fatAnalysis[6].getText().toString());//BMI正常范围
        map.put("normalrange9", fatAnalysis[7].getText().toString());//体脂率正常范围
        map.put("normalrange10", fatAnalysis[8].getText().toString());//腰臀比正常范围
        map.put("bmirange", fatAnalysis[3].getText().toString());//BMI进度条
        map.put("fatraterange", fatAnalysis[4].getText().toString());//体脂率进度条
        map.put("waistraterange", fatAnalysis[5].getText().toString());//腰臀比进度条
        map.put("leftarm", segmentalMuscle[0].getText().toString());//左臂
        map.put("rightarm", segmentalMuscle[1].getText().toString());//右臂
        map.put("trunk", segmentalMuscle[2].getText().toString());//躯干
        map.put("leftleg", segmentalMuscle[3].getText().toString());//左腿
        map.put("rightleg", segmentalMuscle[4].getText().toString());//右腿
        map.put("normalrange11", segmentalMuscle[10].getText().toString());//左臂正常范围
        map.put("normalrange12", segmentalMuscle[11].getText().toString());//右臂正常范围
        map.put("normalrange13", segmentalMuscle[12].getText().toString());//躯干正常范围
        map.put("normalrange14", segmentalMuscle[13].getText().toString());//左腿正常范围
        map.put("normalrange15", segmentalMuscle[14].getText().toString());//右腿正常范围
        map.put("leftarmrange", segmentalMuscle[5].getText().toString());//左臂进度条
        map.put("rightarmrange", segmentalMuscle[6].getText().toString());//右臂进度条
        map.put("trunkrange", segmentalMuscle[7].getText().toString());//躯干进度条
        map.put("leftlegrange", segmentalMuscle[8].getText().toString());//左腿进度条
        map.put("rightlegrange", segmentalMuscle[9].getText().toString());//右腿进度条
        map.put("standardweight", String.valueOf(weightHeight[0].getText().toString()));//标准体重
        map.put("standardheight", String.valueOf(weightHeight[1].getText().toString()));//标准身高
        map.put("musclecontrol", String.valueOf(weightHeight[2].getText().toString()));//肌肉控制
        map.put("weightcontrol", String.valueOf(weightHeight[3].getText().toString()));//体重控制
        map.put("fatcontrol", String.valueOf(weightHeight[4].getText().toString()));//脂肪控制
        map.put("basalmetabolism", String.valueOf(weightHeight[5].getText().toString()));//基础代谢量
        map.put("ra0", String.valueOf(biologyImpedance[0].getText().toString()));//RA 5kHz
        map.put("ra1", String.valueOf(biologyImpedance[1].getText().toString()));//RA 50kHz
        map.put("ra2", String.valueOf(biologyImpedance[2].getText().toString()));//RA 2505kHz
        map.put("la0", String.valueOf(biologyImpedance[3].getText().toString()));//LA 5kHz
        map.put("la1", String.valueOf(biologyImpedance[4].getText().toString()));//LA 50kHz
        map.put("la2", String.valueOf(biologyImpedance[5].getText().toString()));//LA 250kHz
        map.put("tr0", String.valueOf(biologyImpedance[6].getText().toString()));//TR 5kHz
        map.put("tr1", String.valueOf(biologyImpedance[7].getText().toString()));//TR 50kHz
        map.put("tr2", String.valueOf(biologyImpedance[8].getText().toString()));//TR 250kHz
        map.put("rl0", String.valueOf(biologyImpedance[9].getText().toString()));//RL 5kHz
        map.put("rl1", String.valueOf(biologyImpedance[10].getText().toString()));//RL 50kHz
        map.put("rl2", String.valueOf(biologyImpedance[11].getText().toString()));//RL 250kHz
        map.put("ll0", String.valueOf(biologyImpedance[12].getText().toString()));//LL 5kHz
        map.put("ll1", String.valueOf(biologyImpedance[13].getText().toString()));//LL 50kHz
        map.put("ll2", String.valueOf(biologyImpedance[14].getText().toString()));//LL 250kH
        map.put("healthWater", String.valueOf(Integer.parseInt(datas[114], 16)));//健康诊断 身体水分
        map.put("healthEdema", String.valueOf(Integer.parseInt(datas[115], 16)));//健康诊断 浮肿
        map.put("nutritionProtein", String.valueOf(Integer.parseInt(datas[118], 16)));//营养评估 蛋白质
        map.put("nutritionSalt", String.valueOf(Integer.parseInt(datas[119], 16)));//营养评估 无机盐
        map.put("nutritionFat", String.valueOf(Integer.parseInt(datas[120], 16)));//营养评估 体脂肪
        map.put("muscleUp", String.valueOf(Integer.parseInt(datas[121], 16)));//肌肉评估 上肢
        map.put("muscleDown", String.valueOf(Integer.parseInt(datas[122], 16)));//肌肉评估 下肢
        map.put("shapeJudgment", String.valueOf(Integer.parseInt(datas[117], 16)));//体型判断
        map.put("edemaValue", String.valueOf(edemaValue.getText()));//浮肿分析值
        map.put("edemaProgess", String.valueOf(String.valueOf(edemaProgess.getProgress())));//浮肿分析进度条
        return map;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    class MyThread extends Thread {
        //继承Thread类，并改写其run方法
        public void run() {
            DrawTestReport drawTestReport = new DrawTestReport(InBodyTestReportActivity.this, getMap());
            drawTestReport.draw(new Canvas());
            refreshFile();
        }
    }
}
