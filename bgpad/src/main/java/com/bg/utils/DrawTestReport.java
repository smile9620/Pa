package com.bg.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.view.View;

import com.bg.bgpad.R;
import com.bg.bgpad.TestReportActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by zjy on 2017-05-09.
 */

public class DrawTestReport extends View {
    private Context context;
    private Map<String, String> datas;
    private int width = 2480;
    private int height = 3508;


    public DrawTestReport(Context context, Map<String, String> datas) {
        super(context);
        this.context = context;
        this.datas = datas;
    }

    private boolean bg_need;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);//A4纸的分辨率
        Canvas can = new Canvas(b);
        can.drawRGB(0xFF, 0xFF, 0xFF);

        if (bg_need) {
            drawBg(can);
        }
        drawDatas(can);

        String path = null;
        String name = "report.png";
        if (Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().getPath() +
                    "/Bg/Report";
        } else {
            return;
        }
        File folder = new File(path);// 保存到sdcard根目录下，文件名为share_pic.png
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(folder, name);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.PNG, 50, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        b.recycle();
    }

    private void drawProgess(Canvas canvas, int top, String range) {

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setAntiAlias(true);// 去锯齿
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(35);
        int left = 630;
        int progess = 96;
        if (range.equals("低标准")) {
            canvas.drawRect(left, top, left + progess, top + 22, paint);
            canvas.drawText("低标准", left + progess + 10, top + 22, paint);
        } else if (range.equals("正常")) {
            canvas.drawRect(left, top, left + 3 * progess, top + 22, paint);
            canvas.drawText("正常", left + 3 * progess + 10, top + 22, paint);
        } else {
            canvas.drawRect(left, top, left + 5 * progess, top + 22, paint);
            canvas.drawText("超标准", left + 5 * progess + 10, top + 22, paint);
        }
    }

    private void drawBg(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.rgb(82, 158, 215));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, width, 300, paint);
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.bg_logo);
        canvas.drawBitmap(logo, 35, 30, null);

        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);// 去锯齿
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(70);
        canvas.drawText("儿童生长发育测评系统", 900, 100, paint);
        canvas.drawText("检测报告单", 1090, 195, paint);
        paint.setColor(Color.rgb(192, 216, 157));
        canvas.drawRect(70, 350, width - 70, 430, paint);
        paint.setColor(Color.rgb(240, 238, 237));
        canvas.drawRect(70, 430, width - 70, 510, paint);
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        Rect number1 = new Rect(70, 350, 640, 430);//画一个矩形
//        int baseLineY = (int) (rect.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式
        canvas.drawText("编号", number1.centerX(), (int) (number1.centerY() - top / 2 - bottom / 2), paint);
        Rect number2 = new Rect(70, 430, 640, 510);//画一个矩形
        canvas.drawText(datas.get("usernumber"), number2.centerX(), (int) (number2.centerY() - top / 2 - bottom / 2), paint);//编号
        Rect name1 = new Rect(640, 350, 900, 430);//画一个矩形
        canvas.drawText("姓名", name1.centerX(), (int) (name1.centerY() - top / 2 - bottom / 2), paint);
        Rect name2 = new Rect(640, 430, 900, 510);//画一个矩形
        canvas.drawText(datas.get("username"), name2.centerX(), (int) (name2.centerY() - top / 2 - bottom / 2), paint);//姓名
        Rect sex1 = new Rect(900, 350, 1130, 430);//画一个矩形
        canvas.drawText("性别", sex1.centerX(), (int) (sex1.centerY() - top / 2 - bottom / 2), paint);
        Rect sex2 = new Rect(900, 430, 1130, 510);//画一个矩形
        canvas.drawText(datas.get("sex"), sex2.centerX(), (int) (sex2.centerY() - top / 2 - bottom / 2), paint);//性别
        Rect age1 = new Rect(1130, 350, 1340, 430);//画一个矩形
        canvas.drawText("年龄", age1.centerX(), (int) (age1.centerY() - top / 2 - bottom / 2), paint);//
        Rect age2 = new Rect(1130, 430, 1340, 510);//画一个矩形
        canvas.drawText(datas.get("age"), age2.centerX(), (int) (age2.centerY() - top / 2 - bottom / 2), paint);//年龄
        Rect height1 = new Rect(1340, 350, 1620, 430);//画一个矩形
        canvas.drawText("身高(cm)", height1.centerX(), (int) (height1.centerY() - top / 2 - bottom / 2), paint);
        Rect height2 = new Rect(1340, 430, 1620, 510);//画一个矩形
        canvas.drawText(datas.get("testHeight"), height2.centerX(), (int) (height2.centerY() - top / 2 - bottom / 2), paint);//身高
        Rect weight1 = new Rect(1620, 350, 1940, 430);//画一个矩形
        canvas.drawText("体重(kg)", weight1.centerX(), (int) (weight1.centerY() - top / 2 - bottom / 2), paint);
        Rect weight2 = new Rect(1620, 430, 1940, 510);//画一个矩形
        canvas.drawText(datas.get("testWeight"), weight2.centerX(), (int) (weight2.centerY() - top / 2 - bottom / 2), paint);//体重
        Rect strDate1 = new Rect(1940, 350, width - 70, 430);//画一个矩形
        canvas.drawText("测试日期", strDate1.centerX(), (int) (strDate1.centerY() - top / 2 - bottom / 2), paint);
        Rect strDate2 = new Rect(1940, 430, width - 70, 510);//画一个矩形
        canvas.drawText(datas.get("strDate"), strDate2.centerX(), (int) (strDate2.centerY() - top / 2 - bottom / 2), paint);//测试日期
        Rect score1 = new Rect(70, 569, 490, 731);//一个矩形
        paint.setColor(Color.rgb(142, 173, 103));
        canvas.drawRect(score1, paint);
        Rect score2 = new Rect(490, 570, 920, 730);//画一个矩形
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(score2, paint);
        paint.setTextSize(60);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(datas.get("score"), score2.centerX(), (int) (score2.centerY() - top / 2 - bottom / 2), paint);
        paint.setColor(Color.WHITE);
        canvas.drawText("身体总评分：", score1.centerX(), (int) (score1.centerY() - top / 2 - bottom / 2), paint);
        paint.setColor(Color.rgb(119, 165, 197));
        paint.setTextSize(50);
        canvas.drawText("身体成分分析", 70 + paint.measureText("身体成分分析") / 2, 820, paint);
        canvas.drawText("肌肉脂肪分析", 70 + paint.measureText("肌肉脂肪分析") / 2, 820, paint);
        canvas.drawText("肥胖分析", 70 + paint.measureText("肥胖分析") / 2, 820, paint);
        canvas.drawText("节段肌肉分析", 70 + paint.measureText("节段肌肉分析") / 2, 820, paint);
        canvas.drawText("体重身高分析", 70 + paint.measureText("体重身高分析") / 2, 820, paint);
        canvas.drawText("体型判断", 70 + paint.measureText("体型判断") / 2, 820, paint);
        canvas.drawText("营养评估", 70 + paint.measureText("营养评估") / 2, 820, paint);
        canvas.drawText("肌肉评估", 70 + paint.measureText("肌肉评估") / 2, 820, paint);
        canvas.drawText("健康诊断", 70 + paint.measureText("健康诊断") / 2, 820, paint);
        canvas.drawText("浮肿分析", 70 + paint.measureText("浮肿分析") / 2, 820, paint);
        canvas.drawText("生物电阻抗", 70 + paint.measureText("生物电阻抗") / 2, 820, paint);
        paint.setTextSize(40);
        paint.setColor(Color.rgb(192, 216, 157));
        Rect inliquid = new Rect(70, 870, 284, 930);//一个矩形
