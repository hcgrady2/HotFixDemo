package com.study.hotfixdemo.utils;

import android.content.Context;
import android.util.Log;

import com.study.mylibrary.utils.Constants;
import com.study.mylibrary.utils.ReflectUtils;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by hcw on 2019/3/5.
 * Copyright©hcw.All rights reserved.
 */

public class FixDexUtils  {

    public static final String DEX_OPT_DIR = "optimize_dex";//dex的优化路径
    public static final String DEX_BASECLASSLOADER_CLASS_NAME = "dalvik.system.BaseDexClassLoader";
    public static final String DEX_FILE_E = "dex";//扩展名
    public static final String DEX_ELEMENTS_FIELD = "dexElements";//pathList中的dexElements字段
    public static final String DEX_PATHLIST_FIELD = "pathList";//BaseClassLoader中的pathList字段
    public static final String FIX_DEX_PATH = "fix_dex";//fixDex存储的路径



    //开始修复，可能有多个修复包
    private static HashSet<File> loadeDex = new HashSet<>();
    private static      Object systemPathList ;
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
            Log.i("FixDemo", "遍历文件：" + file.getName());

            //修复包加入集合,不是主包
            if (file.getName().endsWith(Constants.DEX_SUFFIX) && !"classes.dex".equals(file.getName())){
                loadeDex.add(file);
                Log.i("FixDemo", "找到文件：" + file.getName());
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
        Log.i("FixDemo", "创建 classloader" );

        //dex 取 java（解压）
        //创建临时解压目录
        String optimizedDir = fileDir.getAbsolutePath() + File.separator + "opt_dex";
        File fopt = new File(optimizedDir);
        if (!fopt.exists()){
            //创建多级目录
            fopt.mkdirs();
        }

        try {
            PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();

            for (File dex :loadeDex){
                //每个需要修复的 dex，都需要插桩
                DexClassLoader classLoader = new DexClassLoader(dex.getAbsolutePath(),optimizedDir,null,pathClassLoader);

            //    hotfix(classLoader,context);

                // PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
                    //   DexClassLoader dexClassLoader = new DexClassLoader(apkPath, file.getParent() + "/optimizedDirectory/", "", pathClassLoader);

                    try {
                        Object dexElements = combineArray(getDexElements(getPathList(pathClassLoader)), getDexElements(getPathList(classLoader)));
                        Object pathList = getPathList(pathClassLoader);
                        setField(pathList, pathList.getClass(), "dexElements", dexElements);

                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.i("FixDemo", "createDexClassLoader:error" + e.toString());

        }

    }

/*
    public static String inject(String apkPath ) {
        boolean hasBaseDexClassLoader = true;

        File file = new File(apkPath);
        try {
            Class.forName("dalvik.system.BaseDexClassLoader");
        } catch (ClassNotFoundException e) {
            hasBaseDexClassLoader = false;
        }
        if (hasBaseDexClassLoader) {
           // PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
         //   DexClassLoader dexClassLoader = new DexClassLoader(apkPath, file.getParent() + "/optimizedDirectory/", "", pathClassLoader);

            DexClassLoader classLoader = new DexClassLoader(dex.getAbsolutePath(),optimizedDir,null,pathClassLoader);


            try {
                Object dexElements = combineArray(getDexElements(getPathList(pathClassLoader)), getDexElements(getPathList(dexClassLoader)));
                Object pathList = getPathList(pathClassLoader);
                setField(pathList, pathList.getClass(), "dexElements", dexElements);
                return "SUCCESS";
            } catch (Throwable e) {
                e.printStackTrace();
                return android.util.Log.getStackTraceString(e);
            }
        }
        return "SUCCESS";
    }*/

    public  static  void setField(Object pathList, Class aClass, String fieldName, Object fieldValue) {

        try {
            Field declaredField = aClass.getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            declaredField.set(pathList, fieldValue);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }


    public  static Object getPathList(BaseDexClassLoader classLoader) {
        Class<? extends BaseDexClassLoader> aClass = classLoader.getClass();

        Class<?> superclass = aClass.getSuperclass();
        try {

            Field pathListField = superclass.getDeclaredField("pathList");
            pathListField.setAccessible(true);
            Object object = pathListField.get(classLoader);

            return object;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            Log.i("FixDemo", "getDexElements: aadsf");

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.i("FixDemo", "getDexElements: aaadsf");

        }
        return null;
    }



    /**
     * 获取指定classloader 中的pathList字段的值（DexPathList）
     *
     * @param classLoader
     * @return
     */
    public  static Object getDexPathListField(Object classLoader) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(classLoader, Class.forName(DEX_BASECLASSLOADER_CLASS_NAME), DEX_PATHLIST_FIELD);
    }

    /**
     * 获取一个字段的值
     *
     * @return
     */
    public static Object getField(Object obj, Class<?> clz, String fieldName) throws NoSuchFieldException, IllegalAccessException {

        Field field = clz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);

    }

    /**
     * 为指定对象中的字段重新赋值
     *
     * @param obj
     * @param claz
     * @param filed
     * @param value
     */
    public  static void setFiledValue(Object obj, Class<?> claz, String filed, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = claz.getDeclaredField(filed);
        field.setAccessible(true);
        field.set(obj, value);
//        field.setAccessible(false);
    }

    /**
     * 获得pathList中的dexElements
     *
     * @param
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static  Object getDexElements(Object object) throws NoSuchFieldException, IllegalAccessException {

        if (object == null)
            return null;

        Class<?> aClass = object.getClass();
        try {
            Field dexElements = aClass.getDeclaredField("dexElements");
            dexElements.setAccessible(true);
            return dexElements.get(object);
        } catch (NoSuchFieldException e) {
            Log.i("FixDemo", "getDexElements: aadsf");

            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.i("FixDemo", "getDexElements: adsf");
        }
        return null;

     /*   Field field = clz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);*/
     //   return getField(obj, obj.getClass(), DEX_ELEMENTS_FIELD);
    }



