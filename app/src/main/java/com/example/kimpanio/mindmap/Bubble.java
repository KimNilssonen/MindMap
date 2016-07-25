package com.example.kimpanio.mindmap;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kimpanio on 2016-07-25.
 */
public class Bubble implements Serializable{

    private String content;
    private float xPosition;
    private float yPosition;

    private UUID id;
    private ArrayList<UUID> connections;

    private TextView textView;
    private ZoomableViewGroup parentView;

    public Bubble(String string, float x, float y){
        content = string;
        xPosition = x;
        yPosition = y;
        id = UUID.randomUUID();
        connections = new ArrayList<UUID>();
    }

    public void show(ZoomableViewGroup zoomableViewGroup){
        parentView = zoomableViewGroup;
        textView = new TextView(zoomableViewGroup.getContext());
        parentView.addView(textView);
    }

    public void connect(Bubble otherBubble){
        if(connections.contains(otherBubble.id))
            return;
        connections.add(otherBubble.id);

    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException{
        outputStream.writeObject(id);
        outputStream.writeObject(content);
        outputStream.writeFloat(xPosition);
        outputStream.writeFloat(yPosition);
        outputStream.writeObject(connections);
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException{
        id = (UUID)inputStream.readObject();
        content = (String)inputStream.readObject();
        xPosition = inputStream.readFloat();
        yPosition = inputStream.readFloat();
        connections = (ArrayList<UUID>)inputStream.readObject();
    }

    private void readObjectNoData() throws ObjectStreamException{
        System.out.println("No Data. Should be an Exception!");
    }

}
