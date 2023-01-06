package com.android.hwgsipartition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;

import java.io.File;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity {
    // FILE PURPOSE
    private static final String FILENAME = "HuaweiFileGPT.txt";
    private static final int RC_STORAGE_WRITE_PERMS = 100;

    Button btnCreateNew, btnReadPhone;
    ProcessFileGPT objProcess = new ProcessFileGPT();
    ReadWriteGPT objReadWrite = new ReadWriteGPT();
    String sMyInitialGPT;

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
        File f = new File(libPath);

        // Checking if the specified file exists or not
        if (f.exists())
            // Show if the file exists
            Log.println(Log.INFO, "ReadGPT", "libparted.so exists");
        else
            // Show if the file does not exists
            Log.println(Log.INFO, "ReadGPT", "libparted.so not exists");


        btnCreateNew = (Button) findViewById(R.id.btnCreateNewGPT);//get id of button 1
        btnReadPhone = (Button) findViewById(R.id.btnReadPhoneGPT);//get id of button 2

        btnCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FillTable(view);
            }
        });

        btnReadPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String nativeLibsDir = getApplicationInfo().nativeLibraryDir;
                String sMyPartitions= objReadWrite.ReadMyGPTPhone(nativeLibsDir);
                Log.println(Log.INFO, "ReadGPT", sMyPartitions);

                sMyInitialGPT=readFromStorage();
                Log.println(Log.INFO, "ReadGPT", sMyInitialGPT);

                //TextView textView = (TextView) findViewById(R.id.edtInitialGPT);
                //textView.setText(sMyInitialGPT.toString()); //set text for text vie
            }
        });

    }

    void FillTable(View view) {
        int iNbPart = 0;
        Partition[] objPartMod;
        Partition[] objPartProc;


        if (objProcess.StartProcess(sMyInitialGPT)==false)
            return;

        // Genere le fichier pour supprimer les partitions
        String szCmd="";
        szCmd=objProcess.GeneratedScriptRM();
        writeFromStorage(1,szCmd);

        // Genere le fichier pour creer les partitions
        szCmd=objProcess.GeneratedScriptMake();
        writeFromStorage(2,szCmd);


        // Remplit les tableaux
        objPartMod=objProcess.getProcPart();
        iNbPart=objProcess.getNbProcPart();
        TextView[] textArrayProc = new TextView[iNbPart];
        TableRow[] tr_headProc = new TableRow[iNbPart];
        TableLayout tlp = (TableLayout) findViewById(R.id.tableParProc);

        for (int i = 0; i < iNbPart; i++) {

            String szPartition;

            szPartition = objPartMod[i].StringInfo();

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

        objPartMod=objProcess.getModPart();
        iNbPart=objProcess.getNbModPart();
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_STORAGE_WRITE_PERMS) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readFromStorage();
            }
        }
    }

    private boolean checkWriteExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{WRITE_EXTERNAL_STORAGE},
                    RC_STORAGE_WRITE_PERMS);
            return true;
        }
        return false;
    }

    private String readFromStorage() {
        String sMyInitialGPT;
        File directory;

        if (checkWriteExternalStoragePermission()) return "";

        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        sMyInitialGPT=objReadWrite.ReadMyGPTFile(directory.toString() + "/" + FILENAME);


        return sMyInitialGPT;

    }

    private void writeFromStorage(int iEtape,String szCmd) {
        String sMyInitialGPT;
        File directory;

        if (checkWriteExternalStoragePermission()) return ;

        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        if (iEtape==1)
            objReadWrite.WriteGPTScript(directory.toString(),"rm-part.sh",szCmd);
        if (iEtape==2)
            objReadWrite.WriteGPTScript(directory.toString(),"make-part.sh",szCmd);

    }
}