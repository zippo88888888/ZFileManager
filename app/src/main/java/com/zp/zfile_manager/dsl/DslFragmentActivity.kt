package com.zp.zfile_manager.dsl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zp.zfile_manager.R

class DslFragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dsl_fragment)
        supportFragmentManager.beginTransaction()
            .add(R.id.dsl_fragmentLayout, DslFragment(), "DslFragment")
            .commit()

    }
}