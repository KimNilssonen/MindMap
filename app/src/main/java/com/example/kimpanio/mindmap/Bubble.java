package com.example.kimpanio.mindmap;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
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
    private float xPositionStart;
    private float yPositionStart;
    private float positionX;
    private float positionY;
    private float width;
    private float height;
    private boolean beingDragged = false;
    private String color;

    private UUID id;
    private HashMap<UUID, Connection> connections;
    private HashSet<UUID> looseConnections;

    public TextView textView;
    private ZoomableViewGroup parentView;

    public Bubble(ZoomableViewGroup zoomableViewGroup, TextView tv, String string, float x, float y){
        content = string;
        positionX = x;
        positionY = y;
        textView = tv;
        parentView = zoomableViewGroup;
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

    public GradientDrawable setDrawableProperties() {
        SharedPreferences sp =  parentView.getContext().getApplicationContext().getSharedPreferences("COLOR_SETTINGS", Activity.MODE_PRIVATE);
        color = sp.getString("COLOR_CODE", "#ff7000");

        int strokeWidth = 2;
        int strokeColor = Color.parseColor("#000000");
        int fillColor = Color.parseColor(color);

        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(fillColor);
        gd.setStroke(strokeWidth, strokeColor);

        return gd;
    }

    private void setTextViewProperties(){
        textView.setBackground(setDrawableProperties());
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(50, 50, 50, 50);
        textView.setText(content);
    }

    private void setTextViewSpawnPoint() {

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        //TODO: "Bubbles spawn in the middle, but only until I start pan the screen."
        Point point = parentView.getScreenMidPoint();
        layoutParams.leftMargin = point.x;
        layoutParams.topMargin = point.y;

        if(positionX == 0 && positionY == 0) {
            textView.setTranslationX(layoutParams.leftMargin);
            textView.setTranslationY(layoutParams.topMargin);
            positionX = layoutParams.leftMargin;
            positionY = layoutParams.topMargin;
        }
        else {
            layoutParams.leftMargin = (int)positionX + textView.getWidth()/2;
            layoutParams.topMargin = (int)positionY + textView.getHeight()/2;
            textView.setTranslationX(layoutParams.leftMargin);
            textView.setTranslationY(layoutParams.topMargin);
            positionX = layoutParams.leftMargin;
            positionY = layoutParams.topMargin;

        }
        textView.setLayoutParams(layoutParams);
        textView.measure(0,0);
        width = textView.getMeasuredWidth();
        height = textView.getMeasuredHeight();
        updateConnections();
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
        return width;
    }

    public float getHeight() {
        return height;
    }

}
