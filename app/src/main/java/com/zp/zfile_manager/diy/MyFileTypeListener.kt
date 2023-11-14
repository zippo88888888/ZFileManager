package com.zp.zfile_manager.diy

import com.zp.z_file.content.JSON
import com.zp.z_file.content.TXT
import com.zp.z_file.content.XML
import com.zp.z_file.listener.ZFileTypeListener
import com.zp.z_file.util.ZFileHelp
import com.zp.zfile_manager.content.Content

class MyFileTypeListener : ZFileTypeListener() {

    override fun getFileType(filePath: String) =
        when (ZFileHelp.getFileTypeBySuffix(filePath)) {
            TXT, XML, JSON, "html" -> MyTxtType()
            Content.APK -> ApkType()
            Content.MOV, "webm" -> MovType()
            else -> super.getFileType(filePath)
        }
}