package com.zp.zfile_manager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zp.z_file.content.ZFileBean
import com.zp.z_file.content.ZFileConfiguration
import com.zp.z_file.content.getZFileConfig
import com.zp.z_file.dsl.config
import com.zp.z_file.dsl.result
import com.zp.z_file.dsl.zfile
import com.zp.zfile_manager.content.Content
import com.zp.zfile_manager.databinding.ActivityMainBinding
import com.zp.zfile_manager.dsl.DslActivity
import com.zp.zfile_manager.fm.FragmentSampleActivity2
import com.zp.zfile_manager.super_.SuperActivity

class MainActivity : AppCompatActivity() {

    private lateinit var vb: ActivityMainBinding

    private var rbId = R.id.main_rb_af

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vb.root)
        vb.mainDefaultMangerBtn.setOnClickListener {
            zfile {
                config {
                    getZFileConfig().apply {
                        boxStyle = ZFileConfiguration.STYLE2
                        maxLength = 6
                        titleGravity = ZFileConfiguration.TITLE_CENTER
                        maxLengthStr = "老铁最多6个文件"
                        authority = Content.AUTHORITY
                    }
                }
                result { setFileListData(this) }
            }
        }
        vb.mainFileMangerBtn.setOnClickListener {
            callPermission()
        }
        vb.mainFragmentBtn2.setOnClickListener {
            when (rbId) {
                R.id.main_rb_af -> {
                    FragmentSampleActivity2.jump(this, 1)
                }
                R.id.main_rb_vpf -> {
                    FragmentSampleActivity2.jump(this, 2)
                }
                R.id.main_rb_ff -> {
                    FragmentSampleActivity2.jump(this, 3)
                }
            }
        }
        vb.mainRg.setOnCheckedChangeListener { _, checkedId ->
            rbId = checkedId
        }
        vb.mainJavaBtn.setOnClickListener {
            startActivity(Intent(this, JavaSampleActivity::class.java))
        }
        vb.mainDslMangerBtn.setOnClickListener {
            startActivity(Intent(this, DslActivity::class.java))
        }
    }

    private fun setFileListData(fileList: MutableList<ZFileBean>?) {
        val sb = StringBuilder()
        fileList?.forEach {
            sb.append(it).append("\n\n")
        }
        vb.mainInclude.mainResultTxt.text = sb.toString()
    }

    private fun jump() {
        startActivity(Intent(this, SuperActivity::class.java))
    }

    private var toManagerPermissionPage = false

    override fun onResume() {
        super.onResume()
        if (toManagerPermissionPage) {
            toManagerPermissionPage = false
            callPermission()
        }
    }

    private fun callPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) checkHasPermission() else jump()
        } else {
            val builder = AlertDialog.Builder(this)
                .setTitle(com.zp.z_file.R.string.zfile_11_title)
                .setMessage(com.zp.z_file.R.string.zfile_11_content)
                .setCancelable(false)
                .setPositiveButton(com.zp.z_file.R.string.zfile_down) { d, _ ->
                    toManagerPermissionPage = true
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    startActivity(intent)
                    d.dismiss()
                }
                .setNegativeButton(com.zp.z_file.R.string.zfile_cancel) { d, _ ->
                    d.dismiss()
                }
            builder.show()
        }
    }

    private fun checkHasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val hasPermission = hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (hasPermission) {
                requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                jump()
            }
        } else {
            jump()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) jump()
            else {
                Toast.makeText(this, "权限申请失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hasPermission(vararg permissions: String) =
        permissions.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
    private fun requestPermission(vararg requestPermission: String) =
        ActivityCompat.requestPermissions(this, requestPermission, 100)

}
