package com.zp.z_file.util

import com.zp.z_file.common.ZFileTypeManage
import com.zp.z_file.content.getFileType
import com.zp.z_file.content.toFile
import java.io.File
import java.util.*

/**
 * 对外工具类
 */
object ZFileHelp {

    /**
     * 获取文件大小
     */
    @JvmStatic
    fun getFileSize(filePath: String) = ZFileUtil.getFileSize(filePath.toFile().length())

    /**
     * 获取文件类型
     */
    @JvmStatic
    fun getFileType(filePath: String) = ZFileTypeManage.getTypeManager().getFileType(filePath)

    /**
     * 根据后缀文件类型
     */
    @JvmStatic
    fun getFileTypeBySuffix(filePath: String) = filePath.getFileType().toLowerCase(Locale.CHINA)

    /**
     * 获取文件 格式化后的Modified
     */
    @JvmStatic
    fun getFormatFileDate(file: File) = ZFileOtherUtil.getFormatFileDate(file.lastModified())

}