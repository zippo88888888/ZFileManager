package com.zp.z_file.ui

import android.os.Bundle
import com.zp.z_file.R
import com.zp.z_file.common.ZFileActivity
import com.zp.z_file.content.getZFileHelp
import com.zp.z_file.content.setStatusBarTransparent
import com.zp.z_file.content.toFile
import kotlinx.android.synthetic.main.activity_zfile_pic.*

internal class ZFilePicActivity : ZFileActivity() {

    override fun getContentView() = R.layout.activity_zfile_pic

    override fun init(savedInstanceState: Bundle?) {
        setStatusBarTransparent()
        val filePath = intent.getStringExtra("picFilePath") ?: ""
        getZFileHelp().getImageLoadListener().loadImage(zfile_pic_show, filePath.toFile())
        zfile_pic_show.setOnClickListener { onBackPressed() }
    }
}
