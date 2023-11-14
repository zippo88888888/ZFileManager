package com.zp.zfile_manager.diy

import android.content.Context
import android.os.Build
import androidx.collection.ArrayMap
import com.zp.z_file.content.ZFileFolderBadgeHintBean
import com.zp.z_file.listener.ZFileFolderBadgeHintListener
import com.zp.zfile_manager.R
import com.zp.zfile_manager.content.Content

class MyFolderBadgeHintListener : ZFileFolderBadgeHintListener() {

    private val SAF_DATA_PATH = "${Content.SD_PATH}Android/data"
    private val SAF_OBB_PATH = "${Content.SD_PATH}Android/obb"
    private val DOC_PATH = "${Content.SD_PATH}Documents"
    private val DCIM_PATH = "${Content.SD_PATH}DCIM"

    /**
     * 说明文字大小
     */
    override fun hintTextSize(folderPath: String): Float {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (folderPath.indexOf(SAF_DATA_PATH) != -1 || folderPath.indexOf(SAF_OBB_PATH) != -1) {
                return 15f
            }
        }
        return super.hintTextSize(folderPath)
    }

    /**
     * 说明文字颜色
     */
    override fun hintTextColor(folderPath: String): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (folderPath.indexOf(SAF_DATA_PATH) != -1 || folderPath.indexOf(SAF_OBB_PATH) != -1) {
                return R.color.red
            }
        }
        if (folderPath.indexOf(DOC_PATH) != -1) {
            return R.color.green
        }
        if (folderPath.indexOf(DCIM_PATH) != -1) {
            return R.color.zfile_base_color
        }
        return super.hintTextColor(folderPath)
    }

    override fun doingWork(context: Context): ArrayMap<String, ZFileFolderBadgeHintBean> {
        val map = super.doingWork(context)
        val androidPath = "${Content.SD_PATH}Android"
        map!![androidPath]?.folderHint = "Android NB 666"
        map[DOC_PATH] = ZFileFolderBadgeHintBean(
            folderPath = DOC_PATH,
            folderHint = "这是我自定义的说明文字"
        )
        val other2Path = "${Content.SD_PATH}Fonts"
        map[other2Path] = ZFileFolderBadgeHintBean(
            folderPath = other2Path,
            folderBadgeIcon = R.drawable.zfile_movie
        )
        return map
    }

}