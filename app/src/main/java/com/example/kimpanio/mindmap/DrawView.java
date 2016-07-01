package com.example.kimpanio.mindmap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Kimpanio on 2016-06-03.
 */
public class DrawView extends View {
    Paint paint = new Paint();

    // Variables for coords for drawing line.
    private float startX;
    private float startY;
    private float stopX;
    private float stopY;

    public DrawView(Context context) {
        super(context);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    public void setStartCoords(float x, float y) {
        startX = x;
        startY = y;
    }

    public void setStopCoords(float x, float y) {
        stopX = x;
        stopY = y;
    }

    public float[] getCoords() {
        float[] coords = new float[]{startX, startY, stopX, stopY};
        return coords;
    }

    public Paint getPaint() {
        return paint;
    }
}