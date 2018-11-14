package com.zhishen.aixuexue.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.zhishen.aixuexue.util.ScreenUtil;

public class MoreIconView extends View {

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    int radius;

    public MoreIconView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint.setColor(Color.parseColor("#333333"));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);

        radius = ScreenUtil.dip2px(context, 2f);
        paint.setStrokeWidth(ScreenUtil.dip2px(context, 1.5f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(getWidth() / 2 + radius * 4, getHeight() / 2, radius, paint);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, paint);
        canvas.drawCircle(getWidth() / 2 - radius * 4, getHeight() / 2, radius, paint);
    }
}
