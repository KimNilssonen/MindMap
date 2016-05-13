package com.example.kimpanio.mindmap;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class CustomTouchListener extends Activity implements View.OnTouchListener {

    private int mXDelta;
    private int mYDelta;
    private float mLeft;
    private float mTop;
    private float mPositionX;
    private float mPositionY;
    private float mLastTouchX;
    private float mLastTouchY;
    private int mActivePointerId;


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        view.bringToFront();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        final float X = motionEvent.getX();
        final float Y = motionEvent.getY();
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                System.out.println("CustomTouch: DOWN");

                mXDelta = (int)X - layoutParams.leftMargin;
                mYDelta = (int)Y - layoutParams.topMargin;

                //mActivePointerId = motionEvent.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                System.out.println("CustomTouch: MOVE");
                //layoutParams.leftMargin = (int)X - mXDelta;
                //layoutParams.topMargin = (int)Y - mYDelta;

                mLeft = X-mXDelta;
                mTop = Y-mYDelta;

                view.setTranslationX(mLeft);
                view.setTranslationY(mTop);
                System.out.println("X: " + X + "\nY: " + Y);
                System.out.println("ViewX: " + view.getX() + "\nViewY: " + view.getY());
                //view.invalidate();
                break;
            }

            case MotionEvent.ACTION_UP:
                System.out.println("ActionUP\n---------------------------------\nViewX: " + view.getX() + "\nViewY: " + view.getY());
                break;

            case MotionEvent.ACTION_CANCEL:
                System.out.println("ActionCANCEL\n---------------------------------\nViewX: " + view.getX() + "\nViewY: " + view.getY());
                break;
        }
        return true;
    }
}