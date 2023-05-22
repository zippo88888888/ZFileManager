package com.zp.z_file.ui.dialog

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zp.z_file.R
import com.zp.z_file.common.ZFileManageDialog
import com.zp.z_file.common.ZFileType
import com.zp.z_file.common.ZFileTypeManage
import com.zp.z_file.content.ZFileBean
import com.zp.z_file.content.ZFileInfoBean
import com.zp.z_file.content.setNeedWH
import com.zp.z_file.content.toast
import com.zp.z_file.databinding.DialogZfileInfoBinding
import com.zp.z_file.type.ZFileAudioType
import com.zp.z_file.type.ZFileImageType
import com.zp.z_file.type.ZFileVideoType
import com.zp.z_file.util.ZFileOtherUtil
import java.lang.ref.WeakReference

internal class ZFileInfoDialog : ZFileManageDialog(), Runnable {

    companion object {
        fun newInstance(bean: ZFileBean) = ZFileInfoDialog().apply {
            arguments = Bundle().apply { putParcelable("fileBean", bean) }
        }
    }

    private var vb: DialogZfileInfoBinding? = null

    private var handler: InfoHandler? = null
    private lateinit var thread: Thread
    private var filePath = ""
    private lateinit var fileType: ZFileType

    private var whStr = ""
    private var durationStr = ""

    override fun create(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vb = DialogZfileInfoBinding.inflate(inflater, container, false)
        return vb?.root
    }

    override fun getContentView() = R.layout.dialog_zfile_info

    override fun init(savedInstanceState: Bundle?) {
        val bean = arguments?.getParcelable("fileBean") ?: ZFileBean()
        filePath = bean.filePath
        fileType = ZFileTypeManage.getTypeManager().getFileType(bean.filePath)
        handler = InfoHandler(this)
        thread = Thread(this)
        thread.start()

        vb?.zfileDialogInfoFileName?.text = bean.fileName
        vb?.zfileDialogInfoFileType?.text = bean.filePath.run {
            substring(lastIndexOf(".") + 1, length)
        }
        vb?.zfileDialogInfoFileDate?.text = bean.date
        vb?.zfileDialogInfoFileSize?.text = bean.size
        vb?.zfileDialogInfoFilePath?.text = bean.filePath

        vb?.zfileDialogInfoMoreBox?.setOnClickListener {
            vb?.zfileDialogInfoMoreLayout?.visibility = if (vb?.zfileDialogInfoMoreBox?.isChecked == true) View.VISIBLE
            else View.GONE
        }
        when (fileType) {
            is ZFileImageType -> {
                vb?.zfileDialogInfoMoreBox?.visibility = View.VISIBLE
                vb?.zfileDialogInfoFileDurationLayout?.visibility = View.GONE
                vb?.zfileDialogInfoFileOther?.text = "无"
                val wh = ZFileOtherUtil.getImageWH(filePath)
                whStr = String.format("%d * %d", wh[0], wh[1])
                vb?.zfileDialogInfoFileFBL?.text = whStr
            }
            is ZFileAudioType -> {
                vb?.zfileDialogInfoMoreBox?.visibility = View.VISIBLE
                vb?.zfileDialogInfoFileFBLLayout?.visibility = View.GONE
                vb?.zfileDialogInfoFileOther?.text = "无"
            }
            is ZFileVideoType -> {
                vb?.zfileDialogInfoMoreBox?.visibility = View.VISIBLE
                vb?.zfileDialogInfoFileOther?.text = "无"
            }
            else -> {
                vb?.zfileDialogInfoMoreBox?.visibility = View.GONE
                vb?.zfileDialogInfoMoreLayout?.visibility = View.GONE
            }
        }
        vb?.zfileDialogInfoDown?.setOnClickListener { dismiss() }
        vb?.zfileDialogInfoCopy?.setOnClickListener {
            copyData(bean)
            dismiss()
        }
    }

    private fun copyData(bean: ZFileBean) {
        val sb = StringBuilder()
        sb.append(getString(R.string.zfile_info_name)).append(bean.fileName).append("\n")
        val type = bean.filePath.run {
            substring(lastIndexOf(".") + 1, length)
        }
        sb.append(getString(R.string.zfile_info_type)).append(type).append("\n")
        sb.append(getString(R.string.zfile_info_size)).append(bean.size).append("\n")
        sb.append(getString(R.string.zfile_info_time2)).append(bean.date).append("\n")
        sb.append(getString(R.string.zfile_info_path)).append(bean.filePath).append("\n")
        if (vb?.zfileDialogInfoMoreBox?.isChecked == true) {
            when (fileType) {
                is ZFileImageType -> {
                    sb.append(getString(R.string.zfile_info_wh)).append(whStr).append("\n")
                }
                is ZFileAudioType -> {
                    sb.append(getString(R.string.zfile_info_duration)).append(durationStr).append("\n")
                }
                is ZFileVideoType -> {
                    sb.append(getString(R.string.zfile_info_duration)).append(durationStr).append("\n")
                    sb.append(getString(R.string.zfile_info_wh)).append(whStr).append("\n")
                }
            }
            sb.append(getString(R.string.zfile_info_other)).append("无").append("\n")
        }
        val copyStr = sb.toString()
        val cmb = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cmb.setPrimaryClip(ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_PLAIN, copyStr))
        requireContext().toast("复制成功")
    }

    override fun onStart() {
        super.onStart()
        setNeedWH()
    }

    override fun onDestroyView() {
        vb = null
        super.onDestroyView()
        handler?.removeMessages(0)
        handler?.removeCallbacks(this)
        handler?.removeCallbacksAndMessages(null)
        handler?.clear()
        handler = null
    }

    override fun run() {
        if (fileType !is ZFileAudioType && fileType !is ZFileVideoType) return
        handler?.sendMessage(Message().apply {
            what = 0
            obj = ZFileOtherUtil.getMultimediaInfo(filePath, fileType is ZFileVideoType)
        })
    }

    class InfoHandler(dialog: ZFileInfoDialog) : Handler(Looper.myLooper()!!) {
        private val week: WeakReference<ZFileInfoDialog> by lazy {
            WeakReference<ZFileInfoDialog>(dialog)
        }

        fun clear() {
            week.clear()
        }

        override fun handleMessage(msg: Message) {
            if (msg.what == 0) {
                val bean = msg.obj as ZFileInfoBean
                week.get()?.apply {
                    when (fileType) {
                        is ZFileAudioType -> {
                            durationStr = bean.duration
                            vb?.zfileDialogInfoFileDuration?.text = durationStr
                        }
                        is ZFileVideoType -> {
                            durationStr = bean.duration
                            whStr = String.format("%s * %s", bean.width, bean.height)
                            vb?.zfileDialogInfoFileDuration?.text = durationStr
                            vb?.zfileDialogInfoFileFBL?.text = whStr
                        }
                    }
                }
            }
        }
    }
}