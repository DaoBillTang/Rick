package com.daotangbill.rick;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by BILL on 2016/11/6.
 * email:tangbaozi@daotangbill.uu.me
 */

public class SplashOnlyExpandView extends View {

    //    第二 部分的动画总时间 各用1/2
    private long mSplashDuration = 1200;
    //    整体背景颜色
    private int mSplashBgColor = Color.WHITE;

    //参数 不断的变化
    //空心圆的半径
    private float mHoleRadius = 0f;

    //绘制背景的画笔
    private Paint mPaintBackground = new Paint();
    //屏幕中心 坐标原点
    private float mCenterX;
    private float mcenterY;
    //屏幕对角线一半2
    private float MdiagnaDist;

    public SplashOnlyExpandView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setStyle(Paint.Style.STROKE);
        mPaintBackground.setColor(mSplashBgColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2f;
        mcenterY = h / 2f;
        MdiagnaDist = (float) Math.sqrt((w * w + h * h) / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制动画
        //1、进来加载第一个动画 小球旋转
        //动画的共性  绘制小圆，绘制背景（还原纯色）
        //使用设计模式： 状态模式
        //也就是绘制 的分发
        if (mState == null) {
            mState = new ExpandingState();
        }
        mState.drawState(canvas);
        super.onDraw(canvas);
    }

    private SplashState mState = null;

    private static abstract class SplashState {
        public abstract void drawState(Canvas canvas);
    }

    private void drawBackGround(Canvas canvas) {
        if (mHoleRadius > 0) {
            //绘制空心圆
            //半径，画笔宽度
            //画笔宽度 对角线一半-空心圆半径
            float strokeWidth = MdiagnaDist = mHoleRadius;
            mPaintBackground.setStrokeWidth(strokeWidth);
            //空心圆的半径 +画笔宽度的一半=画笔的半径
            float radius = mHoleRadius + strokeWidth / 2;
            canvas.drawCircle(mCenterX, mcenterY, radius, mPaintBackground);
        } else {
            //檫黑板
            canvas.drawColor(mSplashBgColor);
        }
    }


    /**
     * 水波纹 空心扩散动画 动画
     */
    private class ExpandingState extends SplashState {
        ValueAnimator animator;

        public ExpandingState() {
            //  小圆的坐标 -》大圆的旋转角度(0->2 π)
            //控制 空心圆的半径0-> 对角线的一半
            animator = ValueAnimator.ofFloat(0, MdiagnaDist);
            animator.setDuration(mSplashDuration / 2);//作用时间
            animator.setInterpolator(new AccelerateInterpolator());//加速
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    //不断改变的空心圆 的半径
                    mHoleRadius = (float) animation.getAnimatedValue();
                    //刷新->绘制
                    invalidate();
                }
            });
//            animator.reverse(); 反向计算 并开始
            animator.start();

        }

        @Override
        public void drawState(Canvas canvas) {
            //绘制背景
            drawBackGround(canvas);
        }
    }
}

