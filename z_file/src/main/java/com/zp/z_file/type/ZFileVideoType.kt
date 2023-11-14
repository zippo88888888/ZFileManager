package com.zp.z_file.type

import android.view.View
import android.widget.ImageView
import com.zp.z_file.common.ZFileType
import com.zp.z_file.content.getZFileHelp
import com.zp.z_file.content.toFile
import com.zp.z_file.ui.ZFileVideoPlayer

/**
 * 视频文件
 */
open class ZFileVideoType : ZFileType() {

    /** 中心裁剪模式 */
    protected val CENTER_CROP_MODE = ZFileVideoPlayer.CENTER_CROP_MODE
    /** 中心填充模式 */
    protected val CENTER_MODE = ZFileVideoPlayer.CENTER_MODE

    override fun openFile(filePath: String, view: View) {
        getZFileHelp().getFileOpenListener().openVideo(filePath, view)
    }

    override fun loadingFile(filePath: String, pic: ImageView) {
        getZFileHelp().getImageLoadListener().loadVideo(pic, filePath.toFile())
    }

    /**
     * 视频查看页面 获取 播放器画面 展示方式
     */
    open fun getVideoSizeType(): Int {
        return CENTER_MODE
    }

    /**
     * 视频查看页面 获取 视频 预览图 展示方式
     */
    open fun getImageScaleType(): ImageView.ScaleType {
        return ImageView.ScaleType.FIT_CENTER
    }
}