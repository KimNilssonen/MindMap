package com.example.kimpanio.mindmap;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class OpenMapActivity extends AppCompatActivity {

    private ListView listView;
    private File[] fileList;
    ArrayAdapter<File> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.openMapListView);

        File dir = new File(getFilesDir().getPath());
        fileList = dir.listFiles();

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, fileList);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);

        // Used for opening files on click.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openFile(adapter.getItem(position));
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void deleteFile(File file){
        file.delete();
        Toast.makeText(getApplicationContext(), "File deleted!", Toast.LENGTH_LONG).show();
        finish();
        startActivity(getIntent());
    }

    public void openFile(File file) {
        try {
            //TODO: FIX the lines when opening a file!
            Intent intent = new Intent(this, MainActivity.class);
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));

            intent.putExtra("MAP", (HashMap) ois.readObject());
            ois.close();

            Toast.makeText(getApplicationContext(), "File opened!", Toast.LENGTH_LONG).show();
            startActivity(intent);

        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.open_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.delete_settings:
                deleteFile(adapter.getItem(info.position));
            default:
                return super.onContextItemSelected(item);
        }
    }

}
