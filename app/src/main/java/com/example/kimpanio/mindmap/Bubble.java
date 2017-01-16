package com.example.kimpanio.mindmap;


import android.graphics.Color;

import android.graphics.Point;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by Kimpanio on 2016-07-25.
 */
public class Bubble implements Serializable, View.OnTouchListener {

    private String content;
    private int xPositionStart;
    private int yPositionStart;
    private float positionX;
    private float positionY;
    private boolean beingDragged = false;

    private UUID id;
    private HashMap<UUID, Connection> connections;
    private HashSet<UUID> looseConnections;

    private TextView textView;
    private ZoomableViewGroup parentView;

    public Bubble(String string, float x, float y){
        content = string;
        positionX = x;
        positionY = y;
        id = UUID.randomUUID();
        connections = new HashMap<UUID, Connection>();
        looseConnections = new HashSet<UUID>();
    }

    public void setParentView(ZoomableViewGroup zoomableViewGroup) {
        parentView = zoomableViewGroup;
    }

    public void setTextView() {
        textView = new TextView(parentView.getContext());
    }

    public void show(ZoomableViewGroup zoomableViewGroup){
        if(parentView == null) {
            setParentView(zoomableViewGroup);
        }
        if(textView == null) {
            setTextView();
        }
        setTextViewProperties();
        setTextViewSpawnPoint();
        textView.setOnTouchListener(this);
        parentView.addView(textView);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        view.bringToFront();

        final int X = (int)motionEvent.getRawX();
        final int Y = (int)motionEvent.getRawY();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (parentView.handleBubbleTouch(this))
                    break;
                beingDragged = true;
                xPositionStart = X - layoutParams.leftMargin;
                yPositionStart = Y - layoutParams.topMargin;
                break;

            case MotionEvent.ACTION_MOVE:
                if(!beingDragged)
                    break;
                positionX = X - xPositionStart;
                positionY = Y - yPositionStart;

                layoutParams.leftMargin = (int) positionX;
                layoutParams.topMargin = (int) positionY;
                view.setTranslationX(layoutParams.leftMargin);
                view.setTranslationY(layoutParams.topMargin);
                view.setLayoutParams(layoutParams);

                updateConnections();
                break;

            case MotionEvent.ACTION_UP:
                beingDragged = false;
                break;
        }
        return true;
    }

    public void connect(Bubble otherBubble){
        if(connections.containsKey(otherBubble.id) || otherBubble == this)
            return;
        Connection connection = new Connection(this, otherBubble);
        connections.put(otherBubble.id, connection);
        otherBubble.connections.put(this.id, connection);
        looseConnections.add(otherBubble.id);
        otherBubble.looseConnections.add(this.id);

        connection.show(parentView);
    }

    public void reconnect(HashMap<UUID, Bubble> allBubbles) {
        for(UUID otherId : looseConnections) {
            Bubble other = allBubbles.get(otherId);
            if (other == null || connections.containsKey(otherId))
                continue;
            connect(other);
        }
    }

    public void updateConnections(){
        for(Connection connection : connections.values()){
            connection.update();
        }
    }

    private void setTextViewProperties(){
        textView.setBackgroundResource(R.drawable.background);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(20, 20, 20, 20);
        textView.setText(content);
    }

    private void setTextViewSpawnPoint() {

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        //TODO: "Bubbles spawn in the middle, but only until I start pan the screen."
        Point point = parentView.getScreenMidPoint();
        layoutParams.leftMargin = point.x + textView.getWidth()/2;
        layoutParams.topMargin = point.y + textView.getHeight()/2;
        if(positionX == 0 && positionY == 0) {
            textView.setTranslationX(layoutParams.leftMargin);
            textView.setTranslationY(layoutParams.topMargin);
        }
        else {
            layoutParams.leftMargin = (int)positionX;
            layoutParams.topMargin = (int)positionY;
            textView.setTranslationX(layoutParams.leftMargin);
            textView.setTranslationY(layoutParams.topMargin);
        }
        textView.setLayoutParams(layoutParams);


    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException{
        outputStream.writeObject(id);
        outputStream.writeObject(content);
        outputStream.writeFloat(positionX);
        outputStream.writeFloat(positionY);
        outputStream.writeObject(looseConnections.toArray());
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException{
        id = (UUID)inputStream.readObject();
        content = (String)inputStream.readObject();
        positionX = inputStream.readFloat();
        positionY = inputStream.readFloat();
        connections = new HashMap<UUID, Connection>();
        looseConnections = new HashSet<UUID>();
        Object[] rawSet = (Object[])inputStream.readObject();
        for (Object rawUUID : rawSet) {
            looseConnections.add((UUID)rawUUID);
        }
    }

    private void readObjectNoData() throws ObjectStreamException{
        System.out.println("No Data. Should be an Exception!");
    }

    public UUID getId() {
        return id;
    }

    public float getPositionX(){
        return positionX;
    }

    public float getPositionY(){
        return positionY;
    }

    public float getWidth(){
        if(textView == null) {
            setTextView();
        }
        return textView.getWidth();
    }

    public float getHeight() {
        if(textView == null) {
            setTextView();
        }
        return textView.getHeight();
    }

}
