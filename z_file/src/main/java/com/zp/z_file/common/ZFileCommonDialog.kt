package com.zp.z_file.common

import android.content.Context
import androidx.appcompat.app.AlertDialog
import java.lang.ref.SoftReference

internal class ZFileCommonDialog(context: Context, private var isUserDefaultTitle: Boolean = true) {

    private val reference: SoftReference<Context> by lazy { SoftReference(context) }

    private fun createDialog(listener1: () -> Unit, str: Array<out String>): AlertDialog.Builder? {
        if (reference.get() != null) {
            return AlertDialog.Builder(reference.get()!!).apply {
                if (isUserDefaultTitle) {
                    setTitle("温馨提示")
                    setMessage(str[0])
                    setPositiveButton(str[1]) { dialog, _ ->
                        dialog.dismiss()
                        listener1.invoke()
                    }
                } else {
                    setTitle(str[0])
                    setMessage(str[1])
                    setPositiveButton(str[2]) { dialog, _ ->
                        dialog.dismiss()
                        listener1.invoke()
                    }
                }
                setCancelable(false)
            }
        } else return null
    }

    fun showDialog1(listener1: () -> Unit, vararg str: String) {
        createDialog(listener1, str)?.apply { show() }
    }

    fun showDialog2(listener1: () -> Unit, listener2: () -> Unit, vararg str: String) {
        createDialog(listener1, str)?.apply {
            setNegativeButton(if (isUserDefaultTitle) str[2] else str[3]) { dialog, _ ->
                dialog.dismiss()
                listener2.invoke()
            }
            show()
        }
    }

    fun showDialog3(listener1: () -> Unit, listener2: () -> Unit, listener3: () -> Unit, vararg str: String) {
        createDialog(listener1, str)?.apply {
            setNegativeButton(if (isUserDefaultTitle) str[2] else str[3]) { dialog, _ ->
                dialog.dismiss()
                listener2.invoke()
            }
            setNeutralButton(if (isUserDefaultTitle) str[3] else str[4]) { dialog, _ ->
                dialog.dismiss()
                listener3.invoke()
            }
            show()
        }
    }
}