package com.zp.zfile_manager.dsl

import com.zp.z_file.content.JSON
import com.zp.z_file.content.TXT
import com.zp.z_file.content.XML
import com.zp.z_file.listener.ZFileTypeListener
import com.zp.z_file.type.OtherType
import com.zp.z_file.util.ZFileHelp
import com.zp.zfile_manager.content.Content
import com.zp.zfile_manager.diy.ApkType

class MyDslFileTypeListener : ZFileTypeListener() {

    override fun getFileType(filePath: String) =
        when (ZFileHelp.getFileTypeBySuffix(filePath)) {
            TXT, XML, JSON -> OtherType()
            Content.APK -> ApkType()
            else -> super.getFileType(filePath)
        }
}
