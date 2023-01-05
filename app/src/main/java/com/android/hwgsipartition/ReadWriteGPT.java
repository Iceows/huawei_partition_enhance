package com.android.hwgsipartition;

import android.util.Log;
import android.os.Environment;
import androidx.core.content.FileProvider;


import java.io.*;

public class ReadWriteGPT {

    String ReadMyGPTPhone () {
        try {
            // Executes the command.
            // ./parted /dev/block/mmcblk0 --script unit s print
            Process process = Runtime.getRuntime().exec("ls /sdcard");

            Log.println(Log.INFO, "ReadGPT", "Début de lecture");

            // Reads stdout.
            // NOTE: You can write to stdin of the command using
            //       process.getOutputStream().
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
                Log.println(Log.INFO, "ReadGPT", buffer.toString());
            }
            reader.close();

            Log.println(Log.INFO, "ReadGPT", "Fin de lecture");

            // Waits for the command to finish.
            process.waitFor();


            //return output.toString();
            return output.toString();
        } catch (IOException e) {
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

    String ReadMyGPTFile (String szFile) {
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
