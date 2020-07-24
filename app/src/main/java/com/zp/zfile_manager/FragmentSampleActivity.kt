package com.zp.zfile_manager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class FragmentSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_sample)
        supportFragmentManager.beginTransaction()
            .add(R.id.framgent_s_layout, BlankFragment(), "BlankFragment")
            .commit()
    }
}
