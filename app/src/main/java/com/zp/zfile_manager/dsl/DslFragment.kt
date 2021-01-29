package com.zp.zfile_manager.dsl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zp.z_file.content.ZFileConfiguration
import com.zp.z_file.dsl.config
import com.zp.z_file.dsl.result
import com.zp.z_file.dsl.zfile
import com.zp.zfile_manager.R
import kotlinx.android.synthetic.main.fragment_dsl.*
import kotlinx.android.synthetic.main.layout_result_txt.*

class DslFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dsl, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dsl_fragmentStartBtn.setOnClickListener {
            dsl()
        }
    }

    private fun dsl() {
        zfile {
            config {
                ZFileConfiguration().apply {
                    filePath = ZFileConfiguration.WECHAT
                }
            }
            result {
                val sb = StringBuilder()
                this?.forEach {
                    sb.append(it).append("\n\n")
                }
                main_resultTxt.text = sb.toString()
            }
        }
    }

}