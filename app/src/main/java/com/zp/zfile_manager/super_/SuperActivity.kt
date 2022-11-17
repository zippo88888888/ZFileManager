package com.zp.zfile_manager.super_

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zp.z_file.async.ZFileStipulateAsync
import com.zp.z_file.content.*
import com.zp.z_file.dsl.config
import com.zp.z_file.dsl.result
import com.zp.z_file.dsl.zfile
import com.zp.zfile_manager.JavaSampleActivity
import com.zp.zfile_manager.R
import com.zp.zfile_manager.content.Content
import com.zp.zfile_manager.content.Content.FILTER
import com.zp.zfile_manager.content.Content.QQ_MAP
import com.zp.zfile_manager.content.Content.TITLES
import com.zp.zfile_manager.databinding.ActivitySuperBinding
import com.zp.zfile_manager.diy.MyQWFileListener
import com.zp.zfile_manager.diy.SunActivity

class SuperActivity : AppCompatActivity() {

    private lateinit var vb: ActivitySuperBinding
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivitySuperBinding.inflate(layoutInflater)
        setContentView(vb.root)
        dialog = ProgressDialog(this).run {
            setMessage("获取中，请稍后...")
            setCancelable(false)
            this
        }

        vb.superPicTxt.setOnClickListener {
            showDialog(arrayOf(PNG, JPEG, JPG, GIF))
        }

        vb.superVideoTxt.setOnClickListener {
            showDialog(arrayOf(MP4, _3GP))
        }

        vb.superAudioTxt.setOnClickListener {
            showDialog(arrayOf(MP3, AAC, WAV, M4A))
        }

        vb.superFileTxt.setOnClickListener {
            showDialog(arrayOf(TXT, JSON, XML))
        }

        vb.superWpsTxt.setOnClickListener {
            showDialog(arrayOf(DOC, DOCX, XLS, XLSX, PPT, PPTX, PDF))
        }

        vb.superApkTxt.setOnClickListener {
            showDialog(arrayOf("apk"))
        }

        vb.superQqTxt.setOnClickListener {
            toQW(ZFileConfiguration.QQ)
        }

        vb.superWechatTxt.setOnClickListener {
            toQW(ZFileConfiguration.WECHAT)
        }

        vb.superOtherTxt.setOnClickListener {
            SunActivity.jump(this)
        }

        vb.superGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.super_diyRadio -> {
                    getZFileHelp().setQWFileLoadListener(MyQWFileListener())
                }
                else -> {
                    getZFileHelp().setQWFileLoadListener(null)
                }
            }
        }

        vb.superInnerTxt.setOnClickListener {
            zfile {
                config {
                    getZFileConfig().apply {
                        needLongClick = false
                        clickAndAutoSelected = true
                        titleGravity = ZFileConfiguration.TITLE_CENTER
                        sortordBy = ZFileConfiguration.BY_NAME
                        sortord = ZFileConfiguration.ASC
                    }
                }
                result { setResultData(this) }
            }
        }
        vb.superJavaTxt.setOnClickListener {
            startActivity(Intent(this, JavaSampleActivity::class.java))
        }
    }

    private fun toQW(path: String) {
        Log.e("ZFileManager", "请注意：QQ、微信目前只能获取用户手动保存到手机里面的文件，" +
                "且保存文件到手机的目录用户没有修改")
        Log.i("ZFileManager", "参考自腾讯自己的\"腾讯文件\"App，部分文件无法获取")
        jump(path)
    }

    private fun jump(path: String) {
        zfile {
            config {
                getZFileConfig().apply {
                    boxStyle = ZFileConfiguration.STYLE2
                    filePath = path
                    authority = Content.AUTHORITY
                    if (path == ZFileConfiguration.QQ) { // 打开QQ时，简单配置
                        qwData = ZFileQWData().apply {
                            titles = TITLES
                            filterArrayMap = FILTER
                            qqFilePathArrayMap = QQ_MAP
                        }
                    } else {
                        qwData = ZFileQWData()
                    }
                }
            }
            result {
                setResultData(this)
            }
        }
    }

    private fun showDialog(filterArray: Array<String>) {
        dialog?.show()
        ZFileStipulateAsync(this) {
            dialog?.dismiss()
            if (isNullOrEmpty()) {
                Toast.makeText(this@SuperActivity, "暂无数据", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@SuperActivity, "共找到${this?.size}条数据", Toast.LENGTH_SHORT).show()
                Log.i("ZFileManager", "共找到${this?.size}条数据")
                if (this!!.size > 100) {
                    Log.e("ZFileManager", "这里考虑到传值大小限制，截取前100条数据")
                    SuperDialog.newInstance(changeList(this))
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

    private fun setResultData(list: MutableList<ZFileBean>?) {
        val sb = StringBuilder()
        list?.forEach {
            sb.append(it).append("\n\n")
        }
        vb.superResultTxt.text = sb.toString()
    }

}
