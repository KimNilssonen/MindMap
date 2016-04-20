package com.example.kimpanio.mindmap;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Kimpanio on 2016-04-20.
 */
public class CustomTouchListener extends MainActivity implements View.OnTouchListener {

    /*TODO: Fix on touch event. Start with get the coords out when holding finger on textview!
            Commented lines does not work atm*/

    public boolean onTouch(View view, MotionEvent motionEvent) {
        //TextView coords = (TextView) findViewById(R.id.coordsTextView);
        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN: {

                break;
            }

            case MotionEvent.ACTION_MOVE:{
                float xCoords = motionEvent.getRawX();
                float yCoords = motionEvent.getRawY();

                //coords.setText("x:" + xCoords + " y:" + yCoords);

                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // Action you you want on finger up
                break;
        }
        return true;
    }
}
