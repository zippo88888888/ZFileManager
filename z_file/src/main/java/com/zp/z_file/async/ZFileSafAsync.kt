package com.zp.z_file.async

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.zp.z_file.content.*
import com.zp.z_file.util.ZFileOtherUtil
import com.zp.z_file.util.ZFileSth
import com.zp.z_file.util.ZFileUtil

internal class ZFileSafAsync(
    context: Context,
    block: MutableList<ZFileBean>?.() -> Unit
) : ZFileAsync(context, block) {

    override fun doingWorkForSAF(documentFiles: Array<DocumentFile>?): MutableList<ZFileBean> {
        val list = arrayListOf<ZFileBean>()
        if (documentFiles.isNullOrEmpty()) return list
        for (d in documentFiles) {
            if (d.name.isNull() || d.name?.indexOf(".") == 0) { // 过滤掉 为空的及 以.开头的文件
                continue
            }
            val bean = ZFileBean(
                fileName = d.name!!,
                isFile = d.isFile,
                filePath = d.uri.toString().changeToPathBySAF(),
                date = ZFileOtherUtil.getFormatFileDate(d.lastModified()),
                originalDate = d.lastModified().toString(),
                size = ZFileOtherUtil.getFileSize(d.length()),
                originaSize = d.length(),
                folderLength = ZFileUtil.getFolderLength(d.uri.toString().changeToPathBySAF().toFile()),
                parent = ""
            )
            list.add(bean)
        }
        ZFileSth.sortord(list)
        return list
    }

    private fun String.changeToPathBySAF(): String {
        var path = this
        if (this has "${SAF_TREE_ROOT}primary%3AAndroid%2Fdata") {
            path = this.replace(
                "${SAF_TREE_ROOT}primary%3AAndroid%2Fdata/document/primary%3A", ""
            ).replace("%2F", "/")
        } else if (this has "${SAF_TREE_ROOT}primary%3AAndroid%2Fobb") {
            path = this.replace(
                "${SAF_TREE_ROOT}primary%3AAndroid%2Fobb/document/primary%3A", ""
            ).replace("%2F", "/")
        }
        path = Uri.decode(path)
        return "$SD_ROOT$path"
    }

}