package com.zp.z_file.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Gravity
import android.view.View
import com.zp.z_file.R
import com.zp.z_file.common.ZFileManageDialog
import com.zp.z_file.common.ZFileType
import com.zp.z_file.common.ZFileTypeManage
import com.zp.z_file.content.ZFileBean
import com.zp.z_file.content.ZFileInfoBean
import com.zp.z_file.content.setNeedWH
import com.zp.z_file.type.AudioType
import com.zp.z_file.type.ImageType
import com.zp.z_file.type.VideoType
import com.zp.z_file.util.ZFileOtherUtil
import kotlinx.android.synthetic.main.dialog_zfile_info.*
import java.lang.ref.WeakReference

internal class ZFileInfoDialog : ZFileManageDialog(), Runnable {

    companion object {
        fun newInstance(bean: ZFileBean) = ZFileInfoDialog().apply {
            arguments = Bundle().apply { putParcelable("fileBean", bean) }
        }
    }

    private var handler: InfoHandler? = null
    private lateinit var thread: Thread
    private var filePath = ""
    private lateinit var fileType: ZFileType

    override fun getContentView() = R.layout.dialog_zfile_info

    override fun createDialog(savedInstanceState: Bundle?) = Dialog(context!!, R.style.ZFile_Common_Dialog).apply {
        window?.setGravity(Gravity.CENTER)
    }

    override fun init(savedInstanceState: Bundle?) {
        val bean = arguments?.getParcelable("fileBean") ?: ZFileBean()
        filePath = bean.filePath
        fileType = ZFileTypeManage.getTypeManager().getFileType(bean.filePath)
        handler = InfoHandler(this)
        thread = Thread(this)
        thread.start()

        zfile_dialog_info_fileName.text = bean.fileName
        zfile_dialog_info_fileType.text = bean.filePath.run {
            substring(lastIndexOf(".") + 1, length)
        }
        zfile_dialog_info_fileDate.text = bean.date
        zfile_dialog_info_fileSize.text = bean.size
        zfile_dialog_info_filePath.text = bean.filePath

        zfile_dialog_info_moreBox.setOnClickListener {
            zfile_dialog_info_moreLayout.visibility = if (zfile_dialog_info_moreBox.isChecked) View.VISIBLE
            else View.GONE
        }
        when (fileType) {
            is ImageType -> {
                zfile_dialog_info_moreBox.visibility = View.VISIBLE
                zfile_dialog_info_fileDurationLayout.visibility = View.GONE
                zfile_dialog_info_fileOther.text = "无"
                val wh = ZFileOtherUtil.getImageWH(filePath)
                zfile_dialog_info_fileFBL.text = String.format("%d * %d", wh[0], wh[1])
            }
            is AudioType -> {
                zfile_dialog_info_moreBox.visibility = View.VISIBLE
                zfile_dialog_info_fileFBLLayout.visibility = View.GONE
                zfile_dialog_info_fileOther.text = "无"
            }
            is VideoType -> {
                zfile_dialog_info_moreBox.visibility = View.VISIBLE
                zfile_dialog_info_fileOther.text = "无"
            }
            else -> {
                zfile_dialog_info_moreBox.visibility = View.GONE
                zfile_dialog_info_moreLayout.visibility = View.GONE
            }
        }
        zfile_dialog_info_down.setOnClickListener { dismiss() }
    }

    override fun onStart() {
        super.onStart()
        setNeedWH()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler?.removeMessages(0)
        handler?.removeCallbacks(this)
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }

    override fun run() {
        if (fileType !is AudioType && fileType !is VideoType) return
        handler?.sendMessage(Message().apply {
            what = 0
            obj = ZFileOtherUtil.getMultimediaInfo(filePath, fileType is VideoType)
        })
    }

    class InfoHandler(dialog: ZFileInfoDialog) : Handler() {
        private val week: WeakReference<ZFileInfoDialog> by lazy {
            WeakReference<ZFileInfoDialog>(dialog)
        }

        override fun handleMessage(msg: Message) {
            if (msg.what == 0) {
                val bean = msg.obj as ZFileInfoBean
                week.get()?.apply {
                    when (fileType) {
                        is AudioType -> {
                            zfile_dialog_info_fileDuration.text = bean.duration
                        }
                        is VideoType -> {
                            zfile_dialog_info_fileDuration.text = bean.duration
                            zfile_dialog_info_fileFBL.text =
                                String.format("%s * %s", bean.width, bean.height)
                        }
                    }
                }
            }
        }
    }
}