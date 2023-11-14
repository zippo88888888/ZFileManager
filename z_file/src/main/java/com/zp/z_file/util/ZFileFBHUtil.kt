package com.zp.z_file.util

import android.content.Context
import android.os.Build
import androidx.collection.ArrayMap
import com.zp.z_file.R
import com.zp.z_file.content.*
import com.zp.z_file.content.QQ_PIC
import com.zp.z_file.content.QQ_PIC_MOVIE
import com.zp.z_file.content.SD_ROOT

internal object ZFileFBHUtil {

    /**
     * 通用的
     */
    fun doingWork(context: Context): ArrayMap<String, ZFileFolderBadgeHintBean> {
        val map = ArrayMap<String, ZFileFolderBadgeHintBean>()
        val androidPath = "${SD_ROOT}Android"
        val cameraPath = "${SD_ROOT}DCIM"
        val cameraPath2 = "${cameraPath}/Camera"
        val downloadPath = "${SD_ROOT}Download"
        val musicPath = "${SD_ROOT}Music"
        val picturesPath = "${SD_ROOT}Pictures"
        val moviePath = "${SD_ROOT}Movies"
        val tencentPath = "${SD_ROOT}tencent"
        map[androidPath] = ZFileFolderBadgeHintBean(
            folderPath = androidPath,
            folderHint = "Android 系统文件",
            folderBadgeIcon = R.drawable.zfile_android
        )
        map[SAF_DATA_PATH] = ZFileFolderBadgeHintBean(
            folderPath = SAF_DATA_PATH,
            folderHint = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) "访问限制" else "Application Package Data",
            folderBadgeIcon = R.drawable.zfile_sys
        )
        map[SAF_OBB_PATH] = ZFileFolderBadgeHintBean(
            folderPath = SAF_OBB_PATH,
            folderHint = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) "访问限制" else "数据包",
            folderBadgeIcon = R.drawable.zfile_obb
        )
        map[cameraPath] = ZFileFolderBadgeHintBean(
            folderPath = cameraPath,
            folderHint = "相册",
            folderBadgeIcon = R.drawable.zfile_camera
        )
        map[cameraPath2] = ZFileFolderBadgeHintBean(
            folderPath = cameraPath2,
            folderHint = "相机",
            folderBadgeIcon = R.drawable.zfile_camera
        )
        map[downloadPath] = ZFileFolderBadgeHintBean(
            folderPath = downloadPath,
            folderHint = "系统下载",
            folderBadgeIcon = R.drawable.zfile_download
        )
        map[musicPath] = ZFileFolderBadgeHintBean(
            folderPath = musicPath,
            folderHint = "音乐",
            folderBadgeIcon = R.drawable.zfile_music
        )
        map[picturesPath] = ZFileFolderBadgeHintBean(
            folderPath = picturesPath,
            folderHint = "图片",
            folderBadgeIcon = R.drawable.zfile_photo
        )
        map[moviePath] = ZFileFolderBadgeHintBean(
            folderPath = moviePath,
            folderHint = "视频",
            folderBadgeIcon = R.drawable.zfile_movie
        )
        map[tencentPath] = ZFileFolderBadgeHintBean(
            folderPath = tencentPath,
            folderHint = "腾讯",
            folderBadgeIcon = R.drawable.zfile_tencent
        )
        val qqPath = QQ_PIC.substring(0, QQ_PIC.length - 1)
        map[qqPath] = ZFileFolderBadgeHintBean(
            folderPath = qqPath,
            folderHint = "QQ图片",
            folderBadgeIcon = R.drawable.zfile_qq
        )
        val qqPath2 = QQ_PIC_MOVIE.substring(0, QQ_PIC_MOVIE.length - 1)
        map[qqPath2] = ZFileFolderBadgeHintBean(
            folderPath = qqPath2,
            folderHint = "QQ图片视频",
            folderBadgeIcon = R.drawable.zfile_qq
        )
        val wechatPath = WECHAT_FILE_PATH.substring(0, WECHAT_FILE_PATH.length - 1)
        map[wechatPath] = ZFileFolderBadgeHintBean(
            folderPath = WECHAT_FILE_PATH,
            folderHint = "微信",
            folderBadgeIcon = R.drawable.zfile_wechat
        )
        val wPath2 = WECHAT_FILE_PATH + WECHAT_PHOTO_VIDEO
        val wechatPath2 = wPath2.substring(0, wPath2.length - 1)
        map[wechatPath2] = ZFileFolderBadgeHintBean(
            folderPath = wechatPath2,
            folderHint = "微信图片视频",
            folderBadgeIcon = R.drawable.zfile_wechat
        )
        val wPath3 = WECHAT_FILE_PATH + WECHAT_DOWLOAD
        val wechatPath3 = wPath3.substring(0, wPath3.length - 1)
        map[wechatPath3] = ZFileFolderBadgeHintBean(
            folderPath = wechatPath3,
            folderHint = "微信下载",
            folderBadgeIcon = R.drawable.zfile_wechat
        )
        return map
    }

}