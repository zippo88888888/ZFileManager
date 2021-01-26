package com.zp.z_file.async

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.zp.z_file.content.ZFileBean
import com.zp.z_file.content.getZFileHelp
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference
import kotlin.concurrent.thread

internal class ZFileThread(
    private var context: Context,
    private var block: MutableList<ZFileBean>?.() -> Unit
) {

    private var handler = ZFileHandler(this)

    private val softReference by lazy {
        SoftReference<Context>(context)
    }

    fun start(filePath: String?) {
        thread {
            val list = getZFileHelp().getFileLoadListener().getFileList(softReference.get(), filePath)
            handler.sendMessage(Message().apply {
                what = 10
                obj = list
            })
        }
    }

    class ZFileHandler(thread: ZFileThread) : Handler(Looper.myLooper()!!) {
        private val weakReference by lazy {
            WeakReference<ZFileThread>(thread)
        }

        @Suppress("UNCHECKED_CAST")
        override fun handleMessage(msg: Message) {
            val list = msg.obj as? MutableList<ZFileBean>
            weakReference.get()?.block?.invoke(list)
        }
    }

}