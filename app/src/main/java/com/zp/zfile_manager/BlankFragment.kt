package com.zp.zfile_manager


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zp.z_file.content.ZFileBean
import com.zp.z_file.content.getZFileHelp
import com.zp.z_file.listener.ZFileSelectListener
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.layout_result_txt.*

class BlankFragment : Fragment(), ZFileSelectListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        framgent_startBtn.setOnClickListener {
            getZFileHelp().setFileResultListener(this).start(this)
        }
    }

    override fun onSelected(fileList: MutableList<ZFileBean>?) {
        val sb = StringBuilder()
        fileList?.forEach {
            sb.append(it).append("\n\n")
        }
        main_resultTxt.text = sb.toString()
    }

}
