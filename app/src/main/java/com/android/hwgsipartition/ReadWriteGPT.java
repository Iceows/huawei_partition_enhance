package com.android.hwgsipartition;

import android.util.Log;
import android.os.Environment;


import java.io.*;

public class ReadWriteGPT {

    String ReadMyGPTPhone (String basedir) {
        try {
            // Executes the command.
            // ./parted /dev/block/mmcblk0 --script unit s print quit > HuaweiFileGPT.txt
            // ls -al  /dev/block/platform/hi_mci.0/by-name/ > HuaweiFilePart.txt

            //String[] cmdarray = new String[] {command,"/dev/block/mmcblk0","--script unit s print"};
            //String command = String.format("%s/libparted.so", basedir);
            String command = "ls -al  /dev/block/platform/hi_mci.0/by-name/";

            Log.println(Log.INFO, "ReadGPT", "Exec : " + command);

            Process process = Runtime.getRuntime().exec( command );
            Log.println(Log.INFO, "ReadGPT", "Début de lecture");

            // Reads stdout.
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();

            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);

            }
            reader.close();

            Log.println(Log.INFO, "ReadGPT", "Fin de lecture");

            // Waits for the command to finish.
            process.waitFor();

            Log.println(Log.INFO, "ReadGPT", output.toString());

            return output.toString();
        } catch (IOException e) {
            Log.e("ReadGPT",e.toString());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void WriteGPTScript (String szDir,String szFile,String szCmd) {
        Log.println(Log.INFO, "ReadGPT", szFile);
        try {
            File gpxfile = new File(szDir, szFile);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(szCmd);
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    String ReadMyFile (String szFile) {
        String result = null;
        BufferedReader br;

        Log.println(Log.INFO, "ReadGPT", "Open file : " + szFile);

        try {

            br = new BufferedReader(new FileReader(szFile));

            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }

                result = sb.toString();
            }
            finally {
                br.close();
            }
        }
        catch (IOException e) {
            Log.println(Log.INFO, "ReadGPT", "Erreur file : " + e.toString());
            return "";
        }

        return result;
    }

    // Checks if a volume containing external storage is available
    // for read and write.
    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    // Checks if a volume containing external storage is available to at least read.
    private boolean isExternalStorageReadable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }


}
