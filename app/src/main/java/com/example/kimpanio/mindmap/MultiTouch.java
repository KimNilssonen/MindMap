package com.example.kimpanio.mindmap;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class MultiTouch extends RelativeLayout{

    private float mPositionX = 0;
    private float mPositionY = 0;
    private float mScale = 1.0f;

    public MultiTouch(Context context) {
        super(context);
        this.setWillNotDraw(false);
        this.setOnTouchListener(mTouchListener);
    }

    public void setPosition(float lPositionX, float lPositionY){
        mPositionX = lPositionX;
        mPositionY = lPositionY;
    }

    public void setMovingPosition(float lPositionX, float lPositionY){
        mPositionX += lPositionX;
        mPositionY += lPositionY;
    }

    public void setScale(float lScale){
        mScale = lScale;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.translate(mPositionX*mScale, mPositionY*mScale);
        canvas.scale(mScale, mScale);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    // touch events
    private final int NONE = 0;
    private final int DRAG = 1;
    private final int ZOOM = 2;
    private final int CLICK = 3;

    // pinch to zoom
    private float mOldDist;
    private float mNewDist;
    private float mScaleFactor = 0.01f;

    // position
    private float mPreviousX;
    private float mPreviousY;

    int mode = NONE;

    @SuppressWarnings("deprecation")
    public OnTouchListener mTouchListener = new  OnTouchListener(){
        public boolean onTouch(View v, MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN: // one touch: drag
                    mode = CLICK;
                    break;
                case MotionEvent.ACTION_POINTER_2_DOWN: // two touches: zoom
                    mOldDist = spacing(e);
                    mode = ZOOM; // zoom
                    break;
                case MotionEvent.ACTION_UP: // no mode
                    mode = NONE;
                    break;
                case MotionEvent.ACTION_POINTER_2_UP: // no mode
                    mode = NONE;
                    break;
                case MotionEvent.ACTION_MOVE: // rotation
                    if (e.getPointerCount() > 1 && mode == ZOOM) {
                        mNewDist = spacing(e) - mOldDist;

                        mScale += mNewDist*mScaleFactor;
                        invalidate();

                        mOldDist = spacing(e);

                    } else if (mode == CLICK || mode == DRAG) {
                        float dx = (x - mPreviousX)/mScale;
                        float dy = (y - mPreviousY)/mScale;

                        setMovingPosition(dx, dy);
                        invalidate();
                        mode = DRAG;
                    }
                    break;
            }
            mPreviousX = x;
            mPreviousY = y;
            return true;
        }
    };

    // finds spacing
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }
}


