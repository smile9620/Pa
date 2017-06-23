package com.bg.model;

import android.database.Cursor;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017-02-14.
 */

public class InBodyData extends DataSupport {
    private long id;
    private Date testDate;  //测试日期，用于排序
    private String strDate; // 用于显示
    private int date;       //用于搜索 20170504
    private float height;   // 身高
    private float weight;   //体重
    private String user_number;        //编号 外键
    protected User user;
    //身体成分分析
    private float inliquid;//细胞内液
    private float outliquid;//细胞外液
    private float totalprotein;//蛋白质
    private float totalinorganicsalt;//无机盐
    private float bodyfat;//身体成分分析体脂肪
    private float totalwater;//总水分
    private float muscle;//肌肉量
    private float fatfree;//去脂体重
    private String normalrange0;//细胞内液正常范围
    private String normalrange1;//细胞外液正常范围
    private String normalrange2;//蛋白质正常范围
    private String normalrange3;//无机盐正常范围
    private String normalrange4;//体脂肪正常范围
    //肌肉脂肪分析
    private float bones;//骨骼肌
    private float musclefat;//肌肉脂肪分析体脂肪
    private String weightrange;//体重分析
    private int weightvalue;//体重分析进度条
    private String bonesrange;//骨骼肌分析
    private int bonesevalue;//骨骼肌分析进度条
    private String fatrange;//体脂肪分析
    private int fatvalue;//体脂肪分析进度条
    private String normalrange5;//体重正常范围
    private String normalrange6;//骨骼肌正常范围
    private String normalrange7;//体脂肪正常范围
    //肥胖分析
    private float bmi;//bmi
    private float fatrate;//体脂率%
    private float waistrate;//腰臀比
    private String bmirange;//bmi分析
    private int bmivalue;//bmi分析进度条
    private String fatraterange;//体脂率%分析
    private int fatratevalue;//体脂率%分析进度条
    private String waistraterange;//腰臀比分析
    private int waistratevalue;//腰臀比分析进度条
    private String normalrange8;//bmi正常范围
    private String normalrange9;//体脂率%正常范围
    private String normalrange10;//腰臀比正常范围
    //段肌肉分析
    private float leftarm;//左臂
    private float rightarm;//右臂
    private float trunk;//躯干
    private float leftleg;//左腿
    private float rightleg;//右腿
    private String leftarmrange;//左臂分析
    private int leftarmvalue;//左臂分析进度条
    private String rightarmrange;//右臂分析
    private int rightarmvalue;//右臂分析进度条
    private String trunkrange;//躯干分析
    private int trunkvalue;//躯干分析进度条
    private String leftlegrange;//左腿分析
    private int leftlegvalue;//左腿分析进度条
    private String rightlegrange;//右腿分析
    private int rightlegvalue;//右腿分析进度条
    private String normalrange11;//左臂正常范围
    private String normalrange12;//右臂正常范围
    private String normalrange13;//躯干正常范围
    private String normalrange14;//左腿正常范围
    private String normalrange15;//右腿正常范围
    //体重身高分析
    private float standardweight;//标准体重
    private float standardheight;//标准身高
    private float musclecontrol;//肌肉控制
    private float weightcontrol;//体重控制
    private float fatcontrol;//脂肪控制
    private float basalmetabolism;//基础代谢量
    //健康诊断
    private String water;//身体水分
    private int waterint;//下标
    private String edema;//浮肿
    private int edemaint;//下标
    //营养评估
    private String protein;//蛋白质
    private int proteinint;//下标
    private String inorganicsalt;//无机盐
    private int saltint;//下标
    private String fat;//体脂肪
    private int fatint;//下标
    //肌肉评估
    private String upbalanced;//上肢均衡
    private int upint;//下标
    private String downbalanced;//下肢均衡
    private int downint;//下标
    //体型判断
    private String shape; //浮肿分析值
    private int shapeint;//下标
    //生物电阻抗
    private float ra0; //RA 5kHz
    private float ra1; //RA 50kHz
    private float ra2; //RA 250kHz
    private float la0; //LA 5kHz
    private float la1; //LA 50kHz
    private float la2; //LA 50kHz
    private float tr0; //TR 5kHz
    private float tr1; //TR 50kHz
    private float tr2; //TR 250kHz
    private float rl0; //RL 5kHz
    private float rl1; //RL 50kHz
    private float rl2; //RL 250kHz
    private float ll0; //LL 5kHz
    private float ll1; //LL 50kHz
    private float ll2; //LL 250kHz
    //浮肿分析
    private float edemavalue; //浮肿分析值
    private int edemarange; //浮肿分析结果进度条
    //总得分
    private int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public User getUser() {
        return DataSupport.where("user_number = ?", user_number).find(User.class).get(0);
    }

