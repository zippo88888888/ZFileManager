package com.zp.z_file.ui.adapter

import android.content.Context
import com.zp.z_file.R
import com.zp.z_file.common.ZFileAdapter
import com.zp.z_file.common.ZFileViewHolder
import com.zp.z_file.content.*
import com.zp.z_file.content.FILE
import com.zp.z_file.content.FOLDER
import com.zp.z_file.content.folderRes
import com.zp.z_file.content.lineColor

internal class ZFileFolderAdapter(context: Context) : ZFileAdapter<ZFileBean>(context) {

    override fun getItemViewType(position: Int) = if (getItem(position).isFile) FILE else FOLDER

    override fun getLayoutID(viewType: Int) =
        when (viewType) {
            FILE -> R.layout.item_zfile_list_empty
            else -> R.layout.item_zfile_list_folder
        }

    override fun bindView(holder: ZFileViewHolder, item: ZFileBean, position: Int) {
        if (holder.itemViewType == FOLDER) {
            holder.apply {
                setText(R.id.item_zfile_list_folderNameTxt, item.fileName)
                setImageRes(R.id.item_zfile_list_folderPic, folderRes)
                setBgColor(R.id.item_zfile_list_folder_line, lineColor)
                setVisibility(R.id.item_zfile_list_folder_line, position < itemCount - 1)
            }
        }
    }

    override fun setDatas(list: MutableList<ZFileBean>?) {
        list?.add(ZFileBean(isFile = true))
        super.setDatas(list)
    }
}