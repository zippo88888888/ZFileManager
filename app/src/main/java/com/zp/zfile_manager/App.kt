package com.zp.zfile_manager

import android.app.Application
import com.zp.z_file.common.ZFileManageHelp
import com.zp.z_file.content.getZFileHelp
import com.zp.zfile_manager.diy.MyFileTypeListener

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        getZFileHelp().init(MyFileImageListener())
            .setFileTypeListener(MyFileTypeListener())
    }

}