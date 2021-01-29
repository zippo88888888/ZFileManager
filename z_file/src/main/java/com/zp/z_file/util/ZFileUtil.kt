package com.zp.z_file.util

import android.app.Activity
import android.content.Context
import android.view.View
import com.zp.z_file.common.ZFileTypeManage
import com.zp.z_file.content.*
import com.zp.z_file.async.ZFileThread
import com.zp.z_file.listener.ZFileQWFilter
import com.zp.z_file.ui.dialog.ZFileLoadingDialog
import java.io.*
import java.nio.channels.FileChannel
import java.text.DecimalFormat
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.concurrent.thread

internal object ZFileUtil {

    /**
     * 获取文件
     */
    fun getList(context: Context, bolck: MutableList<ZFileBean>?.() -> Unit) {
        ZFileThread(context, bolck).start(getZFileConfig().filePath)
    }

    /**
     * 打开文件
     */
    fun openFile(filePath: String, view: View) {
        ZFileTypeManage.getTypeManager().openFile(filePath, view)
    }

    /**
     * 查看文件详情
     */
    fun infoFile(bean: ZFileBean, context: Context) {
        ZFileTypeManage.getTypeManager().infoFile(bean, context)
    }

    /**
     * 重命名文件
     */
    fun renameFile(
        filePath: String,
        newName: String,
        context: Context,
        block: (Boolean, String) -> Unit
    ) {
        val activity = context as Activity
        val dialog = ZFileLoadingDialog(activity, "重命名中...").run {
            setCancelable(false)
            show()
            this
        }
        thread {
            val isSuccess = try {
                val oldFile = filePath.toFile()
                val oldFileType = oldFile.getFileType()
                val oldPath = oldFile.path.substring(0, oldFile.path.lastIndexOf("/") + 1)
                val newFile = File("$oldPath$newName.$oldFileType")
                oldFile.renameTo(newFile)
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
            activity.runOnUiThread {
                dialog.dismiss()
                activity.toast(if (isSuccess) "重命名成功" else "重命名失败")
                block.invoke(isSuccess, newName)
            }
        }
    }

    /**
     * 删除文件
     */
    fun deleteFile(filePath: String, context: Context, block: Boolean.() -> Unit) {
        ZFileLog.i("删除文件的目录：$filePath")
        callFileByType(filePath, "", context, DELTE_TYPE, block)
    }

    /**
     * 复制文件
     */
    fun copyFile(filePath: String, outPath: String, context: Context, block: Boolean.() -> Unit) {
        ZFileLog.i("源文件目录：$filePath")
        ZFileLog.i("复制文件目录：$outPath")
        callFileByType(filePath, outPath, context, COPY_TYPE, block)
    }

    /**
     * 剪切文件
     */
    fun cutFile(filePath: String, outPath: String, context: Context, block: Boolean.() -> Unit) {
        ZFileLog.i("源文件目录：$filePath")
        ZFileLog.i("移动目录：$outPath")
        callFileByType(filePath, outPath, context, CUT_TYPE, block)
    }

    /**
     * 解压文件
     */
    fun zipFile(filePath: String, outZipPath: String, context: Context, block: Boolean.() -> Unit) {
        ZFileLog.i("源文件目录：$filePath")
        ZFileLog.i("解压目录：$outZipPath")
        callFileByType(filePath, outZipPath, context, ZIP_TYPE, block)
    }

    private fun callFileByType(
        filePath: String,
        outPath: String,
        context: Context,
        type: Int,
        block: Boolean.() -> Unit
    ) {
        val msg = when (type) {
            COPY_TYPE -> ZFileConfiguration.COPY
            CUT_TYPE -> ZFileConfiguration.MOVE
            DELTE_TYPE -> ZFileConfiguration.DELETE
            else -> "解压"
        }
        val activity = context as Activity
        val dialog = ZFileLoadingDialog(activity, "${msg}中...").run {
            setCancelable(false)
            show()
            this
        }
        thread {
            val isSuccess = when (type) {
                COPY_TYPE -> copyFile(filePath, outPath)
                CUT_TYPE -> cutFile(filePath, outPath)
                DELTE_TYPE -> File(filePath).delete()
                else -> extractFile(filePath, outPath)
            }
            activity.runOnUiThread {
                dialog.dismiss()
                activity.toast(if (isSuccess) "${msg}成功" else "${msg}失败")
                block.invoke(isSuccess)
            }
        }
    }

    /**
     * 复制文件
     */
    private fun copyFile(sourceFile: String, targetFile: String) : Boolean {
        var success = true
        val oldFile = File(sourceFile)
        val outFile = File(targetFile + "/" + oldFile.name)

        var inputChannel: FileChannel? = null
        var outputChannel: FileChannel? = null
        try {
            inputChannel = FileInputStream(oldFile).channel
            outputChannel = FileOutputStream(outFile).channel
            outputChannel?.transferFrom(inputChannel, 0, inputChannel?.size() ?: 0L)
        } catch (e: Exception) {
            e.printStackTrace()
            success = false
        } finally {
            inputChannel?.close()
            outputChannel?.close()
            return success
        }
    }

    /**
     * 剪切文件
     */
    private fun cutFile(sourceFile: String, targetFile: String): Boolean {
        val copySuccess = copyFile(sourceFile, targetFile)
        var delSuccess = false
        try {
            delSuccess = File(sourceFile).delete()
        } catch (e: Exception) {
            e.printStackTrace()
            delSuccess = false
        } finally {
            return copySuccess && delSuccess
        }
    }

    /**
     * 解压文件
     */
    private fun extractFile(zipFileName: String, outPutDir: String): Boolean {
        var flag = true
        var zipInputStream: ZipInputStream? = null
        val zipEntry: ZipEntry
        var outputStream: FileOutputStream? = null
        var name: String
        try {
            zipInputStream = ZipInputStream(FileInputStream(zipFileName))
            zipEntry = zipInputStream.nextEntry
            while (zipEntry != null) {
                name = zipEntry.name
                if (zipEntry.isDirectory) {
                    name = name.substring(0, name.length - 1)
                    val file = File(outPutDir + File.separator + name)
                    file.mkdirs()
                } else {
                    val file = File(outPutDir + File.separator + name)
                    file.createNewFile()
                    outputStream = FileOutputStream(file)
                    var ch = 0
                    val bytes = ByteArray(1024)
                    while (ch != -1) {
                        ch = zipInputStream.read(bytes)
                        outputStream.write(bytes, 0, ch)
                        outputStream.flush()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            flag = false
            ZFileLog.e("解压失败（目前解压只支持压缩包里只有一个文件，多个需要自己实现）")
        } finally {
            outputStream?.close()
            try {
                zipInputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return flag
        }
    }

    /**
     * 获取文件大小
     */
    fun getFileSize(fileS: Long): String {
        val df = DecimalFormat("#.00")
        val byte_size = java.lang.Double.valueOf(df.format(fileS.toDouble()))
        if (byte_size < 1024) {
            return "$byte_size B"
        }
        val kb_size = java.lang.Double.valueOf(df.format(fileS.toDouble() / 1024))
        if (kb_size < 1024) {
            return "$kb_size KB"
        }
        val mb_size = java.lang.Double.valueOf(df.format(fileS.toDouble() / 1048576))
        if (mb_size < 1024) {
            return "$mb_size MB"
        }
        val gb_size = java.lang.Double.valueOf(df.format(fileS.toDouble() / 1073741824))
        if (gb_size < 1024) {
            return "$gb_size GB"
        }
        return ">1TB"
    }

    /**
     * 获取QQ微信文件
     * @param type              文件类型
     * @param filePathArray     路径
     * @param filterArray       过滤规则
     */
    fun getQWFileData(type: Int, filePathArray: MutableList<String>, filterArray: Array<String>): MutableList<ZFileBean> {
        val list = ArrayList<ZFileBean>()
        var listFiles: Array<File>? = null
        if (filePathArray.size <= 1) {
            val file = filePathArray[0].toFile()
            if (file.exists()) {
                listFiles = file.listFiles(ZFileQWFilter(filterArray, type == ZFILE_QW_OTHER))
            }
        } else {
            val file1 = filePathArray[0].toFile()
            var list1: Array<File>? = null
            if (file1.exists()) {
                list1 = file1.listFiles(ZFileQWFilter(filterArray, type == ZFILE_QW_OTHER))
            } /*else {
                ZFileLog.e("路径 ${filePathArray[0]} 不存在")
            }*/
            var list2: Array<File>? = null
            val file2 = filePathArray[1].toFile()
            if (file2.exists()) {
                list2 = file2.listFiles(ZFileQWFilter(filterArray, type == ZFILE_QW_OTHER))
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
                        getFileSize(it.length()),
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

    fun resetAll() {
        getZFileConfig().apply {
            filePath = null
            /*resources = ZFileConfiguration.ZFileResources()
            showHiddenFile = false
            sortordBy = ZFileConfiguration.BY_DEFAULT
            sortord = ZFileConfiguration.ASC
            fileFilterArray = null
            maxSize = 10
            maxSizeStr = "您只能选取小于${maxSize}M的文件"
            maxLength = 9
            maxLengthStr = "您最多可以选取${maxLength}个文件"
            boxStyle = ZFileConfiguration.STYLE2
            needLongClick = true
            isOnlyFileHasLongClick = true
            longClickOperateTitles = null
            isOnlyFolder = false
            isOnlyFile = false*/
        }
    }

}