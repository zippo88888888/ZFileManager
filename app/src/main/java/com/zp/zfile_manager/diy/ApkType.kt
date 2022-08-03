package com.zp.zfile_manager.diy

import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.zp.z_file.common.ZFileType
import com.zp.zfile_manager.R


/**
 * 自定义Apk文件类型
 */
class ApkType : ZFileType() {

    /**
     * 打开文件
     * @param filePath  文件路径
     * @param view      当前视图
     */
    override fun openFile(filePath: String, view: View) {
        Toast.makeText(view.context, "打开自定义Apk文件类型", Toast.LENGTH_SHORT).show()
    }

    /**
     * 加载文件
     * @param filePath 文件路径
     * @param pic      文件展示的图片
     */
    override fun loadingFile(filePath: String, pic: ImageView) {
        pic.setImageResource(R.mipmap.ic_launcher)
    }

}