    public String getUser_number() {
        return user_number;
    }

    public void setUser_number(String user_number) {
        this.user_number = user_number;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTestdate() {
        return testDate;
    }

    public void setTestdate(Date testDate) {
        this.testDate = testDate;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setAllDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        setTestdate(currentTime);
        setStrDate(dateString);

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String mon = String.valueOf(month);
        String da = String.valueOf(day);
        if ((month + 1) < 10) {
            mon = "0" + month;
        }
        if (day < 10) {
            da = "0" + day;
        }
        String fillDate = cal.get(Calendar.YEAR) + mon + da;
        setDate(Integer.parseInt(fillDate));
    }

    public float getInliquid() {
        return inliquid;
    }

    public void setInliquid(float inliquid) {
        this.inliquid = inliquid;
    }

    public float getOutliquid() {
        return outliquid;
    }

    public void setOutliquid(float outliquid) {
        this.outliquid = outliquid;
    }

    public float getTotalprotein() {
        return totalprotein;
    }

    public void setTotalprotein(float totalprotein) {
        this.totalprotein = totalprotein;
    }

    public float getTotalinorganicsalt() {
        return totalinorganicsalt;
    }

    public void setTotalinorganicsalt(float totalinorganicsalt) {
        this.totalinorganicsalt = totalinorganicsalt;
    }

    public float getBodyfat() {
        return bodyfat;
    }

    public void setBodyfat(float bodyfat) {
        this.bodyfat = bodyfat;
    }

    public float getTotalwater() {
        return totalwater;
    }

    public void setTotalwater(float totalwater) {
        this.totalwater = totalwater;
    }

    public float getMuscle() {
        return muscle;
    }

    public void setMuscle(float muscle) {
        this.muscle = muscle;
    }

    public float getFatfree() {
        return fatfree;
    }

    public void setFatfree(float fatfree) {
        this.fatfree = fatfree;
    }

    public String getNormalrange0() {
        return normalrange0;
    }

    public void setNormalrange0(String normalrange0) {
        this.normalrange0 = normalrange0;
    }

    public String getNormalrange1() {
        return normalrange1;
    }

    public void setNormalrange1(String normalrange1) {
        this.normalrange1 = normalrange1;
    }

    public String getNormalrange2() {
        return normalrange2;
    }

    public void setNormalrange2(String normalrange2) {
        this.normalrange2 = normalrange2;
    }

    public String getNormalrange3() {
        return normalrange3;
    }

    public void setNormalrange3(String normalrange3) {
        this.normalrange3 = normalrange3;
    }

    public String getNormalrange4() {
        return normalrange4;
    }

    public void setNormalrange4(String normalrange4) {
        this.normalrange4 = normalrange4;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getBones() {
        return bones;
    }

    public void setBones(float bones) {
        this.bones = bones;
    }

    public float getMusclefat() {
        return musclefat;
    }

    public void setMusclefat(float musclefat) {
        this.musclefat = musclefat;
    }

    public String getWeightrange() {
        return weightrange;
    }

    public void setWeightrange(String weightrange) {
        this.weightrange = weightrange;
    }

    public String getBonesrange() {
        return bonesrange;
    }

    public void setBonesrange(String bonesrange) {
        this.bonesrange = bonesrange;
    }

    public String getFatrange() {
        return fatrange;
    }

    public void setFatrange(String fatrange) {
        this.fatrange = fatrange;
    }

    public String getNormalrange5() {
        return normalrange5;
    }

    public void setNormalrange5(String normalrange5) {
        this.normalrange5 = normalrange5;
    }

    public String getNormalrange6() {
        return normalrange6;
    }

    public void setNormalrange6(String normalrange6) {
        this.normalrange6 = normalrange6;
    }

    public String getNormalrange7() {
        return normalrange7;
    }

    public void setNormalrange7(String normalrange7) {
        this.normalrange7 = normalrange7;
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public float getFatrate() {
        return fatrate;
    }

    public void setFatrate(float fatrate) {
        this.fatrate = fatrate;
    }

    public float getWaistrate() {
        return waistrate;
    }

    public void setWaistrate(float waistrate) {
        this.waistrate = waistrate;
    }

    public String getBmirange() {
        return bmirange;
    }

    public void setBmirange(String bmirange) {
        this.bmirange = bmirange;
    }

    public String getFatraterange() {
        return fatraterange;
    }

    public void setFatraterange(String fatraterange) {
        this.fatraterange = fatraterange;
    }

    public String getWaistraterange() {
        return waistraterange;
    }

    public void setWaistraterange(String waistraterange) {
        this.waistraterange = waistraterange;
    }

    public String getNormalrange8() {
        return normalrange8;
    }

    public void setNormalrange8(String normalrange8) {
        this.normalrange8 = normalrange8;
    }

    public String getNormalrange9() {
        return normalrange9;
    }

    public void setNormalrange9(String normalrange9) {
        this.normalrange9 = normalrange9;
    }

    public String getNormalrange10() {
        return normalrange10;
    }

    public void setNormalrange10(String normalrange10) {
        this.normalrange10 = normalrange10;
    }

    public float getLeftarm() {
        return leftarm;
    }

    public void setLeftarm(float leftarm) {
        this.leftarm = leftarm;
    }

    public float getRightarm() {
        return rightarm;
    }

    public void setRightarm(float rightarm) {
        this.rightarm = rightarm;
    }

    public float getTrunk() {
        return trunk;
    }

    public void setTrunk(float trunk) {
        this.trunk = trunk;
    }

    public float getLeftleg() {
        return leftleg;
    }

    public void setLeftleg(float leftleg) {
        this.leftleg = leftleg;
    }

    public float getRightleg() {
        return rightleg;
    }

    public void setRightleg(float rightleg) {
        this.rightleg = rightleg;
    }

    public String getLeftarmrange() {
        return leftarmrange;
    }

    public void setLeftarmrange(String leftarmrange) {
        this.leftarmrange = leftarmrange;
    }

    public String getRightarmrange() {
        return rightarmrange;
    }

    public void setRightarmrange(String rightarmrange) {
        this.rightarmrange = rightarmrange;
    }

    public String getTrunkrange() {
        return trunkrange;
    }

    public void setTrunkrange(String trunkrange) {
        this.trunkrange = trunkrange;
    }

    public String getLeftlegrange() {
        return leftlegrange;
    }

    public void setLeftlegrange(String leftlegrange) {
        this.leftlegrange = leftlegrange;
    }

    public String getRightlegrange() {
        return rightlegrange;
    }

    public void setRightlegrange(String rightlegrange) {
        this.rightlegrange = rightlegrange;
    }

    public String getNormalrange11() {
        return normalrange11;
    }

    public void setNormalrange11(String normalrange11) {
        this.normalrange11 = normalrange11;
    }

    public String getNormalrange12() {
        return normalrange12;
    }

    public void setNormalrange12(String normalrange12) {
        this.normalrange12 = normalrange12;
    }

    public String getNormalrange13() {
        return normalrange13;
    }

    public void setNormalrange13(String normalrange13) {
        this.normalrange13 = normalrange13;
    }

    public String getNormalrange14() {
        return normalrange14;
    }

    public void setNormalrange14(String normalrange14) {
        this.normalrange14 = normalrange14;
    }

    public String getNormalrange15() {
        return normalrange15;
    }

    public void setNormalrange15(String normalrange15) {
        this.normalrange15 = normalrange15;
    }

    public float getStandardweight() {
        return standardweight;
    }

    public void setStandardweight(float standardweight) {
        this.standardweight = standardweight;
    }

    public float getStandardheight() {
        return standardheight;
    }

    public void setStandardheight(float standardheight) {
        this.standardheight = standardheight;
    }

    public float getMusclecontrol() {
        return musclecontrol;
    }

    public void setMusclecontrol(float musclecontrol) {
        this.musclecontrol = musclecontrol;
    }

    public float getWeightcontrol() {
        return weightcontrol;
    }

    public void setWeightcontrol(float weightcontrol) {
        this.weightcontrol = weightcontrol;
    }

    public float getFatcontrol() {
        return fatcontrol;
    }

    public void setFatcontrol(float fatcontrol) {
        this.fatcontrol = fatcontrol;
    }

    public float getBasalmetabolism() {
        return basalmetabolism;
    }

    public void setBasalmetabolism(float basalmetabolism) {
        this.basalmetabolism = basalmetabolism;
    }

    public String getWater() {
        return water;
    }

    public void setWater(String water) {
        this.water = water;
    }

    public String getEdema() {
        return edema;
    }

    public void setEdema(String edema) {
        this.edema = edema;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getInorganicsalt() {
        return inorganicsalt;
    }

    public void setInorganicsalt(String inorganicsalt) {
        this.inorganicsalt = inorganicsalt;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getUpbalanced() {
        return upbalanced;
    }

    public void setUpbalanced(String upbalanced) {
        this.upbalanced = upbalanced;
    }

    public String getDownbalanced() {
        return downbalanced;
    }

    public void setDownbalanced(String downbalanced) {
        this.downbalanced = downbalanced;
    }

    public float getRa0() {
        return ra0;
    }

    public void setRa0(float ra0) {
        this.ra0 = ra0;
    }

    public float getRa1() {
        return ra1;
    }

    public void setRa1(float ra1) {
        this.ra1 = ra1;
    }

    public float getRa2() {
        return ra2;
    }

    public void setRa2(float ra2) {
        this.ra2 = ra2;
    }

    public float getLa0() {
        return la0;
    }

    public void setLa0(float la0) {
        this.la0 = la0;
    }

    public float getLa1() {
        return la1;
    }

    public void setLa1(float la1) {
        this.la1 = la1;
    }

    public float getLa2() {
        return la2;
    }

    public void setLa2(float la2) {
        this.la2 = la2;
    }

    public float getTr0() {
        return tr0;
    }

    public void setTr0(float tr0) {
        this.tr0 = tr0;
    }

    public float getTr1() {
        return tr1;
    }

    public void setTr1(float tr1) {
        this.tr1 = tr1;
    }

    public float getTr2() {
        return tr2;
    }

    public void setTr2(float tr2) {
        this.tr2 = tr2;
    }

    public float getLl0() {
        return ll0;
    }

    public void setLl0(float ll0) {
        this.ll0 = ll0;
    }

    public float getLl1() {
        return ll1;
    }

    public void setLl1(float ll1) {
        this.ll1 = ll1;
    }

    public float getLl2() {
        return ll2;
    }

    public void setLl2(float ll2) {
        this.ll2 = ll2;
    }

    public float getEdemavalue() {
        return edemavalue;
    }

    public void setEdemavalue(float edemavalue) {
        this.edemavalue = edemavalue;
    }

    public int getEdemarange() {
        return edemarange;
    }

    public void setEdemarange(int edemarange) {
        this.edemarange = edemarange;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public int getWeightvalue() {
        return weightvalue;
    }

    public void setWeightvalue(int weightvalue) {
        this.weightvalue = weightvalue;
    }

    public int getBonesevalue() {
        return bonesevalue;
    }

    public void setBonesevalue(int bonesevalue) {
        this.bonesevalue = bonesevalue;
    }

    public int getFatvalue() {
        return fatvalue;
    }

    public void setFatvalue(int fatvalue) {
        this.fatvalue = fatvalue;
    }

    public int getBmivalue() {
        return bmivalue;
    }

    public void setBmivalue(int bmivalue) {
        this.bmivalue = bmivalue;
    }

    public int getFatratevalue() {
        return fatratevalue;
    }

    public void setFatratevalue(int fatratevalue) {
        this.fatratevalue = fatratevalue;
    }

    public int getWaistratevalue() {
        return waistratevalue;
    }

    public void setWaistratevalue(int waistratevalue) {
        this.waistratevalue = waistratevalue;
    }

    public int getLeftarmvalue() {
        return leftarmvalue;
    }

    public void setLeftarmvalue(int leftarmvalue) {
        this.leftarmvalue = leftarmvalue;
    }

    public int getRightarmvalue() {
        return rightarmvalue;
    }

    public void setRightarmvalue(int rightarmvalue) {
        this.rightarmvalue = rightarmvalue;
    }

    public int getTrunkvalue() {
        return trunkvalue;
    }

    public void setTrunkvalue(int trunkvalue) {
        this.trunkvalue = trunkvalue;
    }

    public int getLeftlegvalue() {
        return leftlegvalue;
    }

    public void setLeftlegvalue(int leftlegvalue) {
        this.leftlegvalue = leftlegvalue;
    }

    public int getRightlegvalue() {
        return rightlegvalue;
    }

    public void setRightlegvalue(int rightlegvalue) {
        this.rightlegvalue = rightlegvalue;
    }

    public int getWaterint() {
        return waterint;
    }

    public void setWaterint(int waterint) {
        this.waterint = waterint;
    }

    public int getEdemaint() {
        return edemaint;
    }

    public void setEdemaint(int edemaint) {
        this.edemaint = edemaint;
    }

    public int getProteinint() {
        return proteinint;
    }

    public void setProteinint(int proteinint) {
        this.proteinint = proteinint;
    }

    public int getSaltint() {
        return saltint;
    }

    public void setSaltint(int saltint) {
        this.saltint = saltint;
    }

    public int getFatint() {
        return fatint;
    }

    public void setFatint(int fatint) {
        this.fatint = fatint;
    }

    public int getUpint() {
        return upint;
    }

    public void setUpint(int upint) {
        this.upint = upint;
    }

    public int getDownint() {
        return downint;
    }

    public void setDownint(int downint) {
        this.downint = downint;
    }

    public float getRl0() {
        return rl0;
    }

    public void setRl0(float rl0) {
        this.rl0 = rl0;
    }

    public float getRl1() {
        return rl1;
    }

    public void setRl1(float rl1) {
        this.rl1 = rl1;
    }

    public float getRl2() {
        return rl2;
    }

    public void setRl2(float rl2) {
        this.rl2 = rl2;
    }

    public int getShapeint() {
        return shapeint;
    }

    public void setShapeint(int shapeint) {
        this.shapeint = shapeint;
    }
}
