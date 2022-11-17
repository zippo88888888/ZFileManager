package com.zp.zfile_manager.diy

import com.zp.z_file.content.*
import com.zp.z_file.listener.ZQWFileLoadListener
import com.zp.z_file.util.ZFileHelp
import com.zp.zfile_manager.content.Content
import java.io.File
import java.io.FileFilter
import java.util.*

/**
 * 完全自定义 获取 QQ、Wechat 文件
 */
class MyQWFileListener : ZQWFileLoadListener() {

    /**
     * 获取标题
     * @return Array<String>
     */
    override fun getTitles(): Array<String>? {
        return arrayOf("图片", "媒体", "文档", "这是我自定义的title")
    }

    /**
     * 获取过滤规则
     * @param fileType Int      文件类型 see [ZFILE_QW_PIC] [ZFILE_QW_MEDIA] [ZFILE_QW_DOCUMENT] [ZFILE_QW_OTHER]
     */
    override fun getFilterArray(fileType: Int): Array<String> {
        return when (fileType) {
            ZFILE_QW_PIC -> arrayOf(PNG, JPG, JPEG, "gif")
            ZFILE_QW_MEDIA -> arrayOf(MP4, "3gp", "mp3")
            ZFILE_QW_DOCUMENT -> arrayOf(PDF, PPT, PPTX, DOC, DOCX, XLS, XLSX)
            else -> arrayOf(TXT, JSON, XML, ZIP, "rar")
        }
    }

    /**
     * 获取 QQ 或 WeChat 文件路径
     * @param qwType String         QQ 或 WeChat  see [ZFileConfiguration.QQ] [ZFileConfiguration.WECHAT]
     * @param fileType Int          文件类型 see [ZFILE_QW_PIC] [ZFILE_QW_MEDIA] [ZFILE_QW_DOCUMENT] [ZFILE_QW_OTHER]
     * @return MutableList<String>  文件路径集合（因为QQ或WeChat保存的文件可能存在多个路径）
     */
    override fun getQWFilePathArray(qwType: String, fileType: Int): MutableList<String> {
        val listArray = arrayListOf<String>()
        if (qwType == ZFileConfiguration.QQ) { // QQ
            when (fileType) {
                ZFILE_QW_PIC -> {
                    listArray.add("${Content.SD_PATH}tencent/QQ_Images/")
                    listArray.add("${Content.SD_PATH}Pictures/") // QQ文件保存路径1，仅做演示
                    listArray.add("${Content.SD_PATH}DCIM/") // QQ文件保存路径2，仅做演示
                    listArray.add("${Content.SD_PATH}Pictures/QQ/")
                }
                ZFILE_QW_MEDIA -> {
                    listArray.add("${Content.SD_PATH}Pictures/QQ/")
                }
                ZFILE_QW_DOCUMENT -> {
                    listArray.add("${Content.SD_PATH}Android/data/com.tencent.mobileqq/Tencent/QQfile_recv/")
                    listArray.add("${Content.SD_PATH}Android/data/com.tencent.mobileqq/Tencent/QQ_business/")
                }
                else -> {
                    listArray.add("${Content.SD_PATH}Android/data/com.tencent.mobileqq/Tencent/QQfile_recv/")
                    listArray.add("${Content.SD_PATH}Android/data/com.tencent.mobileqq/Tencent/QQ_business/")
                }
            }
        } else { // WeChat
            when (fileType) {
                ZFILE_QW_PIC -> {
                    listArray.add("${Content.SD_PATH}tencent/MicroMsg/WeiXin/")
                }
                ZFILE_QW_MEDIA -> {
                    listArray.add("${Content.SD_PATH}tencent/MicroMsg/WeiXin/")
                }
                ZFILE_QW_DOCUMENT -> {
                    listArray.add("${Content.SD_PATH}tencent/MicroMsg/Download/")
                    listArray.add("${Content.SD_PATH}Pictures/WeiXin/") // 微信文件保存路径
                }
                else -> {
                    listArray.add("${Content.SD_PATH}tencent/MicroMsg/Download/")
                }
            }
        }
        return listArray
    }

    /**
     * 获取数据
     * @param fileType Int                          文件类型 see [ZFILE_QW_PIC] [ZFILE_QW_MEDIA] [ZFILE_QW_DOCUMENT] [ZFILE_QW_OTHER]
     * @param qwFilePathArray MutableList<String>   QQ 或 WeChat 文件路径集合
     * @param filterArray Array<String>             过滤规则
     */
    override fun getQWFileDatas(fileType: Int, qwFilePathArray: MutableList<String>, filterArray: Array<String>): MutableList<ZFileBean> {
        val pathListFile = arrayListOf<Array<File>?>()
        qwFilePathArray.forEach {
            val file = File(it)
            if (file.exists()) {
                pathListFile.add(file.listFiles(MyQWFilter(filterArray)))
            }
        }
        if (pathListFile.isEmpty()) return mutableListOf()
        val list = mutableListOf<ZFileBean>()
        pathListFile.forEach { item ->
            item?.forEach {
                if (!it.isHidden) {
                    val bean = ZFileBean(
                            it.name,
                            it.isFile,
                            it.path,
                            ZFileHelp.getFormatFileDate(it),
                            it.lastModified().toString(),
                            ZFileHelp.getFileSize(it.path),
                            it.length()
                    )
                    list.add(bean)
                }
            }
        }
        if (!list.isNullOrEmpty()) {
            list.sortByDescending { it.originalDate }
        }
        return list
    }

    class MyQWFilter(private var filterArray: Array<String>) : FileFilter {

        override fun accept(file: File): Boolean {
            filterArray.forEach {
                if (file.name.accept(it)) {
                    return true
                }
            }
            return false
        }

        private fun String.accept(type: String) =
                this.endsWith(type.toLowerCase(Locale.CHINA)) || this.endsWith(type.toUpperCase(Locale.CHINA))
    }
}

