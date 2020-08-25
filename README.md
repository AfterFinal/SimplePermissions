# SimplePermissions

#### 介绍
简化动态权限申请

#### 软件架构
AspectJ + 注解


#### 使用说明

项目根目录build.gradle配置:
```
buildscript {
    repositories {
        maven {
            url "https://jitpack.io"
        }
    }
    dependencies {
        classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.4'
    }
}
```

模块build.gradle配置:
```
apply plugin: 'android-aspectjx'

dependencies {
    implementation 'com.gitee.lnvip:simple-permissions:V1.0.1'
}
```

初始化：
```
Permissions.init(this);
```

注解申请权限：
```
    @RequestPermissions({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    private void doOnCreate() {
    }
```

接口申请权限：
```
       Permissions.request(new Permissions.Callback() {
            @Override
            public void onResult(final List<String> granted, final List<String> rejected) {
                if (0 == rejected.size()) {
                    EventBus.getDefault().post("success", "on_init_permissions_result");
                } else {
                    EventBus.getDefault().post("以下权限申请失败:\n" + Permissions.getPermissionNames(rejected, "\n"), "on_init_permissions_result");
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
```


