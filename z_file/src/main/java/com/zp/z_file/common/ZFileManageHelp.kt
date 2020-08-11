package com.zp.z_file.common

import android.app.Activity
import android.content.Intent
import androidx.collection.ArrayMap
import androidx.fragment.app.Fragment
import com.zp.z_file.content.*
import com.zp.z_file.listener.*
import com.zp.z_file.listener.ZFileDefaultLoadListener
import com.zp.z_file.ui.ZFileListActivity
import com.zp.z_file.ui.ZFileQWActivity
import java.lang.StringBuilder

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
     * 文件选取，请使用onActivityResult
     */
    private var fileResultListener: ZFileSelectListener? = null
    @Deprecated("即将失效")
    fun getFileResultListener() = fileResultListener
    @Deprecated("即将失效")
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
     * 获取返回的数据
     */
    fun getSelectData(requestCode: Int, resultCode: Int, data: Intent?): MutableList<ZFileBean>? {
        var list: MutableList<ZFileBean>? = ArrayList()
        if (requestCode == ZFILE_REQUEST_CODE && resultCode == ZFILE_RESULT_CODE) {
            list = data?.getParcelableArrayListExtra<ZFileBean>(ZFILE_SELECT_DATA)
        }
        return list
    }

    /**
     * 跳转至文件管理页面
     */
    fun start(fragmentOrActivity: Any) {
        when (getConfiguration().filePath) {
            ZFileConfiguration.QQ -> startByQQ(fragmentOrActivity)
            ZFileConfiguration.WECHAT -> startByWechat(fragmentOrActivity)
            else -> startByFileManager(fragmentOrActivity, getConfiguration().filePath)
        }
    }

    private fun startByQQ(fragmentOrActivity: Any) {
        when (fragmentOrActivity) {
            is Activity -> fragmentOrActivity.jumpActivity(ZFileQWActivity::class.java,
                ArrayMap<String, Any>().apply { put("fileType", ZFileConfiguration.QQ) })
            is Fragment -> fragmentOrActivity.jumpActivity(ZFileQWActivity::class.java,
                ArrayMap<String, Any>().apply { put("fileType", ZFileConfiguration.QQ) })
            else -> throw IllegalArgumentException("fragmentOrActivity is not Activity or Fragment")
        }
    }

    private fun startByWechat(fragmentOrActivity: Any) {
        when (fragmentOrActivity) {
            is Activity -> fragmentOrActivity.jumpActivity(ZFileQWActivity::class.java,
                ArrayMap<String, Any>().apply { put("fileType", ZFileConfiguration.WECHAT) })
            is Fragment -> fragmentOrActivity.jumpActivity(ZFileQWActivity::class.java,
                ArrayMap<String, Any>().apply { put("fileType", ZFileConfiguration.WECHAT) })
            else -> throw IllegalArgumentException("fragmentOrActivity is not Activity or Fragment")
        }
    }

    private fun startByFileManager(fragmentOrActivity: Any, path: String? = null) {
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