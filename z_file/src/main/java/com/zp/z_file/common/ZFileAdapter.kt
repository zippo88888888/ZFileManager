package com.zp.z_file.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

internal abstract class ZFileAdapter<T>(protected var context: Context) : RecyclerView.Adapter<ZFileViewHolder>() {

    constructor(context: Context, layoutID: Int) : this(context) {
        this.layoutID = layoutID
    }

    var itemClick: ((View, Int, T) -> Unit)? = null
    var itemLongClick: ((View, Int, T) -> Boolean)? = null

    private var layoutID = -1
    private var datas: MutableList<T> = ArrayList()

    open fun getDatas() = datas

    open fun setDatas(list: MutableList<T>?) {
        clear()
        if (!list.isNullOrEmpty()) {
            if (datas.addAll(list)) {
                notifyDataSetChanged()
            }
        }

    }

    open fun addAll(list: MutableList<T>) {
        val oldSize = itemCount
        if (datas.addAll(list)) {
            notifyItemRangeChanged(oldSize, list.size)
        }
    }

    open fun addItem(position: Int, t: T) {
        datas.add(position, t)
        notifyItemInserted(position)
    }

    open fun setItem(position: Int, t: T) {
        if (itemCount > 0) {
            datas[position] = t
            notifyItemChanged(position)
        }
    }

    open fun remove(position: Int, changeDataNow: Boolean = true) {
        if (itemCount > 0) {
            datas.removeAt(position)
            if (changeDataNow) {
                notifyItemRangeRemoved(position, 1)
            }
        }
    }

    open fun clear(changeDataNow: Boolean = true) {
        datas.clear()
        if (changeDataNow) {
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZFileViewHolder {
        val layoutRes = getLayoutID(viewType)
        if (layoutRes > 0) {
            val view = LayoutInflater.from(context).inflate(layoutRes, parent, false)
            return ZFileViewHolder(view)
        } else {
            throw NullPointerException("adapter layoutId is not null")
        }
    }

    override fun onBindViewHolder(holder: ZFileViewHolder, position: Int) {
        holder.setOnItemClickListener {
            itemClick?.invoke(this, position, getItem(position))
        }
        holder.setOnItemLongClickListener {
            itemLongClick?.invoke(this, position, getItem(position)) ?: true
        }
        bindView(holder, getItem(position), position)
    }

    override fun getItemCount() = datas.size

    fun getItem(position: Int) = datas[position]

    open fun getLayoutID(viewType: Int) = layoutID

    protected abstract fun bindView(holder: ZFileViewHolder, item: T, position: Int)

}