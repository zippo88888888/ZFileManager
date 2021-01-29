package com.zp.z_file.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.collection.ArrayMap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.zp.z_file.content.*
import com.zp.z_file.dsl.ZFileDsl
import com.zp.z_file.listener.*
import com.zp.z_file.ui.ZFileListActivity
import com.zp.z_file.ui.ZFileProxyFragment
import com.zp.z_file.ui.ZFileQWActivity

class ZFileManageHelp {

    private object Builder {
        val MANAGER = ZFileManageHelp()
    }

    companion object {
        @JvmStatic
        fun getInstance() = Builder.MANAGER
    }

    /**
     * 图片类型和视频类型的显示方式，必须手动实现 并在调用前或Application中初始化
     */
    private var imageLoadeListener: ZFileImageListener? = null
    internal fun getImageLoadListener(): ZFileImageListener {
        if (imageLoadeListener == null) {
            throw NullPointerException("ZFileImageListener is Null, You need call method \"init()\"")
        }
        return imageLoadeListener!!
    }

    fun init(imageLoadeListener: ZFileImageListener): ZFileManageHelp {
        this.imageLoadeListener = imageLoadeListener
        return this
    }

    /**
     * 文件数据获取
     */
    private var fileLoadListener: ZFileLoadListener = ZFileDefaultLoadListener()
    internal fun getFileLoadListener() = fileLoadListener
    fun setFileLoadListener(fileLoadListener: ZFileLoadListener): ZFileManageHelp {
        this.fileLoadListener = fileLoadListener
        return this
    }

    /**
     * QQ or WeChat 文件获取
     */
    private var qwLoadListener: ZQWFileLoadListener? = null
    internal fun getQWFileLoadListener() = qwLoadListener
    fun setQWFileLoadListener(qwLoadListener: ZQWFileLoadListener?): ZFileManageHelp {
        this.qwLoadListener = qwLoadListener
        return this
    }

    /**
     * 文件类型
     */
    private var fileTypeListener = ZFileTypeListener()
    internal fun getFileTypeListener() = fileTypeListener
    fun setFileTypeListener(fileTypeListener: ZFileTypeListener): ZFileManageHelp {
        this.fileTypeListener = fileTypeListener
        return this
    }

    /**
     * 文件操作
     */
    private var fileOperateListener = ZFileOperateListener()
    internal fun getFileOperateListener() = fileOperateListener
    fun setFileOperateListener(fileOperateListener: ZFileOperateListener): ZFileManageHelp {
        this.fileOperateListener = fileOperateListener
        return this
    }

