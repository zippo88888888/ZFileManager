package com.zp.zfile_manager.diy

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.zp.z_file.listener.ZFileOpenListener

class MyFileOpenListener : ZFileOpenListener() {


    /**
     * 打开Txt
     * @param filePath String   文件路径
     * @param view View         RecyclerView itemView
     */
    override fun openTXT(filePath: String, view: View) {
        Toast.makeText(view.context, "自定义查看Txt，但是我懒就用内置的！", Toast.LENGTH_SHORT).show()
        super.openTXT(filePath, view)
    }

    /**
     * 打开视频
     * @param filePath String   文件路径
     * @param view View         RecyclerView itemView
     */
    override fun openVideo(filePath: String, view: View) {
        AlertDialog.Builder(view.context).apply {
            setTitle("自定义")
            setMessage("自定义打开视频！")
            setCancelable(false)
            setPositiveButton("打开") { dialog, _ ->
                super.openVideo(filePath, view)
                dialog.dismiss()
            }
            show()
        }

    }
}