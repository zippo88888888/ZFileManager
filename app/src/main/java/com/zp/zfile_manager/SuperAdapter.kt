package com.zp.zfile_manager

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zp.z_file.content.ZFileBean

class SuperAdapter(private var datas: MutableList<ZFileBean>) : RecyclerView.Adapter<SuperAdapter.SuperViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dialog_super, parent, false)
        return SuperViewHolder(view)
    }

    override fun getItemCount() = datas.size

    private fun getItem(position: Int) = datas[position]

    override fun onBindViewHolder(holder: SuperViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            Log.i("ZFileManager", "已选中${getItem(position).fileName}")
        }
        holder.setData(getItem(position))
    }

    class SuperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val nameTxt = itemView.findViewById<TextView>(R.id.item_super_nameTxt)
        private val dateTxt = itemView.findViewById<TextView>(R.id.item_super_dateTxt)
        private val sizeTxt = itemView.findViewById<TextView>(R.id.item_super_sizeTxt)

        fun setData(item: ZFileBean) {
            nameTxt.text = item.fileName
            dateTxt.text = item.date
            sizeTxt.text = item.size
        }
    }

}