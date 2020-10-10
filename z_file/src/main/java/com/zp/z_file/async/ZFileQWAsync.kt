package com.zp.z_file.async

import android.content.Context
import com.zp.z_file.content.*
import com.zp.z_file.util.ZFileUtil

/**
 * 获取 QQ 或 WeChat 文件 （QQ WeChat ---> QW）
 * @property fileType           QQ 或 WeChat
 * @property type               文件类型
 * @property filePathArray      过滤规则
 */
internal class ZFileQWAsync(
    private var fileType: String,
    private var type: Int,
    context: Context,
    block: MutableList<ZFileBean>?.() -> Unit
) : ZFileAsync(context, block) {

    private var filePathArray = ArrayList<String>()

    /**
     * 执行前调用 mainThread
     */
    override fun onPreExecute() {
        val loadListener = getZFileHelp().getQWFileLoadListener()
        val list = loadListener?.getQWFilePathArray(fileType, type) ?: getQWFilePathArray()
        filePathArray.addAll(list)
    }

    /**
     * 获取数据
     * @param filterArray  过滤规则
     */
    override fun doingWork(filterArray: Array<String>): MutableList<ZFileBean> {
        val loadListener = getZFileHelp().getQWFileLoadListener()
        return loadListener?.getQWFileDatas(type, filePathArray, filterArray)
                ?: ZFileUtil.getQWFileData(type, filePathArray, filterArray)
    }

    /**
     * 完成后调用 mainThread
     */
    override fun onPostExecute() {
        filePathArray.clear()
    }

    /**
     * 获取 QQ 或 WeChat 文件路径
     */
    private fun getQWFilePathArray(): MutableList<String> {
        val listArray = arrayListOf<String>()
        if (fileType == ZFileConfiguration.QQ) {
            when (type) {
                ZFILE_QW_PIC -> {
                    listArray.add(QQ_PIC)
                    listArray.add(QQ_PIC_MOVIE)
                }
                ZFILE_QW_MEDIA -> listArray.add(QQ_PIC_MOVIE)
                else -> {
                    listArray.add(QQ_DOWLOAD1)
                    listArray.add(QQ_DOWLOAD2)
                }
            }
        } else {
            when (type) {
                ZFILE_QW_PIC, ZFILE_QW_MEDIA -> listArray.add(WECHAT_FILE_PATH + WECHAT_PHOTO_VIDEO)
                else -> listArray.add(WECHAT_FILE_PATH + WECHAT_DOWLOAD)
            }
        }
        return listArray
    }

}