package com.example.kimpanio.mindmap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.*;
import android.widget.RelativeLayout;


public class ZoomableViewGroup extends ViewGroup{

    private static final int INVALID_POINTER_ID = 1;
    private int mActivePointerId = INVALID_POINTER_ID;
    private boolean drawLineActivated = false;
    private int touchCounter;

    private float mScaleFactor = 1;
    private ScaleGestureDetector mScaleDetector;
    private Matrix mScaleMatrix = new Matrix();
    private Matrix mScaleMatrixInverse = new Matrix();

    private float mPosX;
    private float mPosY;
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
    private DrawView drawView;

    public ZoomableViewGroup(Context context) {
        super(context);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mTranslateMatrix.setTranslate(0, 0);
        mScaleMatrix.setScale(1, 1);
        drawView = new DrawView(context);
        touchCounter = 0;
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
        if(drawLineActivated) {
            drawView.draw(canvas);
        }
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

        System.out.println("Hej jag är ZooooomVY!!");
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
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    mXDelta = X - lParams.leftMargin;
                    mYDelta = Y - lParams.topMargin;

                    //TODO: Make line not dissappear and should be able to have more than one line.
                    if(drawLineActivated) {
                        touchCounter = 0;
                        drawLineActivated = false;
                    }

                    touchCounter++;
                    if (touchCounter == 1) {
                        System.out.println("Inne i ==1, ");
                        drawView.setStartCoords(view.getX() + view.getWidth() / 2, view.getY() + view.getHeight() / 2);
                        //drawLineActivated = false;
                    }
                    else if (touchCounter == 2) {
                        drawView.setStopCoords(view.getX() + view.getWidth() / 2, view.getY() + view.getHeight() / 2);
                        drawLineActivated = true;
                        System.out.println("INNE i ==2, ");
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
                    invalidate();
                    break;

                case MotionEvent.ACTION_UP:
                    mActivePointerId = INVALID_POINTER_ID;
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
            System.out.println(touchCounter+" -> touchcounter");
            return true;
        }
    };

    private boolean isChildTextView(View view){
        if(view.getTag() == "TextView") {
            return true;
        }
        return false;
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
