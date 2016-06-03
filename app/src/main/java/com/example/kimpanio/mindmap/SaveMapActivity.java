package com.example.kimpanio.mindmap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;

public class SaveMapActivity extends AppCompatActivity {

    private Button saveButton;
    private EditText editText;
    private XmlSerializer serializer;
    private FileOutputStream fileOutputStream;
    private FileInputStream fileInputStream;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        serializer = Xml.newSerializer();
        final Intent previousIntent = getIntent();
        final Byte xmlContent = previousIntent.getExtras().getByte("XML_INTENT");
        //final Serializable previousXmlIntent = previousIntent.getSerializableExtra("XML_INTENT");

        saveButton = (Button) findViewById(R.id.saveFileButton);
        editText = (EditText) findViewById(R.id.saveEditText);

        System.out.println(xmlContent);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = editText.getText().toString();
                //File file = new File(getApplicationContext().getFilesDir(), fileName);

                try {
                    fileOutputStream = openFileOutput(fileName, Context.MODE_APPEND);
                    fileOutputStream.write(xmlContent);
                    fileOutputStream.close();
                    editText.setText("");
                    Toast.makeText(getApplicationContext(), "File saved!", Toast.LENGTH_LONG).show();
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    public boolean isSavePermitted(){
        if(Build.VERSION.SDK_INT >= 23){
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("PERMISSION","Permission is granted");
                return true;
            }
        }
        return false;
    }

}
