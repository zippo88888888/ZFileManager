package com.zp.zfile_manager.diy

import android.content.Context
import androidx.collection.ArrayMap
import com.zp.z_file.content.ZFileFolderBadgeHintBean
import com.zp.z_file.listener.ZFileFolderBadgeHintListener
import com.zp.zfile_manager.R

class MyFolderBadgeHintListener : ZFileFolderBadgeHintListener() {

    override fun doingWork(context: Context): ArrayMap<String, ZFileFolderBadgeHintBean> {
        val map = super.doingWork(context)
        val androidPath = "/storage/emulated/0/Android"
        map!![androidPath]?.folderHint = "Android NB 666"
        val otherPath = "/storage/emulated/0/Documents"
        map[otherPath] = ZFileFolderBadgeHintBean(
            folderPath = otherPath,
            folderHint = "这是我自定义的说明文字",
            folderBadgeType = 0
        )
        val other2Path = "/storage/emulated/0/Fonts"
        map[other2Path] = ZFileFolderBadgeHintBean(
            folderPath = other2Path,
            folderBadgeIcon = R.drawable.zfile_movie,
            folderBadgeType = 1
        )
        return map
    }

}