//        Rect outliquid = new Rect(70, 990, 490, 731);//一个矩形
        canvas.drawRect(inliquid, paint);
//        canvas.drawRect(outliquid,paint);


    }

    private void drawDatas(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);// 去锯齿
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        canvas.drawText(datas.get("usernumber"), 171, 378, paint);//编号
        canvas.drawText(datas.get("username"), 683, 378, paint);//姓名
        canvas.drawText(datas.get("sex"), 988, 378, paint);//性别
        canvas.drawText(datas.get("age"), 1189, 378, paint);//年龄
        canvas.drawText(datas.get("testHeight"), 1417, 378, paint);//身高
        canvas.drawText(datas.get("testWeight"), 1703, 378, paint);//体重
        canvas.drawText(datas.get("strDate"), 1993, 378, paint);//测试日期
        Paint score = new Paint();
        score.setTextSize(100);
        score.setColor(Color.RED);
        score.setAntiAlias(true);// 去锯齿
        score.setStyle(Paint.Style.FILL);
        canvas.drawText(datas.get("score"), 690, 610, score);//总评分
        Paint paintText = new Paint();
        paintText.setAntiAlias(true);// 去锯齿
        paintText.setStyle(Paint.Style.FILL);
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(40);
        canvas.drawText(datas.get("inliquid"), 333, 876, paintText);//细胞内液
        canvas.drawText(datas.get("outliquid"), 333, 942, paintText);//细胞外液
        canvas.drawText(datas.get("totalprotein"), 438, 1008, paintText);//蛋白质
        canvas.drawText(datas.get("totalinorganicsalt"), 544, 1072, paintText);//无机盐
        canvas.drawText(datas.get("bodyfat"), 650, 1140, paintText);//体脂肪
        canvas.drawText(datas.get("totalwater"), 543, 909, paintText);//总水分
        canvas.drawText(datas.get("muscle"), 758, 956, paintText);//肌肉量
        canvas.drawText(datas.get("fatfree"), 970, 992, paintText);//去脂体重
        canvas.drawText(datas.get("weight"), 1183, 1005, paintText);//总体重
        canvas.drawText(datas.get("normalrange0"), 1342, 873, paintText);////细胞内液正常范围
        canvas.drawText(datas.get("normalrange1"), 1342, 940, paintText);////细胞外液正常范围
        canvas.drawText(datas.get("normalrange2"), 1342, 1004, paintText);//蛋白质正常范围
        canvas.drawText(datas.get("normalrange3"), 1342, 1066, paintText);//无机盐正常范围
        canvas.drawText(datas.get("normalrange4"), 1342, 1136, paintText);//体脂肪正常范围

        canvas.drawText(datas.get("weight"), 473, 1382, paintText);//体重
        drawProgess(canvas, 1392, datas.get("weightrange"));
        canvas.drawText(datas.get("bones"), 473, 1448, paintText);//骨骼肌
        drawProgess(canvas, 1459, datas.get("boneserange"));
        canvas.drawText(datas.get("musclefat"), 473, 1511, paintText);//体脂肪
        drawProgess(canvas, 1525, datas.get("fatrange"));
        canvas.drawText(datas.get("normalrange5"), 1321, 1382, paintText);//体重正常范围
        canvas.drawText(datas.get("normalrange6"), 1321, 1446, paintText);//骨骼肌正常范围
        canvas.drawText(datas.get("normalrange7"), 1321, 1512, paintText);//体脂肪正常范围

        canvas.drawText(datas.get("bmi"), 473, 1760, paintText);//BMI
        drawProgess(canvas, 1769, datas.get("bmirange"));
        canvas.drawText(datas.get("fatrate"), 473, 1826, paintText);//体脂率
        drawProgess(canvas, 1836, datas.get("fatraterange"));
        canvas.drawText(datas.get("waistrate"), 473, 1889, paintText);//腰臀比
        drawProgess(canvas, 1902, datas.get("waistraterange"));
        canvas.drawText(datas.get("normalrange8"), 1321, 1760, paintText);//BMI正常范围
        canvas.drawText(datas.get("normalrange9"), 1321, 1824, paintText);//体脂率正常范围
        canvas.drawText(datas.get("normalrange10"), 1321, 1890, paintText);//腰臀比正常范围

        canvas.drawText(datas.get("leftarm"), 473, 2132, paintText);//左臂
        drawProgess(canvas, 2142, datas.get("leftarmrange"));
        canvas.drawText(datas.get("rightarm"), 473, 2198, paintText);//右臂
        drawProgess(canvas, 2209, datas.get("rightarmrange"));
        canvas.drawText(datas.get("trunk"), 473, 2261, paintText);//躯干
        drawProgess(canvas, 2275, datas.get("trunkrange"));
        canvas.drawText(datas.get("leftleg"), 473, 2330, paintText);//左腿
        drawProgess(canvas, 2349, datas.get("leftlegrange"));
        canvas.drawText(datas.get("rightleg"), 473, 2395, paintText);//右腿
        drawProgess(canvas, 2406, datas.get("rightlegrange"));
        canvas.drawText(datas.get("normalrange11"), 1321, 2132, paintText);//左臂正常范围
        canvas.drawText(datas.get("normalrange12"), 1321, 2196, paintText);//右臂正常范围
        canvas.drawText(datas.get("normalrange13"), 1321, 2262, paintText);//躯干正常范围
        canvas.drawText(datas.get("normalrange14"), 1321, 2329, paintText);//左腿正常范围
        canvas.drawText(datas.get("normalrange15"), 1321, 2394, paintText);//右腿正常范围

        canvas.drawText(datas.get("standardweight"), 574, 2575, paintText);//标准体重
        canvas.drawText(datas.get("standardheight"), 574, 2642, paintText);//标准身高
        canvas.drawText(datas.get("musclecontrol"), 574, 2706, paintText);//肌肉控制
        canvas.drawText(datas.get("weightcontrol"), 1310, 2575, paintText);//体重控制
        canvas.drawText(datas.get("fatcontrol"), 1310, 2642, paintText);//脂肪控制
        canvas.drawText(datas.get("basalmetabolism"), 1310, 2706, paintText);//基础代谢量

        canvas.drawText(datas.get("edemaValue"), 1833, 2194, paintText);//浮肿分析值

        canvas.drawText(datas.get("ra0"), 1807, 2556, paintText);//RA 5kHz
        canvas.drawText(datas.get("ra1"), 1807, 2632, paintText);//RA 50kHz
        canvas.drawText(datas.get("ra2"), 1807, 2706, paintText);//RA 2505kHz
        canvas.drawText(datas.get("la0"), 1930, 2556, paintText);//LA 5kHz
        canvas.drawText(datas.get("la1"), 1930, 2632, paintText);//LA 50kHz
        canvas.drawText(datas.get("la2"), 1930, 2706, paintText);//LA 250kHz
        canvas.drawText(datas.get("tr0"), 2063, 2556, paintText);//TR 5kHz
        canvas.drawText(datas.get("tr1"), 2063, 2632, paintText);//TR 50kHz
        canvas.drawText(datas.get("tr2"), 2063, 2706, paintText);//TR 250kHz
        canvas.drawText(datas.get("rl0"), 2192, 2556, paintText);//RL 5kHz
        canvas.drawText(datas.get("rl1"), 2192, 2632, paintText);//RL 50kHz
        canvas.drawText(datas.get("rl2"), 2192, 2706, paintText);//RL 250kHz
        canvas.drawText(datas.get("ll0"), 2311, 2556, paintText);//LL 5kHz
        canvas.drawText(datas.get("ll1"), 2311, 2632, paintText);//LL 50kHz
        canvas.drawText(datas.get("ll2"), 2311, 2706, paintText);//LL 250kH

        Bitmap check = BitmapFactory.decodeResource(getResources(), R.drawable.check);
        String a = datas.get("healthWater");
        int healthWater = Integer.parseInt(datas.get("healthWater"));//健康诊断 身体水分
        int healthEdema = Integer.parseInt(datas.get("healthEdema"));//健康诊断 浮肿
        int nutritionProtein = Integer.parseInt(datas.get("nutritionProtein"));//营养评估 蛋白质
        int nutritionSalt = Integer.parseInt(datas.get("nutritionSalt"));//营养评估 无机盐
        int nutritionFat = Integer.parseInt(datas.get("nutritionFat"));//营养评估 体脂肪
        int muscleUp = Integer.parseInt(datas.get("muscleUp"));//肌肉评估 上肢
        int muscleDown = Integer.parseInt(datas.get("muscleDown"));//肌肉评估 下肢
        int shapeJudgment = Integer.parseInt(datas.get("shapeJudgment"));//体型判断
        int edemaProgess = Integer.parseInt(datas.get("edemaProgess"));//浮肿分析进度条
        if (healthWater == 0) {//健康诊断 身体水分
            canvas.drawBitmap(check, 1817, 1859, null);
        } else {
            canvas.drawBitmap(check, 2020, 1859, null);
        }
        if (healthEdema == 0) {//健康诊断 浮肿
            canvas.drawBitmap(check, 1817, 1946, null);
        } else if (healthEdema == 1) {
            canvas.drawBitmap(check, 2020, 1946, null);
        } else {
            canvas.drawBitmap(check, 2259, 1946, null);
        }
        if (nutritionProtein == 0) {//营养评估 蛋白质
            canvas.drawBitmap(check, 1817, 1190, null);
        } else {
            canvas.drawBitmap(check, 2019, 1190, null);
        }
        if (nutritionSalt == 0) {//营养评估 无机盐
            canvas.drawBitmap(check, 1817, 1276, null);
        } else {
            canvas.drawBitmap(check, 2019, 1276, null);
        }
        if (nutritionFat == 0) {//营养评估 体脂肪
            canvas.drawBitmap(check, 1817, 1363, null);
        } else if (nutritionFat == 1) {
            canvas.drawBitmap(check, 2019, 1363, null);
        } else {
            canvas.drawBitmap(check, 2259, 1363, null);
        }
        if (muscleUp == 0) {//肌肉评估 上肢
            canvas.drawBitmap(check, 1817, 1567, null);
        } else {
            canvas.drawBitmap(check, 2019, 1567, null);
        }
        if (muscleDown == 0) {//肌肉评估 下肢
            canvas.drawBitmap(check, 1817, 1654, null);
        } else {
            canvas.drawBitmap(check, 2019, 1654, null);
        }
        Bitmap start = BitmapFactory.decodeResource(getResources(), R.drawable.start);
        switch (shapeJudgment) {
            case 0:
                canvas.drawBitmap(start, 1903, 594, null);//运动员型
                break;
            case 1:
                canvas.drawBitmap(start, 2145, 594, null);//偏胖
                break;
            case 2:
                canvas.drawBitmap(start, 2354, 594, null);//肥胖
                break;
            case 3:
                canvas.drawBitmap(start, 1903, 719, null);//肌肉型
                break;
            case 4:
                canvas.drawBitmap(start, 1738, 826, null);//苗条肌肉型
                break;
            case 5:
                canvas.drawBitmap(start, 1946, 849, null);//苗条
                break;
            case 6:
                canvas.drawBitmap(start, 2084, 832, null);//健康型
                break;
            case 7:
                canvas.drawBitmap(start, 2357, 719, null);//偏胖
                break;
            case 8:
                canvas.drawBitmap(start, 1745, 965, null);//消瘦型
                break;
            case 9:
                canvas.drawBitmap(start, 2069, 965, null);//偏瘦型
                break;
            case 10:
                canvas.drawBitmap(start, 2358, 897, null);//隐性肥胖
                break;
        }
    }

}
