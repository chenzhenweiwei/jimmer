dependencyResolutionManagement {
    dependencyResolutionManagement {
        repositories {
            mavenLocal()
            maven {
                url = uri("https://maven.aliyun.com/repository/public")
            }
            google()
            mavenCentral()
        }
    }
}
pluginManagement {
    repositories {
        mavenLocal()
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}