package com.bg.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.ref.SoftReference;
import java.math.BigDecimal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;

public class BitmapTool {

    //按比例大小压缩法
    public static Bitmap decodeSampledbitmapFromResource(Bitmap bitmap,
                                                         int width, int height) {

        byte[] data = Bitmap2Bytes(bitmap);
        // 給定的BitmapFactory設置解碼的參數
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 從原始的圖片獲得原始圖片的寬高，而避免申請內存空間
        options.inJustDecodeBounds = true;
        // BitmapFactory.decodeResource(resourse, resid, options);
        Bitmap bit = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInsampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        // return BitmapFactory.decodeResource(resourse, resid, options);
        bit = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        return bit;
    }

    // 指出圖片的縮放比例
    public static int calculateInsampleSize(BitmapFactory.Options options,
                                            int reqwidth, int reqheight) {
        // 獲得原始圖片的寬高
        int imageHeight = options.outHeight;
        int imagewidth = options.outWidth;
        int inSimpleSize = 1;
        if (imageHeight > reqheight || imagewidth > reqwidth) {
            // 計算壓縮比例，分為寬高比例
            final int heightRatio = Math.round((float) imageHeight
                    / (float) reqwidth);
            final int widthRataio = Math.round((float) imagewidth
                    / (float) reqheight);
            inSimpleSize = heightRatio < widthRataio ? heightRatio
                    : widthRataio;
        }
        return inSimpleSize;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    //质量压缩法
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

}
