package com.zp.zfile_manager.dsl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zp.z_file.content.ZFileConfiguration
import com.zp.z_file.dsl.config
import com.zp.z_file.dsl.fileType
import com.zp.z_file.dsl.result
import com.zp.z_file.dsl.zfile
import com.zp.zfile_manager.content.Content
import com.zp.zfile_manager.databinding.FragmentDslBinding

class DslFragment : Fragment() {

    private var vb: FragmentDslBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vb = FragmentDslBinding.inflate(inflater, container, false)
        return vb?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vb?.dslFragmentStartBtn?.setOnClickListener {
            dsl()
        }
    }

    override fun onDestroyView() {
        vb = null
        super.onDestroyView()
    }

    private fun dsl() {
        zfile {
            config {
                Content.CONFIG.run {
                    filePath = ZFileConfiguration.WECHAT
                    this
                }
            }
            fileType {
                MyDslFileTypeListener()
            }
            result {
                val sb = StringBuilder()
                this?.forEach {
                    sb.append(it).append("\n\n")
                }
                vb?.dslInclude2?.mainResultTxt?.text = sb.toString()
            }
        }
    }

}