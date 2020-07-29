package com.zp.z_file.content

import android.os.Parcelable
import com.zp.z_file.R
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * 配置信息
 */
class ZFileConfiguration : Serializable {

    companion object {

        /** 默认 */
        const val BY_DEFAULT = 0x1000
        /** 根据名字 */
        const val BY_NAME = 0x1001
        /** 根据最后修改时间 */
        const val BY_DATE = 0x1003
        /** 根据大小 */
        const val BY_SIZE = 0x1004

        /** 升序 */
        const val ASC = 0x2001
        /** 降序 */
        const val DESC = 0x2002

        /** 样式一 */
        const val STYLE1 = 1
        /** 样式二 */
        const val STYLE2 = 2

        const val COPY = "复制"
        const val MOVE = "移动"
        const val DELETE = "删除"
        const val INFO = "查看详情"
    }

    /**
     * 起始访问位置，空为SD卡根目录
     */
    var filePath: String? = null

    /**
     * 图片资源配置
     */
    var resources = ZFileResources()

    /**
     * 是否显示隐藏文件
     */
    var showHiddenFile = false

    /**
     * 根据什么排序 see [BY_DEFAULT] [BY_NAME] [BY_DATE] [BY_SIZE]
     */
    var sortordBy = BY_DEFAULT

    /**
     * 排序方式 see [ASC] [DESC]
     */
    var sortord = ASC

    /**
     * 过滤规则，默认显示所有的文件类型
     * 如 arrayOf(PNG, JPG, JPEG, GIF) 只显示图片类型
     */
    var fileFilterArray: Array<String>? = null

    /**
     * 最大选取数量
     */
    var maxLength = 9

    /**
     * 超过最大选择数量文字提醒
     */
    var maxLengthStr = "您最多可以选取${maxLength}个文件"

    /**
     * 选中的样式 see [STYLE1] [STYLE2]
     */
    var boxStyle = STYLE2

    /**
     * 是否需要长按事件
     */
    var needLongClick = true

    /**
     * 默认只有文件才有长按事件
     * 长按暂不支持对于文件夹的操作（删除、复制、剪切），如有需要，请实现 ZFileOperateListener
     */
    var isOnlyFileHasLongClick = true

    /**
     * 长按后需要显示的操作类型 see [COPY] [MOVE] [DELETE] [INFO]
     * 空默认为 arrayOf(COPY, MOVE, DELETE, INFO)
     * 目前只可以是这四种类型，个数、顺序可以自定义，文字暂不支持自定义
     */
    var longClickOperateTitles: Array<String>? = null

    /**
     * 是否只需要显示文件夹（二选一）
     */
    var isOnlyFolder = false

    /**
     * 是否只需要显示文件（二选一）
     */
    var isOnlyFile = false

    /**
     * Android 7.0以上 需要的 FileProvider，一般都是包名 + xxxFileProvider
     * 如果项目中存在或其他原因可以不设置，自己实现 ZFileOpenListener
     */
    var authority = "com.zp.zfile_manager.ZFileManagerProvider"

    /**
     * 是否显示日志
     */
    var showLog = true

    /**
     * 方便java同学调用
     */
    class Build {

        private var filePath: String? = null
        private var resources = ZFileResources()
        private var showHiddenFile = false
        private var sortordBy = BY_DEFAULT
        private var sortord = ASC
        private var fileFilterArray: Array<String>? = null
        private var maxLength = 9
        private var maxLengthStr = "您最多可以选取${maxLength}个文件"
        private var boxStyle = STYLE1
        private var needLongClick = true
        private var isOnlyFileHasLongClick = true
        private var longClickOperateTitles: Array<String>? = null
        private var isOnlyFolder = false
        private var isOnlyFile = false
        private var authority = "com.zp.zfile_manager.ZFileManagerProvider"
        private var showLog = true

        fun filePath(filePath: String?): Build {
            this.filePath = filePath
            return this
        }

        fun resources(resources: ZFileResources): Build {
            this.resources = resources
            return this
        }

