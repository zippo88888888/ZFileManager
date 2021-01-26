package com.zp.zfile_manager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zp.z_file.content.ZFileConfiguration
import com.zp.z_file.content.getZFileConfig
import com.zp.z_file.content.getZFileHelp
import com.zp.zfile_manager.content.Content
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_result_txt.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_defaultMangerBtn.setOnClickListener {
            getZFileHelp()
                .setConfiguration(getZFileConfig().apply {
                    boxStyle = ZFileConfiguration.STYLE2
                    maxLength = 6
                    maxLengthStr = "老铁最多6个文件"
                    authority = Content.AUTHORITY
                })
                .start(this)
        }
        main_fileMangerBtn.setOnClickListener {
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
        main_fragmentBtn.setOnClickListener {
            startActivity(Intent(this, FragmentSampleActivity::class.java))
        }
        main_javaBtn.setOnClickListener {
            startActivity(Intent(this, JavaSampleActivity::class.java))
        }
    }

    private fun jump() {
        startActivity(Intent(this, SuperActivity::class.java))
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
