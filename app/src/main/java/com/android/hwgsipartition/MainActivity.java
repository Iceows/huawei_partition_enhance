package com.android.hwgsipartition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import android.Manifest;
import android.net.Uri;
import android.widget.Toast;



import java.io.File;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

// Executes the command.
// ./parted /dev/block/mmcblk0 --script unit s print quit >  ./Documents/HuaweiFileGPT.txt
//  ls -la /dev/block/platform/hi_mci.0/by-name/  | grep "\->" | cut -c46- > ./Documents/HuaweiFilePart.txt
// chown media_rw:media_rw HuaweiFilePart.txt


public class MainActivity extends AppCompatActivity {
    // FILE PURPOSE
    private static final String HWFILEGPT = "HuaweiFileGPT.txt";
    private static final String HWFILEPARTITION = "HuaweiFilePart.txt";

    //PERMISSION request constant, assign any value
    private static final int STORAGE_PERMISSION_CODE = 100;

    private static final String TAG = "ReadGPT";

    Button btnCreateNew, btnReadPhone;
    ProcessFileGPT objProcess = new ProcessFileGPT();
    ReadWriteGPT objReadWrite = new ReadWriteGPT();

    String sMyInitialGPT;
    String sMyPartitions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Test code: see if we can find the /libs library
        String libName = "libparted.so";

        ApplicationInfo appInfo = this.getApplicationInfo();
        String nativeLibsDir = appInfo.nativeLibraryDir;
        String libPath = nativeLibsDir + "/" + libName;

        // Getting the file by creating object of File class
        //File f = new File(libPath);

        // Checking if the specified file exists or not
        //if (f.exists())
        // Show if the file exists
        //    Log.println(Log.INFO, "ReadGPT", "libparted.so exists");
        //else
        // Show if the file does not exists
        //    Log.println(Log.INFO, "ReadGPT", "libparted.so not exists");


        btnCreateNew = (Button) findViewById(R.id.btnCreateNewGPT);//get id of button 1
        btnReadPhone = (Button) findViewById(R.id.btnReadPhoneGPT);//get id of button 2

        btnCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean r=generateScript();

            }
        });

        btnReadPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Try execute command - not work on non root device
                //String nativeLibsDir = getApplicationInfo().nativeLibraryDir;
                //String sMyPartitions= objReadWrite.ReadMyGPTPhone(nativeLibsDir);
                //Log.println(Log.INFO, "ReadGPT", sMyPartitions);

                if (checkPermission()) {
                    Log.d("ReadGPT", "onClick: Permissions already granted...");
                    //createFolder();
                    readPartFromStorage();
                    Log.d("ReadGPT", sMyPartitions);
                    Log.d("ReadGPT", sMyInitialGPT);

                    FillTable(view);

                } else {
                    Log.d("ReadGPT", "onClick: Permissions was not granted, request...");
                    requestPermission();
                }


                //TextView textView = (TextView) findViewById(R.id.edtInitialGPT);
                //textView.setText(sMyInitialGPT.toString()); //set text for text vie
            }
        });

    }

    void FillTable(View view) {
        int iNbPart = 0;
        Partition[] objPartMod;
        Partition[] objPartProc;

        if (objProcess.StartProcess(sMyInitialGPT,sMyPartitions) == false)
            return;

        // Remplit les tableaux
        objPartProc = objProcess.getProcPart();
        iNbPart = objProcess.getNbProcPart();
        TextView[] textArrayProc = new TextView[iNbPart];
        TableRow[] tr_headProc = new TableRow[iNbPart];
        TableLayout tlp = (TableLayout) findViewById(R.id.tableParProc);

        for (int i = 0; i < iNbPart; i++) {

            String szPartition;

            szPartition = objPartProc[i].StringInfo();

            //Create the tablerows
            tr_headProc[i] = new TableRow(this);
            tr_headProc[i].setId(i + 1);
            tr_headProc[i].setBackgroundColor(Color.GRAY);
            tr_headProc[i].setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            // Here create the TextView dynamically
            textArrayProc[i] = new TextView(this);
            textArrayProc[i].setId(i + 101);
            textArrayProc[i].setText(szPartition);
            textArrayProc[i].setTextColor(Color.WHITE);
            textArrayProc[i].setPadding(5, 5, 5, 5);
            tr_headProc[i].addView(textArrayProc[i]);

            // Add each table row to table layout
            tlp.addView(tr_headProc[i], new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

        } // end of for loop


        // ----------- ----------

        objPartMod = objProcess.getModPart();
        iNbPart = objProcess.getNbModPart();
        TextView[] textArray = new TextView[iNbPart];
        TableRow[] tr_head = new TableRow[iNbPart];
        TableLayout tl = (TableLayout) findViewById(R.id.tablePartMod);

        for (int i = 0; i < iNbPart; i++) {
            String szPartition;

            szPartition = objPartMod[i].StringInfo();

            //Create the tablerows
            tr_head[i] = new TableRow(this);
            tr_head[i].setId(i + 201);
            tr_head[i].setBackgroundColor(Color.GRAY);
            tr_head[i].setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            // Here create the TextView dynamically
            textArray[i] = new TextView(this);
            textArray[i].setId(i + 301);
            textArray[i].setText(szPartition);
            textArray[i].setTextColor(Color.WHITE);
            textArray[i].setPadding(5, 5, 5, 5);
            tr_head[i].addView(textArray[i]);

            // Add each table row to table layout
            tl.addView(tr_head[i], new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

        } // end of for loop
    }

    private boolean generateScript()
    {
        // Genere le fichier pour supprimer les partitions
        String szCmd = "";
        szCmd = objProcess.GeneratedScriptRM();
        writeToStorage(1, szCmd);

        // Genere le fichier pour creer les partitions
        szCmd = objProcess.GeneratedScriptMake();
        writeToStorage(2, szCmd);

        // Genere le fichier pour creer les partitions
        szCmd = objProcess.GeneratedScriptFormat();
        writeToStorage(3, szCmd);

        Toast.makeText(this, "Les scripts ont été générés avec succés", Toast.LENGTH_SHORT).show();

        return true;
    }

    private boolean readPartFromStorage() {
        File directory;

        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        sMyPartitions=objReadWrite.ReadMyFile(directory.toString() + "/" + HWFILEPARTITION);
        sMyInitialGPT=objReadWrite.ReadMyFile(directory.toString() + "/" + HWFILEGPT);

        if (!sMyPartitions.isEmpty() && !sMyInitialGPT.isEmpty() )
            return true;

        return false;
    }

    private boolean writeToStorage(int i, String szCmd) {
        File directory;

        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        if (i==1)
            objReadWrite.WriteGPTScript(directory.toString(),"clearpart.sh", szCmd);
        if (i==2)
            objReadWrite.WriteGPTScript(directory.toString(),"makepart.sh", szCmd);
        if (i==3)
            objReadWrite.WriteGPTScript(directory.toString(),"formatpart.sh", szCmd);

        return false;
    }

    //https://devofandroid.blogspot.com/2022/05/manage-external-storage-permission_8.html
    private void createFolder(){
        //get folder name
        String folderName = "HWGsiPartition";

        //create folder using name we just input
        File file = new File(Environment.getExternalStorageDirectory() + "/" + folderName);
        //create folder
        boolean folderCreated = file.mkdir();

        //show if folder created or not
        if (folderCreated) {
            Toast.makeText(this, "Folder Created....\n" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Folder not created...", Toast.LENGTH_SHORT).show();
        }

    }

    // TODO isAOnly
    private void requestPermission() {


        CheckBox simpleCheckBox = (CheckBox) findViewById(R.id.chkAOnly);
        Boolean checkBoxState = simpleCheckBox.isChecked();

        boolean isAOnly = checkBoxState;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (isAOnly) {
                //Android 11 with a-only system
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE
                );
            }
            else {
                //Android is 11(R) or above
                try {
                    Log.d(TAG, "requestPermission: try");

                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                    intent.setData(uri);
                    storageActivityResultLauncher.launch(intent);
                } catch (Exception e) {
                    Log.e(TAG, "requestPermission: catch", e);
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    storageActivityResultLauncher.launch(intent);
                }
            }
        } else {
            //Android is below 11(R)
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE
            );
        }
    }

    private ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: ");
                    //here we will handle the result of our intent
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        //Android is 11(R) or above
                        if (Environment.isExternalStorageManager()) {
                            //Manage External Storage Permission is granted
                            Log.d(TAG, "onActivityResult: Manage External Storage Permission is granted");
                            //createFolder();
                            readPartFromStorage();
                        } else {
                            //Manage External Storage Permission is denied
                            Log.d(TAG, "onActivityResult: Manage External Storage Permission is denied");
                            Toast.makeText(MainActivity.this, "Manage External Storage Permission is denied", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //Android is below 11(R)
                    }
                }
            }
    );

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11(R) or above
            return Environment.isExternalStorageManager();
        } else {
            //Android is below 11(R)
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }

    /*Handle permission request results*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                //check each permission if granted or not
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean manage = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                if (write && read && manage) {
                    //External Storage permissions granted
                    Log.d(TAG, "onRequestPermissionsResult: External Storage permissions granted");
                    readPartFromStorage();
                } else {
                    //External Storage permission denied
                    Log.d(TAG, "onRequestPermissionsResult: External Storage permission denied");
                    Toast.makeText(this, "External Storage permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}