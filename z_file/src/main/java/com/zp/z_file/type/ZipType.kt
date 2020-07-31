package com.zp.z_file.type

import android.view.View
import android.widget.ImageView
import com.zp.z_file.common.ZFileType
import com.zp.z_file.content.getZFileConfig
import com.zp.z_file.content.getZFileHelp

open class ZipType : ZFileType() {

    override fun openFile(filePath: String, view: View) {
        getZFileHelp().getFileOpenListener().openZIP(filePath, view)
    }

    override fun loadingFile(filePath: String, pic: ImageView) {
        val resId = getZFileConfig().resources.zipRes
        pic.setImageResource(resId)
    }
}