    /**
     * 打开默认支持的文件
     */
    private var fileOpenListener = ZFileOpenListener()
    internal fun getFileOpenListener() = fileOpenListener
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
            list = data?.getParcelableArrayListExtra<ZFileBean>(ZFILE_SELECT_DATA_KEY)
        }
        return list
    }

    /**
     * 重置所有配置信息
     */
    @JvmOverloads
    fun resetAll(imageLoadReset: Boolean = false) {
        if (imageLoadReset) imageLoadeListener = null
        fileLoadListener = ZFileDefaultLoadListener()
        qwLoadListener = null
        fileTypeListener = ZFileTypeListener()
        fileOperateListener = ZFileOperateListener()
        fileOpenListener = ZFileOpenListener()
        config = ZFileConfiguration()
    }

    /**
     * 跳转至文件管理页面
     */
    fun start(fragmentOrActivity: Any, resultListener: ZFileSelectResultListener) {
        when (getConfiguration().filePath) {
            ZFileConfiguration.QQ -> startByQQ(fragmentOrActivity, resultListener)
            ZFileConfiguration.WECHAT -> startByWechat(fragmentOrActivity, resultListener)
            else -> startByFileManager(fragmentOrActivity, getConfiguration().filePath, resultListener)
        }
    }

    private fun startByQQ(fragmentOrActivity: Any, resultListener: ZFileSelectResultListener) {
        when (fragmentOrActivity) {
            is FragmentActivity -> {
                if (fragmentOrActivity.isDestroyed || fragmentOrActivity.isFinishing) return
                addFragment(fragmentOrActivity.supportFragmentManager, fragmentOrActivity,
                        ZFileQWActivity::class.java, ZFileConfiguration.QQ, resultListener)
            }
            is Fragment -> {
                if (fragmentOrActivity.isRemoving || fragmentOrActivity.isDetached) return
                addFragment(fragmentOrActivity.childFragmentManager, fragmentOrActivity.context!!,
                        ZFileQWActivity::class.java, ZFileConfiguration.QQ, resultListener)
            }
            else -> throw IllegalArgumentException(ERROR_MSG)
        }
    }

    private fun startByWechat(fragmentOrActivity: Any, resultListener: ZFileSelectResultListener) {
        when (fragmentOrActivity) {
            is FragmentActivity -> {
                if (fragmentOrActivity.isDestroyed || fragmentOrActivity.isFinishing) return
                addFragment(fragmentOrActivity.supportFragmentManager, fragmentOrActivity,
                        ZFileQWActivity::class.java, ZFileConfiguration.WECHAT, resultListener)
            }
            is Fragment -> {
                if (fragmentOrActivity.isRemoving || fragmentOrActivity.isDetached) return
                addFragment(fragmentOrActivity.childFragmentManager, fragmentOrActivity.context!!,
                        ZFileQWActivity::class.java, ZFileConfiguration.WECHAT, resultListener)
            }
            else -> throw IllegalArgumentException(ERROR_MSG)
        }
    }

    private fun startByFileManager(fragmentOrActivity: Any, path: String? = null, resultListener: ZFileSelectResultListener) {
        val newPath = if (path.isNullOrEmpty()) SD_ROOT else path
        if (!newPath.toFile().exists()) {
            throw NullPointerException("$newPath 路径不存在")
        }
        when (fragmentOrActivity) {
            is FragmentActivity -> {
                if (fragmentOrActivity.isDestroyed || fragmentOrActivity.isFinishing) return
                addFragment(fragmentOrActivity.supportFragmentManager, fragmentOrActivity,
                        ZFileListActivity::class.java, path, resultListener)
            }
            is Fragment -> {
                if (fragmentOrActivity.isRemoving || fragmentOrActivity.isDetached) return
                addFragment(fragmentOrActivity.childFragmentManager, fragmentOrActivity.context!!,
                        ZFileListActivity::class.java, path, resultListener)
            }
            else -> throw IllegalArgumentException(ERROR_MSG)
        }
    }

    private fun addFragment(
            fragmentManager: FragmentManager,
            context: Context,
            clazz: Class<*>,
            path: String?,
            resultListener: ZFileSelectResultListener
    ) {
        var fragment = fragmentManager.findFragmentByTag(ZFileProxyFragment.TAG) as? ZFileProxyFragment
        if (fragment == null) {
            fragment = ZFileProxyFragment()
            fragmentManager.beginTransaction().add(fragment, ZFileProxyFragment.TAG).commitNow()
        }
        fragment.jump(ZFILE_REQUEST_CODE, Intent(context, clazz).apply {
            putExtra(FILE_START_PATH_KEY, path)
        }, resultListener)
    }

    @Deprecated("不推荐使用 Not recommended")
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
                    getMap().apply { put(QW_FILE_TYPE_KEY, ZFileConfiguration.QQ) })
            is Fragment -> fragmentOrActivity.jumpActivity(ZFileQWActivity::class.java,
                    getMap().apply { put(QW_FILE_TYPE_KEY, ZFileConfiguration.QQ) })
            else -> throw IllegalArgumentException(ERROR_MSG)
        }
    }

    private fun startByWechat(fragmentOrActivity: Any) {
        when (fragmentOrActivity) {
            is Activity -> fragmentOrActivity.jumpActivity(ZFileQWActivity::class.java,
                    getMap().apply { put(QW_FILE_TYPE_KEY, ZFileConfiguration.WECHAT) })
            is Fragment -> fragmentOrActivity.jumpActivity(ZFileQWActivity::class.java,
                    getMap().apply { put(QW_FILE_TYPE_KEY, ZFileConfiguration.WECHAT) })
            else -> throw IllegalArgumentException(ERROR_MSG)
        }
    }

    private fun startByFileManager(fragmentOrActivity: Any, path: String? = null) {
        val newPath = if (path.isNullOrEmpty()) SD_ROOT else path
        if (!newPath.toFile().exists()) {
            throw NullPointerException("$newPath 路径不存在")
        }
        when (fragmentOrActivity) {
            is Activity -> fragmentOrActivity.jumpActivity(ZFileListActivity::class.java,
                    if (path == null) null else getMap().apply { put(FILE_START_PATH_KEY, path) })
            is Fragment -> fragmentOrActivity.jumpActivity(ZFileListActivity::class.java,
                    if (path == null) null else getMap().apply { put(FILE_START_PATH_KEY, path) })
            else -> throw IllegalArgumentException(ERROR_MSG)
        }
    }

    private fun getMap() = ArrayMap<String, Any>()

}