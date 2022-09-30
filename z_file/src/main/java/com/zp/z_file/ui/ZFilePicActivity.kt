package com.zp.z_file.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.zp.z_file.R
import com.zp.z_file.common.ZFileActivity
import com.zp.z_file.common.ZFileTypeManage
import com.zp.z_file.content.inflate
import com.zp.z_file.content.setStatusBarTransparent
import com.zp.z_file.databinding.ActivityZfilePicBinding

internal class ZFilePicActivity : ZFileActivity() {

    private val vb by inflate<ActivityZfilePicBinding>()

    override fun getContentView() = R.layout.activity_zfile_pic

    override fun create() = Unit

    override fun init(savedInstanceState: Bundle?) {
        setStatusBarTransparent()
        val filePath = intent.getStringExtra("picFilePath") ?: ""
        ZFileTypeManage.getTypeManager().loadingFile(filePath, vb.zfilePicShow)
        vb.zfilePicShow.setOnClickListener { onBackPressed() }
    }

    companion object {

        fun show(context: Context, picFilePath: String) {
            context.startActivity(Intent(context, ZFilePicActivity::class.java).apply {
                putExtra("picFilePath", picFilePath)
            })
        }

    }
}
