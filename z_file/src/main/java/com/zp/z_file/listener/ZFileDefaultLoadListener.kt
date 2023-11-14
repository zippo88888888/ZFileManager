package com.zp.z_file.listener

import android.content.Context
import com.zp.z_file.content.*
import com.zp.z_file.content.SD_ROOT
import com.zp.z_file.content.toFile
import com.zp.z_file.util.ZFileOtherUtil
import com.zp.z_file.util.ZFileSth
import java.io.File
import java.util.*

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
        val list = arrayListOf<ZFileBean>()
        val listFiles = path.toFile().listFiles(
            ZFileFilter(
                config.fileFilterArray,
                config.isOnlyFolder,
                config.isOnlyFile
            )
        )
        if (listFiles.isNullOrEmpty()) return list
        for (it in listFiles) {
            if (it.path == SAF_DATA_PATH || it.path == SAF_OBB_PATH) {
                if (config.showDataAndObbFolder) {
                    addFileToList(config, list, it)
                } else {
                    continue
                }
            } else {
                addFileToList(config, list, it)
            }
        }
        ZFileSth.sortord(list)
        return list
    }

    private fun addFileToList(
        config: ZFileConfiguration,
        list: MutableList<ZFileBean>,
        file: File
    ) {
        if (config.showHiddenFile) {
            list.add(file.toZFileBean())
        } else {
            if (!file.isHidden) {
                list.add(file.toZFileBean())
            }
        }
    }

}