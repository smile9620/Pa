package com.bg.bgpad;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bg.model.InBodyData;
import com.bg.model.User;
import com.bg.utils.DrawTestReport;
import com.bg.utils.SetTitle;

import org.litepal.crud.DataSupport;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class TestReportActivity extends BaseActivity implements SetTitle.OnTitleBtClickListener {

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
    private InBodyData inBodyData;
    private User user;
    private MyHandler handler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private WeakReference<TestReportActivity> mActivity;

        public MyHandler(TestReportActivity activity) {
            mActivity = new WeakReference<TestReportActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TestReportActivity act = mActivity.get();
            if (act != null) {
                switch (msg.what) {
                    case 0:
                        act.changeData();
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_body_test_report);
        ButterKnife.bind(this);
        new SetTitle(this, view, new boolean[]{true, false},
                "测试报告", new int[]{R.drawable.back_bt, R.drawable.ble_bt});
        Intent intent = getIntent();
        final String data_id = intent.getStringExtra("data_id");
        inBodyData = DataSupport.where("id = ?", data_id).find(InBodyData.class).get(0);
        handler.sendEmptyMessage(0);
        save.setVisibility(View.GONE);
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences printShare = getSharedPreferences(TestReportActivity.this.getString(R.string.printshare), MODE_PRIVATE);
                Map<String, ?> printmap = printShare.getAll();
                if (printShare != null && printmap.size() != 0) {
                    if (printmap.get("print").equals("wifi")) {
                        Intent intent = new Intent();
                        ComponentName componentName = new ComponentName("com.lenovo.vop", "com.lenovo.vop.StartActivity");
                        if (componentName != null) {
                            intent.setComponent(componentName);
                            startActivity(intent);
                            new MyThread().start();
                        } else {
                            showToast("请安装打印机！");
                        }
                    } else {
                        showToast("usb 打印");
                    }
                } else {
                    showToast("usb 打印");  //默认情况下为USB打印
                }
            }
        });
    }

    @Override
    public void leftBt(ImageButton left) {
        finish();
    }

    @Override
    public void rightBt(ImageButton right) {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void changeData() {
        //身体总评分 score
        score.setText(String.valueOf(inBodyData.getScore()));
        //身体成分分析
        user = inBodyData.getUser();
        usernumber.setText(user.getUser_number());
        username.setText(user.getUser_name());
        sex.setText((user.getSex() == 0 ? "男" : "女"));
        testHeight.setText(String.valueOf(inBodyData.getHeight()));//身高
        testWeight.setText(String.valueOf(inBodyData.getWeight()));//体重
        body[0].setText(String.valueOf(inBodyData.getInliquid()));//细胞内液
        body[1].setText(String.valueOf(inBodyData.getOutliquid()));//细胞外液
        body[2].setText(String.valueOf(inBodyData.getTotalprotein()));//蛋白质
        body[3].setText(String.valueOf(inBodyData.getTotalinorganicsalt()));//无机盐
        body[4].setText(String.valueOf(inBodyData.getBodyfat()));//体脂肪
        body[5].setText(String.valueOf(inBodyData.getTotalwater()));//总水分
        body[6].setText(String.valueOf(inBodyData.getMuscle()));//肌肉量
        body[7].setText(String.valueOf(inBodyData.getFatfree()));//去脂体重
        body[8].setText(String.valueOf(inBodyData.getWeight()));//总体重
        body[9].setText(inBodyData.getNormalrange0());//细胞内液正常范围
        body[10].setText(inBodyData.getNormalrange1());//细胞外液正常范围
        body[11].setText(inBodyData.getNormalrange2());//蛋白质正常范围
        body[12].setText(inBodyData.getNormalrange3());//无机盐正常范围
        body[13].setText(inBodyData.getNormalrange4());//体脂肪正常范围
        //肌肉脂肪分析
        muscleFat[0].setText(String.valueOf(inBodyData.getWeight()));//体重
        muscleFat[1].setText(String.valueOf(inBodyData.getBones()));//骨骼肌
        muscleFat[2].setText(String.valueOf(inBodyData.getMusclefat()));//体脂肪
        muscleFat[6].setText(inBodyData.getNormalrange5());//体重正常范围
        muscleFat[7].setText(inBodyData.getNormalrange6());//骨骼肌正常范围
        muscleFat[8].setText(inBodyData.getNormalrange7());//体脂肪正常范围
        muscleFat[3].setText(inBodyData.getWeightrange());
        muscleFat[4].setText(inBodyData.getBonesrange());
        muscleFat[5].setText(inBodyData.getFatrange());
        muscleProgress[0].setProgress(inBodyData.getWeightvalue());//体重进度条
        muscleProgress[1].setProgress(inBodyData.getBonesevalue());//骨骼肌进度条
        muscleProgress[2].setProgress(inBodyData.getFatvalue());//体脂肪进度条
        // 肥胖分析
        fatAnalysis[0].setText(String.valueOf(inBodyData.getBmi()));//BMI
        fatAnalysis[1].setText(String.valueOf(inBodyData.getFatrate()));//体脂率
        fatAnalysis[2].setText(String.valueOf(inBodyData.getWaistrate()));//腰臀比
        fatAnalysis[6].setText(inBodyData.getNormalrange8());//BMI正常范围
        fatAnalysis[7].setText(inBodyData.getNormalrange9());//体脂率正常范围
        fatAnalysis[8].setText(inBodyData.getNormalrange10());//腰臀比正常范围
        fatAnalysis[3].setText(inBodyData.getBmirange());
        fatAnalysis[4].setText(inBodyData.getFatraterange());
        fatAnalysis[5].setText(inBodyData.getWaistraterange());
        fatProgress[0].setProgress(inBodyData.getBmivalue());//BMI进度条
        fatProgress[1].setProgress(inBodyData.getFatratevalue());//体脂率进度条
        fatProgress[2].setProgress(inBodyData.getWaistratevalue());//腰臀比进度条
        //节段肌肉分析
        segmentalMuscle[0].setText(String.valueOf(inBodyData.getLeftarm()));//左臂
        segmentalMuscle[1].setText(String.valueOf(inBodyData.getRightarm()));//右臂
        segmentalMuscle[2].setText(String.valueOf(inBodyData.getTrunk()));//躯干
        segmentalMuscle[3].setText(String.valueOf(inBodyData.getLeftleg()));//左腿
        segmentalMuscle[4].setText(String.valueOf(inBodyData.getRightleg()));//右腿
        segmentalMuscle[10].setText(inBodyData.getNormalrange11());//左臂正常范围
        segmentalMuscle[11].setText(inBodyData.getNormalrange12());//右臂正常范围
        segmentalMuscle[12].setText(inBodyData.getNormalrange13());//躯干正常范围
        segmentalMuscle[13].setText(inBodyData.getNormalrange14());//左腿正常范围
        segmentalMuscle[14].setText(inBodyData.getNormalrange15());//右腿正常范围
        segmentalMuscle[5].setText(inBodyData.getLeftarmrange());
        segmentalMuscle[6].setText(inBodyData.getRightarmrange());
        segmentalMuscle[7].setText(inBodyData.getTrunkrange());
        segmentalMuscle[8].setText(inBodyData.getLeftlegrange());
        segmentalMuscle[9].setText(inBodyData.getRightlegrange());
        segmentalProgress[0].setProgress(inBodyData.getLeftarmvalue());//左臂进度条
        segmentalProgress[1].setProgress(inBodyData.getRightarmvalue());//右臂进度条
        segmentalProgress[2].setProgress(inBodyData.getTrunkvalue());//躯干进度条
        segmentalProgress[3].setProgress(inBodyData.getLeftlegvalue());//左腿进度条
        segmentalProgress[4].setProgress(inBodyData.getRightlegvalue());//右腿进度条
        //体重身高分析
        weightHeight[0].setText(String.valueOf(inBodyData.getStandardweight()));//标准体重
        weightHeight[1].setText(String.valueOf(inBodyData.getStandardheight()));//标准身高
        weightHeight[2].setText(String.valueOf(inBodyData.getMusclecontrol()));//肌肉控制
        weightHeight[3].setText(String.valueOf(inBodyData.getWeightcontrol()));//体重控制
        weightHeight[4].setText(String.valueOf(inBodyData.getFatcontrol()));//脂肪控制
        weightHeight[5].setText(String.valueOf(inBodyData.getBasalmetabolism()));//基础代谢量
        //生物电阻抗 biologyImpedance
        biologyImpedance[0].setText(String.valueOf(inBodyData.getRa0()));//RA 5kHz
        biologyImpedance[1].setText(String.valueOf(inBodyData.getRa1()));//RA 50kHz
        biologyImpedance[2].setText(String.valueOf(inBodyData.getRa2()));//RA 2505kHz
        biologyImpedance[3].setText(String.valueOf(inBodyData.getLa0()));//LA 5kHz
        biologyImpedance[4].setText(String.valueOf(inBodyData.getLa1()));//LA 50kHz
        biologyImpedance[5].setText(String.valueOf(inBodyData.getLa2()));//LA 250kHz
        biologyImpedance[6].setText(String.valueOf(inBodyData.getTr0()));//TR 5kHz
        biologyImpedance[7].setText(String.valueOf(inBodyData.getTr1()));//TR 50kHz
        biologyImpedance[8].setText(String.valueOf(inBodyData.getTr2()));//TR 250kHz
        biologyImpedance[9].setText(String.valueOf(inBodyData.getRl0()));//RL 5kHz
        biologyImpedance[10].setText(String.valueOf(inBodyData.getRl1()));//RL 50kHz
        biologyImpedance[11].setText(String.valueOf(inBodyData.getRl2()));//RL 250kHz
        biologyImpedance[12].setText(String.valueOf(inBodyData.getLl0()));//LL 5kHz
        biologyImpedance[13].setText(String.valueOf(inBodyData.getLl1()));//LL 50kHz
        biologyImpedance[14].setText(String.valueOf(inBodyData.getLl2()));//LL 250kH
        //健康诊断  身体水分 healthWater 健康诊断浮肿 healthEdema
        Drawable drawable1 = getResources().getDrawable(R.drawable.check_down, null);
        drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
        healthWater[inBodyData.getWaterint()].setCompoundDrawables(drawable1, null, null, null);
        healthEdema[inBodyData.getEdemaint()].setCompoundDrawables(drawable1, null, null, null);
        //营养评估蛋白质 nutritionProtein 营养评估无机盐 nutritionSalt 营养评估体脂肪 nutritionFat;
        nutritionProtein[inBodyData.getProteinint()].setCompoundDrawables(drawable1, null, null, null);
        nutritionSalt[inBodyData.getSaltint()].setCompoundDrawables(drawable1, null, null, null);
        nutritionFat[inBodyData.getFatint()].setCompoundDrawables(drawable1, null, null, null);
        //肌肉评估上肢 muscleUp 肌肉评估下肢 muscleDown;
        muscleUp[inBodyData.getUpint()].setCompoundDrawables(drawable1, null, null, null);
        muscleDown[inBodyData.getDownint()].setCompoundDrawables(drawable1, null, null, null);
        //体型判断 shapeJudgment;
        Drawable drawable2 = getResources().getDrawable(R.drawable.current, null);
        drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
        shapeJudgment[inBodyData.getShapeint()].setCompoundDrawables(drawable2, null, null, null);
        //浮肿分析值 edemaValue 浮肿分析进度条 edemaProgess
        edemaValue.setText(String.valueOf(inBodyData.getEdemavalue()));
        edemaProgess.setProgress(inBodyData.getEdemarange());
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
        map.put("healthWater", String.valueOf(inBodyData.getWaterint()));//健康诊断 身体水分
        map.put("healthEdema", String.valueOf(inBodyData.getEdemaint()));//健康诊断 浮肿
        map.put("nutritionProtein", String.valueOf(inBodyData.getProteinint()));//营养评估 蛋白质
        map.put("nutritionSalt", String.valueOf(inBodyData.getSaltint()));//营养评估 无机盐
        map.put("nutritionFat", String.valueOf(inBodyData.getFatint()));//营养评估 体脂肪
        map.put("muscleUp", String.valueOf(inBodyData.getUpint()));//肌肉评估 上肢
        map.put("muscleDown", String.valueOf(inBodyData.getDownint()));//肌肉评估 下肢
        map.put("shapeJudgment", String.valueOf(inBodyData.getShapeint()));//体型判断
        map.put("edemaValue", String.valueOf(inBodyData.getEdemavalue()));//浮肿分析值
        map.put("edemaProgess", String.valueOf(inBodyData.getEdemarange()));//浮肿分析进度条
        return map;
    }

    class MyThread extends Thread {
        //继承Thread类，并改写其run方法
        public void run() {
            DrawTestReport drawTestReport = new DrawTestReport(TestReportActivity.this, getMap());
            drawTestReport.draw(new Canvas());
            refreshFile();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
