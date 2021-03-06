package com.halohoop.levelseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Halohoop on 2017/10/13.
 */

public class LevelSeekbar extends View {
    private final String TAG = "LevelSeekbar";

    private LevelChangedListener mLevelChangedListener = null;
    private List<LevelDot> mLevelDots;
    private int mCurrentLevelDotIndex;
    private int mLastCurrentLevelDotIndex;
    private int mMeasuredHeight;
    private int mMeasuredWidth;

    /**
     * 自定义属性，距离边缘的padding值
     */
    private int mPaddingHorizontalFromEdge;
    /**
     * 自定义属性，刻度上的圆的大小
     */
    private int mRulerRadius;
    /**
     * 自定义属性，触摸的圆的大小
     */
    private int mHandlerRadius;
    /**
     * 自定义属性，触摸的圆的大小
     */
    private int mPaddingBetweenDescAndRuler;

    /**
     * 触摸的handler圆的颜色
     */
    private int mHandlerColor;

    /**
     * 刻度的颜色，包括刻度上的圆
     */
    private int mRulerColor;

    /**
     * 刻度的个数
     */
    private int mDotCount;

    private Paint mPaint;
    private Path mRulerPath;

    public LevelSeekbar(Context context) {
        this(context, null);
    }

    public LevelSeekbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LevelSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttrs(context, attrs);

        mLevelDots = new ArrayList<>(mDotCount);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(10);
        mRulerPath = new Path();
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LevelSeekbar);
        mPaddingHorizontalFromEdge = typedArray.getDimensionPixelSize(R.styleable.LevelSeekbar_padding_horizontal_from_edge, 50);
        mHandlerRadius = typedArray.getDimensionPixelSize(R.styleable.LevelSeekbar_handler_radius, 30);
        mRulerRadius = typedArray.getDimensionPixelSize(R.styleable.LevelSeekbar_ruler_radius, 15);
        mPaddingBetweenDescAndRuler = typedArray.getDimensionPixelSize(R.styleable.LevelSeekbar_padding_between_desc_and_ruler, 30);
        mHandlerColor = typedArray.getInt(R.styleable.LevelSeekbar_handler_color, Color.RED);
        mRulerColor = typedArray.getInt(R.styleable.LevelSeekbar_ruler_color, Color.BLACK);
        mDotCount = typedArray.getInt(R.styleable.LevelSeekbar_dot_count, 2);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasuredWidth = getMeasuredWidth();
        mMeasuredHeight = getMeasuredHeight();

        final List<LevelDot> levelDots = this.mLevelDots;
        if (levelDots == null) {
            return;
        }
        final int size = mDotCount;

        final int measuredWidth = this.mMeasuredWidth;
        final int measuredHeight = this.mMeasuredHeight;
        final int halfMeasuredWidth = measuredWidth >> 1;
        final int halfMeasuredHeight = measuredHeight >> 1;
        final int halfPaddingBetweenDescAndRuler = mPaddingBetweenDescAndRuler >> 1;

        /**
         * 中间剩下的可用空间
         */
        final int distanceLeft = measuredWidth - (mPaddingHorizontalFromEdge * 2);
        final float paddingBetween2DotsOnRuler = distanceLeft / ((float) (size - 1));
        final float halfPaddingBetween2DotsOnRuler = paddingBetween2DotsOnRuler / 2;

        final float startRulerX = mPaddingHorizontalFromEdge;
        final float startRulerY = halfMeasuredHeight - halfPaddingBetween2DotsOnRuler;
        mRulerPath.rewind();
        mRulerPath.moveTo(startRulerX, startRulerY);
//        mRulerPath.rLineTo(distanceLeft, 0);

        mLevelDots.clear();
        for (int i = 0; i < size; i++) {
            LevelDot levelDot = new LevelDot();
            levelDot.levelDesc = "" + i;
            levelDot.levelVal = i;
            if (i == 0) {
                levelDot.x = startRulerX;
                levelDot.y = startRulerY;
            } else {
                LevelDot prelevelDot = levelDots.get(i - 1);
                levelDot.x = prelevelDot.x + paddingBetween2DotsOnRuler;
                levelDot.y = prelevelDot.y;
            }
            mRulerPath.addCircle(levelDot.x, levelDot.y, mRulerRadius, Path.Direction.CW);
            mRulerPath.rLineTo(paddingBetween2DotsOnRuler, 0);
            mLevelDots.add(levelDot);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final List<LevelDot> levelDots = this.mLevelDots;
        if (levelDots == null || levelDots.size() <= 0) {
            return;
        }
//        final int count = levelDots.size();

        final LevelDot firstLevelDot = levelDots.get(0);
        final LevelDot lastLevelDot = levelDots.get(levelDots.size() - 1);

        mPaint.setColor(mRulerColor);
        //画直线
        canvas.drawLine(firstLevelDot.x, firstLevelDot.y, lastLevelDot.x, lastLevelDot.y, mPaint);
        //直接画，画圆点
        canvas.drawPath(mRulerPath, mPaint);

        //画把手
        mPaint.setColor(mHandlerColor);
        mPaint.setStyle(Paint.Style.STROKE);
        final LevelDot levelDot = levelDots.get(mCurrentLevelDotIndex);

        canvas.drawCircle(levelDot.x, levelDot.y, mHandlerRadius, mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        //debug
        /*for (int i = 0; i < mLevelDots.size(); i++) {
            LevelDot levelDot = mLevelDots.get(i);
            canvas.drawCircle(levelDot.x, 20, 10, mPaint);
        }*/
    }

    public void setLevelChangeListener(LevelChangedListener levelChangedListener) {
        this.mLevelChangedListener = levelChangedListener;
    }

    public int getCurrentLevelDotIndex() {
        return mCurrentLevelDotIndex;
    }

    private class LevelDot {
        int levelVal;
        String levelDesc;
        //画的位置缓存
        float x = -1;
        float y = -1;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                whichDotReach(x, y, mLevelDots);
                break;
            case MotionEvent.ACTION_MOVE:
                whichDotReach(x, y, mLevelDots);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mLevelChangedListener != null && mLastCurrentLevelDotIndex != mCurrentLevelDotIndex) {
                    final LevelDot levelDot = mLevelDots.get(mCurrentLevelDotIndex);
                    if (levelDot!=null) {
                        mLevelChangedListener.onLevelChanged(mCurrentLevelDotIndex, levelDot
                                .levelVal, levelDot.levelDesc);
                        mLastCurrentLevelDotIndex = mCurrentLevelDotIndex;
                    }
                }
                break;
        }
        return true;
    }

    private void whichDotReach(float x, float y, final List<LevelDot> levelDots) {
        int halfPaddingBetweenDescAndRuler = mPaddingBetweenDescAndRuler >> 1;
        for (int i = 0; i < levelDots.size(); i++) {
            LevelDot levelDot = levelDots.get(i);
            float deltaX = levelDot.x - x;
            if (Math.abs(deltaX) <= halfPaddingBetweenDescAndRuler) {
                mCurrentLevelDotIndex = i;
                invalidate();
                break;
            }
        }
    }


    public interface LevelChangedListener {
        void onLevelChanged(int levelIndex, int levelVal, String levelDesc);
    }
}
