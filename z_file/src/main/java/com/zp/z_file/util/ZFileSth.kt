package com.zp.z_file.util

import android.app.Activity
import android.content.Context
import android.os.Build
import com.zp.z_file.R
import com.zp.z_file.content.*
import com.zp.z_file.content.COPY_TYPE
import com.zp.z_file.content.CUT_TYPE
import com.zp.z_file.content.DELTE_TYPE
import com.zp.z_file.content.ZFileException
import com.zp.z_file.content.ZIP_BUFFER_SIZE
import com.zp.z_file.content.getStringById
import com.zp.z_file.content.toast
import com.zp.z_file.ui.dialog.ZFileLoadingDialog
import java.io.*
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.concurrent.thread

internal object ZFileSth {

    fun callFileByType(
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
        (context as? Activity)?.let {
            val dialog = getZFileHelp().getOtherListener()?.getLoadingDialog(it, "${msg}中...")
                ?: ZFileLoadingDialog(it, "${msg}中...").run {
                    setCanceledOnTouchOutside(false)
                    this
                }
            dialog.show()
            thread {
                val isSuccess = when (type) {
                    COPY_TYPE -> copyFile(filePath, outPath, context)
                    CUT_TYPE -> cutFile(filePath, outPath, context)
                    DELTE_TYPE -> File(filePath).delete()
                    else -> extractFile(filePath, outPath, context)
                }
                it.runOnUiThread {
                    dialog.dismiss()
                    it.toast(if (isSuccess) "${msg}成功" else "${msg}失败或已存在相同文件")
                    block.invoke(isSuccess)
                }
            }
        }
    }


    /**
     * 复制文件
     */
    private fun copyFile(sourceFile: String, targetFile: String, context: Context): Boolean {
        var success = true
        val oldFile = File(sourceFile)
        val outFile = File("${targetFile}/${oldFile.name}")
        if (oldFile.path == outFile.path) {
            ZFileLog.e("复制文件 --->>> 目录相同，不做任何处理！")
            return false
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
        val zipFile = File(zipFileName)
        val outPutDirFile = File(outPutDir)

        val maxLength = context.resources.getInteger(R.integer.zfile_zip_max_length)
        val maxSize = context.resources.getInteger(R.integer.zfile_zip_max_size)

        var success = true
        var zis: ZipInputStream? = null
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(zipFile)
            zis = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 中文乱码
                ZipInputStream(BufferedInputStream(fis), Charset.forName("GBK"))
            } else {
                ZipInputStream(BufferedInputStream(fis))
            }
            var entry: ZipEntry?
            var count = 0
            var total = 0L
            while (true) {
                entry = zis.nextEntry
                if (entry == null) {
                    break
                } else {
                    var bytesRead = 0
                    val data = ByteArray(ZIP_BUFFER_SIZE)
                    // 目标目录
                    val zipOutPutTarget = File(outPutDirFile, entry.name)
                    val zipzipOutPutPath = zipOutPutTarget.canonicalPath
                    if (zipzipOutPutPath.startsWith(outPutDirFile.canonicalPath)) {
                        if (entry.isDirectory) {
                            File(zipzipOutPutPath).mkdirs()
                        } else {
                            File(zipzipOutPutPath).parentFile?.mkdirs()
                            val fos = FileOutputStream(zipzipOutPutPath)
                            val dest = BufferedOutputStream(fos, ZIP_BUFFER_SIZE)
                            while (total + ZIP_BUFFER_SIZE <= maxSize && zis.read(
                                    data,
                                    0,
                                    ZIP_BUFFER_SIZE
                                ).also {
                                    bytesRead = it
                                } != -1
                            ) {
                                dest.write(data, 0, bytesRead)
                                total += bytesRead.toLong()
                            }
                            dest.flush()
                            fos.fd.sync()
                            dest.close()
                            if (total + ZIP_BUFFER_SIZE > maxSize) {
                                val size = maxSize / 1024 / 1024
                                ZFileLog.e("Zip压缩包中某个文件已超过最大值：${maxSize}（${size}M），可重写 【R.integer.zfile_zip_max_size】解除该限制")
                                throw ZFileException("Zip压缩包中某个文件已超过最大值：${maxSize}（${size}M），解压失败")
                            }
                        }
                        zis.closeEntry()
                        count++
                        if (count > maxLength) {
                            ZFileLog.e("Zip压缩包中数量已超过【${maxLength}】个，可重写 【R.integer.zfile_zip_max_length】解除该限制")
                            throw ZFileException("Zip压缩包中数量已超过【${maxLength}】个，解压失败")
                        }
                    } else {
                        throw ZFileException("Zip压缩包解压时不在解压目录中：${outPutDirFile.canonicalPath}")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            success = false
        } finally {
            try {
                zis?.close()
                fis?.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return success
        }
    }

}