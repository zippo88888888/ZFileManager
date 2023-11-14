package com.zp.z_file.async

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.documentfile.provider.DocumentFile
import com.zp.z_file.content.ZFileBean
import com.zp.z_file.content.async
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference

/**
 * 更方便的去获取符合要求的数据
 */
open class ZFileAsync(
    private var context: Context,
    private var block: MutableList<ZFileBean>?.() -> Unit
) {

    private val LIST: Int
        get() = 21

    private val OTHER: Int
        get() = 20

    private val SAF: Int
        get() = 22

    private var handler: ZFileAsyncHandler? = null

    private val softReference by lazy {
        SoftReference<Context>(context)
    }

    /**
     * 获取数据
     * @param filterArray 过滤规则
     */
    fun start(filterArray: Array<String>) {
        doStart()
        async {
            sendMessage(OTHER, doingWork(filterArray))
        }
    }

    protected fun getContext(): Context? = softReference.get()

    /**
     * 执行前调用 mainThread
     */
    protected open fun onPreExecute() = Unit

    /**
     * 获取数据
     * @param filterArray  过滤规则
     */
    protected open fun doingWork(filterArray: Array<String>): MutableList<ZFileBean>? {
        return null
    }

    private fun onPostExecute(list: MutableList<ZFileBean>?) {
        destory()
        block.invoke(list)
        onPostExecute()
    }

    /**
     * 完成后调用 mainThread
     */
    protected open fun onPostExecute() = Unit

    private class ZFileAsyncHandler(zFileAsync: ZFileAsync) : Handler(Looper.myLooper()!!) {

        private val weakReference by lazy {
            WeakReference<ZFileAsync>(zFileAsync)
        }

        @Suppress("UNCHECKED_CAST")
        override fun handleMessage(msg: Message) {
            val list = msg.obj as? MutableList<ZFileBean>
            /*when (msg.what) {
                weakReference.get()?.OTHER -> {}
                weakReference.get()?.LIST -> {}
            }*/
            weakReference.get()?.onPostExecute(list)
        }
    }

    private fun doStart() {
        if (handler == null) {
            handler = ZFileAsyncHandler(this)
        }
        onPreExecute()
    }

    private fun sendMessage(messageWhat: Int, messageObj: Any?) {
        handler?.sendMessage(Message.obtain().apply {
            what = messageWhat
            obj = messageObj
        })
    }

    // =============================================================================================

    internal fun start(filePath: String?) {
        doStart()
        async {
            sendMessage(LIST, doingWork(filePath))
        }
    }

    internal open fun doingWork(filePath: String?): MutableList<ZFileBean>? {
        return null
    }

    /** 开始 SAF 操作 */
    internal fun startSAF(documentFiles: Array<DocumentFile>?) {
        doStart()
        async {
            sendMessage(SAF, doingWorkForSAF(documentFiles))
        }
    }

    /** 执行 SAF 具体数据操作 */
    internal open fun doingWorkForSAF(documentFiles: Array<DocumentFile>?): MutableList<ZFileBean>? {
        return null
    }

    private fun destory() {
        handler?.removeMessages(LIST)
        handler?.removeMessages(OTHER)
        handler?.removeMessages(SAF)
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }

}