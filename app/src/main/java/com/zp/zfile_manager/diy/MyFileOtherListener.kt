package com.zp.zfile_manager.diy

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.zp.z_file.listener.ZFileOtherListener
import com.zp.zfile_manager.R
import java.io.File

class MyFileOtherListener : ZFileOtherListener() {

    /**
     * 获取 权限失败 时的 布局
     * 请注意：布局中必须包含控件 id：zfile_list_againBtn
     * 该id对应视图功能：用户点击后再次申请权限
     */
    override fun getPermissionFailedLayoutId(): Int {
        return R.layout.layout_diy_no_permission
    }

    /**
     * 获取 当前目录没有文件时（为空） 的布局
     */
    override fun getFileListEmptyLayoutId(): Int {
        return R.layout.layout_diy_empty
    }

    /**
     * 获取 查看图片 展示 View（LayoutParams 为 FrameLayout.LayoutParams）
     * @param context Context   Context
     * @param imgPath String    图片路径
     * @return View?     空表示使用默认值
     */
    override fun getImgInfoView(context: Context, imgPath: String): View? {
        // 方式一
        /*val imgView = ImageView(context).run {
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            this
        }
        Glide.with(context)
            .load(File(imgPath))
            .apply(RequestOptions().apply {
                placeholder(R.drawable.ic_zfile_other)
                error(R.drawable.ic_zfile_other)
            })
            .into(imgView)
        return imgView*/
        // 方式二
        val view = LayoutInflater.from(context).inflate(R.layout.layout_diy_img_info, null)
        val pic = view.findViewById<ImageView>(R.id.dii_pic)
        Glide.with(context)
            .load(File(imgPath))
            .apply(RequestOptions().apply {
                placeholder(R.drawable.ic_zfile_other)
                error(R.drawable.ic_zfile_other)
            })
            .into(pic)
        view.findViewById<View>(R.id.dii_btn).setOnClickListener { (context as? Activity)?.finish() }
        return view
    }
}