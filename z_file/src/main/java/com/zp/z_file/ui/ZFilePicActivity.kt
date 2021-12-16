package com.zp.z_file.ui

import android.os.Bundle
import com.zp.z_file.R
import com.zp.z_file.common.ZFileActivity
import com.zp.z_file.common.ZFileTypeManage
import com.zp.z_file.content.setStatusBarTransparent
import kotlinx.android.synthetic.main.activity_zfile_pic.*

internal class ZFilePicActivity : ZFileActivity() {

    override fun getContentView() = R.layout.activity_zfile_pic

    override fun init(savedInstanceState: Bundle?) {
        setStatusBarTransparent()
        val filePath = intent.getStringExtra("picFilePath") ?: ""
        ZFileTypeManage.getTypeManager().loadingFile(filePath, zfile_pic_show)
        zfile_pic_show.setOnClickListener { onBackPressed() }
    }
}
