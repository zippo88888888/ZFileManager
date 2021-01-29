package com.zp.z_file.ui.adapter

import android.content.Context
import android.view.View
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.collection.ArrayMap
import com.zp.z_file.R
import com.zp.z_file.common.ZFileAdapter
import com.zp.z_file.common.ZFileTypeManage
import com.zp.z_file.common.ZFileViewHolder
import com.zp.z_file.content.*
import com.zp.z_file.util.ZFileLog

internal class ZFileListAdapter(context: Context) : ZFileAdapter<ZFileBean>(context) {

    constructor(context: Context, isQW: Boolean) : this(context) {
        this.isQW = isQW
    }

    private var isQW = false

    private var config = getZFileConfig()

    // box选中的数据
    private val boxMap by lazy {
        ArrayMap<Int, Boolean>()
    }

    // 当前文件夹选择的数量
    private val countMap by lazy {
        ArrayMap<String, Int>()
    }

    // 已选中的数据
    var selectData = ArrayList<ZFileBean>()

    var isManage = false
        set(value) {
            if (isQW) {
                if (value) {
                    notifyDataSetChanged()
                } else {
                    clearSelectMap()
                    clearCountMap()
                }
            } else {
                if (!value) {
                    clearSelectMap()
                    clearCountMap()
                }
            }
            field = value
        }

    var itemClickByAnim: ((View, Int, ZFileBean) -> Unit)? = null
    var changeListener: ((Boolean, Int) -> Unit)? = null
    var qwListener: ((Boolean, ZFileBean, Boolean) -> Unit)? = null

    override fun setDatas(list: MutableList<ZFileBean>?) {
        if (list.isNullOrEmpty()) {
            clear()
        } else {
            boxMap.clear()
            list.indices.forEach {
                // 判断当前List数据是否存在已经选中过的值
                if (selectData.isNullOrEmpty()) {
                    boxMap[it] = false
                } else {
                    boxMap[it] = selectData.contains(list[it])
                }
            }
            super.setDatas(list)
        }
    }

    override fun getItemViewType(position: Int) = if (getItem(position).isFile) FILE else FOLDER

    override fun getLayoutID(viewType: Int) =
        when (viewType) {
            FILE -> R.layout.item_zfile_list_file
            else -> R.layout.item_zfile_list_folder
        }

    override fun bindView(holder: ZFileViewHolder, item: ZFileBean, position: Int) {
        if (item.isFile) {
            setFileData(holder, item, position)
        } else {
            setFolderData(holder, item, position)
        }
    }

    private fun setFileData(holder: ZFileViewHolder, item: ZFileBean, position: Int) {
        holder.apply {
            setText(R.id.item_zfile_list_file_nameTxt, item.fileName)
            setText(R.id.item_zfile_list_file_dateTxt, item.date)
            setText(R.id.item_zfile_list_file_sizeTxt, item.size)
            setBgColor(R.id.item_zfile_list_file_line, lineColor)
            setVisibility(R.id.item_zfile_list_file_line, position < itemCount - 1)
            setVisibility(R.id.item_zfile_file_box_pic, !isManage)
            when (config.boxStyle) {
                ZFileConfiguration.STYLE1 -> setVisibility(R.id.item_zfile_list_file_box1, isManage)
                ZFileConfiguration.STYLE2 -> setVisibility(R.id.item_zfile_list_file_box2, isManage)
                else -> throw IllegalArgumentException("ZFileConfiguration boxStyle error")
            }
        }
        val box1 = holder.getView<CheckBox>(R.id.item_zfile_list_file_box1)
        box1.isChecked = boxMap[position] ?: false
        box1.setOnClickListener {
            boxClick(position, item)
        }
        val box2 = holder.getView<TextView>(R.id.item_zfile_list_file_box2)
        box2.isSelected = boxMap[position] ?: false
        box2.setOnClickListener {
            box2.isSelected = !(boxMap[position] ?: false)
            boxClick(position, item)
        }

        val pic = holder.getView<ImageView>(R.id.item_zfile_list_file_pic).run {
            ZFileTypeManage.getTypeManager().loadingFile(item.filePath, this)
            this
        }
        holder.getView<FrameLayout>(R.id.item_zfile_list_file_boxLayout).setOnClickListener {
            boxLayoutClick(position, item)
        }
        holder.itemView.setOnClickListener {
            itemClickByAnim?.invoke(pic, position, item)
        }
    }

