package com.zp.z_file.listener

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.zp.z_file.R
import com.zp.z_file.common.ZFileCommonDialog
import com.zp.z_file.common.ZFileType
import com.zp.z_file.content.*
import com.zp.z_file.type.*
import com.zp.z_file.ui.ZFilePicActivity
import com.zp.z_file.ui.ZFileVideoPlayActivity
import com.zp.z_file.ui.dialog.ZFileAudioPlayDialog
import com.zp.z_file.ui.dialog.ZFileInfoDialog
import com.zp.z_file.ui.dialog.ZFileRenameDialog
import com.zp.z_file.ui.dialog.ZFileSelectFolderDialog
import com.zp.z_file.util.ZFileLog
import com.zp.z_file.util.ZFileOpenUtil
import com.zp.z_file.util.ZFileUtil
import java.io.File
import java.util.*

/**
 * 获取文件数据
 */
interface ZFileLoadListener {

    /**
     * 获取手机里的文件List
     * @param filePath String           指定的文件目录访问，空为SD卡根目录
     * @return MutableList<ZFileBean>?  list
     */
    fun getFileList(context: Context?, filePath: String?): MutableList<ZFileBean>?
}

/**
 * 文件选择
 */
interface ZFileSelectListener {
    fun onSelected(fileList: MutableList<ZFileBean>?)
}

/**
 * 图片加载
 */
abstract class ZFileImageListener {
    abstract fun loadImage(imageView: ImageView, file: File)
}

/**
 * 文件类型
 */
open class ZFileTypeListener {

    open fun getFileType(filePath: String): ZFileType {
        return when (filePath.getFileType().toLowerCase(Locale.CHINA)) {
            PNG, JPG, JPEG, GIF -> ImageType()
            MP3, AAC, WAV -> AudioType()
            MP4, _3GP -> VideoType()
            TXT, XML, JSON -> TxtType()
            ZIP -> ZipType()
            DOC -> WordType()
            XLS -> XlsType()
            PPT -> PptType()
            PDF -> PdfType()
            else -> OtherType()
        }
    }
}

/**
 * 打开文件
 */
open class ZFileOpenListener {

    open fun openAudio(filePath: String, view: View) {
        (view.context as? AppCompatActivity)?.apply {
            val tag = "ZFileAudioPlayDialog"
            checkFragmentByTag(tag)
            ZFileAudioPlayDialog.getInstance(filePath).show(supportFragmentManager, tag)
        }
    }

    open fun openImage(filePath: String, view: View) {
        view.context.startActivity(Intent(view.context, ZFilePicActivity::class.java).apply {
            putExtra("picFilePath", filePath)
        }, ActivityOptions.makeSceneTransitionAnimation(view.context as Activity, view,
            view.context.getStringById(R.string.zfile_sharedElement_pic)).toBundle())
    }

    open fun openVideo(filePath: String, view: View) {
        view.context.startActivity(
            Intent(view.context, ZFileVideoPlayActivity::class.java).apply {
                putExtra("videoFilePath", filePath)
            }, ActivityOptions.makeSceneTransitionAnimation(view.context as Activity, view,
                view.context.getStringById(R.string.zfile_sharedElement_video)).toBundle())
    }

    open fun openTXT(filePath: String, view: View) {
        ZFileOpenUtil.openTXT(filePath, view)
    }

    open fun openZIP(filePath: String, view: View) {
        AlertDialog.Builder(view.context).apply {
            setTitle("请选择")
            setItems(arrayOf("打开", "解压")) { dialog, which ->
                if (which == 0) {
                    ZFileOpenUtil.openZIP(filePath, view)
                } else {
                    zipSelect(filePath, view)
                }
                dialog.dismiss()
            }
            setPositiveButton("取消") { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

    private fun zipSelect(filePath: String, view: View) {
        (view.context as? AppCompatActivity)?.let {
            it.checkFragmentByTag("ZFileSelectFolderDialog")
            ZFileSelectFolderDialog.newInstance("解压").apply {
                selectFolder = {
                    getZFileHelp().getFileOperateListener().zipFile(filePath, this, it) {
                        if (this) {
                            ZFileLog.i("解压成功")
                        } else {
                            ZFileLog.e("解压失败")
                        }
                        ZFileLiveData.getInstance().value = this
                    }
                }
            }.show(it.supportFragmentManager, "ZFileSelectFolderDialog")
        }
    }

    open fun openDOC(filePath: String, view: View) {
        ZFileOpenUtil.openDOC(filePath, view)
    }

    open fun openXLS(filePath: String, view: View) {
        ZFileOpenUtil.openXLS(filePath, view)
    }

    open fun openPPT(filePath: String, view: View) {
        ZFileOpenUtil.openPPT(filePath, view)
    }

    open fun openPDF(filePath: String, view: View) {
        ZFileOpenUtil.openPDF(filePath, view)
    }

    open fun openOther(filePath: String, view: View) {
        ZFileLog.e("【${filePath.getFileType()}】不支持预览该文件 ---> $filePath")
        view.toast("暂不支持预览该文件")
    }
}

/**
 * 文件操作
 */
open class ZFileOperateListener {

    /**
     * 文件重命名
     * @param filePath String   文件路径
     * @param context Context   Context
     * @param block Function2<Boolean, String, Unit> Boolean：成功或失败；String：新名字
     */
    open fun renameFile(
        filePath: String,
        context: Context,
        block: (Boolean, String) -> Unit
    ) {
        (context as? AppCompatActivity)?.let {
            it.checkFragmentByTag("ZFileRenameDialog")
            ZFileRenameDialog().apply {
                reanameDown = {
                    ZFileUtil.renameFile(filePath, this, context, block)
                }
            }.show(it.supportFragmentManager, "ZFileRenameDialog")
        }
    }

    /**
     * 复制文件
     * @param sourceFile String     源文件地址
     * @param targetFile String     目标文件地址
     * @param context Context       Context
     */
    open fun copyFile(
        sourceFile: String,
        targetFile: String,
        context: Context,
        block: Boolean.() -> Unit
    ) = ZFileUtil.copyFile(sourceFile, targetFile, context, block)

    /**
     * 移动文件
     * @param sourceFile String     源文件地址
     * @param targetFile String     目标文件地址
     * @param context Context       Context
     */
    open fun moveFile(
        sourceFile: String,
        targetFile: String,
        context: Context,
        block: Boolean.() -> Unit
    ) = ZFileUtil.cutFile(sourceFile, targetFile, context, block)

    /**
     * 删除文件
     * @param filePath String   源文件地址
     */
    open fun deleteFile(filePath: String, context: Context, block: Boolean.() -> Unit) {
        ZFileCommonDialog(context).showDialog2({
            ZFileUtil.deleteFile(filePath, context, block)
        }, {}, "您确定要删除吗？", "删除", "取消")
    }

    /**
     * 解压文件
     * 请注意，文件解压目前只支持压缩包里面只有一个文件的情况，多个暂不支持，如有需要，请自己实现
     * @param sourceFile String     源文件地址
     * @param targetFile String     目标文件地址
     */
    open fun zipFile(
        sourceFile: String,
        targetFile: String,
        context: Context,
        block: Boolean.() -> Unit
    ) {
        ZFileUtil.zipFile(sourceFile, targetFile, context, block)
    }

    /**
     * 文件详情
     */
    open fun fileInfo(bean: ZFileBean, context: Context) {
        val tag = ZFileInfoDialog::class.java.simpleName
        (context as? AppCompatActivity)?.apply {
            checkFragmentByTag(tag)
            ZFileInfoDialog.newInstance(bean).show(supportFragmentManager, tag)
        }

    }
}
