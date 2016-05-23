package com.example.kimpanio.mindmap;

import android.app.Application;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ZoomableViewGroup zoomableViewGroup;
    private CustomTouchListener customTouchListener;

    private EditText editText;
    private Button addTextButton;
    private ViewGroup addTextLayout;
    private ViewGroup rootLayout;
    private ViewGroup mapLayout;
    private CustomTextView customTextView;
    private TextView textView;
    private RelativeLayout.LayoutParams layoutParam;

    private int mScreenWidth;
    private int mScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Retrieve the device dimensions to adapt interface
        mScreenWidth = getApplicationContext().getResources()
                .getDisplayMetrics().widthPixels;
        mScreenHeight = getApplicationContext().getResources()
                .getDisplayMetrics().heightPixels;

        zoomableViewGroup = new ZoomableViewGroup(this);
        zoomableViewGroup.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
        zoomableViewGroup.setBackgroundColor(Color.parseColor("#32000000"));

        rootLayout = (ViewGroup) findViewById(R.id.root);
        editText = (EditText) findViewById(R.id.editTextField);
        addTextButton = (Button) findViewById(R.id.addTextButton);
        addTextLayout = (RelativeLayout) findViewById(R.id.addTextLayout);
        mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);



        addTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(editText.getText())) {

                    RelativeLayout.LayoutParams layoutParams =  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);

                    textView = new TextView(MainActivity.this);
                    setTextViewProperties(textView);
                    textView.setOnTouchListener(zoomableViewGroup.mTouchListener);
                    textView.setLayoutParams(layoutParams);
                    zoomableViewGroup.addView(textView);

                    //createTextView(50,50);
                    editText.setText(emptyEditTextField());
                    Toast.makeText(view.getContext(), "Success!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(), "Fail!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mapLayout.setBackgroundColor(Color.TRANSPARENT);
        mapLayout.addView(zoomableViewGroup);
    }

    public void setTextViewProperties(TextView textView){
        textView.setBackgroundResource(R.drawable.background);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(20, 20, 20, 20);
        textView.setTag("TextView");
        textView.setText(editText.getText());

    }

//    // Creation of a textview element.
//    private TextView createTextView(int pPosX, int pPosY) {
//
//        customTextView = new CustomTextView(MainActivity.this);
//        setTextViewProperties(customTextView);
//
//        Point lPoint = new Point();
//        lPoint.x = pPosX;
//        lPoint.y = pPosY;
//        customTextView.setPosition(lPoint);
//
//        return customTextView;
//    }

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
            return true;
        }

        if (id == R.id.save_map) {
            //TODO: SaveToDevice.
        }

        return super.onOptionsItemSelected(item);
    }
}