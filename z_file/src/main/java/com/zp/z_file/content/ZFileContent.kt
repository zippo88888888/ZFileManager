package com.zp.z_file.content

import android.app.Activity
import android.app.Application
import android.content.ContentProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.zp.z_file.R
import com.zp.z_file.common.ZFileManageDialog
import com.zp.z_file.common.ZFileManageHelp
import java.io.File
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

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

/** 图片 */
const val ZFILE_QW_PIC = 0
/** 媒体 */
const val ZFILE_QW_MEDIA = 1
/** 文档 */
const val ZFILE_QW_DOCUMENT = 2
/** 其他 */
const val ZFILE_QW_OTHER = 3

/** 默认资源 */
const val ZFILE_DEFAULT = -1
/** onActivityResult requestCode */
const val ZFILE_REQUEST_CODE = 0x1000
/** onActivityResult resultCode */
const val ZFILE_RESULT_CODE = 0x1001
/**
 * onActivityResult data key  --->>>
 * val list = data?.getParcelableArrayListExtra<[ZFileBean]>([ZFILE_SELECT_DATA_KEY])
 */
const val ZFILE_SELECT_DATA_KEY = "ZFILE_SELECT_RESULT_DATA"

fun getZFileHelp() = ZFileManageHelp.getInstance()
fun getZFileConfig() = getZFileHelp().getConfiguration()



internal const val COPY_TYPE = 0x2001
internal const val CUT_TYPE = 0x2002
internal const val DELTE_TYPE = 0x2003
internal const val ZIP_TYPE = 0x2004

internal const val FILE = 0
internal const val FOLDER = 1

internal const val QQ_PIC = "/storage/emulated/0/tencent/QQ_Images/" // 保存的图片
internal const val QQ_PIC_MOVIE = "/storage/emulated/0/Pictures/QQ/" // 保存的图片和视频
// 保存的文档（未保存到手机的图片和视频也在这个位置，感觉QQ的文件乱糟糟的）
internal const val QQ_DOWLOAD1 = "/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/QQfile_recv/"
internal const val QQ_DOWLOAD2 = "/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/QQ_business/"

internal const val WECHAT_FILE_PATH = "/storage/emulated/0/tencent/MicroMsg/"
internal const val WECHAT_PHOTO_VIDEO = "WeiXin/" // 图片、视频保存位置
internal const val WECHAT_DOWLOAD = "Download/" // 其他文件保存位置

internal const val LOG_TAG = "ZFileManager"
internal const val ERROR_MSG = "fragmentOrActivity is not Activity or Fragment"
internal const val QW_FILE_TYPE_KEY = "QW_fileType"
internal const val FILE_START_PATH_KEY = "fileStartPath"

internal fun Context.getStatusBarHeight() = getSystemHeight("status_bar_height")
internal fun Context.getSystemHeight(name: String, defType: String = "dimen") =
    resources.getDimensionPixelSize(
        resources.getIdentifier(name, defType, "android")
    )

