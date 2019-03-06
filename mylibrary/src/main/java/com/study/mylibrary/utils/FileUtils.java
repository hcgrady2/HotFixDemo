package com.study.mylibrary.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by hcw on 2019/3/6.
 * Copyright©hcw.All rights reserved.
 */

public class FileUtils {
    public static void copyFile(File sourceFile,File targetFile) throws IOException{

        //新建文件输入流，并进行缓冲
        FileInputStream inputStream = new FileInputStream(sourceFile);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        //新建文件输出流并缓冲
        FileOutputStream outputStream = new FileOutputStream(targetFile);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

        //缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = bufferedInputStream.read(b)) != -1){
            bufferedOutputStream.write(b,0,len);
        }
        //刷新缓冲输入流、
        bufferedInputStream.close();
        bufferedOutputStream.close();
        inputStream.close();
        outputStream.close();


    }
}
