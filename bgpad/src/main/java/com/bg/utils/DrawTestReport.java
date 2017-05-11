package com.bg.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.view.View;

import com.bg.bgpad.R;

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

    public DrawTestReport(Context context, Map<String, String> datas) {
        super(context);
        this.context = context;
        this.datas = datas;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Bitmap b = Bitmap.createBitmap(2480, 3508, Bitmap.Config.RGB_565);//A4纸的分辨率
        Canvas c = new Canvas(b);
        c.drawRGB(0xFF, 0xFF, 0xFF);
        Paint paint = new Paint();
        paint.setAntiAlias(true);// 去锯齿
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        c.drawText(datas.get("usernumber"), 243, 376, paint);//编号
        c.drawText(datas.get("username"), 689, 376, paint);//姓名
        c.drawText(datas.get("sex"), 939, 376, paint);//性别
        c.drawText(datas.get("age"), 1155, 376, paint);//年龄
        c.drawText(datas.get("testHeight"), 1384, 376, paint);//身高
        c.drawText(datas.get("testWeight"), 1680, 376, paint);//体重
        c.drawText(datas.get("strDate"), 2090, 376, paint);//测试日期
        Paint paintText = new Paint();
        paintText.setAntiAlias(true);// 去锯齿
        paintText.setStyle(Paint.Style.STROKE);
        paintText.setStrokeWidth(3);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(40);
        c.drawText(datas.get("inliquid"), 384, 630, paintText);//细胞内液
        c.drawText(datas.get("outliquid"), 384, 697, paintText);//细胞外液
        c.drawText(datas.get("totalprotein"), 557, 667, paintText);//蛋白质
        c.drawText(datas.get("totalinorganicsalt"), 452, 764, paintText);//无机盐
        c.drawText(datas.get("bodyfat"), 528, 827, paintText);//体脂肪
        c.drawText(datas.get("totalwater"), 642, 895, paintText);//总水分
        c.drawText(datas.get("muscle"), 769, 698, paintText);//去脂体重
        c.drawText(datas.get("weight"), 1188, 764, paintText);//总体重
        c.drawText(datas.get("normalrange0"), 1188, 764, paintText);////细胞内液正常范围
        c.drawText(datas.get("normalrange1"), 1188, 764, paintText);////细胞外液正常范围
        c.drawText(datas.get("normalrange2"), 1188, 764, paintText);//蛋白质正常范围
        c.drawText(datas.get("normalrange3"), 1188, 764, paintText);//无机盐正常范围
        c.drawText(datas.get("normalrange4"), 1188, 764, paintText);//体脂肪正常范围
//        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.bit);
//        c.drawBitmap(icon, 0, 0, null);

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
    }
}
