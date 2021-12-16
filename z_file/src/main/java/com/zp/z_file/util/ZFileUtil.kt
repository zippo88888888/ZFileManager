package com.zp.z_file.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import com.zp.z_file.R
import com.zp.z_file.async.ZFileListAsync
import com.zp.z_file.common.ZFileTypeManage
import com.zp.z_file.content.*
import com.zp.z_file.ui.dialog.ZFileLoadingDialog
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.text.DecimalFormat
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.concurrent.thread

internal object ZFileUtil {

    /**
     * 获取文件
     */
    fun getList(context: Context, block: MutableList<ZFileBean>?.() -> Unit) {
        ZFileListAsync(context, block).start(getZFileConfig().filePath)
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
            setCanceledOnTouchOutside(false)
            show()
            this
        }
        thread {
            val isSuccess = when (type) {
                COPY_TYPE -> copyFile(filePath, outPath, context)
                CUT_TYPE -> cutFile(filePath, outPath, context)
                DELTE_TYPE -> File(filePath).delete()
                else -> extractFile(filePath, outPath, context)
            }
            activity.runOnUiThread {
                dialog.dismiss()
                activity.toast(if (isSuccess) "${msg}成功" else "${msg}失败或已存在相同文件")
                block.invoke(isSuccess)
            }
        }
    }


    /**
     * 复制文件
     */
    private fun copyFile(sourceFile: String, targetFile: String, context: Context): Boolean {
        var success = true
        val oldFile = File(sourceFile)
        var outFile = File("${targetFile}/${oldFile.name}")
        if (oldFile.path == outFile.path) {
            ZFileLog.e("目录相同，不做任何处理！")
            return false
        }
        if (getZFileConfig().keepDuplicate) {
            val duplicateStr = context getStringById R.string.zfile_duplicate
            while (true) {
                if (outFile.exists()) {
                    ZFileLog.i("文件已存在 ---> 重命名为副本")
                    outFile = File("${targetFile}/${duplicateStr}${outFile.name}")
                } else {
                    break
                }
            }
        }
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
    private fun cutFile(sourceFile: String, targetFile: String, context: Context): Boolean {
        val copySuccess = copyFile(sourceFile, targetFile, context)
        var delSuccess = false
        try {
            if (copySuccess) {
                delSuccess = File(sourceFile).delete()
            }
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
    private fun extractFile(zipFileName: String, outPutDir: String, context: Context): Boolean {
        var zipInputStream: ZipInputStream? = null
        val zipEntry: ZipEntry
        var outputStream: FileOutputStream? = null
        var name: String
        try {
            // 中文乱码 https://stackoverflow.com/questions/20013091/java-unzip-error-malformed
            zipInputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ZipInputStream(FileInputStream(zipFileName), Charset.forName("GBK"))
            } else {
                ZipInputStream(FileInputStream(zipFileName))
            }
            zipEntry = zipInputStream.nextEntry
            while (zipEntry != null) {
                name = zipEntry.name
                if (zipEntry.isDirectory) {
                    name = name.substring(0, name.length - 1)
                    val file = File(outPutDir + File.separator + name)
                    file.mkdirs()
                } else {
                    var file = File(outPutDir + File.separator + name)

                    if (getZFileConfig().keepDuplicate) {
                        val duplicateStr = context getStringById R.string.zfile_duplicate
                        while (true) {
                            if (file.exists()) {
                                ZFileLog.i("文件已存在 ---> 重命名为副本")
                                file = File(outPutDir + File.separator + "${duplicateStr}${file.name}")
                            } else {
                                break
                            }
                        }
                    }

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
        } finally {
            outputStream?.close()
            try {
                zipInputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return true
        }
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