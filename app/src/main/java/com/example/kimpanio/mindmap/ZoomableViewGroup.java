package com.example.kimpanio.mindmap;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;


public class ZoomableViewGroup extends ViewGroup{

    private static final int INVALID_POINTER_ID = 1;
    private int mActivePointerId = INVALID_POINTER_ID;
    public boolean drawLineActivated = false;
    private int touchCounter;

    private float mScaleFactor = 1;
    private ScaleGestureDetector mScaleDetector;
    private Matrix mScaleMatrix = new Matrix();
    private Matrix mScaleMatrixInverse = new Matrix();

    public float mPosX;
    public float mPosY;
    private Matrix mTranslateMatrix = new Matrix();
    private Matrix mTranslateMatrixInverse = new Matrix();

    private float mLastTouchX;
    private float mLastTouchY;

    private float mFocusX;
    private float mFocusY;

    private int mXDelta;
    private int mYDelta;

    private float[] mInvalidateWorkingArray = new float[6];
    private float[] mDispatchTouchEventWorkingArray = new float[2];
    private float[] mOnTouchEventWorkingArray = new float[2];

    // Tried to use these for drawing line.
    private View firstPressedView;
    private View secondPressedView;
    private float startX;
    private float startY;
    private float stopX;
    private float stopY;
    private DisplayMetrics metrics;

