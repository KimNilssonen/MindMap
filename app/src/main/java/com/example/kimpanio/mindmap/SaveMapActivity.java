package com.example.kimpanio.mindmap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class SaveMapActivity extends AppCompatActivity {

    private Button saveButton;
    private EditText editText;
    private String fileName;
    private FileOutputStream fileOutputStream;
    private FileInputStream fileInputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO: xmlContent get a value of null. Fix this!
        final Serializable xmlContent = getIntent().getExtras().getSerializable("XML_INTENT");

        saveButton = (Button) findViewById(R.id.saveFileButton);
        editText = (EditText) findViewById(R.id.saveEditText);

        //System.out.println(xmlContent);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileName = editText.getText().toString();
                XmlSerializer serializer = Xml.newSerializer();
                //File file = new File(getApplicationContext().getFilesDir(), fileName);

                if (!TextUtils.isEmpty(fileName)) {
                    try {
                        fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                        serializer.setOutput(fileOutputStream, "UTF-8");
                        serializer.startDocument(null, Boolean.valueOf(true));
                        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

                        serializer.startTag(null, "RelativeLayout");

                        for(int j = 0 ; j < 3 ; j++)
                        {

                            serializer.startTag(null, "TextView");

                            serializer.text(xmlContent.toString());

                            serializer.endTag(null, "TextView");
                        }
                        serializer.endDocument();

                        serializer.flush();

                        fileOutputStream.close();

                        editText.setText("");

                        Toast.makeText(getApplicationContext(), "File saved!", Toast.LENGTH_LONG).show();

                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "You must establish a file name!", Toast.LENGTH_LONG).show();
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