    /**
     *  热修复
     * @param classLoader   自有的加载了补丁的 classloader
     * @param context
     */
    private static void hotfix(DexClassLoader classLoader, Context context) {
        Log.i("FixDemo", "hotfix" );

        try {
            //获取系统PathClassLoader
            PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();

      /*      //获取自己的 dexElements 数组对象
            Object myDexElements =  ReflectUtils.getDexElements(ReflectUtils.getPathList(classLoader));
            //获取系统的 dexElements 数组
            Object systemElemets =  ReflectUtils.getDexElements(ReflectUtils.getPathList(pathClassLoader));

            //合并新的 dexElements
            Object dexElement =  combineArray(myDexElements,systemElemets);
            //通过反射获取系统的  pathList 对象
             systemPathList = ReflectUtils.getPathList(pathClassLoader);
            //重新复制给系统 pathList 属性
            ReflectUtils.setField(systemPathList,systemPathList.getClass(),dexElement);
            systemPathList = ReflectUtils.getPathList(pathClassLoader);*/



            //获取app自身的BaseDexClassLoader中的pathList字段
            Object appDexPathList = getDexPathListField(pathClassLoader);
            //获取补丁的BaseDexClassLoader中的pathList字段
            Object fixDexPathList = getDexPathListField(classLoader);

            Object appDexElements = getDexElements(appDexPathList);
            Object fixDexElements = getDexElements(fixDexPathList);
            //合并两个elements的数据，将修复的dex插入到数组最前面
          //  Object finalElements = combineArray(fixDexElements, appDexElements);
            //给app 中的dex pathList 中的dexElements 重新赋值
            Object finalElements = combineArray(fixDexElements,appDexElements);
            setFiledValue(appDexPathList, appDexPathList.getClass(), DEX_ELEMENTS_FIELD, finalElements);

            systemPathList = ReflectUtils.getPathList(pathClassLoader);

            Log.i("FixDemo", "修复成功");

        //    UserDemo userDemo = new UserDemo();
          //  userDemo.setName("new");

        } catch (NoSuchFieldException e) {
            Log.i("FixDemo", "1"+e.toString() );

            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i("FixDemo", "2"+e.toString() );

            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
            Log.i("FixDemo", "3"+e.toString() );

        }

    }

    /**
     * 两个数组合并
     *
     * @param arrayLhs
     * @param arrayRhs
     * @return
     */
    private static Object combineArray(Object arrayLhs, Object arrayRhs) {
        Class<?> localClass = arrayLhs.getClass().getComponentType();
        int i = Array.getLength(arrayLhs);
        int j = i + Array.getLength(arrayRhs);
        Object result = Array.newInstance(localClass, j);
        for (int k = 0; k < j; ++k) {
            if (k < i) {
                Array.set(result, k, Array.get(arrayLhs, k));
            } else {
                Array.set(result, k, Array.get(arrayRhs, k - i));
            }
        }
        return result;
    }


}
