package com.zp.zfile_manager


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zp.z_file.content.ZFileBean
import com.zp.z_file.content.ZFileConfiguration
import com.zp.z_file.content.getZFileConfig
import com.zp.z_file.content.getZFileHelp
import com.zp.z_file.dsl.result
import com.zp.zfile_manager.content.Content
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.layout_result_txt.*

class BlankFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        framgent_startBtn.setOnClickListener {
            getZFileHelp().result(this) {
                setResultData(this)
            }
        }
        framgent_qqBtn.setOnClickListener {
            getZFileHelp().setConfiguration(getZFileConfig().apply {
                filePath = ZFileConfiguration.QQ
                authority = Content.AUTHORITY
            }).result(this) {
                setResultData(this)
            }
        }
        framgent_wechatBtn.setOnClickListener {
            getZFileHelp().setConfiguration(getZFileConfig().apply {
                filePath = ZFileConfiguration.WECHAT
                authority = Content.AUTHORITY
            }).result(this) {
                setResultData(this)
            }
        }
    }

    private fun setResultData(list: MutableList<ZFileBean>?) {
        val sb = StringBuilder()
        list?.forEach {
            sb.append(it).append("\n\n")
        }
        main_resultTxt.text = sb.toString()
    }

}
