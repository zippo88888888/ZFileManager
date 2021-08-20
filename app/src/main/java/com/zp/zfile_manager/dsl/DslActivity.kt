package com.zp.zfile_manager.dsl

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.zp.z_file.async.ZFileAsync
import com.zp.z_file.common.ZFileManageHelp
import com.zp.z_file.content.ZFileBean
import com.zp.z_file.content.ZFileConfiguration
import com.zp.z_file.content.getZFileHelp
import com.zp.z_file.dsl.config
import com.zp.z_file.dsl.fileType
import com.zp.z_file.dsl.result
import com.zp.z_file.dsl.zfile
import com.zp.zfile_manager.R
import com.zp.zfile_manager.content.Content
import com.zp.zfile_manager.diy.MyFileTypeListener
import kotlinx.android.synthetic.main.activity_dsl.*
import kotlinx.android.synthetic.main.layout_result_txt.*

class DslActivity : AppCompatActivity() {

    private var anim: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dsl)
        anim = getAnim()
        anim?.start()
        dsl_startBtn.setOnClickListener {
            dsl()
        }
        dsl_fragmentBtn.setOnClickListener {
            startActivity(Intent(this, DslFragmentActivity::class.java))
        }
    }

    private fun dsl() {
        zfile {
            config {
                Content.CONFIG
            }
            result {
                setResultData(this)
            }
        }
    }

    private fun setResultData(selectList: MutableList<ZFileBean>?) {
        val sb = StringBuilder()
        selectList?.forEach {
            sb.append(it).append("\n\n")
        }
        main_resultTxt.text = sb.toString()
    }

    override fun onDestroy() {
        anim?.apply {
            end()
            cancel()
            removeAllListeners()
            removeAllUpdateListeners()
        }
        anim = null
        super.onDestroy()
    }

    private fun getAnim() = ObjectAnimator.ofFloat(dsl_dslTxt, "rotation", 0f, 360f).run {
        duration = 5000L
        repeatCount = -1
        interpolator = LinearInterpolator()
        this
    }
}