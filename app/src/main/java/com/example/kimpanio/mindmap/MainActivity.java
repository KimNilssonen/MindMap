package com.example.kimpanio.mindmap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private ZoomableViewGroup zoomableViewGroup;

    private EditText editText;
    private Button addTextButton;
    private ViewGroup addTextLayout;
    private ViewGroup rootLayout;
    private ViewGroup mapLayout;
    private HashMap<UUID, Bubble> bubbleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        zoomableViewGroup = new ZoomableViewGroup(MainActivity.this);
        zoomableViewGroup.setClipChildren(false);

        if(getIntent().hasExtra("MAP")) {
            renderBubbleMap(getIntent().getSerializableExtra("MAP"));
        }
        else {
            bubbleMap = new HashMap<UUID, Bubble>();
        }

        rootLayout = (ViewGroup) findViewById(R.id.root);
        editText = (EditText) findViewById(R.id.editTextField);
        addTextButton = (Button) findViewById(R.id.addTextButton);
        addTextLayout = (RelativeLayout) findViewById(R.id.addTextLayout);
        mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);

        addTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(editText.getText())) {
                    Bubble bubble = new Bubble(zoomableViewGroup, new TextView(MainActivity.this), editText.getText().toString(),0,0);
                    bubbleMap.put(bubble.getId(), bubble);
                    bubble.show(zoomableViewGroup);

                    editText.setText(emptyEditTextField());
                    Toast.makeText(view.getContext(), "Success!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(), "Fail!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mapLayout.addView(zoomableViewGroup);
    }

    public void saveMap(View view) {
        Intent intent = new Intent(this, SaveMapActivity.class);
        intent.putExtra("MAP", bubbleMap);
        startActivity(intent);
    }

    public void openMap(View view) {
        Intent intent = new Intent(this, OpenMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void drawLines(ZoomableViewGroup view, MenuItem item) {
        if(!view.drawLineActivated) {
            item.setIcon(getResources().getDrawable(R.drawable.line_activated));
            view.drawLineActivated = true;
            Toast.makeText(view.getContext(), "Drawing line activated!", Toast.LENGTH_SHORT).show();
        }
        else {
            item.setIcon(getResources().getDrawable(R.drawable.line));
            view.drawLineActivated = false;
            Toast.makeText(view.getContext(), "Drawing line de-activated!", Toast.LENGTH_SHORT).show();
        }
    }

    public void renderBubbleMap(Serializable mapData) {
        bubbleMap = (HashMap) mapData;
        for(Bubble bubble: bubbleMap.values()) {
            bubble.show(zoomableViewGroup);
            bubble.reconnect(bubbleMap);
        }
    }

    public void newBoard() {
        bubbleMap.values().clear();
        zoomableViewGroup.removeAllViews(); // Used to visually remove and re-draw the view.
    }

    public String emptyEditTextField(){
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
        }

        if(id == R.id.clear_board) {
            newBoard();
        }

        if (id == R.id.save_map) {
            saveMap(mapLayout);
        }

        if(id == R.id.open_map) {
            openMap(mapLayout);
        }

        if(id == R.id.draw_line) {
            drawLines(zoomableViewGroup, item);
        }
        if(id == R.id.exit_application) {
            this.finishAffinity();
        }

        return super.onOptionsItemSelected(item);
    }
}