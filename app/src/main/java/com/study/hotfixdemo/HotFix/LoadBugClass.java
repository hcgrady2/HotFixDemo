package com.study.hotfixdemo.HotFix;

/**
 * Created by WenTong on 2019/3/8.
 */


public class LoadBugClass {


    public String getBugString()
    {
        BugClass bugClass = new BugClass();
        return bugClass.bug();

    }


}
