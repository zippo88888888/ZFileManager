package com.zp.z_file.content

import androidx.collection.ArrayMap
import com.zp.z_file.listener.ZQWFileLoadListener
import java.io.Serializable

/**
 * QQ、Wechat基本配置信息
 * 通过简单配置，即可 修改标题，文件类型，文件路径，获取相应的数据
 *
 * 若需要完全自定义获取 QQ、Wechat 文件，请实现 [ZQWFileLoadListener] 所有方法
 */
class ZFileQWData : Serializable {

    /**
     * 显示的标题 ，空使用默认
     */
    var titles: Array<String>? = null

    /**
     * QQ、Wechat 文件过滤规则的 Map ，空使用默认
     * Int             表示 文件类型 see[ZFILE_QW_PIC]、[ZFILE_QW_MEDIA]、[ZFILE_QW_DOCUMENT]、[ZFILE_QW_OTHER]
     * Array<String>   表示 过滤规则
     */
    var filterArrayMap: ArrayMap<Int, Array<String>>? = null

    /**
     * QQ 保存至本地SD卡上路径的 Map ，空使用默认
     * Int                  表示 文件类型 see[ZFILE_QW_PIC]、[ZFILE_QW_MEDIA]、[ZFILE_QW_DOCUMENT]、[ZFILE_QW_OTHER]
     * MutableList<String>  表示 SD卡上的路径
     */
    var qqFilePathArrayMap: ArrayMap<Int, MutableList<String>>? = null

    /**
     * Wechat 保存至本地SD卡上路径的 Map ，空使用默认
     * Int                  表示 文件类型 see[ZFILE_QW_PIC]、[ZFILE_QW_MEDIA]、[ZFILE_QW_DOCUMENT]、[ZFILE_QW_OTHER]
     * MutableList<String>  表示 SD卡上的路径
     */
    var wechatFilePathArrayMap: ArrayMap<Int, MutableList<String>>? = null


    /**
     * 方便java同学调用
     */
    class Build {

        private var titles: Array<String>? = null
        private var filterArrayMap: ArrayMap<Int, Array<String>>? = null
        private var qqFilePathArrayMap: ArrayMap<Int, MutableList<String>>? = null
        private var wechatFilePathArrayMap: ArrayMap<Int, MutableList<String>>? = null

        fun titles(titles: Array<String>?): Build {
            this.titles = titles
            return this
        }

        fun filterArrayMap(filterArray: ArrayMap<Int, Array<String>>?): Build {
            this.filterArrayMap = filterArray
            return this
        }

        fun qqFilePathArrayMap(qqFilePathArray: ArrayMap<Int, MutableList<String>>?): Build {
            this.qqFilePathArrayMap = qqFilePathArray
            return this
        }

        fun wechatFilePathArrayMap(wechatFilePathArray: ArrayMap<Int, MutableList<String>>?): Build {
            this.wechatFilePathArrayMap = wechatFilePathArray
            return this
        }

        fun build() = ZFileQWData().apply {
            this.titles = this@Build.titles
            this.filterArrayMap = this@Build.filterArrayMap
            this.qqFilePathArrayMap = this@Build.qqFilePathArrayMap
            this.wechatFilePathArrayMap = this@Build.wechatFilePathArrayMap
        }
    }

}