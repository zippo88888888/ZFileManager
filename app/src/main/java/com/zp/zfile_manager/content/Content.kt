package com.zp.zfile_manager.content

import com.zp.z_file.content.ZFileConfiguration

object Content {

    const val APK = "apk"

    const val AUTHORITY = "com.zp.zfile_manager.ZFileManagerProvider"

    val CONFIG: ZFileConfiguration
        get() = ZFileConfiguration().apply {
            authority = AUTHORITY
        }

}