internal fun Context.getZDisplay() = IntArray(2).apply {
    val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    manager.defaultDisplay.getSize(point)
    this[0] = point.x
    this[1] = point.y
}
internal fun AppCompatActivity.checkFragmentByTag(tag: String) {
    val fragment = supportFragmentManager.findFragmentByTag(tag)
    if (fragment != null) {
        supportFragmentManager.beginTransaction().remove(fragment).commit()
    }
}
internal fun Activity.jumpActivity(clazz: Any, map: ArrayMap<String, Any>? = null) {
    if (clazz !is Class<*>) return
    startActivityForResult(Intent(this, clazz).apply {
        if (!map.isNullOrEmpty()) putExtras(map.toBundle())
    }, ZFILE_REQUEST_CODE)
}
internal fun Fragment.jumpActivity(clazz: Any, map: ArrayMap<String, Any>? = null) {
    if (clazz !is Class<*>) return
    startActivityForResult(Intent(context, clazz).apply {
        if (!map.isNullOrEmpty()) putExtras(map.toBundle())
    }, ZFILE_REQUEST_CODE)
}
internal fun ArrayMap<String, Any>.toBundle() = Bundle().apply {
    for ((key, value) in this@toBundle) {
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
}
internal fun Activity.setStatusBarTransparent() {
    val decorView = window.decorView
    val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    decorView.systemUiVisibility = option
    window.statusBarColor = Color.TRANSPARENT
}
internal fun ZFileManageDialog.setNeedWH() {
    val display = context?.getZDisplay()
    val width = if (display?.isEmpty() == true) ViewGroup.LayoutParams.MATCH_PARENT else (display!![0] * 0.88f).toInt()
    dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
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
internal fun Int.getFilterArray() = when (this) {
    ZFILE_QW_PIC -> arrayOf(PNG, JPEG, JPG, GIF)
    ZFILE_QW_MEDIA -> arrayOf(MP4, _3GP)
    ZFILE_QW_DOCUMENT -> arrayOf(TXT, JSON, XML, DOC, XLS, PPT, PDF)
    else -> arrayOf("")
}
internal fun Any.getContext(): Context? = when (this) {
    is Activity -> this
    is Fragment -> context
    is Application -> applicationContext
    is ContentProvider -> context
    else -> null
}
fun Context.dip2pxF(dpValue: Float) = dpValue * resources.displayMetrics.density + 0.5f
fun Context.dip2px(dpValue: Float) = dip2pxF(dpValue).toInt()
fun Context.px2dipF(pxValue: Float) = pxValue / resources.displayMetrics.density + 0.5f
fun Context.px2dip(pxValue: Float) = px2dipF(pxValue).toInt()
internal fun Context.getColorById(colorID: Int) = ContextCompat.getColor(this, colorID)
internal fun Context.getStringById(stringID: Int) = resources.getString(stringID)
internal fun <E> Set<E>.indexOf(value: String): Boolean {
    var flag = false
    forEach forEach@{
        if ((it?.toString()?.indexOf(value) ?: -1) >= 0) {
            flag = true
            return@forEach
        }
    }
    return flag
}
internal fun File.getFileType() = this.path.getFileType()
internal fun String.getFileType() = this.run {
    substring(lastIndexOf(".") + 1, length)
}
internal fun String.accept(type: String) =
    this.endsWith(type.toLowerCase(Locale.CHINA)) || this.endsWith(type.toUpperCase(Locale.CHINA))
internal fun String.getFileName() = File(this).name
internal fun String.toFile() = File(this)
internal fun ZFileBean.toPathBean() = ZFilePathBean().apply {
    fileName = this@toPathBean.fileName
    filePath = this@toPathBean.filePath
}
internal fun ZFileBean.toQWBean(isSelected: Boolean = true) = ZFileQWBean(this, isSelected)
internal fun File.toPathBean() = ZFilePathBean().apply {
    fileName = this@toPathBean.name
    filePath = this@toPathBean.path
}
internal fun ArrayMap<String, ZFileBean>.toFileList(): MutableList<ZFileBean> {
    val list = ArrayList<ZFileBean>()
    for ((_, v) in this) {
        list.add(v)
    }
    return list
}
/** SD卡的根目录  */
internal val SD_ROOT: String
    get() {
        return Environment.getExternalStorageDirectory().path
    }
internal val emptyRes: Int
    get() {
        return if (getZFileConfig().resources.emptyRes == ZFILE_DEFAULT) R.drawable.ic_zfile_empty
        else getZFileConfig().resources.emptyRes
    }
internal val folderRes: Int
    get() {
        return if (getZFileConfig().resources.folderRes == ZFILE_DEFAULT) R.drawable.ic_zfile_folder
        else getZFileConfig().resources.folderRes
    }
internal val lineColor: Int
    get() {
        return if (getZFileConfig().resources.lineColor == ZFILE_DEFAULT) R.color.zfile_line_color
        else getZFileConfig().resources.lineColor
    }