    public ZoomableViewGroup(Context context) {
        super(context);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mTranslateMatrix.setTranslate(0, 0);
        mScaleMatrix.setScale(1, 1);
        touchCounter = 0;
        metrics = Resources.getSystem().getDisplayMetrics();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.layout(l, t, l + child.getMeasuredWidth(), t + child.getMeasuredHeight());
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mPosX, mPosY);
        canvas.scale(mScaleFactor, mScaleFactor, mFocusX, mFocusY);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        mDispatchTouchEventWorkingArray[0] = motionEvent.getX();
        mDispatchTouchEventWorkingArray[1] = motionEvent.getY();
        mDispatchTouchEventWorkingArray = screenPointsToScaledPoints(mDispatchTouchEventWorkingArray);
        motionEvent.setLocation(mDispatchTouchEventWorkingArray[0],
                mDispatchTouchEventWorkingArray[1]);
        return super.dispatchTouchEvent(motionEvent);
    }

    /**
     * Although the docs say that you shouldn't override this, I decided to do
     * so because it offers me an easy way to change the invalidated area to my
     * likening.
     */
    @Override
    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {

        mInvalidateWorkingArray[0] = dirty.left;
        mInvalidateWorkingArray[1] = dirty.top;
        mInvalidateWorkingArray[2] = dirty.right;
        mInvalidateWorkingArray[3] = dirty.bottom;


        mInvalidateWorkingArray = scaledPointsToScreenPoints(mInvalidateWorkingArray);
        dirty.set(Math.round(mInvalidateWorkingArray[0]), Math.round(mInvalidateWorkingArray[1]),
                Math.round(mInvalidateWorkingArray[2]), Math.round(mInvalidateWorkingArray[3]));

        location[0] *= mScaleFactor;
        location[1] *= mScaleFactor;
        return super.invalidateChildInParent(location, dirty);
    }

    private float[] scaledPointsToScreenPoints(float[] a) {
        mScaleMatrix.mapPoints(a);
        mTranslateMatrix.mapPoints(a);
        return a;
    }

    private float[] screenPointsToScaledPoints(float[] a){
        mTranslateMatrixInverse.mapPoints(a);
        mScaleMatrixInverse.mapPoints(a);
        return a;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        mOnTouchEventWorkingArray[0] = motionEvent.getX();
        mOnTouchEventWorkingArray[1] = motionEvent.getY();

        mOnTouchEventWorkingArray = scaledPointsToScreenPoints(mOnTouchEventWorkingArray);

        motionEvent.setLocation(mOnTouchEventWorkingArray[0], mOnTouchEventWorkingArray[1]);
        mScaleDetector.onTouchEvent(motionEvent);

        final int action = motionEvent.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {

                final float x = motionEvent.getX();
                final float y = motionEvent.getY();

                mLastTouchX = x;
                mLastTouchY = y;

                // Save the ID of this pointer
                mActivePointerId = motionEvent.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {

                // Find the index of the active pointer and fetch its position
                final int pointerIndex = motionEvent.findPointerIndex(mActivePointerId);
                final float x = motionEvent.getX(pointerIndex);
                final float y = motionEvent.getY(pointerIndex);

                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                mPosX += dx;
                mPosY += dy;

                mTranslateMatrix.preTranslate(dx, dy);
                mTranslateMatrix.invert(mTranslateMatrixInverse);

                mLastTouchX = x;
                mLastTouchY = y;

                invalidate();
                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                // Extract the index of the pointer that left the touch sensor
                final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = motionEvent.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = motionEvent.getX(newPointerIndex);
                    mLastTouchY = motionEvent.getY(newPointerIndex);
                    mActivePointerId = motionEvent.getPointerId(newPointerIndex);
                }
                break;
            }
        }
        return true;
    }

    // Used for the textviews.
    public OnTouchListener mTouchListener = new  OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            view.bringToFront();

            final int X = (int)motionEvent.getRawX();
            final int Y = (int)motionEvent.getRawY();

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if(drawLineActivated) {
                        touchCounter++;
                    }
                    else {
                        touchCounter = 0;
                    }
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    mXDelta = X - lParams.leftMargin;
                    mYDelta = Y - lParams.topMargin;
                    if(touchCounter == 1) {
                        firstPressedView = view;
                    }
                    else if(touchCounter == 2) {
                        secondPressedView = view;
                    }
                    System.out.println(touchCounter+" --- id:"+view.getId());
                    // Save the ID of this pointer
                    mActivePointerId = motionEvent.getPointerId(0);
                    break;

                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    layoutParams.leftMargin = X - mXDelta;
                    layoutParams.topMargin = Y - mYDelta;
                    view.setTranslationX(layoutParams.leftMargin);
                    view.setTranslationY(layoutParams.topMargin);
                    view.setLayoutParams(layoutParams);

                    // TODO: Check foreach drawview, if that drawview has same start or stop coords as the view currently moving. In that case, change that drawviews coords. Or something like that...
                    break;

                case MotionEvent.ACTION_UP:
                    mActivePointerId = INVALID_POINTER_ID;
                    if(firstPressedView != null && secondPressedView != null) {
                        if(firstPressedView.getId() == secondPressedView.getId()) {
                            if(drawLineActivated) {
                                if (touchCounter >= 2) {
                                    touchCounter--;
                                }
                            }
                            else {
                                touchCounter = 0;
                            }
                        }
                    }

                    if (touchCounter == 1) {
                        startX = view.getX() + view.getWidth() / 2;
                        startY = view.getY() + view.getHeight() / 2;

                        System.out.println("startX: "+startX+", startyY: " + startY);
                    }
                    else if (touchCounter == 2) {
                        stopX = view.getX() + view.getWidth() / 2;
                        stopY = view.getY() + view.getHeight() / 2;

                        DrawView drawView = new DrawView(getContext());
                        drawView.setStartCoords(startX, startY);
                        drawView.setStopCoords(stopX, stopY);

                        ZoomableViewGroup.this.addView(drawView);
                        firstPressedView.bringToFront();
                        secondPressedView.bringToFront();
                        invalidate();
                        requestLayout();
                        //drawViewList.add(drawView);

                        //for(View child: drawViewList){
                        //    ZoomableViewGroup.this.addView(child);
                        //}
                        touchCounter = 0;
                    }
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    // Extract the index of the pointer that left the touch sensor
                    final int pointerIndex = (motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerId = motionEvent.getPointerId(pointerIndex);
                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        mLastTouchX = motionEvent.getX(newPointerIndex);
                        mLastTouchY = motionEvent.getY(newPointerIndex);
                        break;
                    }
            }
            return true;
        }
    };

    // TODO: Fix better spawnposition for textviews. Even when you have panned the screen, it should work but now it doesnt.
    public Point getScreenMidPoint() {
        Rect rectf = new Rect();
        getLocalVisibleRect(rectf);


        Log.d("clipBounds   :", String.valueOf(this.getClipBounds()));
        Log.d("Rectf        :", String.valueOf(rectf));
        int centerX = rectf.centerX();
        int centerY = rectf.centerY();

        return new Point(centerX, centerY);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            if (detector.isInProgress()) {
                mFocusX = detector.getFocusX();
                mFocusY = detector.getFocusY();
            }

            mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 3.0f));
            mScaleMatrix.setScale(mScaleFactor, mScaleFactor,mFocusX, mFocusY);
            mScaleMatrix.invert(mScaleMatrixInverse);
            invalidate();
            requestLayout();

            return true;
        }
    }
}
