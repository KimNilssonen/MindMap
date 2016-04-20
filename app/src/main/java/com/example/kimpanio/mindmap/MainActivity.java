package com.example.kimpanio.mindmap;

import android.app.ActionBar;
import android.app.usage.UsageEvents;
import android.bluetooth.BluetoothClass;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    Button addTextButton;
    LinearLayout linearLayout;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editText = (EditText) findViewById(R.id.editTextField);
        linearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
        addTextButton = (Button) findViewById(R.id.addTextButton);

        addTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(editText.getText().toString())){
                    textView = new TextView(getApplicationContext());
                    textView.setText(editText.getText());
                    setTextViewStyle(textView);
                    textView.setOnTouchListener(new CustomTouchListener());

                    editText.setText(emptyEditTextField());

                    linearLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    Toast.makeText(view.getContext(), "Success!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(view.getContext(), "Fail!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setTextViewStyle(TextView textView){
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(20);
        textView.setBackgroundColor(Color.CYAN);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
