package com.zp.z_file.content

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.zp.z_file.R
import com.zp.z_file.common.ZFileManageDialog
import com.zp.z_file.common.ZFileManageHelp
import com.zp.z_file.listener.ZFileQWLoadListener
import com.zp.z_file.listener.ZFragmentListener
import com.zp.z_file.type.*
import com.zp.z_file.ui.ZFileListFragment
import com.zp.z_file.util.ZFileHelp
import com.zp.z_file.util.ZFileLog
import com.zp.z_file.util.ZFileOtherUtil
import com.zp.z_file.util.ZFileUtil
import java.io.File
import java.util.*

const val PNG = "png"
const val JPG = "jpg"
const val JPEG = "jpeg"
const val GIF = "gif"

const val MP3 = "mp3"
const val AAC = "aac"
const val WAV = "wav"
const val M4A = "m4a"

const val MP4 = "mp4"
const val _3GP = "3gp"

const val TXT = "txt"
const val XML = "xml"
const val JSON = "json"

const val DOC = "doc"
const val DOCX = "docx"

const val XLS = "xls"
const val XLSX = "xlsx"

const val PPT = "ppt"
const val PPTX = "pptx"

const val PDF = "pdf"

const val ZIP = "zip"

/** 默认资源 */
const val ZFILE_DEFAULT = -1

/** 图片 */
const val ZFILE_QW_PIC = 0
/** 媒体 */
const val ZFILE_QW_MEDIA = 1
/** 文档 */
const val ZFILE_QW_DOCUMENT = 2
/** 其他 (过滤规则可以使用 "" 代替) */
const val ZFILE_QW_OTHER = 3

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

@Deprecated("请使用 ZFileAudioType")
typealias AudioType = ZFileAudioType
@Deprecated("请使用 ZFileImageType")
typealias ImageType = ZFileImageType
@Deprecated("请使用 ZFileOtherType")
typealias OtherType = ZFileOtherType
@Deprecated("请使用 ZFilePdfType")
typealias PdfType = ZFilePdfType
@Deprecated("请使用 ZFilePptType")
typealias PptType = ZFilePptType
@Deprecated("请使用 ZFileTxtType")
typealias TxtType = ZFileTxtType
@Deprecated("请使用 ZFileVideoType")
typealias VideoType = ZFileVideoType
@Deprecated("请使用 ZFileWordType")
typealias WordType = ZFileWordType
@Deprecated("请使用 ZFileXlsType")
typealias XlsType = ZFileXlsType
@Deprecated("请使用 ZFileZipType")
typealias ZipType = ZFileZipType
@Deprecated("请使用 ZFileQWLoadListener")
typealias ZQWFileLoadListener = ZFileQWLoadListener

/**
 * SD 卡根目录
 */
val SD_ROOT: String
    get() {
        return "/storage/emulated/0/"
    }

/**
 * 将 [File] 转为 [ZFileBean]
 */
fun File.toZFileBean() = ZFileBean().apply {
    fileName = name
    isFile = this@toZFileBean.isFile
    filePath = path
    date = ZFileOtherUtil.getFormatFileDate(lastModified())
    originalDate = lastModified().toString()
    size = ZFileOtherUtil.getFileSize(length())
    originaSize = length()
    folderLength = ZFileUtil.getFolderLength(this@toZFileBean)
    parent = this@toZFileBean.parent
}

/**
 * 获取 [ZFileListFragment]
 * @param fragmentTag String    Fragment Tag，默认为 [ZFileConfiguration.fragmentTag]
 */
fun FragmentActivity.getZFileListFragment(fragmentTag: String = getZFileConfig().fragmentTag): ZFileListFragment? {
    return supportFragmentManager.findFragmentByTag(fragmentTag) as? ZFileListFragment
}

/**
 * 在 [FragmentActivity] 中 [FragmentActivity.onCreate] 使用 --->>> 仅支持 [ZFileListFragment] 嵌套 [FragmentActivity] 中使用
 */
fun FragmentActivity.zfileInitAndStart(id: Int, zFragmentListener: ZFragmentListener?) {
    val fragment = ZFileListFragment.newInstance()
    fragment.zFragmentListener = zFragmentListener
    supportFragmentManager.beginTransaction()
        .add(id, fragment, getZFileConfig().fragmentTag)
        .commit()
}
/**
 * 在 [FragmentActivity] 中 [FragmentActivity.onResume] 使用 --->>> 仅支持 [ZFileListFragment] 嵌套 [FragmentActivity] 中使用
 */
