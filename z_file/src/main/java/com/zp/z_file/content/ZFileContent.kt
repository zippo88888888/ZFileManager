package com.zp.z_file.content

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.zp.z_file.R
import com.zp.z_file.common.ZFileManageDialog
import com.zp.z_file.common.ZFileManageHelp
import java.io.File
import java.io.Serializable

const val PNG = "png"
const val JPG = "jpg"
const val JPEG = "jpeg"
const val GIF = "gif"

const val MP3 = "mp3"
const val AAC = "aac"
const val WAV = "wav"

const val MP4 = "mp4"
const val _3GP = "3gp"

const val TXT = "txt"
const val XML = "xml"
const val JSON = "json"

const val DOC = "docx"
const val XLS = "xlsx"
const val PPT = "pptx"

const val PDF = "pdf"

const val ZIP = "zip"

internal const val COPY_TYPE = 0x1001
internal const val CUT_TYPE = 0x1002
internal const val DELTE_TYPE = 0x1003
internal const val ZIP_TYPE = 0x1004

internal const val FILE = 0
internal const val FOLDER = 1

internal const val LOG_TAG = "ZFileManager"

/** SD卡的根目录  */
internal val SD_ROOT by lazy {
    Environment.getExternalStorageDirectory().path
}

fun getZFileHelp() = ZFileManageHelp.getInstance()

fun getZFileConfig() = getZFileHelp().getZfileConfig()

internal fun Context.getStatusBarHeight() = getSystemHeight("status_bar_height")
internal fun Context.getSystemHeight(name: String, defType: String = "dimen") =
    resources.getDimensionPixelSize(
        resources.getIdentifier(name, defType, "android")
    )

/**
 * 获取屏幕的宽，高
 */
internal fun Context.getDisplay() = IntArray(2).apply {
    val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    manager.defaultDisplay.getSize(point)
    this[0] = point.x
    this[1] = point.y
}
/**
 * 根据Tag检查是否存在Fragment实例，如果存在就移除！
 */
internal fun AppCompatActivity.checkFragmentByTag(tag: String) {
    val fragment = supportFragmentManager.findFragmentByTag(tag)
    if (fragment != null) {
        supportFragmentManager.beginTransaction().remove(fragment).commit()
    }
}
/**
 * 跳转Activity
 */
internal fun Context.jumpActivity(clazz: Any, map: ArrayMap<String, Any>? = null) {
    if (clazz !is Class<*>) return
    startActivity(Intent(this, clazz).apply {
        if (map != null && map.isNotEmpty()) {
            putExtras(Bundle().apply {
                for ((key, value) in map) {
                    when (value) {
                        is Int -> putInt(key, value)
                        is Double -> putDouble(key, value)
                        is Float -> putFloat(key, value)
                        is Long -> putLong(key, value)
                        is Boolean -> putBoolean(key, value)
                        is String -> putString(key, value)
                        is Serializable -> putSerializable(key, value)
                        is Parcelable -> putParcelable(key, value)
                        else -> throw IllegalArgumentException("map type Error")
                    }
                }
            })
        }
    })
}

internal fun Activity.setStatusBarTransparent() {
    val decorView = window.decorView
    val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    decorView.systemUiVisibility = option
    window.statusBarColor = Color.TRANSPARENT
}
internal fun ZFileManageDialog.setNeedWH() {
    val width = context!!.getDisplay()[0] * 0.88f
    dialog?.window?.setLayout(width.toInt(), android.view.ViewGroup.LayoutParams.WRAP_CONTENT)
}
internal fun SwipeRefreshLayout.property(
    block: () -> Unit,
    color: Int = R.color.zfile_base_color,
    scale: Boolean = false,
    height: Int = 0
): SwipeRefreshLayout {
    setColorSchemeColors(context.getColorById(color))
    if (scale) setProgressViewEndTarget(scale, height)
    setOnRefreshListener(block)
    return this
}
internal fun View.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    context.toast(msg, duration)
}
internal fun Context.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, duration).show()
}
internal fun Context.getColorById(colorID: Int) = ContextCompat.getColor(this, colorID)
internal fun Context.getStringById(stringID: Int) = resources.getString(stringID)
internal fun Context.dip2px(dpValue: Float) = dpValue * resources.displayMetrics.density + 0.5f
internal fun Context.px2dip(pxValue: Float) = pxValue / resources.displayMetrics.density + 0.5f
internal fun File.getFileType() = this.path.getFileType()
internal fun String.getFileType() = this.run {
    substring(lastIndexOf(".") + 1, length)
}
internal fun String.getFileName() = File(this).name
internal fun String.toFile() = File(this)
internal fun getLastPath() = if (getZFileConfig().filePath.isNullOrEmpty()) SD_ROOT else getZFileConfig().filePath
internal fun ZFileBean.toPathBean() = ZFilePathBean().apply {
    fileName = this@toPathBean.fileName
    filePath = this@toPathBean.filePath
}
internal fun File.toPathBean() = ZFilePathBean().apply {
    fileName = this@toPathBean.name
    filePath = this@toPathBean.path
}





