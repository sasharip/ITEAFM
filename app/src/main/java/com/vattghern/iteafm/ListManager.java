package com.vattghern.iteafm;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;
public class ListManager {

    public static ArrayList<String> pathToArray(String path) {
        ArrayList<String> filesList = null;
        if (path == null) {
            Log.w("babah",path);

        } else {
            Log.d(TAG, "BABAH");

            filesList = new ArrayList<String>();
            File file = new File(path);
            File files[] = file.listFiles();
            Log.d(TAG, String.valueOf(files.toString().length()));
            for (int i = 0; i < files.length; i++) {
                filesList.add(files[i].getName());
            }

        }
        return filesList;
    }
}
