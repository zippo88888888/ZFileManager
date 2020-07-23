package com.zp.z_file.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

internal abstract class ZFileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())
        init(savedInstanceState)
    }

    abstract fun getContentView(): Int
    abstract fun init(savedInstanceState: Bundle?)


}