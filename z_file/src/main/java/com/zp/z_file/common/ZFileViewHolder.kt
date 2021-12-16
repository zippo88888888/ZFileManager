package com.zp.z_file.common

import android.util.SparseArray
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zp.z_file.content.getColorById
import java.io.File

@Suppress("UNCHECKED_CAST")
internal class ZFileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var array: SparseArray<View> = SparseArray()

    fun <V : View> getView(id: Int): V {
        var view: View? = array.get(id)
        if (view == null) {
            view = itemView.findViewById(id)
            array.put(id, view)
        }
        return view as V
    }

    fun setText(id: Int, msg: String) {
        val txtView = getView<TextView>(id)
        txtView.text = msg
    }

    fun setImageRes(id: Int, res: Int) {
        val pic = getView<ImageView>(id)
        pic.setImageResource(res)
    }

    fun setImage(id: Int, path: String) {
        val pic = getView<ImageView>(id)
        ZFileTypeManage.getTypeManager().loadingFile(path, pic)
    }

    fun setBgColor(id: Int, color: Int) {
        getView<View>(id).setBackgroundColor(itemView.context getColorById color)
    }

    fun setVisibility(id: Int, visibility: Int) {
        getView<View>(id).visibility = visibility
    }

    fun setVisibility(id: Int, isVisibility: Boolean) {
        if (isVisibility) setVisibility(id, View.VISIBLE) else setVisibility(id, View.GONE)
    }

    fun setChecked(id: Int, checked: Boolean?) {
        getView<CheckBox>(id).isChecked = checked ?: false
    }

    fun setSelected(id: Int, selected: Boolean?) {
        getView<View>(id).isSelected = selected ?: false
    }

    fun setOnViewClickListener(id: Int, listener: View.() -> Unit) {
        itemView.findViewById<View>(id)?.setOnClickListener {
            listener.invoke(it)
        }
    }

    fun setOnItemClickListener(listener: View.() -> Unit) {
        itemView.setOnClickListener {
            listener.invoke(it)
        }
    }

    fun setOnItemLongClickListener(listener: View.() -> Boolean) {
        itemView.setOnLongClickListener {
            listener.invoke(it)
        }
    }
}