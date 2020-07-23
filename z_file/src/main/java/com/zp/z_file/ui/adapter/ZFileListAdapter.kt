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

internal class ZFileListAdapter(context: Context) : ZFileAdapter<ZFileBean>(context) {

    private var config = getZFileConfig()

    // box选中的数据
    private val boxMap by lazy {
        ArrayMap<Int, Boolean>()
    }

    // 已选中的数据
    var selectData = ArrayList<ZFileBean>()

    var isManage = false
        set(value) {
            if (!value) {
                selectData.clear()
                for ((k, _) in boxMap) {
                    boxMap[k] = false
                }
                notifyDataSetChanged()
            }
            field = value
        }

    var itemClickByAnim: ((View, Int, ZFileBean) -> Unit)? = null
    var changeListener: ((Boolean, Int) -> Unit)? = null

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
            setBgColor(R.id.item_zfile_list_file_line, config.resources.lineColor)
            setVisibility(R.id.item_zfile_list_file_line, position < itemCount - 1)
            setVisibility(R.id.item_zfile_file_box_pic, !isManage)
            if (config.boxStyle == ZFileConfiguration.STYLE1) {
                setVisibility(R.id.item_zfile_list_file_box1, isManage)
            } else {
                setVisibility(R.id.item_zfile_list_file_box2, isManage)
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
            if (isManage) { // 管理状态
                boxClick(position, item)
                notifyItemChanged(position)
            } else { // 非管理状态
                isManage = !isManage
                notifyDataSetChanged()
            }
            changeListener?.invoke(isManage, selectData.size)
        }
        holder.itemView.setOnClickListener {
            itemClickByAnim?.invoke(pic, position, item)
        }
    }

    private fun boxClick(position: Int, item: ZFileBean) {
        val isSelect = boxMap[position] ?: false
        if (isSelect) {
            selectData.remove(item)
            boxMap[position] = !isSelect
            changeListener?.invoke(isManage, selectData.size)
        } else {
            if (selectData.size >= config.maxLength) {
                context.toast(config.maxLengthStr)
                notifyItemChanged(position)
            } else {
                selectData.add(item)
                boxMap[position] = !isSelect
                changeListener?.invoke(isManage, selectData.size)
            }
        }
    }

    private fun setFolderData(holder: ZFileViewHolder, item: ZFileBean, position: Int) {
        holder.apply {
            setText(R.id.item_zfile_list_folderNameTxt, item.fileName)
            setImageRes(R.id.item_zfile_list_folderPic, config.resources.folderRes)
            setBgColor(R.id.item_zfile_list_folder_line, config.resources.lineColor)
            setVisibility(R.id.item_zfile_list_folder_line, position < itemCount - 1)
        }
        holder.itemView.setOnClickListener {
            itemClickByAnim?.invoke(it, position, item)
        }
    }
}