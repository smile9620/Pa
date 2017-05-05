package com.bg.bgpad;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.bg.utils.SetTitle;

import org.litepal.crud.DataSupport;

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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            changeData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_body_test_report);
        ButterKnife.bind(this);
        new SetTitle(this, view, new boolean[]{true, false},
                "测试报告", new int[]{R.drawable.back_bt, R.drawable.ble_bt});
        Intent intent = getIntent();
        String data_id = intent.getStringExtra("data_id");
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
        //身体x总评分 score
        score.setText(inBodyData.getScore() + "");
        //身体成分分析
        User user = inBodyData.getUser();
        usernumber.setText(user.getUser_number());
        username.setText(user.getUser_name());
        sex.setText((user.getSex() == 0 ? "男" : "女"));
        testHeight.setText(inBodyData.getHeight() + "");//身高
        testWeight.setText(inBodyData.getWeight() + "");//体重
        body[0].setText(inBodyData.getInliquid() + "");//细胞内液
        body[1].setText(inBodyData.getOutliquid() + "");//细胞外液
        body[2].setText(inBodyData.getTotalprotein() + "");//蛋白质
        body[3].setText(inBodyData.getTotalinorganicsalt() + "");//无机盐
        body[4].setText(inBodyData.getBodyfat() + "");//体脂肪
        body[5].setText(inBodyData.getTotalwater() + "");//总水分
        body[6].setText(inBodyData.getMuscle() + "");//肌肉量
        body[7].setText(inBodyData.getFatfree() + "");//去脂体重
        body[8].setText(inBodyData.getWeight() + "");//总体重
        body[9].setText(inBodyData.getNormalrange0());//细胞内液正常范围
        body[10].setText(inBodyData.getNormalrange1());//细胞外液正常范围
        body[11].setText(inBodyData.getNormalrange2());//蛋白质正常范围
        body[12].setText(inBodyData.getNormalrange3());//无机盐正常范围
        body[13].setText(inBodyData.getNormalrange4());//体脂肪正常范围
        //肌肉脂肪分析
        muscleFat[0].setText(inBodyData.getWeight() + "");//体重
        muscleFat[1].setText(inBodyData.getBones() + "");//骨骼肌
        muscleFat[2].setText(inBodyData.getMusclefat() + "");//体脂肪
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
        fatAnalysis[0].setText(inBodyData.getBmi() + "");//BMI
        fatAnalysis[1].setText(inBodyData.getFatrate() + "");//体脂率
        fatAnalysis[2].setText(inBodyData.getWaistrate() + "");//腰臀比
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
        segmentalMuscle[0].setText(inBodyData.getLeftarm() + "");//左臂
        segmentalMuscle[1].setText(inBodyData.getRightarm() + "");//右臂
        segmentalMuscle[2].setText(inBodyData.getTrunk() + "");//躯干
        segmentalMuscle[3].setText(inBodyData.getLeftleg() + "");//左腿
        segmentalMuscle[4].setText(inBodyData.getRightleg() + "");//右腿
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
        weightHeight[0].setText(inBodyData.getStandardweight() + "");//标准体重
        weightHeight[1].setText(inBodyData.getStandardheight() + "");//标准身高
        weightHeight[2].setText(inBodyData.getMusclecontrol() + "");//肌肉控制
        weightHeight[3].setText(inBodyData.getWeightcontrol() + "");//体重控制
        weightHeight[4].setText(inBodyData.getFatcontrol() + "");//脂肪控制
        weightHeight[5].setText(inBodyData.getBasalmetabolism() + "");//基础代谢量
        //生物电阻抗 biologyImpedance
        biologyImpedance[0].setText(inBodyData.getRa0() + "");//RA 5kHz
        biologyImpedance[1].setText(inBodyData.getRa1() + "");//RA 50kHz
        biologyImpedance[2].setText(inBodyData.getRa2() + "");//RA 2505kHz
        biologyImpedance[3].setText(inBodyData.getLa0() + "");//LA 5kHz
        biologyImpedance[4].setText(inBodyData.getLa1() + "");//LA 50kHz
        biologyImpedance[5].setText(inBodyData.getLa2() + "");//LA 250kHz
        biologyImpedance[6].setText(inBodyData.getTr0() + "");//TR 5kHz
        biologyImpedance[7].setText(inBodyData.getTr1() + "");//TR 50kHz
        biologyImpedance[8].setText(inBodyData.getTr2() + "");//TR 250kHz
        biologyImpedance[9].setText(inBodyData.getRl0() + "");//RL 5kHz
        biologyImpedance[10].setText(inBodyData.getRl1() + "");//RL 50kHz
        biologyImpedance[11].setText(inBodyData.getRl2() + "");//RL 250kHz
        biologyImpedance[12].setText(inBodyData.getLl0() + "");//LL 5kHz
        biologyImpedance[13].setText(inBodyData.getLl1() + "");//LL 50kHz
        biologyImpedance[14].setText(inBodyData.getLl2() + "");//LL 250kH
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
        edemaValue.setText(inBodyData.getEdemavalue() + "");
        edemaProgess.setProgress(inBodyData.getEdemarange());
    }
}
