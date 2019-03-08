package com.study.mylibrary.utils;

import android.util.Log;

import java.lang.reflect.Array;

/**
 * Created by hcw on 2019/3/6.
 * Copyright©hcw.All rights reserved.
 */

public class ArrayUtils {
    public static Object combineArray(Object arrayLhs,Object arrayRhs){
        //获取一个数组的 Class 对象，通过 Array.newInstance() 反射生成数组对象
        Class<?> localClass = arrayLhs.getClass().getComponentType();
        //前数组长度
        int i = Array.getLength(arrayLhs);
        //新数组长度
        int j = i + Array.getLength(arrayRhs);
        //生成数组对象
        Object result = Array.newInstance(localClass,j);
        for (int k = 0; k < j; ++k){
            if (k < i){
                //如果前数组有值，添加到数组的第一个位置
                Array.set(result,k,Array.get(arrayLhs,k));
            }else {
                //添加完前数组，添加后数组，完成合并
                Array.set(result,k,Array.get(arrayRhs,k-i));
            }
        }
        Log.i("FixDemo", "合并数组完成" );

        return result;
    }
}
