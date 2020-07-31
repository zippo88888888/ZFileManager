[![Travis](https://img.shields.io/badge/ZFile-1.1.1-yellowgreen)](https://github.com/zippo88888888/ZFileManager)
[![Travis](https://img.shields.io/badge/API-21%2B-green)](https://github.com/zippo88888888/ZFileManager)
[![Travis](https://img.shields.io/badge/Apache-2.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# 特点

### 1. 支持操作音频，视频，图片，txt，zip，word，excel，ppt，pdf等文件
### 2. 支持音频，视频播放，图片查看，zip解压 && 重命名、复制、移动、删除、查看详情
### 3. 支持查看指定文件类型，支持文件类型拓展
### 4. 支持多选，最大数量、文件大小限制、实时排序、指定文件路径访问
### 5. 高度可定制化，兼容AndroidX

#### 即将支持
##### 支持QQ、微信单独选择

### 截图
<div align="center">
<img src = "app/src/main/assets/s0.jpg" width=180 >
<img src = "app/src/main/assets/s1.jpg" width=180 >
<img src = "app/src/main/assets/s2.jpg" width=180 >
<img src = "app/src/main/assets/s3.jpg" width=180 >
</div>

## 基本使用 （[Java使用](https://github.com/zippo88888888/ZFileManager/blob/master/app/src/main/java/com/zp/zfile_manager/JavaSampleActivity.java)）

#### Step 0. 添加依赖 申明权限
```groovy

implementation 'com.github.zp:z_file:1.1.1'
```
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```
#### Step 1. 实现ZFileImageListener，并在Application中或调用前初始化 
```Kotlin

class MyFileImageListener : ZFileImageListener() {

    override fun loadImage(imageView: ImageView, file: File) {
        Glide.with(imageView.context)
            .load(file)
            .apply(RequestOptions().apply {
                placeholder(R.drawable.ic_zfile_other)
                error(R.drawable.ic_zfile_other)
            })
            .into(imageView)
    }
}

getZFileHelp().init(MyFileImageListener())
```
#### Step 2. 在Activity或Fragment中 实现 ZFileSelectListener 接口

```kotlin

// 打开文件管理
getZFileHelp().setFileResultListener(this).start(this)
getZFileHelp().setFileResultListener(this).start(this, "指定目录")

class MainActivity : AppCompatActivity(), ZFileSelectListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_defaultMangerBtn.setOnClickListener {
            getZFileHelp()
                .setFileResultListener(this)
                .start(this)
        }
    }

    override fun onSelected(fileList: MutableList<ZFileBean>?) {
        val sb = StringBuilder()
        fileList?.forEach {
            sb.append(it).append("\n\n")
        }
        main_resultTxt.text = sb.toString()
    }

}


```

## 高级用法   
> ##### ZFileManager额外提供了对外的帮助类 [ZFileHelp](https://github.com/zippo88888888/ZFileManager/blob/master/z_file/src/main/java/com/zp/z_file/util/ZFileHelp.kt)

### 文件类型拓展

#### Step 1. 新建一个类：ZFileType，重写里面的openFile()、loadingFile()方法 

```kotlin

// 自定义的类型
const val APK = "apk"

/**
 * 自定义Apk文件类型
 */
class ApkType : ZFileType() {

    /**
     * 打开文件
     * @param filePath  文件路径
     * @param view      当前视图
     */
    override fun openFile(filePath: String, view: View) {
        Toast.makeText(view.context, "打开自定义拓展文件", Toast.LENGTH_SHORT).show()
    }

    /**
     * 加载文件
     * @param filePath 文件路径
     * @param pic      文件展示的图片
     */
    override fun loadingFile(filePath: String, pic: ImageView) {
        pic.setImageResource(R.mipmap.ic_launcher_round)
    }
    
}

```

#### Step 2. 新建一个类：ZFileTypeListener，重写里面的getFileType()方法 （有多个自定义类型，公用即可）
```kotlin

class MyFileTypeListener : ZFileTypeListener() {

    override fun getFileType(filePath: String) =
        when (ZFileHelp.getFileTypeBySuffix(filePath)) {
            APK -> ApkType()
            else -> super.getFileType(filePath)
        }
}

```

#### Step 3. 在Application中或调用前赋值  

```kotlin
getZFileHelp().setFileTypeListener(MyFileTypeListener())

```

##### 搞定，是不是很简单 ^_^

> ###### 切，简单是简单，但是你这个获取文件实现的方式不优雅，你界面上的图片太丑了，打开文件你全部都是调用系统方式打开(作者你个渣渣)，我只想选择文件，不想要长按事件，你的长按事件弹出的功能有些不是我想要的...
> ###### 扶我起来，我要搞死杠精 

#### 自定义文件获取
```kotlin

/**
 * 获取文件
 * 此方式，排序、是否显示隐藏文件、过滤规则等等操作都需要自己实现
 * Kotlin 获取配置信息：getZFileConfig()
 * Java 获取配置信息：ZFileManageHelp.getInstance().getConfiguration()
 */
class MyFileLoadListener : ZFileLoadListener {

    /**
     * 获取手机里的文件List
     * @param filePath String?          指定的文件目录访问，空为SD卡根目录
     * @return MutableList<ZFileBean>?  list
     */
    override fun getFileList(context: Context?, filePath: String?) =
        getFileList(context, filePath)

    private fun getFileList(context: Context?, filePath: String?): MutableList<ZFileBean> {
         
    }
}

getZFileHelp().setFileLoadListener(MyFileLoadListener())


```

#### UI 或操作自定义 更多可查看 [ZFileConfiguration](https://github.com/zippo88888888/ZFileManager/blob/master/z_file/src/main/java/com/zp/z_file/content/ZFileConfiguration.kt) 或 [values](https://github.com/zippo88888888/ZFileManager/tree/master/z_file/src/main/res/values)
 
```kotlin

    /**
     * 是否需要长按事件
     */
    var needLongClick = true

    /**
     * 默认只有文件才有长按事件
     */
    var isOnlyFileHasLongClick = true

    /**
     * 长按后需要显示的操作类型
     */
    var longClickOperateTitles: Array<String>? = null

    // 自定义方式一

    /**
     * 相关资源配置
     * @property audioRes Int        音频
     * @property txtRes Int          文本
     * @property pdfRes Int          PDF
     * @property pptRes Int          PPT
     * @property wordRes Int         Word
     * @property excelRes Int        Excel
     * @property zipRes Int          ZIP
     * @property otherRes Int        其他类型
     * @property emptyRes Int        空资源
     * @property folderRes Int       文件夹
     * @property lineColor Int       列表分割线颜色
     */
    @Parcelize
    data class ZFileResources @JvmOverloads constructor(
        var audioRes: Int = R.drawable.ic_zfile_audio,
        var txtRes: Int = R.drawable.ic_zfile_txt,
        var pdfRes: Int = R.drawable.ic_zfile_pdf,
        var pptRes: Int = R.drawable.ic_zfile_ppt,
        var wordRes: Int = R.drawable.ic_zfile_word,
        var excelRes: Int = R.drawable.ic_zfile_excel,
        var zipRes: Int = R.drawable.ic_zfile_zip,
        var otherRes: Int = R.drawable.ic_zfile_other,
        var emptyRes: Int = R.drawable.ic_zfile_empty,
        var folderRes: Int = R.drawable.ic_zfile_folder,
        var lineColor: Int = R.color.zfile_line_color
    ) : Serializable, Parcelable
    
     getZFileHelp().setConfiguration(getZFileConfig().apply {
            resources = ZFileResources(R.drawable.ic_diy_audio)
            maxLength = 6
            maxLengthStr = "666"
            ...
        })
    
    // 方式二 如：Txt类型展示的图片太丑，继承自TxtType，重写方法即可，
    // 可参照项目里面 diy包下面的实现，这种方式对于内置的文件类型可以完全自定义操作

```
#### 自定义打开默认支持的文件
```kotlin

class MyFileOpenListener : ZMyFileOpenListener() {

    override fun openAudio(filePath: String, view: View) {
    }

    override fun openImage(filePath: String, view: View) {
    }

    override fun openVideo(filePath: String, view: View) {
    }

    override fun openTXT(filePath: String, view: View) {
    }

    override fun openZIP(filePath: String, view: View) {
    }

    override fun openDOC(filePath: String, view: View) {
    }

    override fun openXLS(filePath: String, view: View) {
    }

    override fun openPPT(filePath: String, view: View) {
    }

    override fun openPDF(filePath: String, view: View) {
    }

    override fun openOther(filePath: String, view: View) {
    }
}

getZFileHelp().setOpenListener(MyFileOpenListener())

```
#### 自定义文件操作

```kotlin

class MyFileOperateListener : ZFileOperateListener() {

    /**
     * 文件重命名
     * @param filePath String   文件路径
     * @param context Context   Context
     * @param (Boolean, String) Boolean：成功或失败；String：新名字
     */
    open fun renameFile(
        filePath: String,
        context: Context,
        block: (Boolean, String) -> Unit
    ) {
       // 先弹出重命名Dialog，再执行重命名方法  
    }

    /**
     * 复制文件 耗时操作，建议放在非UI线程中执行
     * @param sourceFile String     源文件地址
     * @param targetFile String     目标文件地址
     * @param context Context       Context
     * @param block                 文件操作成功或失败后的监听
     */
    override fun copyFile(
        sourceFile: String,
        targetFile: String,
        context: Context,
        block: Boolean.() -> Unit
    ) {
       thread {
           val success = MyTestFileUtil.copyFile(sourceFile, targetFile, context)
           (context as? Activity)?.let { 
               it.runOnUiThread { 
                   block.invoke(success)
               }
           }
       }
    }

    /**
     * 移动文件 耗时操作，建议放在非UI线程中执行
     */
    override fun moveFile(
        sourceFile: String,
        targetFile: String,
        context: Context,
        block: Boolean.() -> Unit
    ) {
    
    }

    /**
     * 删除文件 耗时操作，建议放在非UI线程中执行
     */
    override fun deleteFile(filePath: String, context: Context, block: Boolean.() -> Unit) {
         
    }

    /**
     * 解压文件 耗时操作，建议放在非UI线程中执行
     */
    override fun zipFile(
        sourceFile: String,
        targetFile: String,
        context: Context,
        block: Boolean.() -> Unit
    ) {
    }

    /**
     * 文件详情
     */
    override fun fileInfo(bean: ZFileBean, context: Context) {
        
    }
}

getZFileHelp().setFileOperateListener(MyFileOperateListener())

```
 
##### 更多操作请查看demo， ^_^ 如果觉得可以 star 一下哦
 
 

> ##### 还是不行，emmmm 源码给你 想怎么弄就怎么弄  溜了溜了



 
