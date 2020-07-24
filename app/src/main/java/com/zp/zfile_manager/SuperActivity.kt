package com.zp.zfile_manager

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zp.z_file.content.*
import com.zp.z_file.async.ZFileAsyncImpl
import kotlinx.android.synthetic.main.activity_super.*
import kotlin.collections.ArrayList

class SuperActivity : AppCompatActivity() {

    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_super)
        dialog = ProgressDialog(this).run {
            setMessage("获取中，请稍后...")
            this
        }

        super_picTxt.setOnClickListener {
            showDialog(arrayOf(PNG, JPEG, JPG, GIF))
        }

        super_videoTxt.setOnClickListener {
            showDialog(arrayOf(MP4, _3GP))
        }

        super_audioTxt.setOnClickListener {
            showDialog(arrayOf(MP3, AAC, WAV))
        }

        super_fileTxt.setOnClickListener {
            showDialog(arrayOf(TXT, JSON, XML))
        }

        super_wpsTxt.setOnClickListener {
            showDialog(arrayOf(DOC, XLS, PPT, PDF))
        }

        super_apkTxt.setOnClickListener {
            showDialog(arrayOf("apk"))
        }

        super_qqTxt.setOnClickListener {
            Toast.makeText(this, "即将支持", Toast.LENGTH_SHORT).show()
        }

        super_wechatTxt.setOnClickListener {
            Toast.makeText(this, "即将支持", Toast.LENGTH_SHORT).show()
        }

        super_otherTxt.setOnClickListener {
            Toast.makeText(this, "我只是一个占位格", Toast.LENGTH_SHORT).show()
        }

        super_innerTxt.setOnClickListener {
            getZFileHelp().setConfiguration(getZFileConfig().apply {
                needLongClick = false
                isOnlyFolder = true
                sortordBy = ZFileConfiguration.BY_NAME
                sortord = ZFileConfiguration.ASC
            }).start(this)
        }
    }

    private fun showDialog(filterArray: Array<String>) {
        dialog?.show()
        ZFileAsyncImpl(this) {
            dialog?.dismiss()
            // 这里考虑到传值大小限制，截取前100条数据
            if (it.isNullOrEmpty()) {
                Toast.makeText(this, "暂无数据", Toast.LENGTH_SHORT).show()
            } else {
                if (it.size > 100) {
                    SuperDialog.newInstance(changeList(it))
                        .show(supportFragmentManager, "SuperDialog")
                } else {
                    SuperDialog.newInstance(it as ArrayList<ZFileBean>)
                        .show(supportFragmentManager, "SuperDialog")
                }
            }
        }.start(filterArray)
    }

    private fun changeList(oldList: MutableList<ZFileBean>): ArrayList<ZFileBean> {
        val list = ArrayList<ZFileBean>()
        var index = 1
        oldList.forEach forEach@{
            if (index >= 100) {
                return@forEach
            }
            list.add(it)
            index++
        }
        return list
    }
}
