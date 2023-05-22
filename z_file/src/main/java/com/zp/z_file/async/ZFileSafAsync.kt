package com.zp.z_file.async

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.zp.z_file.content.*
import com.zp.z_file.util.ZFileOtherUtil
import java.util.*

open class ZFileSafAsync(
    context: Context,
    block: MutableList<ZFileBean>?.() -> Unit
) : ZFileAsync(context, block) {

    override fun doingWorkForSAF(documentFiles: Array<DocumentFile>?): MutableList<ZFileBean> {

        val list = arrayListOf<ZFileBean>()
        if (documentFiles.isNullOrEmpty()) return list

        val config = getZFileConfig()
        for (d in documentFiles) {
            if (d.name.isNull() || d.name?.indexOf(".") == 0) { // 过滤掉 为空的及 以.开头的文件
                continue
            }
            val path = d.uri.toString().changeToPathBySAF()
            if (d.isDirectory) {
                val bean = ZFileBean(
                    d.name!!,
                    false,
                    path,
                    ZFileOtherUtil.getFormatFileDate(d.lastModified()),
                    d.lastModified().toString(),
                    ZFileOtherUtil.getFileSize(d.length()),
                    d.length(),
                    ""
                )
                list.add(bean)
            } else {
                val bean = ZFileBean(
                    d.name!!,
                    true,
                    path,
                    ZFileOtherUtil.getFormatFileDate(d.lastModified()),
                    d.lastModified().toString(),
                    ZFileOtherUtil.getFileSize(d.length()),
                    d.length(),
                    ""
                )
                list.add(bean)
            }
        }

         // 排序相关
         if (config.sortord == ZFileConfiguration.ASC) {
             when (config.sortordBy) {
                 ZFileConfiguration.BY_NAME -> list.sortBy { it.fileName.toLowerCase(Locale.CHINA) }
                 ZFileConfiguration.BY_DATE -> list.sortBy { it.originalDate }
                 ZFileConfiguration.BY_SIZE -> list.sortBy { it.originaSize }
             }
         } else {
             when (config.sortordBy) {
                 ZFileConfiguration.BY_NAME -> list.sortByDescending { it.fileName.toLowerCase(Locale.CHINA) }
                 ZFileConfiguration.BY_DATE -> list.sortByDescending { it.originalDate }
                 ZFileConfiguration.BY_SIZE -> list.sortByDescending { it.originaSize }
             }
         }

        return list

    }

    protected fun String.changeToPathBySAF(): String {
        var path = this
        if (this.contains("${SAF_TREE_ROOT}primary%3AAndroid%2Fdata")) path =
            this.replace(
                "${SAF_TREE_ROOT}primary%3AAndroid%2Fdata/document/primary%3A",
                ""
            ).replace(
                "%2F",
                "/"
            ) else if (this.contains("${SAF_TREE_ROOT}primary%3AAndroid%2Fobb")) path =
            this.replace(
                "${SAF_TREE_ROOT}primary%3AAndroid%2Fobb/document/primary%3A",
                ""
            ).replace("%2F", "/")
        path = Uri.decode(path)
        return "$SD_ROOT$path"
    }

}