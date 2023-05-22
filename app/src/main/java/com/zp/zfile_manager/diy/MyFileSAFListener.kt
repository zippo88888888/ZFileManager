package com.zp.zfile_manager.diy

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.zp.z_file.listener.ZFileSAFListener
import com.zp.zfile_manager.R

class MyFileSAFListener : ZFileSAFListener() {

    /**
    * 获取 /storage/emulated/0/Android/data及obb目录展示的展位图布局（Android 11及以上版本无法查看，但可通过SAF访问）
    * 请注意：布局中必须包含控件 id：zfile_do_btn
    * 该id对应视图功能：用户点击后跳转SAF
    */
    override fun getDataAndObbFoldLayoutId(): Int {
        return R.layout.layout_diy_do
    }

    /**
     * 跳转到 SAF 授权页面
     * @param fragment Fragment     Fragment
     * @param path String           Android/data or Android/obb
     * @param code Int              请求code
     */
    override fun openSAF(fragment: Fragment, path: String, code: Int) {
        if (hasProtectedPermission(fragment.requireContext(), path)) {
            super.openSAF(fragment, path, code)
        } else {
            AlertDialog.Builder(fragment.requireContext()).apply {
                setTitle("温馨提示")
                setMessage("请在SAF授权页面直接点击底部【使用此文件夹】即可授权！")
                setCancelable(false)
                setPositiveButton("我已知晓") { dialog, _ ->
                    toSAF(fragment, path, code)
                    dialog.dismiss()
                }
                show()
            }
        }
    }

    private fun toSAF(fragment: Fragment, path: String, code: Int) {
        var newPath = path
        if (newPath.endsWith("/")) {
            newPath = newPath.substring(0, newPath.length - 1)
        }
        val path2 = newPath.replace("/storage/emulated/0/", "").replace("/", "%2F")
        val uriPath = "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A$path2"
        val uri = Uri.parse(uriPath)
        val intent = Intent("android.intent.action.OPEN_DOCUMENT_TREE")
        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
        }
        fragment.startActivityForResult(intent, code)
    }
}