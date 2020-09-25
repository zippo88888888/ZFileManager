package com.zp.zfile_manager.diy

import android.widget.ImageView
import com.zp.z_file.type.TxtType
import com.zp.zfile_manager.R

/**
 * 改变 txt 原本显示的图标
 */
class MyTxtType : TxtType() {

    override fun loadingFile(filePath: String, pic: ImageView) {
        pic.setImageResource(R.drawable.ic_my_txt)
    }

}