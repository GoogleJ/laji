package com.zhishen.aixuexue.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.zhishen.aixuexue.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jerome on 2018/6/28
 */
public class BitmapUtil {

    private static final int LOAD_CIRCLE = 1;
    private static final int LOAD_CORNER = 2;
    private static final int LOAD_SOURCE = 3;

    public static void loadImg(ImageView iv, String url) {
        loadImage(iv, url, 0, R.drawable.default_image, 0, null);
    }

    public static void loadNormalImg(ImageView iv, String url, int resId) {
        loadImage(iv, url, 0, resId, 0, null);
    }

    public static void loadNormalImg(ImageView iv, String url, int resId, RequestListener<Bitmap> listener) {
        loadImage(iv, url, 0, resId, 0, listener);
    }

    public static void loadCircleImg(ImageView iv, String url, int resId) {
        loadImage(iv, url, LOAD_CIRCLE, resId, 0, null);
    }

    public static void loadCircleImg(ImageView iv, String url, int resId, RequestListener<Bitmap> listener) {
        loadImage(iv, url, LOAD_CIRCLE, resId, 0, listener);
    }

    public static void loadCornerImg(ImageView iv, String url, int resId, int radius) {
        loadImage(iv, url, LOAD_CORNER, resId, radius, null);
    }

    public static void loadCornerImg(ImageView iv, String url, int resId, int radius, RequestListener<Bitmap> listener) {
        loadImage(iv, url, LOAD_CORNER, resId, radius, listener);
    }

    //加载图片实现方法 Glide 可设置加载回调返回bitmap
    private static void loadImage(ImageView iv, String url, int mode, int resId, int radius, RequestListener<Bitmap> listener) {
        RequestManager manager = Glide.with(iv.getContext());
        if (listener != null) {
            manager.asBitmap().listener(listener)
                    .load(url).apply(getRequestOptions(iv.getContext(), mode, resId, radius)).into(iv);
            return;
        }
        manager.load(url).apply(getRequestOptions(iv.getContext(), mode, resId, radius)).into(iv);
    }

    private static void loadImageDrawable(ImageView iv, String url, int mode, int resId, int radius, RequestListener<Drawable> listener) {
        RequestManager manager = Glide.with(iv.getContext());
        if (null != listener) {
            manager.asDrawable().listener(listener)
                    .load(url).apply(getRequestOptions(iv.getContext(),mode, resId, radius)).into(iv);
        }
        manager.load(url).apply(getRequestOptions(iv.getContext(), mode, resId, radius)).into(iv);
    }

    private static RequestOptions getRequestOptions(Context context, int mode, int resId, int radius) {
        switch (mode) {
            case LOAD_CIRCLE:
                return RequestOptions.bitmapTransform(new CircleCrop()).placeholder(resId);
            case LOAD_CORNER:
                return new RequestOptions()
                        .placeholder(resId)
                        .error(resId)
                        .transform(new CenterCropRoundCornerTransform(ScreenUtil.dip2px(context, radius)));
            case LOAD_SOURCE:
                return new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(resId).error(resId);
            default:
                return new RequestOptions().placeholder(resId);
        }
    }

    /**
     * @param context
     * @param source
     * @param radius  0-25
     * @param scale   1/2 1/4 1/8较好
     * @return
     */
    public static Bitmap rsBlur(Context context, Bitmap source, int radius, float scale) {

        int width = Math.round(source.getWidth() * scale);
        int height = Math.round(source.getHeight() * scale);

        Bitmap inputBmp = Bitmap.createScaledBitmap(source, width, height, false);

        RenderScript renderScript = RenderScript.create(context);

        // Allocate memory for Renderscript to work with

        final Allocation input = Allocation.createFromBitmap(renderScript, inputBmp);
        final Allocation output = Allocation.createTyped(renderScript, input.getType());

        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptIntrinsicBlur.setInput(input);

        // Set the blur radius
        scriptIntrinsicBlur.setRadius(radius);

        // Start the ScriptIntrinisicBlur
        scriptIntrinsicBlur.forEach(output);

        // Copy the output to the blurred bitmap
        output.copyTo(inputBmp);


        renderScript.destroy();
        return inputBmp;
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param kind 参照MediaStore.Images(Video).Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height,int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        if(bitmap!= null){
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Axx/Video/video_thumb.png";
        File filePic;

        try {
            filePic = new File(savePath);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

}
