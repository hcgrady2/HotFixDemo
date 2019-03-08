参考:https://blog.csdn.net/lmj623565791/article/details/49883661
一、通过自定义的插件修改 class 阻止类打 CLASS_ISPREVERIFIED 标志
插件的作用是，所有类的构造函数中添加了一个输出：
```
System.out.println( com.study.hotfixdemo.HotFix.hackdex.AntilazyLoad.class ); 
```
二、动态改变BaseDexClassLoader对象间接引用的dexElements
1、将AntilazyLoad.class这个类打成独立的hack_dex.jar(用 AndroidStudio 编译生成的 .class 就行，需要在项目根目录下操作)
```
jar cvf hack.jar com\study\hotfixdemo\HotFix\hackdex\*
e:\DevelopTools\SDK\build-tools\28.0.2\dx.bat --dex --out=hack_dex.jar hack.jar
```
2、app 启动的时候，将 hack_dex.jar 插入到 dexElements(Application 中的 onCreate)
```
 File dexPath = new File(getDir("dex", Context.MODE_PRIVATE), "hackdex_dex.jar");
        Utils.prepareDex(this.getApplicationContext(), dexPath, "hackdex_dex.jar");
        HotFix.patch(this, dexPath.getAbsolutePath(), "dodola.hackdex.AntilazyLoad");
        try
        {
            this.getClassLoader().loadClass("dodola.hackdex.AntilazyLoad");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

```

三、热修复
1、改 bug
2、打 jar 包
```
jar cvf patch.jar com\study\hotfixdemo\activity\SecondActivity.class
e:\DevelopTools\SDK\build-tools\28.0.2\dx.bat --dex --out=patch_dex.jar patch.jar
```
3、jar 需要用合理的方法弄到 sd 卡上，这里从 assets 拷贝过去
```
   dexPath = new File(getDir("dex", Context.MODE_PRIVATE), "path_dex.jar");
        Utils.prepareDex(this.getApplicationContext(), dexPath, "path_dex.jar");
        HotFix.patch(this, dexPath.getAbsolutePath(), "dodola.hotfix.BugClass");
```
4、完成修复
