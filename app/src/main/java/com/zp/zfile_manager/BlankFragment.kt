package com.zp.zfile_manager


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zp.z_file.common.ZFileManageHelp
import com.zp.z_file.content.ZFileConfiguration
import com.zp.z_file.content.getZFileConfig
import com.zp.z_file.content.getZFileHelp
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
            getZFileHelp().start(this)
        }
        framgent_wechatBtn.setOnClickListener {
            getZFileHelp().setConfiguration(getZFileConfig().apply {
                filePath = ZFileConfiguration.WECHAT
            }).start(this)
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
