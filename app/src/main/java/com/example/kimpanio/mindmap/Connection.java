package com.example.kimpanio.mindmap;

import android.os.Build;

/**
 * Created by Kimpanio on 2016-07-25.
 */
public class Connection {
    private Bubble bubbleA;
    private Bubble bubbleB;

    private LineView lineView;
    private ZoomableViewGroup parentView;

    public Connection(Bubble bubbleA, Bubble bubbleB){
        this.bubbleA = bubbleA;
        this.bubbleB = bubbleB;
    }

    public void show(ZoomableViewGroup zoomableViewGroup){
        parentView = zoomableViewGroup;
        lineView = new LineView(zoomableViewGroup.getContext());
        parentView.addView(lineView);
        update();
    }

    public void update() {
//        if(parentView != null){
//            bubbleA.setParentView(parentView);
//            bubbleB.setParentView(parentView);
//        }

        lineView.setStart(bubbleA);
        lineView.setStop(bubbleB);

        lineView.invalidate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lineView.setTranslationZ(-1);
        }
    }

}
