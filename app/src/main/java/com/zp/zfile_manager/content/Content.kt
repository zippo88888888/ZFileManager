package com.zp.zfile_manager.content

import androidx.collection.ArrayMap
import com.zp.z_file.content.*

object Content {

    const val APK = "apk"

    const val AUTHORITY = "com.zp.zfile_manager.ZFileManagerProvider"

    val CONFIG: ZFileConfiguration
        get() = ZFileConfiguration().apply {
            authority = AUTHORITY
        }





    val TITLES: Array<String>
        get() {
            return arrayOf("Image", "Video", "WD", "Other")
        }

    // 简单模拟 QQ的过滤规则
    val FILTER: ArrayMap<Int, Array<String>>
        get() {
            val map = ArrayMap<Int, Array<String>>()
            map[ZFILE_QW_PIC] = arrayOf(PNG, JPEG, JPG, GIF)
            map[ZFILE_QW_MEDIA] = arrayOf(MP4, _3GP, "rmvb", "mp3", "aac")
            map[ZFILE_QW_DOCUMENT] = arrayOf(TXT, JSON, XML, DOC,  XLS,  PPT,  PDF)
            map[ZFILE_QW_OTHER] = arrayOf("") // 空表示 其他文件类型
            return map
        }

    // 简单模拟 QQ的文件保存路径 ，路径很随便，反正可以自定义
    val QQ_MAP: ArrayMap<Int, MutableList<String>>
        get() {
            val map = ArrayMap<Int, MutableList<String>>()
            map[ZFILE_QW_PIC] = arrayListOf(
                "/storage/emulated/0/tencent/QQ_Images/",
                "/storage/emulated/0/Pictures/",
                "/storage/emulated/0/DCIM/",
                "/storage/emulated/0/Pictures/QQ/"
            )
            map[ZFILE_QW_MEDIA] = arrayListOf(
                "/storage/emulated/0/Pictures/QQ/",
                "/storage/emulated/0/tencent/MicroMsg/WeiXin/"
            )
            map[ZFILE_QW_DOCUMENT] = arrayListOf(
                "/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/QQfile_recv/",
                "/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/QQ_business/"
            )
            map[ZFILE_QW_OTHER] = arrayListOf(
                "/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/QQfile_recv/",
                "/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/QQ_business/",
                "/storage/emulated/0/tencent/MicroMsg/Download/"
            )
            return map
        }
}