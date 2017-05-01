package com.example.kimpanio.mindmap;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int count = 0;

        List<CustomColor> colors = new ArrayList<>();

        colors.add(new CustomColor("Blue", "#42c8f4"));
        colors.add(new CustomColor("Yellow", "#efef0b"));
        colors.add(new CustomColor("Red", "#ef1e0a"));
        colors.add(new CustomColor("Purple", "#dc74e0"));
        colors.add(new CustomColor("Green", "#82e073"));
        GridLayout layout = (GridLayout) findViewById(R.id.settingsLayout);

        for(CustomColor c : colors){
            final CustomColor co = c;
            Button btn = new Button(this);

            btn.setText(c.getName());
            btn.setBackgroundColor(Color.parseColor(c.getHex()));
            btn.setPadding(50,50,50,50);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("COLOR_SETTINGS", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("COLOR_CODE", co.getHex());
                    editor.apply();
                    System.out.println("Settings context: "+getApplicationContext());

                    Toast.makeText(view.getContext(), "You have chosen " + co.getName(), Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }
            });
            layout.addView(btn);
            for(int i = 0; i < layout.getChildCount(); i++) {
                if(i%3 != 0) {
                    layout.getChildAt(i).setTranslationX(count * i);
                    count += 100;
                }
                else {
                    count = 0;
                }
            }
        }
    }
}