//-----------------------------------------------------------------
//package com.example.kimpanio.mindmap;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Matrix;
//import android.graphics.Rect;
//import android.view.*;
//
//// Doesn't work as a setOnTouchListener. Don't know how to implement this class on different views.
//// Using CustomTouchListener class for now...
//public class MultiTouch extends ViewGroup {
//
//    private static final int INVALID_POINTER_ID = 1;
//    private int mActivePointerId = INVALID_POINTER_ID;
//
//    private float mScaleFactor = 1;
//    private ScaleGestureDetector mScaleDetector;
//    private Matrix mScaleMatrix = new Matrix();
//    private Matrix mScaleMatrixInverse = new Matrix();
//
//    private float mPosX;
//    private float mPosY;
//    private Matrix mTranslateMatrix = new Matrix();
//    private Matrix mTranslateMatrixInverse = new Matrix();
//
//    private float mLastTouchX;
//    private float mLastTouchY;
//
//    private float mFocusY;
//
//    private float mFocusX;
//
//    private float[] mInvalidateWorkingArray = new float[6];
//    private float[] mDispatchTouchEventWorkingArray = new float[2];
//    private float[] mOnTouchEventWorkingArray = new float[2];
//
//
//    public MultiTouch(Context context) {
//        super(context);
//        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
//        mTranslateMatrix.setTranslate(0, 0);
//        mScaleMatrix.setScale(1, 1);
//    }
//
//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        int childCount = getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View child = getChildAt(i);
//            if (child.getVisibility() != GONE) {
//                child.layout(l, t, l+child.getMeasuredWidth(), t + child.getMeasuredHeight());
//            }
//        }
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//        int childCount = getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View child = getChildAt(i);
//            if (child.getVisibility() != GONE) {
//                measureChild(child, widthMeasureSpec, heightMeasureSpec);
//            }
//        }
//    }
//
//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//        canvas.save();
//        canvas.translate(mPosX, mPosY);
//        canvas.scale(mScaleFactor, mScaleFactor, mFocusX, mFocusY);
//        super.dispatchDraw(canvas);
//        canvas.restore();
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        mDispatchTouchEventWorkingArray[0] = ev.getX();
//        mDispatchTouchEventWorkingArray[1] = ev.getY();
//        mDispatchTouchEventWorkingArray = screenPointsToScaledPoints(mDispatchTouchEventWorkingArray);
//        ev.setLocation(mDispatchTouchEventWorkingArray[0],
//                mDispatchTouchEventWorkingArray[1]);
//        return super.dispatchTouchEvent(ev);
//    }
//
//    /**
//     * Although the docs say that you shouldn't override this, I decided to do
//     * so because it offers me an easy way to change the invalidated area to my
//     * likening.
//     */
//    @Override
//    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
//
//        mInvalidateWorkingArray[0] = dirty.left;
//        mInvalidateWorkingArray[1] = dirty.top;
//        mInvalidateWorkingArray[2] = dirty.right;
//        mInvalidateWorkingArray[3] = dirty.bottom;
//
//
//        mInvalidateWorkingArray = scaledPointsToScreenPoints(mInvalidateWorkingArray);
//        dirty.set(Math.round(mInvalidateWorkingArray[0]), Math.round(mInvalidateWorkingArray[1]),
//                Math.round(mInvalidateWorkingArray[2]), Math.round(mInvalidateWorkingArray[3]));
//
//        location[0] *= mScaleFactor;
//        location[1] *= mScaleFactor;
//        return super.invalidateChildInParent(location, dirty);
//    }
//
//    private float[] scaledPointsToScreenPoints(float[] a) {
//        mScaleMatrix.mapPoints(a);
//        mTranslateMatrix.mapPoints(a);
//        return a;
//    }
//
//    private float[] screenPointsToScaledPoints(float[] a){
//        mTranslateMatrixInverse.mapPoints(a);
//        mScaleMatrixInverse.mapPoints(a);
//        return a;
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        mOnTouchEventWorkingArray[0] = ev.getX();
//        mOnTouchEventWorkingArray[1] = ev.getY();
//
//        mOnTouchEventWorkingArray = scaledPointsToScreenPoints(mOnTouchEventWorkingArray);
//
//        ev.setLocation(mOnTouchEventWorkingArray[0], mOnTouchEventWorkingArray[1]);
//        mScaleDetector.onTouchEvent(ev);
//
//        final int action = ev.getAction();
//        switch (action & MotionEvent.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN: {
//                final float x = ev.getX();
//                final float y = ev.getY();
//
//                mLastTouchX = x;
//                mLastTouchY = y;
//
//                // Save the ID of this pointer
//                mActivePointerId = ev.getPointerId(0);
//                break;
//            }
//
//            case MotionEvent.ACTION_MOVE: {
//                // Find the index of the active pointer and fetch its position
//                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
//                final float x = ev.getX(pointerIndex);
//                final float y = ev.getY(pointerIndex);
//
//                final float dx = x - mLastTouchX;
//                final float dy = y - mLastTouchY;
//
//                mPosX += dx;
//                mPosY += dy;
//                mTranslateMatrix.preTranslate(dx, dy);
//                mTranslateMatrix.invert(mTranslateMatrixInverse);
//
//                mLastTouchX = x;
//                mLastTouchY = y;
//
//                invalidate();
//                break;
//            }
//
//            case MotionEvent.ACTION_UP: {
//                mActivePointerId = INVALID_POINTER_ID;
//                break;
//            }
//
//            case MotionEvent.ACTION_CANCEL: {
//                mActivePointerId = INVALID_POINTER_ID;
//                break;
//            }
//
//            case MotionEvent.ACTION_POINTER_UP: {
//                // Extract the index of the pointer that left the touch sensor
//                final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
//                final int pointerId = ev.getPointerId(pointerIndex);
//                if (pointerId == mActivePointerId) {
//                    // This was our active pointer going up. Choose a new
//                    // active pointer and adjust accordingly.
//                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
//                    mLastTouchX = ev.getX(newPointerIndex);
//                    mLastTouchY = ev.getY(newPointerIndex);
//                    mActivePointerId = ev.getPointerId(newPointerIndex);
//                }
//                break;
//            }
//        }
//        return true;
//    }
//
//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            mScaleFactor *= detector.getScaleFactor();
//            if (detector.isInProgress()) {
//                mFocusX = detector.getFocusX();
//                mFocusY = detector.getFocusY();
//            }
//            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
//            mScaleMatrix.setScale(mScaleFactor, mScaleFactor,
//                    mFocusX, mFocusY);
//            mScaleMatrix.invert(mScaleMatrixInverse);
//            invalidate();
//            requestLayout();
//
//
//            return true;
//        }
//    }
//}