package  com.hc.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project


public class SelfPlugin implements Plugin<Project> {

    void apply(Project project) {
        System.out.println("========================");
        System.out.println("this is the secondPlugin");
        System.out.println("========================");
    }
}