    fun boxLayoutClick(position: Int, item: ZFileBean) {
        if (isManage) { // 管理状态
            boxClick(position, item)
            notifyItemChanged(position)
        } else { // 非管理状态
            isManage = !isManage
            notifyDataSetChanged()
            qwListener?.invoke(isManage, item, false)
        }
        changeListener?.invoke(isManage, selectData.size)
    }

    private fun boxClick(position: Int, item: ZFileBean) {
        val isSelect = boxMap[position] ?: false
        if (isSelect) {
            selectData.remove(item)
            boxMap[position] = !isSelect
            resetCountMapByClick(item, true)
            changeListener?.invoke(isManage, selectData.size)
            qwListener?.invoke(isManage, item, false)
        } else {
            val size = item.originaSize.toDouble() / 1048576 // byte -> MB
            if (size > config.maxSize.toDouble()) {
                context.toast(config.maxSizeStr)
                notifyItemChanged(position)
            } else {
                if (isQW) {
                    selectData.add(item)
                    boxMap[position] = !isSelect
                    changeListener?.invoke(isManage, selectData.size)
                    qwListener?.invoke(isManage, item, true)
                } else {
                    if (selectData.size >= config.maxLength) {
                        context.toast(config.maxLengthStr)
                        notifyItemChanged(position)
                    } else {
                        selectData.add(item)
                        boxMap[position] = !isSelect
                        resetCountMapByClick(item, false)
                        changeListener?.invoke(isManage, selectData.size)
                        qwListener?.invoke(isManage, item, true)
                    }
                }
            }
        }
    }

    private fun setFolderData(holder: ZFileViewHolder, item: ZFileBean, position: Int) {
        holder.apply {
            setText(R.id.item_zfile_list_folderNameTxt, item.fileName)
            setImageRes(R.id.item_zfile_list_folderPic, folderRes)
            setBgColor(R.id.item_zfile_list_folder_line, lineColor)
            setVisibility(R.id.item_zfile_list_folder_line, position < itemCount - 1)
            if (config.showSelectedCountHint) {
                val count = getCountByMap(item)
                setText(R.id.item_zfile_list_folderCountTxt, "$count")
                setVisibility(R.id.item_zfile_list_folderCountTxt, count > 0 && countMap.keys.indexOf(item.filePath))
            } else {
                setVisibility(R.id.item_zfile_list_folderCountTxt, false)
            }
        }
        holder.itemView.setOnClickListener {
            itemClickByAnim?.invoke(it, position, item)
        }
    }

    fun setQWLastState(bean: ZFileBean?) {
        var lastIndex = -1
        getDatas().indices.forEach forEach@{
            if (getItem(it) == bean) {
                lastIndex = it
                return@forEach
            }
        }
        if (lastIndex != -1) {
            selectData.remove(bean)
            boxMap[lastIndex] = false
            notifyItemChanged(lastIndex)
        }
    }

    fun reset() {
        selectData.clear()
        boxMap.clear()
        countMap.clear()
    }

    private fun clearSelectMap() {
        selectData.clear()
        for ((k, _) in boxMap) {
            boxMap[k] = false
        }
        notifyDataSetChanged()
    }

    private fun resetCountMapByClick(item: ZFileBean, remove: Boolean) {
        try {
            val value = countMap[item.parent] ?: 0
            if (remove) { // 移除
                countMap[item.parent] = value - 1
            } else { // 新增
                countMap[item.parent] = value + 1
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCountByMap(item: ZFileBean) : Int {
        var count = 0
        for ((k, v) in countMap) {
            if (k.indexOf(item.fileName) >= 0) {
                count += v
            }
        }
        return count
    }

    private fun clearCountMap() {
        countMap.clear()
    }
}