        fun showHiddenFile(showHiddenFile: Boolean): Build {
            this.showHiddenFile = showHiddenFile
            return this
        }

        fun sortordBy(sortordBy: Int): Build {
            this.sortordBy = sortordBy
            return this
        }

        fun sortord(sortord: Int): Build {
            this.sortord = sortord
            return this
        }

        fun fileFilterArray(fileFilterArray: Array<String>?): Build {
            this.fileFilterArray = fileFilterArray
            return this
        }

        fun maxLength(maxLength: Int): Build {
            this.maxLength = maxLength
            return this
        }

        fun maxLengthStr(maxLengthStr: String): Build {
            this.maxLengthStr = maxLengthStr
            return this
        }

        fun boxStyle(boxStyle: Int): Build {
            this.boxStyle = boxStyle
            return this
        }

        fun needLongClick(needLongClick: Boolean): Build {
            this.needLongClick = needLongClick
            return this
        }

        fun isOnlyFileHasLongClick(isOnlyFileHasLongClick: Boolean): Build {
            this.isOnlyFileHasLongClick = isOnlyFileHasLongClick
            return this
        }

        fun longClickOperateTitles(longClickOperateTitles: Array<String>?): Build {
            this.longClickOperateTitles = longClickOperateTitles
            return this
        }

        fun isOnlyFolder(isOnlyFolder: Boolean): Build {
            this.isOnlyFolder = isOnlyFolder
            return this
        }

        fun isOnlyFile(isOnlyFile: Boolean): Build {
            this.isOnlyFile = isOnlyFile
            return this
        }

        fun authority(authority: String): Build {
            this.authority = authority
            return this
        }

        fun showLog(showLog: Boolean): Build {
            this.showLog = showLog
            return this
        }

        fun build() = ZFileConfiguration().apply {
            this.filePath = this@Build.filePath
            this.resources = this@Build.resources
            this.showHiddenFile = this@Build.showHiddenFile
            this.sortordBy = this@Build.sortordBy
            this.sortord = this@Build.sortord
            this.fileFilterArray = this@Build.fileFilterArray
            this.maxLength = this@Build.maxLength
            this.maxLengthStr = this@Build.maxLengthStr
            this.boxStyle = this@Build.boxStyle
            this.needLongClick = this@Build.needLongClick
            this.isOnlyFileHasLongClick = this@Build.isOnlyFileHasLongClick
            this.longClickOperateTitles = this@Build.longClickOperateTitles
            this.isOnlyFolder = this@Build.isOnlyFolder
            this.isOnlyFile = this@Build.isOnlyFile
            this.authority = this@Build.authority
            this.showLog = this@Build.showLog
        }

    }

    /**
     * 图片相关资源配置
     * @property audioRes Int        音频
     * @property txtRes Int          文本
     * @property pdfRes Int          PDF
     * @property pptRes Int          PPT
     * @property wordRes Int         Word
     * @property excelRes Int        Excel
     * @property zipRes Int          ZIP
     * @property otherRes Int        其他类型
     * @property emptyRes Int        空资源
     * @property folderRes Int       文件夹
     * @property lineColor Int       列表分割线颜色
     */
    @Parcelize
    data class ZFileResources @JvmOverloads constructor(
        var audioRes: Int = R.drawable.ic_zfile_audio,
        var txtRes: Int = R.drawable.ic_zfile_txt,
        var pdfRes: Int = R.drawable.ic_zfile_pdf,
        var pptRes: Int = R.drawable.ic_zfile_ppt,
        var wordRes: Int = R.drawable.ic_zfile_word,
        var excelRes: Int = R.drawable.ic_zfile_excel,
        var zipRes: Int = R.drawable.ic_zfile_zip,
        var otherRes: Int = R.drawable.ic_zfile_other,
        var emptyRes: Int = R.drawable.ic_zfile_empty,
        var folderRes: Int = R.drawable.ic_zfile_folder,
        var lineColor: Int = R.color.zfile_line_color
    ) : Serializable, Parcelable

}


