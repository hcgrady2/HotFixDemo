package com.study.hotfixdemo.app;

import android.app.Application;
import android.content.Context;

import com.study.hotfixdemo.utils.HotFix;
import com.study.hotfixdemo.utils.Utils;

import java.io.File;

/**
 * Created by WenTong on 2019/3/8.
 */

public class AppApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        File dexPath = new File(getDir("dex", Context.MODE_PRIVATE), "hackdex_dex.jar");
        Utils.prepareDex(this.getApplicationContext(), dexPath, "hackdex_dex.jar");
        HotFix.patch(this, dexPath.getAbsolutePath(), "com.study.hotfixdemo.HotFix.hackdex.AntilazyLoad");
        try
        {
            System.out.println("loadClass");
            this.getClassLoader().loadClass("com.study.hotfixdemo.HotFix.hackdex.AntilazyLoad");
        } catch (ClassNotFoundException e)
        {
            System.out.println(" loadClass" + e.toString());

            e.printStackTrace();
        }

        dexPath = new File(getDir("dex", Context.MODE_PRIVATE), "path_dex.jar");
        Utils.prepareDex(this.getApplicationContext(), dexPath, "path_dex.jar");
        HotFix.patch(this, dexPath.getAbsolutePath(), "com.study.hotfixdemo.HotFix.BugClass");



        dexPath = new File(getDir("dex", Context.MODE_PRIVATE), "fix_dex.jar");
        Utils.prepareDex(this.getApplicationContext(), dexPath, "fix_dex.jar");
        HotFix.patch(this, dexPath.getAbsolutePath(), "com.study.hotfixdemo.activities.SecondActivity");



    }
}
