# SimplePermissions

### 介绍
简化动态权限申请

### 软件架构
AspectJ + 注解


### 使用说明

#### 项目根目录build.gradle配置:
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

#### 模块build.gradle配置:
```
apply plugin: 'android-aspectjx'

dependencies {
    implementation 'com.gitee.lnvip:simple-permissions:V1.0.1'
}
```

#### 初始化：
```
Permissions.init(this);
```

#### 注解申请权限：
```
    @RequestPermissions({
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SYSTEM_ALERT_WINDOW
    })
    private void doRequestUsePermissions() {
        Toast.makeText(MainActivity.this, "doRequestUsePermissions", Toast.LENGTH_SHORT).show();
    }
```

#### 接口申请权限：
```
       Permissions.request(new Permissions.Callback() {
            @Override
            public void onResult(final List<String> granted, final List<String> rejected) {
                if (0 == rejected.size()) {
                    //权限申请成功
                } else {
                    Toast.makeText(context, "以下权限申请失败:\n" + Permissions.getPermissionNames(rejected, "\n"),Toast.LENGTH_SHORT).show();
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
```

#### 更多高级用法请参考源码
