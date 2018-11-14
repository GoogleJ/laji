package com.zhishen.aixuexue.weight;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.zhishen.aixuexue.R;

public class ProgressView extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float progress;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        paint.setStyle(Paint.Style.FILL);
        paint.setDither(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float v = progress / 100f;
        if (v >= 0.7) {
            paint.setAlpha((int) (255 - (150 * (v - 0.7f) / 0.3f)));
        } else {
            paint.setAlpha(255);
        }
        canvas.drawRect(0f, 0f, getWidth() * v, getHeight(), paint);
    }

    @Keep
    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public float getProgress() {
        return this.progress;
    }

    ObjectAnimator pbAnim;

    public void start() {
        setVisibility(VISIBLE);

        pbAnim = ObjectAnimator.ofFloat(this, "progress", 0, 70);
        pbAnim.setDuration(3000);
        pbAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        pbAnim.start();
    }

    //webview progress为100时调用
    public void doFinish() {
        cancel();
        pbAnim = ObjectAnimator.ofFloat(this, "progress", progress, 100);
        pbAnim.setDuration((long) (1500 * (1 - progress / 100f)));
        pbAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(View.INVISIBLE);
            }
        });

        pbAnim.start();
    }

    public void cancel() {
        if (pbAnim != null) {
            pbAnim.cancel();
        }
    }
}
