package com.daotangbill.rick;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by BILL on 2016/11/6.
 * email:tangbaozi@daotangbill.uu.me
 */

public class SplashView extends View {
    //    大圆里面的小圆的半径
    private float mRotationRadius = 90;
    //    每一个小圆的半径
    private float mCirCleRadius = 18;
    //小圆的颜色列表 在 intialize方法中初始化
    private int[] mCircleColors;
    //    大圆和小圆的旋转时间
    private long mRotationDuration = 2000;
    //    第二 部分的动画总时间 各用1/2
    private long mSplashDuration = 1200;
    //    整体背景颜色
    private int mSplashBgColor = Color.WHITE;

    //参数 不断的变化
//空心圆的半径
    private float mHoleRadius = 0f;
    //    当前大圆的旋转角度（弧度）
    private float mCurrentRotationAngle = 0f;
    //    当前大圆的半径
    private float mCurrentRotationRadius = mRotationRadius;

    //绘制的画笔
    private Paint mPaint = new Paint();
    //绘制背景的画笔
    private Paint mPaintBackground = new Paint();
    //屏幕中心 坐标原点
    private float mCenterX;
    private float mcenterY;
    //屏幕对角线一半2
    private float MdiagnaDist;

    public SplashView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mPaint.setAntiAlias(true);//抗锯齿
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setStyle(Paint.Style.STROKE);
        mPaintBackground.setColor(mSplashBgColor);
        mCircleColors = getContext().getResources().getIntArray(R.array.splash_circle_color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2f;
        mcenterY = h / 2f;
        MdiagnaDist = (float) Math.sqrt((w * w + h * h) / 2);
    }

    //    暴露 方法 用于改变动画的状态
    public void SplashDisapper() {
        if (mState != null && mState instanceof RotationState) {
            ((RotationState) mState).Cancel();
            post(new Runnable() {
                @Override
                public void run() {
                    mState = new MargingState();//状态修改
                }
            });
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制动画
        //1、进来加载第一个动画 小球旋转
        //动画的共性  绘制小圆，绘制背景（还原纯色）
        //使用设计模式： 状态模式
        //也就是绘制 的分发
        if (mState == null) {
            mState = new RotationState();
        }
        mState.drawState(canvas);
        super.onDraw(canvas);
    }

    private SplashState mState = null;

    private static abstract class SplashState {
        public abstract void drawState(Canvas canvas);
    }

    /**
     * 小球的旋转 动画
     */
    private class RotationState extends SplashState {

        private ValueAnimator animator;

        RotationState() {
//            小圆的坐标 -》大圆的旋转角度(0->2π)
            animator = ValueAnimator.ofFloat(0, (float) Math.PI * 2);
            animator.setDuration(mRotationDuration);//作用时间
            animator.setRepeatCount(ValueAnimator.INFINITE);//无限循环
            animator.setInterpolator(new LinearInterpolator());//插值器 平滑
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    //不断改变的角度
                    mCurrentRotationAngle = (float) animation.getAnimatedValue();
                    //刷新->绘制
                    invalidate();
                }
            });
            animator.start();
        }

        void Cancel() {
            animator.cancel();
        }

        @Override
        public void drawState(Canvas canvas) {
            //绘制背景
            drawBackGround(canvas);
            //绘制小圆
            drawCircle(canvas);
        }
    }

    private void drawCircle(Canvas canvas) {
//        绘制小圆（坐标，自身半径）
        int num = mCircleColors.length;
//        得到每一个 小圆中间的间隔的角度
        float rotainAngle = (float) (2 * Math.PI / num);
        for (int i = 0; i < num; i++) {
            /**
             * x=r*cos(a)
             * y=r*sin(a)
             */
            double angle = i * rotainAngle + mCurrentRotationAngle;
            float cx = (float) (mCurrentRotationRadius * Math.cos(angle) + mCenterX);//屏幕坐标原点在左上角，现在平移
            float cy = (float) (mCurrentRotationRadius * Math.sin(angle) + mcenterY);
            mPaint.setColor(mCircleColors[i]);
            canvas.drawCircle(cx, cy, mCirCleRadius, mPaint);
        }
    }

    private void drawBackGround(Canvas canvas) {
        if (mHoleRadius>0){
            //绘制空心圆
            //半径，画笔宽度
            //画笔宽度 对角线一半-空心圆半径
            float strokeWidth=MdiagnaDist-mHoleRadius;
            mPaintBackground.setStrokeWidth(strokeWidth);
            //空心圆的半径 +画笔宽度的一半=画笔的半径
            float radius =mHoleRadius+strokeWidth/2;
            canvas.drawCircle(mCenterX, mcenterY,radius,mPaintBackground);
        }else {
            //檫黑板
            canvas.drawColor(mSplashBgColor);
        }
    }

    /**
     * 小球的 聚合 动画
     */
    private class MargingState extends SplashState {
        ValueAnimator animator;

        MargingState() {
            //  小圆的坐标 -》大圆的旋转角度(0->2 π)
            //控制 当前大圆的半径-》0f
            animator = ValueAnimator.ofFloat(mRotationRadius, 0);
            animator.setDuration(mSplashDuration / 2);//作用时间
            animator.setInterpolator(new OvershootInterpolator(10f));//弹射计算
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    //不断改变的角度
                    mCurrentRotationRadius = (float) animation.getAnimatedValue();
                    //刷新->绘制
                    invalidate();
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    //开启下一个动画
                    mState = new ExpandingState();
                }
            });
//            animator.reverse(); 反向计算 并开始
            animator.start();
        }

        @Override
        public void drawState(Canvas canvas) {
            //绘制背景
            drawBackGround(canvas);
            //绘制小圆
            drawCircle(canvas);
        }
    }

    /**
     * 水波纹 空心扩散动画 动画
     */
    private class ExpandingState extends SplashState {
        ValueAnimator animator;
        ExpandingState() {
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
