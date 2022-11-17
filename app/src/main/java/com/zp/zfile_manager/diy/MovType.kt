package com.zp.zfile_manager.diy

import android.view.View
import android.widget.Toast
import com.zp.z_file.type.ZFileVideoType

class MovType : ZFileVideoType() {

    override fun openFile(filePath: String, view: View) {
        Toast.makeText(view.context, "正在为您打开文件中...", Toast.LENGTH_SHORT).show()
        super.openFile(filePath, view)
    }
}