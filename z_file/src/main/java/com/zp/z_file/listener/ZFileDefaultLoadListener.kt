package com.zp.z_file.listener

import android.content.Context
import com.zp.z_file.content.*
import com.zp.z_file.content.SD_ROOT
import com.zp.z_file.content.getZFileConfig
import com.zp.z_file.util.ZFileOtherUtil
import com.zp.z_file.util.ZFileUtil
import java.util.*
import kotlin.collections.ArrayList

internal class ZFileDefaultLoadListener : ZFileLoadListener {

    /**
     * 获取手机里的文件List
     * @param filePath String?          指定的文件目录访问，空为SD卡根目录
     * @return MutableList<ZFileBean>?  list
     */
    override fun getFileList(context: Context?, filePath: String?) =
        getDefaultFileList(context, filePath)

    private fun getDefaultFileList(context: Context?, filePath: String?): MutableList<ZFileBean> {
        val path = if (filePath.isNullOrEmpty()) SD_ROOT else filePath
        val config = getZFileConfig()
        val list = ArrayList<ZFileBean>()
        val listFiles = path.toFile().listFiles(
            ZFileFilter(
                config.fileFilterArray,
                config.isOnlyFolder,
                config.isOnlyFile
            )
        )
        listFiles?.forEach {
            if (config.showHiddenFile) { // 是否显示隐藏文件
                val bean = ZFileBean(
                        it.name,
                        it.isFile,
                        it.path,
                        ZFileOtherUtil.getFormatFileDate(it.lastModified()) ?: "未知时间",
                        it.lastModified().toString(),
                        ZFileUtil.getFileSize(it.length()),
                        it.length(),
                        it.parent
                )
                list.add(bean)
            } else {
                if (!it.isHidden) {
                    val bean = ZFileBean(
                            it.name,
                            it.isFile,
                            it.path,
                            ZFileOtherUtil.getFormatFileDate(it.lastModified()) ?: "未知时间",
                            it.lastModified().toString(),
                            ZFileUtil.getFileSize(it.length()),
                            it.length(),
                            it.parent
                    )
                    list.add(bean)
                }
            }
        }

        // 排序相关
        if (config.sortord == ZFileConfiguration.ASC) {
            when (config.sortordBy) {
                ZFileConfiguration.BY_NAME -> list.sortBy { it.fileName.toLowerCase(Locale.CHINA) }
                ZFileConfiguration.BY_DATE -> list.sortBy { it.originalDate }
                ZFileConfiguration.BY_SIZE -> list.sortBy { it.originaSize }
            }
        } else {
            when (config.sortordBy) {
                ZFileConfiguration.BY_NAME -> list.sortByDescending { it.fileName.toLowerCase(Locale.CHINA) }
                ZFileConfiguration.BY_DATE -> list.sortByDescending { it.originalDate }
                ZFileConfiguration.BY_SIZE -> list.sortByDescending { it.originaSize }
            }
        }
        return list
    }
}