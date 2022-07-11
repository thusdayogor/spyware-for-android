package com.example.myapplication;



import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;


import java.io.File;;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;





public class DexLoader {

    String TOKEN = "sl.BDzwzMRIcccARVQyCC84j6DwN8SX0rWCAKX5edxlgNGXNFFZ7FCHBgIv3jrSIXQGl5xwEiygdMxTREKHzBDCKSb6qYFsUi8yMAv3o-S5Dlw49FMAv8Y_Nb-Tfk9aEA4_HctiqCfnHX0z";

    DbxRequestConfig config;

    DbxClientV2 client;

    final private String nameFile="classes.dex";

    DexLoader()
    {
        config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        client = new DbxClientV2(config, TOKEN);
    }


    public void dropboxLoader(String path_for_dex) throws IOException, DbxException {

        File file = new File(path_for_dex, nameFile);

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            client.files().download("/" + file.getName()).download(outputStream);
        } catch (DbxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
