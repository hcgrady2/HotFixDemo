package com.study.hotfixdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.study.hotfixdemo.activities.SecondActivity;

public class MainActivity extends BaseActivity {

    Button showBug,fixBug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            String[] pers = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

            if (checkSelfPermission(pers[0]) == PackageManager.PERMISSION_DENIED){
                requestPermissions(pers,200);
            }
        }
    }


    public void jump(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }
}
