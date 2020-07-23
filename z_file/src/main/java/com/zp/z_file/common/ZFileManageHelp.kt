package com.zp.z_file.common

import android.content.Context
import androidx.collection.ArrayMap
import com.zp.z_file.content.*
import com.zp.z_file.listener.*
import com.zp.z_file.listener.ZFileDefaultLoadListener
import com.zp.z_file.ui.ZFileListActivity

class ZFileManageHelp {

    private object Builder {
        val MANAGER = ZFileManageHelp()
    }

    companion object {
        @JvmStatic
        fun getInstance() = Builder.MANAGER
    }

    /**
     * 图片的加载方式，必须手动实现
     */
    private var imageLoadeListener: ZFileImageListener? = null
    fun getImageLoadListener() = imageLoadeListener
    fun init(imageLoadeListener: ZFileImageListener) : ZFileManageHelp {
        this.imageLoadeListener = imageLoadeListener
        return this
    }

    /**
     * 文件选取
     */
    private var fileResultListener: ZFileSelectListener? = null
    fun getFileResultListener() = fileResultListener
    fun setFileResultListener(fileResultListener: ZFileSelectListener?): ZFileManageHelp {
        this.fileResultListener = fileResultListener
        return this
    }

    /**
     * 文件数据获取
     */
    private var fileLoadListener: ZFileLoadListener = ZFileDefaultLoadListener()
    fun getFileLoadListener() = fileLoadListener
    fun setFileLoadListener(fileLoadListener: ZFileLoadListener): ZFileManageHelp {
        this.fileLoadListener = fileLoadListener
        return this
    }

    /**
     * 文件类型
     */
    private var fileTypeListener = ZFileTypeListener()
    fun getFileTypeListener() = fileTypeListener
    fun setFileTypeListener(fileTypeListener: ZFileTypeListener): ZFileManageHelp {
        this.fileTypeListener = fileTypeListener
        return this
    }

    /**
     * 文件操作
     */
    private var fileOperateListener = ZFileOperateListener()
    fun getFileOperateListener() = fileOperateListener
    fun setFileOperateListener(fileZipListener: ZFileOperateListener): ZFileManageHelp {
        this.fileOperateListener = fileZipListener
        return this
    }

    /**
     * 打开文件
     */
    private var openListener = ZFileOpenListener()
    fun getOpenListener() = openListener
    fun setOpenListener(openListener: ZFileOpenListener): ZFileManageHelp {
        this.openListener = openListener
        return this
    }

    /**
     * 文件的相关配置信息
     */
    private var config = ZFileConfiguration()
    fun getZfileConfig() = config
    fun setConfiguration(config: ZFileConfiguration): ZFileManageHelp {
        this.config = config
        return this
    }

    /**
     * 跳转至文件管理页面
     * @param path 指定的文件路径
     */
    @JvmOverloads
    fun start(context: Context, path: String? = null) {
        context.jumpActivity(ZFileListActivity::class.java,
            if (path == null) null else ArrayMap<String, Any>().apply { put("zFileStartPath", path) })
    }

}