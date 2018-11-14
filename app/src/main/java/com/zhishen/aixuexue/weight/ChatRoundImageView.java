package com.zhishen.aixuexue.weight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.zhishen.aixuexue.R;

/**
 * Created by yangfaming on 2018/7/21.
 */

public class ChatRoundImageView extends android.support.v7.widget.AppCompatImageView {
    private final Context context;
    private float mCircleRadius;
    // 1 左边 2 右边 3 隐藏
    private int AngleLocation;
    //三角距离自身顶部的距离
    private float mAnglePaddingTop;
    private Paint mPaint;
    private BitmapShader mBitmapShader;
    private Matrix mMatrix;
    private RectF mRectF;
    //角标的宽度
    private int mCornerMarkWidth = 20;
    //角标的高度
    private int mCornerMarkHeight = 40;
    private Path mPath;

    public ChatRoundImageView(Context context) {
        this(context, null);
    }

    public ChatRoundImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatRoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IMImageView);
        mCircleRadius = typedArray.getDimensionPixelOffset(R.styleable.IMImageView_circleRadius, 10);
        AngleLocation = typedArray.getInt(R.styleable.IMImageView_AngleLocation, 1);
        mAnglePaddingTop = typedArray.getDimension(R.styleable.IMImageView_anglePaddingTop, 10f);
        typedArray.recycle();
        mMatrix = new Matrix();
        mPaint = new Paint();
        mRectF = new RectF();
        mPaint.setAntiAlias(true);
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (AngleLocation == 1 || AngleLocation == 2) {
            int size = MeasureSpec.getSize(widthMeasureSpec);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(size + mCornerMarkWidth, MeasureSpec.getMode(widthMeasureSpec));
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = drawableToBitmap(getDrawable());
        if (bitmap == null) {
            return;
        }

        //绘制圆角
        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;
        if (!(bitmap.getWidth() == (getWidth() - mCornerMarkWidth) && bitmap.getHeight() == getHeight())) {
            // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
            if (AngleLocation == 1 || AngleLocation == 2) {
                scale = Math.max((getWidth() - mCornerMarkWidth) * 1.0f / bitmap.getWidth(),
                        getHeight() * 1.0f / bitmap.getHeight());
            } else {
                scale = Math.max(getWidth() * 1.0f / bitmap.getWidth(),
                        getHeight() * 1.0f / bitmap.getHeight());
            }
        }
        // shader的变换矩阵，我们这里主要用于放大或者缩小
        mMatrix.setScale(scale, scale);
        // 设置变换矩阵
        mBitmapShader.setLocalMatrix(mMatrix);
        // 设置shader
        mRectF.left = getPaddingLeft();
        mRectF.right = getWidth() - getPaddingRight();
        if (AngleLocation == 1) {//画在左边
            mRectF.left = getPaddingLeft() + mCornerMarkWidth;
        } else if (AngleLocation == 2) {//右边
            mRectF.right = getWidth() - getPaddingRight() - mCornerMarkWidth;
        }
        mRectF.top = getPaddingTop();
        mRectF.bottom = getHeight() - getPaddingBottom();
        mPaint.setShader(mBitmapShader);
        canvas.drawRoundRect(mRectF, mCircleRadius, mCircleRadius,
                mPaint);
        //绘制凸出三角
        if (AngleLocation == 2) {
            mPath.moveTo(mRectF.right, mRectF.top + mAnglePaddingTop);
            mPath.lineTo(mRectF.right + mCornerMarkWidth, mRectF.top + mAnglePaddingTop + mCornerMarkHeight / 2);
            mPath.lineTo(mRectF.right, mRectF.top + mAnglePaddingTop + mCornerMarkHeight);
            mPath.lineTo(mRectF.right, mRectF.top + mAnglePaddingTop);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            canvas.drawPath(mPath, mPaint);
        } else if (AngleLocation == 1) {
            mPath.moveTo(mRectF.left, mRectF.top + mAnglePaddingTop);
            mPath.lineTo(mRectF.left - mCornerMarkWidth, mRectF.top + mAnglePaddingTop + mCornerMarkHeight / 2);
            mPath.lineTo(mRectF.left, mRectF.top + mAnglePaddingTop + mCornerMarkHeight);
            mPath.lineTo(mRectF.left, mRectF.top + mAnglePaddingTop);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            canvas.drawPath(mPath, mPaint);
        }

    }


    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        } else if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            try {
                Bitmap bitmap;
                if (drawable instanceof ColorDrawable) {
                    bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
                } else {
                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                }

                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap;
            } catch (Exception var4) {
                var4.printStackTrace();
                return null;
            }
        }
    }
}
