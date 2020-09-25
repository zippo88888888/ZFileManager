package com.zp.zfile_manager

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.zp.z_file.async.ZFileAsyncImpl
import com.zp.z_file.content.*
import kotlinx.android.synthetic.main.activity_super.*

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
            toQW(ZFileConfiguration.QQ)
        }

        super_wechatTxt.setOnClickListener {
            toQW(ZFileConfiguration.WECHAT)
        }

        super_otherTxt.setOnClickListener {
            Toast.makeText(this, "我只是一个占位格，好看的", Toast.LENGTH_SHORT).show()
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

    private fun toQW(path: String) {
        Log.e("ZFileManager", "请注意：QQ、微信目前只能获取用户手动保存到手机里面的文件，" +
                "且保存文件到手机的目录用户没有修改")
        Log.i("ZFileManager", "参考自腾讯自己的\"腾讯文件\"App，能力有限，部分文件无法获取")
        jump(path)
    }

    private fun jump(path: String) {
        getZFileHelp().setConfiguration(getZFileConfig().apply {
            boxStyle = ZFileConfiguration.STYLE2
            filePath = path
        }).start(this@SuperActivity)
    }

    private fun showDialog(filterArray: Array<String>) {
        dialog?.show()
        ZFileAsyncImpl(this) {
            dialog?.dismiss()
            if (isNullOrEmpty()) {
                Toast.makeText(this@SuperActivity, "暂无数据", Toast.LENGTH_SHORT).show()
            } else {
                if (this!!.size > 100) {
                    Log.e("ZFileManager", "这里考虑到传值大小限制，截取前100条数据")
                    SuperDialog.newInstance(changeList(this!!))
                            .show(supportFragmentManager, "SuperDialog")
                } else {
                    SuperDialog.newInstance(this as ArrayList<ZFileBean>)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val list = getZFileHelp().getSelectData(requestCode, resultCode, data)
        val sb = StringBuilder()
        list?.forEach {
            sb.append(it).append("\n\n")
        }
        super_resultTxt.text = sb.toString()
    }

    private fun reset() {
        // 这里重置，防止该页面销毁后其他演示页面无法正常获取数据！
        getZFileConfig().apply {
            needLongClick = true
            isOnlyFolder = false
            sortordBy = ZFileConfiguration.BY_DEFAULT
            sortord = ZFileConfiguration.ASC
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        reset()
    }
}
