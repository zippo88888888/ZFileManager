package com.zp.z_file.ui.adapter

import android.content.Context
import com.zp.z_file.R
import com.zp.z_file.common.ZFileAdapter
import com.zp.z_file.common.ZFileViewHolder
import com.zp.z_file.content.ZFilePathBean

internal class ZFilePathAdapter(context: Context) :
    ZFileAdapter<ZFilePathBean>(context, R.layout.item_zfile_path) {

    override fun bindView(holder: ZFileViewHolder, item: ZFilePathBean, position: Int) {
        holder.setText(R.id.item_zfile_path_title, item.fileName)
    }

    override fun addItem(position: Int, t: ZFilePathBean) {
        super.addItem(position, t)
    }

    fun back() {
        remove(itemCount - 1, true)
    }
}