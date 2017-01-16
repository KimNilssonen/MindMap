package com.example.kimpanio.mindmap;

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
        update();
        parentView.addView(lineView);
    }

    public void update() {
        lineView.setStartCoords(bubbleA.getPositionX() + bubbleA.getWidth()/2, bubbleA.getPositionY() + bubbleA.getHeight()/2);
        lineView.setStopCoords(bubbleB.getPositionX() + bubbleB.getWidth()/2, bubbleB.getPositionY() + bubbleB.getHeight()/2);
        lineView.invalidate();
    }

}
