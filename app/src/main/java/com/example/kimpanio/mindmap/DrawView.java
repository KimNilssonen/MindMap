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

    public DrawView(Context context) {
        super(context);
        paint.setColor(Color.BLACK);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawLine(0, 50, 350, 50, paint);
    }

}