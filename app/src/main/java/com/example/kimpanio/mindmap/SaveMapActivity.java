package com.example.kimpanio.mindmap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
        final Serializable previousXmlIntent = previousIntent.getSerializableExtra("XML_INTENT");

        saveButton = (Button) findViewById(R.id.saveFileButton);
        editText = (EditText) findViewById(R.id.saveEditText);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isExternalStorageWritable()) {
                    File newxmlfile = new File("/data/" + editText.getText().toString() + ".xml");
                    try{
                        newxmlfile.createNewFile();
                    }catch(IOException e)
                    {
                        Log.e("IOException", "Exception in create new File(");
                    }
                    FileOutputStream fileos = null;
                    try{
                        fileos = new FileOutputStream(newxmlfile);

                    }catch(FileNotFoundException e)
                    {
                        Log.e("FileNotFoundException", e.toString());
                    }
                    try {
                        serializer.setOutput(fileos, "UTF-8");
                        serializer.startDocument(null, Boolean.valueOf(true));
                        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                        serializer.startTag(null, "root");
                        serializer.startTag(null, "Child1");
                        serializer.endTag(null, "Child1");
                        serializer.attribute(null, "attribute", "value");
                        serializer.endTag(null,"root");
                        serializer.endDocument();
                        serializer.flush();
                        fileos.close();
                    }catch(Exception e)
                    {
                        Log.e("Exception","Exception occured in wroting");
                    }
                }
                else {
                    Snackbar.make(view, "No writable external storage!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
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

}