fun FragmentActivity.zfileResume() {
    getZFileListFragment()?.showPermissionDialog()
}
/**
 * 在 [FragmentActivity] 中 [FragmentActivity.onBackPressed] 使用 --->>> 仅支持 [ZFileListFragment] 嵌套 [FragmentActivity] 中使用
 */
fun FragmentActivity.zfileBackPressed() {
    getZFileListFragment()?.onBackPressed()
}


// inner ===========================================================================================
// inner ===========================================================================================
// inner ===========================================================================================

internal const val I_NAME = "inflate"
internal const val ZFILE_FRAGMENT_TAG = "ZFileListFragment"

internal const val ZIP_BUFFER_SIZE = 16384

internal const val QW_SIZE = 4

internal const val COPY_TYPE = 0x2001
internal const val CUT_TYPE = 0x2002
internal const val DELTE_TYPE = 0x2003
internal const val ZIP_TYPE = 0x2004

internal const val FILE = 0
internal const val FOLDER = 1

internal const val PERMISSION_FAILED_TITLE1 = "自定义权限视图展示：布局文件中某个控件必须要设置ID：zfile_permission_againBtn"
internal const val PERMISSION_FAILED_TITLE1_2 = "【ZFileOtherListener.getPermissionFailedLayoutId()】Can't find id [R.id.zfile_permission_againBtn]! " +
        "You must be set view id(zfile_permission_againBtn) in layout"

internal const val PERMISSION_FAILED_TITLE2 = "自定义data及obb目录展示的展位图展示：布局文件中某个控件必须要设置ID：zfile_do_btn"
internal const val PERMISSION_FAILED_TITLE2_2 = "【ZFileOtherListener.getDataAndObbFoldLayoutId()】Can't find id [R.id.zfile_do_btn]! " +
        "You must be set view id(zfile_do_btn) in layout"

internal const val QQ_PIC = "/storage/emulated/0/tencent/QQ_Images/" // 保存的图片
internal const val QQ_PIC_MOVIE = "/storage/emulated/0/Pictures/QQ/" // 保存的图片和视频
// 保存的文档（未保存到手机的图片和视频也在这个位置）
internal const val QQ_DOWLOAD1 = "/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/QQfile_recv/"
internal const val QQ_DOWLOAD2 = "/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/QQ_business/"

internal const val WECHAT_FILE_PATH = "/storage/emulated/0/tencent/MicroMsg/"
internal const val WECHAT_PHOTO_VIDEO = "WeiXin/" // 图片、视频保存位置
internal const val WECHAT_DOWLOAD = "Download/" // 其他文件保存位置

internal const val LOG_TAG = "ZFileManager"
internal const val ERROR_MSG = "fragmentOrActivity is not FragmentActivity or Fragment"
internal const val QW_FILE_TYPE_KEY = "QW_fileType"
internal const val FILE_START_PATH_KEY = "fileStartPath"

internal const val SAF_DATA_PATH = "/storage/emulated/0/Android/data"
internal const val SAF_OBB_PATH = "/storage/emulated/0/Android/obb"
internal const val SAF_TREE_ROOT = "content://com.android.externalstorage.documents/tree/"
/** SAF Android/data or Android/obb RequestCode */
internal const val SAF_DATA_OBB_CODE = 0x3001

internal fun Context.getStatusBarHeight() = getSystemHeight("status_bar_height")
internal fun Context.getSystemHeight(name: String, defType: String = "dimen") =
    resources.getDimensionPixelSize(
        resources.getIdentifier(name, defType, "android")
    )
