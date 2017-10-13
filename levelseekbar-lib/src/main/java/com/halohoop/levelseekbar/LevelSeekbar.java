package com.halohoop.levelseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
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
    private int mCurrentLevelValue;
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
        final float paddingBetween2DotsOnRuler = distanceLeft / ((float)(size - 1));
        final float halfPaddingBetween2DotsOnRuler = paddingBetween2DotsOnRuler / 2;

        final float startRulerX = mPaddingHorizontalFromEdge;
        final float startRulerY = halfMeasuredHeight - halfPaddingBetween2DotsOnRuler;
        mRulerPath.moveTo(startRulerX, startRulerY);
        mRulerPath.rLineTo(distanceLeft, 0);

        mRulerPath.rewind();
        mLevelDots.clear();
        for (int i = 0; i < size; i++) {
            LevelDot levelDot = new LevelDot();
            levelDot.levelDesc = "" + i;
            levelDot.levelVal = i;
            if (i == 0) {
                levelDot.x = startRulerY;
                levelDot.y = startRulerY;
            } else {
                LevelDot prelevelDot = levelDots.get(i - 1);
                levelDot.x = prelevelDot.x + paddingBetween2DotsOnRuler;
                levelDot.y = prelevelDot.y;
            }
            mRulerPath.addCircle(levelDot.x, levelDot.y, mRulerRadius, Path.Direction.CW);
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

        //直接画
        canvas.drawPath(mRulerPath, mPaint);
    }

    public void setLevelChangeListener(LevelChangedListener levelChangedListener) {
        this.mLevelChangedListener = levelChangedListener;
    }

    public int getCurrentLevelValue() {
        return mCurrentLevelValue;
    }

    private class LevelDot {
        int levelVal;
        String levelDesc;
        //画的位置缓存
        float x = -1;
        float y = -1;
    }

    public interface LevelChangedListener {
        void onLevelChanged(int levelIndex, int levelVal, String levelDesc);
    }
}
