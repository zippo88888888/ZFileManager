package com.zp.zfile_manager.diy

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.zp.z_file.content.ZFileBean
import com.zp.z_file.listener.ZFileClickListener

class MyFileClickListener : ZFileClickListener() {

    private val path1 = "/storage/emulated/0/Android/data"
    private val path2 = "/storage/emulated/0/Android/obb"
    private val path3 = "/storage/emulated/0/DCIM"

    private fun click(fileBean: ZFileBean?, type: String) {
        Log.i("ZFileManager", "捕获【${type}】操作 <<<===>>> name：${fileBean?.fileName}")
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (fileBean.filePath == path1 || fileBean.filePath == path2) {
                AlertDialog.Builder(view.context).apply {
                    setTitle("温馨提示")
                    setMessage("非ROOT情况下：对于【Android/data】及【Android/obb】文件夹，在Android 11" +
                            "及以上版本无法进行任何操作！如需要处理【Android/data】及【Android/obb】文件夹里" +
                            "的内容，建议开发者自行通过StorageAccessFramework操作！")
                    setCancelable(false)
                    setPositiveButton("我知道了") { dialog, _ ->
                        dialog.dismiss()
                    }
                    show()
                }
            }
            if (fileBean.filePath == path3) {
                showPath3Dialog(view.context)
            }
        } else {
            if (fileBean.filePath == path3) {
                showPath3Dialog(view.context)
            }
        }
    }

    private fun showPath3Dialog(context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle("温馨提示")
            setMessage("你当前查看的目录为相机拍摄目录，请勿交头接耳！")
            setPositiveButton("偏不") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
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