internal fun Context.getZDisplay(): IntArray {
    val array = IntArray(2)
    val dm = resources.displayMetrics
    array[0] = dm.widthPixels
    array[1] = dm.heightPixels
    return array
}
internal fun FragmentActivity.checkFragmentByTag(tag: String) {
    val fragment = supportFragmentManager.findFragmentByTag(tag)
    if (fragment != null) {
        supportFragmentManager.beginTransaction().remove(fragment).commit()
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
    color: Int = R.color.zfile_base_color,
    scale: Boolean = false,
    height: Int = 0,
    block: () -> Unit
): SwipeRefreshLayout {
    setColorSchemeColors(context getColorById color)
    if (scale) setProgressViewEndTarget(scale, height)
    setOnRefreshListener(block)
    return this
}
internal fun View.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    context?.toast(msg, duration)
}
internal fun Context.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(applicationContext, msg, duration).show()
}
internal fun getFilePermissionFailedLayoutId(): Int {
    var permissionLayoutResId = getZFileHelp().getOtherListener()?.getPermissionFailedLayoutId() ?: ZFILE_DEFAULT
    if (permissionLayoutResId == ZFILE_DEFAULT) {
        permissionLayoutResId = R.layout.layout_zfile_list_permission
    }
    return permissionLayoutResId
}
internal fun getFileEmptyLayoutId(): Int {
    var emptyLayoutResId = getZFileHelp().getOtherListener()?.getFileListEmptyLayoutId() ?: ZFILE_DEFAULT
    if (emptyLayoutResId == ZFILE_DEFAULT) {
        emptyLayoutResId = R.layout.layout_zfile_list_empty
    }
    return emptyLayoutResId
}
internal fun getFileDoLayoutId(): Int {
    var doLayoutResId = getZFileHelp().getFileSAFListener().getDataAndObbFoldLayoutId()
    if (doLayoutResId == ZFILE_DEFAULT) {
        doLayoutResId = R.layout.layout_zfile_list_do
    }
    return doLayoutResId
}
@RequiresApi(Build.VERSION_CODES.R)
internal fun Context.toFileManagerPage() {
    try {
        val action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
        val uri = Uri.parse("package:${packageName}")
        startActivity(Intent(action, uri))
    } catch (e: Exception) {
        ZFileLog.e("无法跳转到指定App的【所有文件权限访问】页面，现跳转到列表页，需要用户手动寻找！")
        e.printStackTrace()
        startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
    }
}
internal infix fun Context.dip2pxF(dpValue: Float) = dpValue * resources.displayMetrics.density + 0.5f
internal infix fun Context.dip2px(dpValue: Float) = dip2pxF(dpValue).toInt()
internal infix fun Context.getColorById(colorID: Int) = ContextCompat.getColor(this, colorID)
internal infix fun Context.getStringById(stringID: Int) = resources.getString(stringID)
internal infix fun <E> Set<E>.indexOf(value: String): Boolean {
    var flag = false
    for (it in this) {
        if (it.toString().indexOf(value) >= 0) {
            flag = true
            break
        }
    }
    return flag
}
internal infix fun Any.getBarTitle(size: Int) = try {
    String.format(getZFileConfig().titleSelectedStr, size)
} catch (e: Exception) {
    e.printStackTrace()
    ZFileLog.e("getZFileConfig().titleSelectedStr 格式错误，已使用默认值！")
    "已选中%d个文件"
}
internal fun File.getFileType() = this.path.getFileType()
internal fun String.getFileType() = this.run {
    substring(lastIndexOf(".") + 1, length)
}
internal infix fun String.accept(type: String) =
    this.endsWith(type.toLowerCase(Locale.CHINA)) || this.endsWith(type.toUpperCase(Locale.CHINA))
internal fun String.getFileName() = File(this).name
internal fun String.getFileNameOnly() = getFileName().run {
    substring(0, lastIndexOf("."))
}
internal fun String.toFile() = File(this)
internal fun String?.isNull() =
    if (this == null || this.isNullOrEmpty()) true else this.replace(" ".toRegex(), "").isEmpty()
internal infix fun String?.has(value: String?) : Boolean {
    if (this.isNullOrEmpty() || value.isNullOrEmpty()) return false
    return this.indexOf(value) != -1 || value.indexOf(this) != -1
}
internal fun String?.isDataOrObbPath() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) this has SAF_DATA_PATH || this has SAF_OBB_PATH
    else false

internal fun String.toUriNormalForSAF(): String {
    var path = this
    path = path.replace(SD_ROOT, "")
    path = Uri.encode(path).replace("/", "%2F")
    return "${SAF_TREE_ROOT}primary%3A$path"
}
internal fun ZFileBean.toPathBean() = ZFilePathBean().apply {
    fileName = this@toPathBean.fileName
    filePath = this@toPathBean.filePath
}
internal infix fun ZFileBean.toQWBean(isSelected: Boolean) = ZFileQWBean(this, isSelected)
internal infix fun ZFileBean.canNotSelect(array: Array<String>?): Boolean {
    return if (array.isNullOrEmpty()) false
    else array.any { it == ZFileHelp.getFileTypeBySuffix(filePath) }
}
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
internal fun throwError(title: String) {
    ZFileException.throwConfigurationError(title)
}
internal fun async(block: () -> Unit) {
    getZFileHelp().getPoolExecutor().execute(object : Thread() {
        override fun run() {
            block()
        }
    })
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
internal inline fun <reified VB : ViewBinding> AppCompatActivity.inflate(): Lazy<VB> = lazy {
    binding<VB>(layoutInflater).run {
        setContentView(root)
        this
    }
}
internal inline fun <reified VB : ViewBinding> binding(layoutInflater: LayoutInflater): VB =
    (VB::class.java.getMethod(I_NAME, LayoutInflater::class.java).invoke(null, layoutInflater)) as VB








