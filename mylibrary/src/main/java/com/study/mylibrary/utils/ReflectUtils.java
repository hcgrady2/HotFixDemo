package com.study.mylibrary.utils;

import java.lang.reflect.Field;

/**
 * Created by hcw on 2019/3/6.
 * Copyright©hcw.All rights reserved.
 */

public class ReflectUtils {
    /**
     *  反射获取某个对象
     * @param obj
     * @param clazz
     * @param field
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private static Object getFiled(Object obj,Class<?> clazz,String field) throws NoSuchFieldException,IllegalAccessException,IllegalArgumentException{
        Field localField = clazz.getDeclaredField(field);
        localField.setAccessible(true);
        return localField.get(obj);
    }

    public static void setField(Object obj,Class<?> clazz,Object value)
            throws NoSuchFieldException,IllegalAccessException,IllegalArgumentException{
        Field localFiled = clazz.getDeclaredField("dexElements");
        localFiled.setAccessible(true);
        localFiled.set(obj,value);
    }

    public static Object getPathList(Object baseDexClassLoader)
            throws NoSuchFieldException,IllegalAccessException,IllegalArgumentException,ClassNotFoundException{
        return getFiled(baseDexClassLoader,Class.forName("dalvik.system.BaseDexClassLoader"),"pathList");
    }

    public static Object getDexElements(Object paramObject)
            throws NoSuchFieldException,IllegalAccessException,IllegalArgumentException,ClassNotFoundException{
        return getFiled(paramObject,paramObject.getClass(),"dexElements");
    }

}
