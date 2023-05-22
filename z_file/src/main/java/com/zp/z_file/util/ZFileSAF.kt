package com.zp.z_file.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import com.zp.z_file.content.*
import com.zp.z_file.ui.ZFileListFragment

/**
 * SAF 帮助类
 */
@SuppressLint("WrongConstant")
internal object ZFileSAF {

    private var sp: SharedPreferences? = null

    /**
     * 判断 受保护 的文件夹 是否有权限
     */
    fun hasProtectedPermission(context: Context, path: String): Boolean {
        val hasPermisson = ZFilePermissionUtil.hasProtectedPermission(context, path)
        var key = ""
        if (path has SAF_OBB_PATH) {
            key = SAF_OBB_PATH
        }
        if (path has SAF_DATA_PATH) {
            key = SAF_DATA_PATH
        }

        val localHas = getSP(context).getBoolean(key, false)
        return hasPermisson || localHas
    }

    /**
     * 跳转 SAF 唤起授权
     */
    fun toSAF(fragment: Fragment, path: String, code: Int = SAF_DATA_OBB_CODE) {
        val uriPath = path.changeToUriBySAF()
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

    /**
     * SAF 授权 回调
     */
    fun onActivityResult(
        fragment: Fragment,
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        path: String
    ): Boolean {
        try {
            if (requestCode == SAF_DATA_OBB_CODE && data?.data != null && resultCode == Activity.RESULT_OK) {
                val flag1 = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                fragment.requireActivity().contentResolver.takePersistableUriPermission(
                    data.data!!, data.flags and flag1
                )
                var key = ""
                if (path has SAF_OBB_PATH) {
                    key = SAF_OBB_PATH
                }
                if (path has SAF_DATA_PATH) {
                    key = SAF_DATA_PATH
                }
                getSP(fragment.requireContext()).edit().putBoolean(key, true).apply()
                return true
            } else {
                ZFileLog.e("用户已取消 SAF 授权")
                if (fragment is ZFileListFragment) {
                    fragment.onSAFResult(null, true)
                }
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * 根据 受保护的路径 获取 DocumentFile
     */
    fun getDocumentFilePath(context: Context, filePath: String): DocumentFile? {
        var path = filePath
        var path_pattern = SD_ROOT
        var rootUri = "${SAF_TREE_ROOT}primary%3A"
        if (path has "/Android/data") {
            path_pattern = SAF_DATA_PATH
            rootUri = "${SAF_TREE_ROOT}primary%3AAndroid%2Fdata"
        }
        if (path has "/Android/obb") {
            path_pattern = SAF_OBB_PATH
            rootUri = "${SAF_TREE_ROOT}primary%3AAndroid%2Fobb"
        }
        var document = DocumentFile.fromTreeUri(context, Uri.parse(rootUri))
        path = path.replace(path_pattern, "")
        val parts = path.split("/".toRegex()).toTypedArray()
        for (i in parts.indices) {
            if (parts[i] == "") continue
            val encodedPath = Uri.decode(parts[i])
            if (document == null) break
            document = document.findFile(encodedPath)
        }
        return document
    }

    fun String.changeToUriBySAF(): String {
        var path = this
        if (path.endsWith("/")) {
            path = path.substring(0, path.length - 1)
        }
        val path2 = path.replace(SD_ROOT, "").replace("/", "%2F")
        return "${SAF_TREE_ROOT}primary%3AAndroid%2Fdata/document/primary%3A$path2"
    }

    private fun getSP(context: Context): SharedPreferences {
        if (sp == null) {
            sp = context.getSharedPreferences("zfileSP", Context.MODE_PRIVATE)
        }
        return sp!!
    }

}