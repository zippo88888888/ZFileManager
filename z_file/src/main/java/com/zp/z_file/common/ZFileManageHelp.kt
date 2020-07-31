package com.zp.z_file.common

import android.app.Activity
import android.content.Context
import androidx.collection.ArrayMap
import androidx.fragment.app.Fragment
import com.zp.z_file.content.*
import com.zp.z_file.listener.*
import com.zp.z_file.listener.ZFileDefaultLoadListener
import com.zp.z_file.ui.ZFileListActivity
import com.zp.z_file.util.ZFileLog

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
    fun setFileOperateListener(fileOperateListener: ZFileOperateListener): ZFileManageHelp {
        this.fileOperateListener = fileOperateListener
        return this
    }

    /**
     * 打开文件
     */
    private var fileOpenListener = ZFileOpenListener()
    fun getFileOpenListener() = fileOpenListener
    fun setFileOpenListener(fileOpenListener: ZFileOpenListener): ZFileManageHelp {
        this.fileOpenListener = fileOpenListener
        return this
    }

    /**
     * 文件的相关配置信息
     */
    private var config = ZFileConfiguration()
    fun getConfiguration() = config
    fun setConfiguration(config: ZFileConfiguration): ZFileManageHelp {
        this.config = config
        return this
    }

    /**
     * 跳转至文件管理页面
     * @param path 指定的文件路径
     */
    @JvmOverloads
    fun start(fragmentOrActivity: Any, path: String? = null) {
        val newPath = if (path.isNullOrEmpty()) SD_ROOT else path
        if (!newPath.toFile().exists()) {
            throw NullPointerException("$newPath 路径不存在")
        }
        when (fragmentOrActivity) {
            is Activity -> fragmentOrActivity.jumpActivity(ZFileListActivity::class.java,
                if (path == null) null else ArrayMap<String, Any>().apply { put("zFileStartPath", path) })
            is Fragment -> fragmentOrActivity.jumpActivity(ZFileListActivity::class.java,
                if (path == null) null else ArrayMap<String, Any>().apply { put("zFileStartPath", path) })
            else -> throw IllegalArgumentException("fragmentOrActivity is not Activity or Fragment")
        }
    }

}