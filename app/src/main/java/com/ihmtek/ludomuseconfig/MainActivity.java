package com.ihmtek.ludomuseconfig;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    static final int READ_WRITE_EXTERNAL_STORAGE = 0;
    private static final int OPEN_DOCUMENT_REQUEST_CODE = 42;

    private MenuItem runMenuItem;
    private boolean canInitJsonFiles = false;
    private String runMessage = "";
    private boolean canRunLudoMuse = false;

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
            canInitJsonFiles = true;
        }

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        runMenuItem = menu.findItem(R.id.action_run);
        if (canInitJsonFiles)
        {
            initJsonFiles();
        }
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.action_add:
                Toast addFeedback = Toast.makeText(this, "Import d'un package LudoMuse ...", Toast.LENGTH_LONG);
                addFeedback.show();
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                //intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, OPEN_DOCUMENT_REQUEST_CODE);
                break;
            case R.id.action_run:
                Toast runFeedback = Toast.makeText(this, runMessage, Toast.LENGTH_LONG);
                runFeedback.show();

                if (canRunLudoMuse)
                {
                    Intent ludoMuseIntent = getPackageManager().getLaunchIntentForPackage("com.IHMTEK.LudoMuse");
                    if (ludoMuseIntent != null) {
                        startActivity(ludoMuseIntent);
                    } else {
                        Toast error = Toast.makeText(this, "LudoMuse introuvable. L'application est-elle installée ?", Toast.LENGTH_LONG);
                        error.show();
                    }
                }
                break;
            case R.id.action_sync:
                Toast syncFeedback = Toast.makeText(this, "Rechargement des scénarios ...", Toast.LENGTH_LONG);
                syncFeedback.show();
                initJsonFiles();
                break;
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
            canInitJsonFiles = true;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData)
    {
        if (requestCode == OPEN_DOCUMENT_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            Uri uri = null;
            if (resultData != null)
            {
                uri = resultData.getData();
                Log.i("LUDOCONFIG", "open zip : " + uri.getEncodedPath());
            }
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

        ArrayList<Model> jsonFiles = new ArrayList<>();

        String[] subfolders = ludoMuseRoot.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                if (file.isDirectory()) {
                    return true;
                }

                return false;
            }
        });

        if (subfolders != null) {

            for (String subfolder : subfolders) {
                File fSubfolder = new File(ludoMuseRoot.getAbsolutePath() + "/" + subfolder);
                String[] subJSONs = fSubfolder.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File file, String s) {
                        if (s.endsWith(".json"))
                        {
                            return true;
                        }
                        return false;
                    }
                });

                if (subJSONs != null) {
                    for (String subJSON : subJSONs) {
                        jsonFiles.add(new Model(R.drawable.ic_none, subfolder + "/" + subJSON.replace(".json", "")));
                    }
                }
            }

        } else {
            Log.d("LUDOCONFIG", "no LudoMuse directory found or readable");
        }

        ListView view = (ListView) findViewById(R.id.list_view);
        final JsonFileAdapter adapter = new JsonFileAdapter(this, jsonFiles);
        view.setAdapter(adapter);

        final File confFile = new File(ludoMuseRoot + "/LudoMuse.conf");

        try {
            BufferedReader br = new BufferedReader(new FileReader(confFile));
            String selectedJson = br.readLine();
            br.close();
            selectedJson = selectedJson.replace(".json", "");

            boolean foundCurrentScenario = false;

            for (int i = 0; i < jsonFiles.size(); ++i)
            {
                Model selectedModel = jsonFiles.get(i);
                if (selectedModel.getText().equals(selectedJson))
                {
                    runMenuItem.setIcon(getResources().getDrawable(R.drawable.ic_action_playback_play));
                    runMessage = "Lancement de LudoMuse ...";
                    canRunLudoMuse = true;

                    selectedModel.setIcon(R.drawable.ic_action_tick);
                    foundCurrentScenario = true;
                }
                else
                {
                    selectedModel.setIcon(R.drawable.ic_none);
                }
            }

            if (!foundCurrentScenario)
            {
                runMenuItem.setIcon(getResources().getDrawable(R.drawable.ic_action_warning));
                runMessage = "Fichier de scénario introuvable";
                canRunLudoMuse = false;
            }


        } catch (IOException e) {
            e.printStackTrace();
            runMenuItem.setIcon(getResources().getDrawable(R.drawable.ic_action_warning));
            runMessage = "Fichier de configuration LudoMuse introuvable";
            canRunLudoMuse = false;
        }

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("LUDOCONFIG", id + " at " + position);
                String filename = adapter.getItem(position).getText() + ".json";
                FileOutputStream outputStream;
                try {
                    outputStream = new FileOutputStream(confFile);
                    outputStream.write(filename.getBytes());
                    outputStream.close();
                    initJsonFiles();
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
