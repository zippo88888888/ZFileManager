package com.zp.zfile_manager.diy

import com.zp.z_file.listener.ZFileOtherListener
import com.zp.zfile_manager.R

class MyFileOtherListener : ZFileOtherListener() {

    /**
     * 获取 权限失败 时的 布局
     * 请注意：布局中必须包含控件 id：zfile_list_againBtn
     * 该id对应视图功能：用户点击后再次申请权限
     */
    override fun getPermissionFailedLayoutId(): Int {
        return R.layout.layout_diy_no_permission
    }

    /**
     * 获取 当前目录没有文件时（为空） 的布局
     */
    override fun getFileListEmptyLayoutId(): Int {
        return R.layout.layout_diy_empty
    }
}