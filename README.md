# 项目基本结构

## 程序依赖

```groovy
compile "com.android.support:appcompat-v7:${supportVersion}"    Support 库
compile "com.android.support:design:${supportVersion}"
compile "com.android.support:recyclerview-v7:${supportVersion}"
compile "com.android.support:cardview-v7:${supportVersion}"

compile "com.squareup.okhttp3:okhttp:${okhttpVersoin}"      网络请求
compile  'com.github.bumptech.glide:glide:3.7.0''                 图片加载
compile "com.squareup.retrofit2:retrofit:${retrofitVersion}"    Restful 请求
compile "com.squareup.retrofit2:converter-gson:${retrofitVersion}" GSON
compile "com.squareup.retrofit2:adapter-rxjava:${retrofitVersion}" RxJava
compile 'com.jakewharton.timber:timber:4.1.2'       Log
```

## 简单规约

1.  所有 Activity 继承自 BaseActivity，必要时重写父类的 `processArgument()` 来处理前画面传递的值
2.  对于 Fragment 尽量继承自 BaseFragment，必要时重写父类的 `processArgument()` 来处理前画面传递的值
3.  layout xml 命名
     Activity 用界面以  `activity_` 开头
     Fragment 用界面以  `fragment_` 开头
     Adapter  用界面以  `item_` 开头
     其它随意
4.  对于部分常量
     通过 Bundle 传递时，表示 key 的常量以 `EXTRA_` 开头
     对于存放到 Prefs 中的 key 的常量以 `PREFS_` 开头
5.  对于 Activity 和 Fragment 来说在类开始写一个简单的注解介绍一下这是什么画面，如
     ```java
     /**
      * 注册画面
     */
     ```

## 工程目录

```
|- app/src/main/java    应用目录
    |- **/zejia
        |- Zejia    自定义的 Application
        |- config   存放一些应用相关配置
            |- Constants    存放相关常量，比如远程服务器地址，超时时间等
        |- data
            |- local    存放各种访问本地资源的服务，如对 Prefs，本地文件操作的封装
            |- remote   存放各种访问远程资源的服务，如 http, socket 等请求
            |- models  存放各种域模型
        |- interfaces   存放各种公用的 interfaces, callbacks, listeners，只有画面本身可用的就直接定义为内部类即可
        |- ui
            |- activities 存放 Activity
            |- fragments 存放 Fragment
            |- adapters 存放 Adapter
            |- views 存放各种自定义 View
        |- utils    存放各种工具类，助手类
            |- IOUtil io读写操作的工具类
            |- ManifestUtil 读取AndroidManifest的工具类
            |- PrefUtil 读写 SharedPreferences 的工具类
            |- DeviceUtil 读取设备相关信息的工具类，如语言，地区，是否联网
            |- CommonUtil 其它工具类，如字符串操作，日期转换等
            |- Validator 数据验证工具类
            |- Navigator 页面跳转工具类
```

## 示例

### Log 输出

尽量不要使用 `System.out.println()` 和 `Log.v()` 输出日志
而是使用 `Timber` 输出 Log，方便发布时关闭日志功能

例

```java
Timber.i("Hello, %s", "world");
```

### 网络请求

1. 在 `Constants.java` 中定义 API 的详细地址，如 ` String API_GET_COMMUNITY_BY_ID = String.format("%s/%s/%s", ZEJIA_API_URL, VERSION, "getCommunityById");
`，地址名尽量与API文档中的资源名相似。

2. 在需要调用网络请求的地方调用类似如下代码，接口封装了 Common 的那些参数

    ```java
    DataManager dataManager = DataManager.getInstance();
    ZejiaService zejiaService = dataManager.getZejiaService();
    zejiaService.doRequest(context, Constants.API_GET_COMMUNITY_BY_ID,
                            params,
                            jsonObject ->
                                    Timber.d("Receive %s on %s", jsonObject, Thread.currentThread().getName()));
    ```

    如果希望完全自定义参数，可以调用如下方法

    ```java
    JSONObject params = JSONBuilder.newBuilder()
                    .putAlways("x", "100")
                    .putIfNotNull("y", null)
                    .putIfNotZero("z", 10)
                    .build();
    RequestAPI.getInstance()
            .doRxRequest(this, jsonObject ->
                            Timber.d("Receive %s", jsonObject),
                    Constants.API_GET_COMMUNITY_BY_ID,
                    params);
    ```

## Release 准备流程

1. 确认注册登录的假数据是否有删除
2. 确认是否开启 proguard
3. 确认 release 的 debuggable 是否设为 false