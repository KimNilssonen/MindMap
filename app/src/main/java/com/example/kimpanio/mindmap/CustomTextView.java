package com.example.kimpanio.mindmap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomTextView extends TextView
{
    private TextView mTextView;
    private final Point mSize = new Point();
    private final Point mStartPosition = new Point();
    private Region mRegion;

    public CustomTextView(Context context)
    {
        super(context);
        mTextView = new TextView(context);
        mRegion = new Region();
    }

    public final TextView getTextView() { return mTextView; }
    public final void setTextViewSize()
    {
        setSize(mTextView.getWidth(), mTextView.getHeight());
    }

    //@Override
    //protected void onDraw(Canvas canvas)
    //{
    //    Point position = getPosition();
    //    canvas.drawText(mTextView.getText().toString(), position.x, position.y,);
    //}

    public final void setPosition(final Point position)
    {
        mRegion.set(position.x, position.y, position.x + mSize.x, position.y + mSize.y);
    }

    public final Point getPosition()
    {
        Rect bounds = mRegion.getBounds();
        return new Point(bounds.left, bounds.top);
    }

    public final void setSize(int width, int height)
    {
        mSize.x = width;
        mSize.y = height;

        Rect bounds = mRegion.getBounds();
        mRegion.set(bounds.left, bounds.top, bounds.left + width, bounds.top + height);
    }

    public final Point getSize() { return mSize; }

}