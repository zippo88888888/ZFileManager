package com.zp.z_file.type

import android.view.View
import android.widget.ImageView
import com.zp.z_file.common.ZFileType
import com.zp.z_file.content.getZFileConfig
import com.zp.z_file.content.getZFileHelp

open class TxtType : ZFileType() {

    override fun openFile(filePath: String, view: View) {
        getZFileHelp().getOpenListener().openTXT(filePath, view)
    }

    override fun loadingFile(filePath: String, pic: ImageView) {
        val resId = getZFileConfig().resources.txtRes
        pic.setImageResource(resId)
    }

}