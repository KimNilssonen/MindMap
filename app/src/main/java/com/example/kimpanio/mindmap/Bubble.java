package com.example.kimpanio.mindmap;

import android.graphics.Color;
import android.graphics.Point;
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
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    public void show(ZoomableViewGroup zoomableViewGroup){
        parentView = zoomableViewGroup;
        textView = new TextView(zoomableViewGroup.getContext());
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

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (parentView.handleBubbleTouch(this))
                    break;
                beingDragged = true;
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                xPositionStart = X - lParams.leftMargin;
                yPositionStart = Y - lParams.topMargin;
                break;

            case MotionEvent.ACTION_MOVE:
                if(!beingDragged)
                    break;
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                positionX = X - xPositionStart;
                positionY = Y - yPositionStart;
                layoutParams.leftMargin = (int) positionX;
                layoutParams.topMargin = (int) positionY;
                view.setTranslationX(layoutParams.leftMargin);
                view.setTranslationY(layoutParams.topMargin);
                view.setLayoutParams(layoutParams);

                for(Connection connection : connections.values()){
                    connection.update();
                }
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
        layoutParams.leftMargin = point.x - textView.getWidth()/2;
        layoutParams.topMargin = point.y - textView.getHeight()/2;
        textView.setTranslationX(layoutParams.leftMargin);
        textView.setTranslationY(layoutParams.topMargin);
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
        for (Object rawUUID : rawSet)
        {
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
        return textView.getWidth();
    }

    public float getHeight() {
        return textView.getHeight();
    }

}
