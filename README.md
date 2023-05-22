[![Travis](https://img.shields.io/badge/ZFile-1.4.6-yellowgreen)](https://github.com/zippo88888888/ZFileManager)
[![Travis](https://img.shields.io/badge/API-21%2B-green)](https://github.com/zippo88888888/ZFileManager)
[![Travis](https://img.shields.io/badge/Apache-2.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# 特点

### 1. 默认支持 音频，视频，图片，txt，zip，word，excel，ppt，pdf 9种文件
### 2. 支持音频、视频播放，图片查看，zip解压，文件重命名、复制、移动、删除、查看详情
### 3. 支持查看指定文件类型，支持文件类型拓展，支持嵌套在Fragment中使用
### 4. 支持多选，数量、文件大小限制、实时排序、指定文件路径访问、文件夹角标
### 5. 支持QQ、微信文件选择 && 支持自定义获取QQ、微信文件
### 6. 支持 Android/data、Android/obb 数据获取，同时支持自定义
### 7. 不含任何三方框架，极高的定制化，支持Android 10/11/12/+++、DSL


### 部分截图（[下载demo直接体验](https://www.pgyer.com/Q13x)）
<div align="center">
<img src = "app/src/main/assets/s3.jpg" width=150 >
<img src = "app/src/main/assets/s0.jpg" width=150 >
<img src = "app/src/main/assets/s1.jpg" width=150 >
<img src = "app/src/main/assets/s2.jpg" width=150 >
</div>

## 基本使用

> **温馨提示： targetSdkVersion >= 29 清单文件中加上 android:requestLegacyExternalStorage="true"**  
> **温馨提示： targetSdkVersion >= 29 清单文件中加上 android:requestLegacyExternalStorage="true"**  
> **温馨提示： targetSdkVersion >= 29 清单文件中加上 android:requestLegacyExternalStorage="true"**


#### Step 0. 添加依赖

#### [最新版本](https://github.com/zippo88888888/ZFileManager/wiki/%E7%89%88%E6%9C%AC)

```groovy

    // Android 10 及以上版本  请查看最新版本

```

#### Step 1. 实现ZFileImageListener，并在调用前配置
```Kotlin

class MyFileImageListener : ZFileImageListener() {

    override fun loadImage(imageView: ImageView, file: File) {
        // 以Glide为例
        Glide.with(imageView.context)
            .load(file)
            .apply(RequestOptions().apply {
                placeholder(R.drawable.ic_zfile_other)
                error(R.drawable.ic_zfile_other)
            })
            .into(imageView)
    }
}

// 在调用前配置
getZFileHelp().init(MyFileImageListener())
```
#### Step 2. 在Activity或Fragment中使用

```kotlin

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_defaultMangerBtn.setOnClickListener {
            // DSL 方式 
            zfile { 
                result {
                    setFileListData(this)
                }
            }
            // 普通 方式 
            getZFileHelp()
                .result(this) {
                     setFileListData(this)
                }
        }
    }
    
    private fun setFileListData(fileList: MutableList<ZFileBean>?) {
        val sb = StringBuilder()
        fileList?.forEach {
            sb.append(it).append("\n\n")
        }
        main_resultTxt.text = sb.toString()
    }

}


```

### 更多使用方式[点击查看](https://github.com/zippo88888888/ZFileManager/wiki)


 
##### 目前本库趋于稳定， ^_^ 如果觉得可以 star 一下哦！
###### 源自 [FileManager](https://github.com/zippo88888888/FileManager)  [点这里页面加载速度应该会快一点](https://blog.csdn.net/qq_28322987/article/details/81384886)


[![Travis](https://img.shields.io/badge/ZFile-1.4.6-yellowgreen)](https://github.com/zippo88888888/ZFileManager)
[![Travis](https://img.shields.io/badge/API-21%2B-green)](https://github.com/zippo88888888/ZFileManager)
[![Travis](https://img.shields.io/badge/Apache-2.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)



 
