package com.zp.z_file.content

import android.content.Context
import android.os.Handler
import android.os.Message
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference
import kotlin.concurrent.thread

class ZFileThread(
    private var context: Context,
    private var block: (MutableList<ZFileBean>?) -> Unit
) {

    private var handler = ZFileHandler(this)

    private val softReference by lazy {
        SoftReference<Context>(context)
    }

    fun start(filePath: String?) {
        thread {
            val list = getZFileHelp().getFileLoadListener().getFileList(softReference.get(), filePath)
            handler.sendMessage(Message().apply {
                what = 0
                obj = list
            })
        }
    }

    class ZFileHandler(thread: ZFileThread) : Handler() {
        private val weakReference by lazy {
            WeakReference<ZFileThread>(thread)
        }

        @Suppress("UNCHECKED_CAST")
        override fun handleMessage(msg: Message?) {
            val list = msg?.obj as? MutableList<ZFileBean>
            weakReference.get()?.block?.invoke(list)
        }
    }

}