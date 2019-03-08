package  com.hc.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project


public class InjectPlugin implements Plugin<Project> {

    void apply(Project project) {
        System.out.println("========================");
        System.out.println("this the inject Plugin");
        System.out.println("========================");


        def isApp = project.plugins.hasPlugin(AppPlugin.class)

        // 仅处理application合包
        if (isApp) {
            def android = project.extensions.findByType(AppExtension.class)
              android.registerTransform(new MyTransform(project))
            System.out.println("inject finish");
        }

    }
}

