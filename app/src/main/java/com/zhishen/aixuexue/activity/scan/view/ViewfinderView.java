/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhishen.aixuexue.activity.scan.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.ResultPoint;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.scan.camera.CameraManager;

import java.util.ArrayList;
import java.util.List;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */

public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 80L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 6;
    private final Paint paint;
    private final int maskColor;
    private final int resultColor;
    private final int laserColor;
    private final int resultPointColor;
    private CameraManager cameraManager;
    private Bitmap resultBitmap;
    private int scannerAlpha;
    private List<ResultPoint> possibleResultPoints;
    private List<ResultPoint> lastPossibleResultPoints;
    private boolean needDrawText;
    private boolean fullScreen;

    // 扫描线移动的y
    private int scanLineTop;
    // 扫描线移动速度
    private int SCAN_VELOCITY = 20;
    //扫描线高度
    private int scanLightHeight = 20;
    private Bitmap scanLight;
    private int reactColor;//四角角标色
    private final View scanLineView;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            maskColor = resources.getColor(R.color.viewfinder_mask, context.getTheme());
            resultColor = resources.getColor(R.color.result_view, context.getTheme());
            laserColor = resources.getColor(R.color.viewfinder_laser, context.getTheme());
            resultPointColor = resources.getColor(R.color.possible_result_points, context.getTheme());
        } else {
            maskColor = resources.getColor(R.color.viewfinder_mask);
            resultColor = resources.getColor(R.color.result_view);
            laserColor = resources.getColor(R.color.viewfinder_laser);
            resultPointColor = resources.getColor(R.color.possible_result_points);
        }
        reactColor = laserColor;
        scannerAlpha = 0;
        possibleResultPoints = new ArrayList<>(5);
        lastPossibleResultPoints = null;

        scanLight = BitmapFactory.decodeResource(resources, R.drawable.scan_light);
        scanLineView=new View(context);
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    public void setNeedDrawText(boolean needDrawText) {
        this.needDrawText = needDrawText;
    }

    public void setScanAreaFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        if (cameraManager == null) {
            return; // not ready yet, early draw before done configuring
        }
        Rect frame = cameraManager.getFramingRect();
        Rect previewFrame = cameraManager.getFramingRectInPreview();
        if (frame == null || previewFrame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        scanLineView.setLayoutParams(new ViewGroup.LayoutParams(frame.width(),10));
        scanLineView.setBackgroundColor(reactColor);

        /*绘制遮罩*/
        drawMaskView(canvas,frame,width,height);

        /*绘制取景框边框*/
        drawFrameBounds(canvas, frame);








        paint.setAlpha(CURRENT_POINT_OPACITY);
        canvas.drawLine(frame.left, frame.top, frame.right, frame.top, paint);
        canvas.drawLine(frame.left, frame.bottom, frame.right, frame.bottom, paint);
        canvas.drawLine(frame.left, frame.top, frame.left, frame.bottom, paint);
        canvas.drawLine(frame.right, frame.top, frame.right, frame.bottom, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {
            // Draw a red "laser scanner" line through the middle to show decoding is active
            /*paint.setColor(Color.BLUE);
            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
            int middle = frame.height() / 2 + frame.top;
            canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);*/

            /*绘制扫描线*/
            drawScanLight(canvas, frame);
            /*绘制闪动的点*/
            drawPoint(canvas, frame, previewFrame);

            if (needDrawText) {
                TextPaint paint = new TextPaint();
                paint.setColor(Color.WHITE);
                paint.setTextSize(ViewUtil.convertSpToPixels(16, getContext()));
                StaticLayout layout = new StaticLayout(getResources().getString(R.string.hint_scan),
                        paint, width, Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, false);
                canvas.translate(0, frame.bottom + ViewUtil.convertDpToPixels(16, getContext()));
                layout.draw(canvas);
            }

            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY,
                    frame.left - POINT_SIZE,
                    frame.top - POINT_SIZE,
                    frame.right + POINT_SIZE,
                    frame.bottom + POINT_SIZE);
        }
    }

    private void drawScanLight(Canvas canvas, Rect frame) {
        if (scanLineTop == 0 || scanLineTop + SCAN_VELOCITY >= frame.bottom) {
            scanLineTop = frame.top;
        } else {
            /*缓冲动画*/
            //SCAN_VELOCITY = (frame.bottom - scanLineTop) / 12;
            //SCAN_VELOCITY = (int) (SCAN_VELOCITY > 10 ? Math.ceil(SCAN_VELOCITY) : 10);
            scanLineTop += SCAN_VELOCITY;
        }
        //图片扫描线
        Rect scanRect = new Rect(frame.left, scanLineTop, frame.right,
                scanLineTop + scanLightHeight);
        canvas.drawBitmap(scanLight, null, scanRect, paint);

        /*paint.setColor(reactColor);
        paint.setStrokeWidth(8);
        canvas.drawLine(frame.left, scanLineTop, frame.right, scanLineTop, paint);*/
    }

    private void drawPoint(Canvas canvas, Rect frame, Rect previewFrame) {
        float scaleX = frame.width() / (float) previewFrame.width();
        float scaleY = frame.height() / (float) previewFrame.height();

        List<ResultPoint> currentPossible = possibleResultPoints;
        List<ResultPoint> currentLast = lastPossibleResultPoints;
        int frameLeft = frame.left;
        int frameTop = frame.top;
        if (currentPossible.isEmpty()) {
            lastPossibleResultPoints = null;
        } else {
            possibleResultPoints = new ArrayList<>(5);
            lastPossibleResultPoints = currentPossible;
            paint.setAlpha(CURRENT_POINT_OPACITY);
            paint.setColor(resultPointColor);
            synchronized (currentPossible) {
                for (ResultPoint point : currentPossible) {
                    if (fullScreen)
                        canvas.drawCircle((int) (point.getX() * scaleX),
                                (int) (point.getY() * scaleY),
                                POINT_SIZE, paint);
                    else
                        canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                                frameTop + (int) (point.getY() * scaleY),
                                POINT_SIZE, paint);
                }
            }
        }
        if (currentLast != null) {
            paint.setAlpha(CURRENT_POINT_OPACITY / 2);
            paint.setColor(resultPointColor);
            synchronized (currentLast) {
                float radius = POINT_SIZE / 2.0f;
                for (ResultPoint point : currentLast) {
                    if (fullScreen)
                        canvas.drawCircle((int) (point.getX() * scaleX), (int) (point.getY() * scaleY), radius, paint);
                    else
                        canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX), frameTop + (int) (point.getY() * scaleY), radius, paint);
                }
            }
        }
    }

    private void drawMaskView(Canvas canvas, Rect frame, int width, int height){
        // 绘制取景框外的暗灰色的表面，分四个矩形绘制
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        /*上面的框*/
        canvas.drawRect(0, 0, width, frame.top, paint);
        /*绘制左边的框*/
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        /*绘制右边的框*/
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        /*绘制下面的框*/
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);
    }

    private void drawFrameBounds(Canvas canvas, Rect frame) {
        /*扫描框的四个角*/
        paint.setColor(reactColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);

        //四角角标
        int frameWidth = frame.width();
        int corLength = (int) (frameWidth * 0.07);
        int corWidth = (int) (corLength * 0.2);
        corWidth = corWidth > 15 ? 15 : corWidth;
        paint.setColor(laserColor);

        // 左上角
        canvas.drawRect(frame.left - corWidth, frame.top, frame.left, frame.top + corLength, paint);
        canvas.drawRect(frame.left - corWidth, frame.top - corWidth, frame.left + corLength, frame.top, paint);
        // 右上角
        canvas.drawRect(frame.right, frame.top, frame.right + corWidth, frame.top + corLength, paint);
        canvas.drawRect(frame.right - corLength, frame.top - corWidth, frame.right + corWidth, frame.top, paint);
        // 左下角
        canvas.drawRect(frame.left - corWidth, frame.bottom - corLength, frame.left, frame.bottom, paint);
        canvas.drawRect(frame.left - corWidth, frame.bottom, frame.left + corLength, frame.bottom + corWidth, paint);
        // 右下角
        canvas.drawRect(frame.right, frame.bottom - corLength, frame.right + corWidth, frame.bottom, paint);
        canvas.drawRect(frame.right - corLength, frame.bottom, frame.right + corWidth, frame.bottom + corWidth, paint);
    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = possibleResultPoints;
        synchronized (points) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                // trim it
                points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
            }
        }
    }

}
