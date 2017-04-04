package com.ihmtek.ludomuseconfig;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    static final int READ_WRITE_EXTERNAL_STORAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        int readPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (readPermissionCheck == PackageManager.PERMISSION_DENIED || writePermissionCheck == PackageManager.PERMISSION_DENIED)
        {
            Log.d("LUDOCONFIG", "requesting read/write permissions");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_WRITE_EXTERNAL_STORAGE);
        }
        else
        {
            initJsonFiles();
        }

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




    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == READ_WRITE_EXTERNAL_STORAGE
                && grantResults.length == 2
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED)
        {


            initJsonFiles();
        }

    }


    private void initJsonFiles()
    {
        final File ludoMuseRoot = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/LudoMuse/");
        if (!ludoMuseRoot.exists()) {
            Log.d("LUDOCONFIG", "creating directory " + ludoMuseRoot.getAbsolutePath());
            Log.d("LUDOCONFIG", "success ? " + ludoMuseRoot.mkdir());
        }

        Log.d("LUDOCONFIG", ludoMuseRoot.getAbsolutePath());

        ArrayList<String> jsonFiles = new ArrayList<>();

        String[] children = ludoMuseRoot.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith(".json") && !name.equals("main.json")) {
                    return true;
                }

                return false;
            }
        });

        if (children != null) {

            for (String child : children) {
                jsonFiles.add(child);
            }

        } else {
            Log.d("LUDOCONFIG", "no LudoMuse directory found or readable");
        }

        ListView view = (ListView) findViewById(R.id.list_view);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.text_view, jsonFiles);
        view.setAdapter(adapter);

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("LUDOCONFIG", id + " at " + position);
                String filename = adapter.getItem(position);
                File selected = new File(ludoMuseRoot + "/" + filename);
                File main = new File(ludoMuseRoot + "/main.json");
                try {
                    copyFile(selected, main);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // TODO display chekmark on item
                //ImageView image = (ImageView) view.findViewById(R.id.item_icon);
            }
        });

    }


    private void copyFile(File src, File dest) throws IOException
    {
        Log.d("LUDOCONFIG", "copying file " + src + " to " + dest);
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dest);

        byte[] buff = new byte[1024];
        int len;
        while ((len = in.read(buff)) > 0)
        {
            out.write(buff, 0, len);
        }
        in.close();
        out.close();
    }

}
