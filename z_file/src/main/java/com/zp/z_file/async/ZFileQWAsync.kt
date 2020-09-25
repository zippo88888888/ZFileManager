package com.zp.z_file.async

import android.content.Context
import com.zp.z_file.content.*
import com.zp.z_file.listener.ZFileQWFilter
import com.zp.z_file.util.ZFileLog
import com.zp.z_file.util.ZFileOtherUtil
import com.zp.z_file.util.ZFileUtil
import java.io.File

/**
 * QQ WeChat ---> QW
 */
internal class ZFileQWAsync(
    private var fileType: String,
    private var type: Int,
    context: Context,
    block: MutableList<ZFileBean>?.() -> Unit
) : ZFileAsync(context, block) {

    private var filePathArray = ArrayList<String>()

    /**
     * 执行前调用 mainThread
     */
    override fun onPreExecute() {
        if (fileType == ZFileConfiguration.QQ) {
            when (type) {
                QW_PIC -> {
                    filePathArray.add(QQ_PIC)
                    filePathArray.add(QQ_PIC_MOVIE)
                }
                QW_VIDEO -> filePathArray.add(QQ_PIC_MOVIE)
                else -> {
                    filePathArray.add(QQ_DOWLOAD1)
                    filePathArray.add(QQ_DOWLOAD2)
                }
            }
        } else {
            when (type) {
                QW_PIC, QW_VIDEO -> filePathArray.add(WECHAT_FILE_PATH + WECHAT_PHOTO_VIDEO)
                else -> filePathArray.add(WECHAT_FILE_PATH + WECHAT_DOWLOAD)
            }
        }
    }

    /**
     * 获取数据
     * @param filterArray  规则
     */
    override fun doingWork(filterArray: Array<String>): MutableList<ZFileBean>? {
        val list = ArrayList<ZFileBean>()
        var listFiles: Array<File>? = null
        if (filePathArray.size <= 1) {
            val file = filePathArray[0].toFile()
            if (file.exists()) {
                listFiles = file.listFiles(ZFileQWFilter(filterArray, type == QW_OTHER))
            }
        } else {
            val file1 = filePathArray[0].toFile()
            var list1: Array<File>? = null
            if (file1.exists()) {
                list1 = file1.listFiles(ZFileQWFilter(filterArray, type == QW_OTHER))
            } /*else {
                ZFileLog.e("路径 ${filePathArray[0]} 不存在")
            }*/
            var list2: Array<File>? = null
            val file2 = filePathArray[1].toFile()
            if (file2.exists()) {
                list2 = file2.listFiles(ZFileQWFilter(filterArray, type == QW_OTHER))
            } /*else {
                ZFileLog.e("路径 ${filePathArray[1]} 不存在")
            }*/
            if (!list1.isNullOrEmpty() && !list2.isNullOrEmpty()) {
                listFiles = list1 + list2
            } else {
                if (!list1.isNullOrEmpty()) {
                    listFiles = list1
                }
                if (!list2.isNullOrEmpty()) {
                    listFiles = list2
                }
            }
        }

        listFiles?.forEach {
            if (!it.isHidden) {
                val bean = ZFileBean(
                    it.name,
                    it.isFile,
                    it.path,
                    ZFileOtherUtil.getFormatFileDate(it.lastModified()),
                    it.lastModified().toString(),
                    ZFileUtil.getFileSize(it.length()),
                    it.length()
                )
                list.add(bean)
            }
        }
        if (!list.isNullOrEmpty()) {
            list.sortByDescending { it.originalDate }
        }
        return list
    }

    /**
     * 完成后调用 mainThread
     */
    override fun onPostExecute() {
        filePathArray.clear()
    }
}