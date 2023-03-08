package com.zp.zfile_manager.diy

import android.util.Log
import android.view.View
import android.widget.Toast
import com.zp.z_file.content.ZFileBean
import com.zp.z_file.listener.ZFileClickListener

class MyFileClickListener : ZFileClickListener() {

    private fun click(fileBean: ZFileBean?, type: String) {
        Log.d("ZFileManager", "捕获【${type}】操作 <<<===>>> name：${fileBean?.fileName}")
    }

    /**
     * 文件 点击
     * @param fileBean ZFileBean    文件实体
     * @param view View             RecyclerView itemView
     */
    override fun itemFileClick(fileBean: ZFileBean, view: View) {
        click(fileBean, "文件点击 itemFileClick")
    }

    /**
     * 文件夹 点击
     * @param fileBean ZFileBean    文件实体
     * @param view View             RecyclerView itemView
     */
    override fun itemFoldClick(fileBean: ZFileBean, view: View) {
        click(fileBean,  "文件夹点击 itemFoldClick")
        Toast.makeText(view.context, "捕获【文件夹点击】itemFoldClick", Toast.LENGTH_SHORT).show()
    }

    /**
     * 未选中数据时Toolbar完成 点击
     */
    override fun emptyDataDownClick() {
        click(null,  "未选中数据时Toolbar完成点击 emptyDataDownClick")
    }

    /**
     * 重新申请 权限 按钮 点击
     * @param view View             Button
     */
    override fun permissionBtnApplyClick(view: View) {
        click(null,  "重新申请权限按钮点击 permissionBtnApplyClick")
    }
}