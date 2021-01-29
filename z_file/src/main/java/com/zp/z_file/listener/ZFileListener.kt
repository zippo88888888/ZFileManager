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
import com.zp.z_file.ui.ZFileListActivity
import com.zp.z_file.ui.ZFilePicActivity
import com.zp.z_file.ui.ZFileQWActivity
import com.zp.z_file.ui.ZFileVideoPlayActivity
import com.zp.z_file.ui.dialog.ZFileAudioPlayDialog
import com.zp.z_file.ui.dialog.ZFileInfoDialog
import com.zp.z_file.ui.dialog.ZFileRenameDialog
import com.zp.z_file.ui.dialog.ZFileSelectFolderDialog
import com.zp.z_file.util.ZFileHelp
import com.zp.z_file.util.ZFileLog
import com.zp.z_file.util.ZFileOpenUtil
import com.zp.z_file.util.ZFileUtil
import java.io.File
import java.util.*

/**
 * 图片或视频 显示
 */
abstract class ZFileImageListener {

    /**
     * 图片类型加载
     */
    abstract fun loadImage(imageView: ImageView, file: File)

    /**
     * 视频类型加载
     */
    open fun loadVideo(imageView: ImageView, file: File) {
        loadImage(imageView, file)
    }
}

/**
 * 文件选取 后 的监听
 */
interface ZFileSelectResultListener {

    fun selectResult(selectList: MutableList<ZFileBean>?)

}

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
 * QQ 或 WeChat 获取
 */
abstract class ZQWFileLoadListener {

    /**
     * 获取标题
     * @return Array<String>
     */
    open fun getTitles(): Array<String>? = null

    /**
     * 获取过滤规则
     * @param fileType Int      文件类型 see [ZFILE_QW_PIC] [ZFILE_QW_MEDIA] [ZFILE_QW_DOCUMENT] [ZFILE_QW_OTHER]
     */
    abstract fun getFilterArray(fileType: Int): Array<String>

    /**
     * 获取 QQ 或 WeChat 文件路径
     * @param qwType String         QQ 或 WeChat  see [ZFileConfiguration.QQ] [ZFileConfiguration.WECHAT]
     * @param fileType Int          文件类型 see [ZFILE_QW_PIC] [ZFILE_QW_MEDIA] [ZFILE_QW_DOCUMENT] [ZFILE_QW_OTHER]
     * @return MutableList<String>  文件路径集合（因为QQ或WeChat保存的文件可能存在多个路径）
     */
    abstract fun getQWFilePathArray(qwType: String, fileType: Int): MutableList<String>

    /**
     * 获取数据
     * @param fileType Int                          文件类型 see [ZFILE_QW_PIC] [ZFILE_QW_MEDIA] [ZFILE_QW_DOCUMENT] [ZFILE_QW_OTHER]
     * @param qwFilePathArray MutableList<String>   QQ 或 WeChat 文件路径集合
     * @param filterArray Array<String>             过滤规则
     */
    abstract fun getQWFileDatas(fileType: Int, qwFilePathArray: MutableList<String>, filterArray: Array<String>): MutableList<ZFileBean>

}

/**
 * 文件类型
 */
open class ZFileTypeListener {

    open fun getFileType(filePath: String): ZFileType {
        return when (ZFileHelp.getFileTypeBySuffix(filePath)) {
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

    /**
     * 打开音频
     */
    open fun openAudio(filePath: String, view: View) {
        (view.context as? AppCompatActivity)?.apply {
            val tag = "ZFileAudioPlayDialog"
            checkFragmentByTag(tag)
            ZFileAudioPlayDialog.getInstance(filePath).show(supportFragmentManager, tag)
        }
    }

    /**
     * 打开图片
     */
    open fun openImage(filePath: String, view: View) {
        view.context.startActivity(Intent(view.context, ZFilePicActivity::class.java).apply {
            putExtra("picFilePath", filePath)
        }, ActivityOptions.makeSceneTransitionAnimation(view.context as Activity, view,
            view.context.getStringById(R.string.zfile_sharedElement_pic)).toBundle())
    }

    /**
     * 打开视频
     */
    open fun openVideo(filePath: String, view: View) {
        view.context.startActivity(
            Intent(view.context, ZFileVideoPlayActivity::class.java).apply {
                putExtra("videoFilePath", filePath)
            }, ActivityOptions.makeSceneTransitionAnimation(view.context as Activity, view,
                view.context.getStringById(R.string.zfile_sharedElement_video)).toBundle())
    }

    /**
     * 打开Txt
     */
    open fun openTXT(filePath: String, view: View) {
        ZFileOpenUtil.openTXT(filePath, view)
    }

    /**
     * 打开zip
     */
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
        val activity = view.context
        if (activity is AppCompatActivity) {
            activity.checkFragmentByTag("ZFileSelectFolderDialog")
            ZFileSelectFolderDialog.newInstance("解压").apply {
                selectFolder = {
                    getZFileHelp().getFileOperateListener().zipFile(filePath, this, activity) {
                        ZFileLog.i(if (this) "解压成功" else "解压失败")
                        (activity as? ZFileListActivity).let {
                            it?.observer(this)
                        }
                    }
                }
            }.show(activity.supportFragmentManager, "ZFileSelectFolderDialog")
        }
    }

    /**
     * 打开word
     */
    open fun openDOC(filePath: String, view: View) {
        ZFileOpenUtil.openDOC(filePath, view)
    }

    /**
     * 打开表格
     */
    open fun openXLS(filePath: String, view: View) {
        ZFileOpenUtil.openXLS(filePath, view)
    }

    /**
     * 打开PPT
     */
    open fun openPPT(filePath: String, view: View) {
        ZFileOpenUtil.openPPT(filePath, view)
    }

    /**
     * 打开PDF
     */
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
     * 文件重命名（该方式需要先弹出重命名弹窗或其他页面，再执行重命名逻辑）
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
                    renameFile(filePath, this, context, block)
                }
            }.show(it.supportFragmentManager, "ZFileRenameDialog")
        }
    }

    /**
     * 文件重命名（该方式只需要实现重命名逻辑即可）
     * @param filePath String       文件路径
     * @param fileNewName String    新名字
     * @param context Context       Context
     * @param block Function2<Boolean, String, Unit> Boolean：成功或失败；String：新名字
     */
    open fun renameFile(
        filePath: String,
        fileNewName: String,
        context: Context,
        block: (Boolean, String) -> Unit
    ) = ZFileUtil.renameFile(filePath, fileNewName, context, block)

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
