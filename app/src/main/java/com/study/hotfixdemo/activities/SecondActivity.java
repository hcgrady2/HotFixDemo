package com.study.hotfixdemo.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.study.hotfixdemo.BaseActivity;
import com.study.hotfixdemo.R;
import com.study.mylibrary.utils.Constants;
import com.study.mylibrary.utils.FileUtils;
import com.study.hotfixdemo.utils.FixDexUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by hcw on 2019/3/5.
 * Copyright©hcw.All rights reserved.
 */

public class SecondActivity extends BaseActivity {


    /**
     * 加载 jar ,apk 还是 dex，是不是 CLASS_ISPREVERIFIED
     */
    Button showBug,fixBug;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        showBug = findViewById(R.id.showBug);
        fixBug = findViewById(R.id.fixBug);
        showBug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

        fixBug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fix();
            }
        });
    }

    public void show(){
        int a = 10;
        int b = 0;
        int c = a/b;
        Toast.makeText(getApplicationContext(),"the result" +c,Toast.LENGTH_SHORT).show();

        UserDemo userDemo = new UserDemo();
        userDemo.setName("new");
        Log.i("FixDemo", "验证： " +userDemo.getName());



    }
    public String dexName="fix.dex";

    public void fix2(){
       // MergeDexUtil.copyDexFileToAppAndFix(this,dexName,true);
    }
    public void fix(){
        Log.i("FixDemo", "fix: ");
        //通过服务器下载的 dex 文件的位置
        File sourceFile = new File(Environment.getExternalStorageDirectory(), Constants.DEX_NAME);
        //复制到私有目录 odex 下
        File targetFile = new File(getDir(Constants.DEX_DIR, Context.MODE_PRIVATE).getAbsolutePath()+File.separator + Constants.DEX_NAME);

        //检测是否有补丁文件存在
        if (!sourceFile.exists()){
            Toast.makeText(getApplicationContext(),"补丁文件不存在",Toast.LENGTH_SHORT).show();
            return;
        }

        //判断格式是否正确
        if (!sourceFile.getAbsolutePath().endsWith(Constants.DEX_SUFFIX)){
            Toast.makeText(getApplicationContext(),"补丁文件格式不正确",Toast.LENGTH_SHORT).show();
        }

        //如果存在，清理之前修复过的 dex 文件
        if (targetFile.exists()){
            Log.i("FixDemo", "fix:2 ");
            targetFile.delete();
            Toast.makeText(getApplicationContext(),"删除已经存在 dex 文件",Toast.LENGTH_SHORT).show();
        }

        try {
            Log.i("FixDemo", "fix:333 ");
            //复制已经修复的 dex 文件到私有目录
            FileUtils.copyFile(sourceFile,targetFile);
            Toast.makeText(getApplicationContext(),"复制 dex 文件完成",Toast.LENGTH_SHORT).show();



              FixDexUtils.loadFixedDex(this);


        }catch (IOException e){
            e.printStackTrace();
            Log.i("FixDemo", "fix: error" + e.toString());

        }
    }
}
