package com.zp.zfile_manager.dsl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zp.z_file.content.getZFileHelp
import com.zp.zfile_manager.R
import com.zp.zfile_manager.diy.MyFileTypeListener

class DslFragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dsl_fragment)
        supportFragmentManager.beginTransaction()
            .add(R.id.dsl_fragmentLayout, DslFragment(), "DslFragment")
            .commit()

    }

    override fun onDestroy() {
        super.onDestroy()
        // 该页面演示结束后保证其他页面不受影响
        getZFileHelp().setFileTypeListener(MyFileTypeListener())
    }
}