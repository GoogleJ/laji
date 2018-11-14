package com.zhishen.aixuexue.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

/**
 * Created by yangfaming on 2018/7/3.
 */

public class ImageUtils {
    public static Bitmap decodeScaleImage(String var0) {
        BitmapFactory.Options var3 = getBitmapOptions(var0);
        int var4 = calculateInSampleSize(var3, 420, 420);
        var3.inSampleSize = var4;
        var3.inJustDecodeBounds = false;
        Bitmap var5 = BitmapFactory.decodeFile(var0, var3);
        int var6 = readPictureDegree(var0);
        Bitmap var7 = null;
        if (var5 != null && var6 != 0) {
            var7 = rotaingImageView(var6, var5);
            var5.recycle();
            var5 = null;
            return var7;
        } else {
            return var5;
        }
    }
    /**
     * 获取BitmapFactory.Options
     *
     * @param pathName
     * @return
     */
    public static BitmapFactory.Options getBitmapOptions(String pathName) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, opts);
        return opts;
    }
    /**
     * 计算样本大小
     *
     * @param options
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int targetWidth, int targetHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int scale = 1;
        if (height > targetHeight || width > targetWidth) {
            int heightScale = Math.round((float) height / (float) targetHeight);
            int widthScale = Math.round((float) width / (float) targetWidth);
            scale = heightScale > widthScale ? heightScale : widthScale;
        }
        return scale;
    }
    /**
     * 获取图片角度
     *
     * @param filename
     * @return
     */
    public static int readPictureDegree(String filename) {
        short degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(filename);
            int anInt = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            switch (anInt) {
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                case ExifInterface.ORIENTATION_TRANSPOSE:
                case ExifInterface.ORIENTATION_TRANSVERSE:
                default:
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return degree;
    }
    public static Bitmap rotaingImageView(int var0, Bitmap var1) {
        Matrix var2 = new Matrix();
        var2.postRotate((float) var0);
        Bitmap var3 = Bitmap.createBitmap(var1, 0, 0, var1.getWidth(), var1.getHeight(), var2, true);
        return var3;
    }
}
