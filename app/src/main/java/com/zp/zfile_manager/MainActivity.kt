package com.zp.zfile_manager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zp.z_file.common.ZFileManageHelp
import com.zp.z_file.content.ZFileConfiguration
import com.zp.z_file.content.getZFileConfig
import com.zp.z_file.content.getZFileHelp
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_result_txt.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_defaultMangerBtn.setOnClickListener {
            getZFileHelp()
                .setConfiguration(getZFileConfig().apply {
                    boxStyle = ZFileConfiguration.STYLE2
                    maxLength = 6
                    maxLengthStr = "老铁最多6个文件"
                })
                .start(this)
        }
        main_fileMangerBtn.setOnClickListener {
            startActivity(Intent(this, SuperActivity::class.java))
        }
        main_fragmentBtn.setOnClickListener {
            startActivity(Intent(this, FragmentSampleActivity::class.java))
        }
        main_javaBtn.setOnClickListener {
            startActivity(Intent(this, JavaSampleActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val list = getZFileHelp().getSelectData(requestCode, resultCode, data)
        val sb = StringBuilder()
        list?.forEach {
            sb.append(it).append("\n\n")
        }
        main_resultTxt.text = sb.toString()

    }
}
