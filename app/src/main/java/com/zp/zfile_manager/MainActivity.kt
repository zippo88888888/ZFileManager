package com.zp.zfile_manager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zp.z_file.common.ZFileManageHelp
import com.zp.z_file.content.ZFileBean
import com.zp.z_file.content.getZFileHelp
import com.zp.z_file.listener.ZFileSelectListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ZFileSelectListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getZFileHelp().setFileResultListener(this)
        main_defaultMangerBtn.setOnClickListener {
            getZFileHelp().start(this/*, "/storage/emulated/0/压缩包"*/)
        }

        main_fileMangerBtn.setOnClickListener {

        }
    }

    override fun onSelected(fileList: MutableList<ZFileBean>?) {
        val sb = StringBuilder()
        fileList?.forEach {
            sb.append(it).append("\n\n")
        }
        main_resultTxt.text = sb.toString()
    }
}
