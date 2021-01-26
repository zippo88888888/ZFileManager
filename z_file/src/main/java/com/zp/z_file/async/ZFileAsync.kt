package com.zp.z_file.async

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.zp.z_file.content.ZFileBean
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference
import kotlin.concurrent.thread

/**
 * 更方便的去获取符合要求的数据
 */
open class ZFileAsync(
    private var context: Context,
    private var block: MutableList<ZFileBean>?.() -> Unit
) {

    private var handler: ZFileAsyncHandler? = null

    private val softReference by lazy {
        SoftReference<Context>(context)
    }

    fun start(filterArray: Array<String>) {
        if (handler == null) {
            handler = ZFileAsyncHandler(this)
        }
        onPreExecute()
        thread {
            val list = doingWork(filterArray)
            handler?.sendMessage(Message().apply {
                what = 20
                obj = list
            })
        }
    }

    protected fun getContext(): Context? = softReference.get()

    /**
     * 执行前调用 mainThread
     */
    protected open fun onPreExecute() = Unit

    /**
     * 获取数据
     * @param filterArray  规则
     */
    protected open fun doingWork(filterArray: Array<String>): MutableList<ZFileBean>? {
        return null
    }

    private fun onPostExecute(list: MutableList<ZFileBean>?) {
        handler?.removeMessages(20)
        handler?.removeCallbacksAndMessages(null)
        handler = null
        block.invoke(list)
        onPostExecute()
    }

    /**
     * 完成后调用 mainThread
     */
    protected open fun onPostExecute() = Unit

    class ZFileAsyncHandler(zFileAsync: ZFileAsync) : Handler(Looper.myLooper()!!) {

        private val weakReference by lazy {
            WeakReference<ZFileAsync>(zFileAsync)
        }

        @Suppress("UNCHECKED_CAST")
        override fun handleMessage(msg: Message) {
            val list = msg.obj as? MutableList<ZFileBean>
            weakReference.get()?.onPostExecute(list)
        }
    }

}