package com.zp.z_file.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.zp.z_file.content.toUriNormalForSAF

internal object ZFilePermissionUtil {

    /** 读写SD卡权限  */
    const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

    const val WRITE_EXTERNAL_CODE = 0x1001

    /**
     * 检验 受保护的文件夹是否有访问权限
     * @param context Context
     * @param path String
     * @return Boolean
     */
    fun hasProtectedPermission(context: Context, path: String): Boolean {
        val newPath = path.toUriNormalForSAF()
        for (permission in context.contentResolver.persistedUriPermissions) {
            if (permission.isReadPermission && permission.uri.toString() == newPath) {
                return true
            }
        }
        return false
    }

    /**
     * 判断 小于 Android 11 或 有完全的文件管理权限
     */
    fun isRorESM() = Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()

    /**
     * 判断是否申请过权限
     * @param permissions   权限
     * @return true表示没有申请过
     */
    fun hasPermission(context: Context, vararg permissions: String) =
            permissions.any { ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED }

    /**
     * 请求权限
     * @param code  请求码
     * @param requestPermission 权限
     */
    fun requestPermission(fragmentOrActivity: Any, code: Int, vararg requestPermission: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (fragmentOrActivity) {
                is Activity -> ActivityCompat.requestPermissions(fragmentOrActivity, requestPermission, code)
                is Fragment -> fragmentOrActivity.requestPermissions(requestPermission, code)
            }

        }
    }

}