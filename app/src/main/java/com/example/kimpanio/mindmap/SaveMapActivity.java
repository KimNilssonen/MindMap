package com.example.kimpanio.mindmap;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SaveMapActivity extends AppCompatActivity {

    private Button saveButton;
    private EditText editText;
    private String fileName;
    private FileOutputStream fileOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveButton = (Button) findViewById(R.id.saveFileButton);
        editText = (EditText) findViewById(R.id.saveEditText);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Serializable mapData = getIntent().getSerializableExtra("MAP");
                fileName = editText.getText().toString();

                if (!TextUtils.isEmpty(fileName)) {
                    try {
                        File file = new File(getFilesDir(), fileName);
                        if (!file.exists())
                            file.createNewFile();
                        fileOutputStream = new FileOutputStream(file);
                        ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
                        oos.writeObject(mapData);
                        oos.close();

                        fileOutputStream.close();
                        editText.setText("");

                        Toast.makeText(getApplicationContext(), "File saved!", Toast.LENGTH_LONG).show();

                        finish();

                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }catch(IOException e){
                        e.printStackTrace();
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "You must establish a file name!", Toast.LENGTH_LONG).show();
                }
            }
        });
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
