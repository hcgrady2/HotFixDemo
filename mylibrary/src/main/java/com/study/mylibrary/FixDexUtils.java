package com.study.mylibrary;

import android.content.Context;

import com.study.mylibrary.utils.ArrayUtils;
import com.study.mylibrary.utils.Constants;
import com.study.mylibrary.utils.ReflectUtils;

import java.io.File;
import java.util.HashSet;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by hcw on 2019/3/5.
 * Copyright©hcw.All rights reserved.
 */

public class FixDexUtils  {
    //开始修复，可能有多个修复包
    private static HashSet<File> loadeDex = new HashSet<>();
    static {
        //修复前，进行清理工作
        loadeDex.clear();
    }

    /**
     * 加载热修复 dex
     * @param context
     */
    public static void loadFixedDex(Context context) {
        if (context == null) {
            return;
        }

        File fileDir = context.getDir(Constants.DEX_DIR,Context.MODE_PRIVATE);
        //遍历
        File[] listFiles = fileDir.listFiles();
        for (File file:listFiles){
            //修复包加入集合,不是主包
            if (file.getName().endsWith(Constants.DEX_SUFFIX) && "classes.dex".equals(file.getName())){
                loadeDex.add(file);
            }
        }

        //模拟类加载器
        createDexClassLoader(context,fileDir);



    }

    /**
     *  创建加载补丁的 DexClassLoader（私有）
     * @param context
     * @param fileDir
     */
    private static void createDexClassLoader(Context context, File fileDir) {
        //dex 取 java（解压）
        //创建临时解压目录
        String optimizedDir = fileDir.getAbsolutePath() + File.separator + "opt_dex";
        File fopt = new File(optimizedDir);
        if (!fopt.exists()){
            //创建多级目录
            fopt.mkdirs();
        }

        for (File dex :loadeDex){
            //每个需要修复的 dex，都需要插桩
            DexClassLoader classLoader = new DexClassLoader(dex.getAbsolutePath(),optimizedDir,null,context.getClassLoader());
            hotfix(classLoader,context);
        }
    }

    /**
     *  热修复
     * @param classLoader   自有的加载了补丁的 classloader
     * @param context
     */
    private static void hotfix(DexClassLoader classLoader, Context context) {

        try {
            //获取系统PathClassLoader
            PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
            //获取自己的 dexElements 数组对象
            Object myDexElements =  ReflectUtils.getDexElements(ReflectUtils.getPathList(classLoader));
            //获取系统的 dexElements 数组
            Object systemElemets =  ReflectUtils.getDexElements(ReflectUtils.getPathList(pathClassLoader));
            //合并新的 dexElements
            Object dexElement =  ArrayUtils.combineArray(myDexElements,systemElemets);
            //通过反射获取系统的  pathList 对象
            Object systemPathList = ReflectUtils.getPathList(pathClassLoader);
            //重新复制给系统 pathList 属性
            ReflectUtils.setField(systemPathList,systemPathList.getClass(),dexElement);


        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

    }
}
