package com.zp.z_file.ui

import android.os.Bundle
import com.zp.z_file.R
import com.zp.z_file.common.ZFileActivity
import com.zp.z_file.content.FILE_START_PATH_KEY
import com.zp.z_file.content.ZFILE_FRAGMENT_TAG
import com.zp.z_file.content.ZFileBean
import com.zp.z_file.content.getZFileConfig
import com.zp.z_file.listener.ZFragmentListener
import com.zp.z_file.util.ZFileUtil

internal class ZFileListActivity2 : ZFileActivity() {

    override fun getContentView() = R.layout.activity_zfile_list2

    override fun init(savedInstanceState: Bundle?) {
        getZFileConfig().apply {
            fragmentTag = ZFILE_FRAGMENT_TAG
            filePath = intent.getStringExtra(FILE_START_PATH_KEY)
        }
        supportFragmentManager
            .beginTransaction()
            .add(
                R.id.zfile_2rootLayout,
                ZFileListFragment.newInstance().apply {
                    zFragmentListener = mListener
                },
                ZFILE_FRAGMENT_TAG
            )
            .commit()
    }

    override fun onBackPressed() {
        (supportFragmentManager.findFragmentByTag(getZFileConfig().fragmentTag) as? ZFileListFragment)?.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        (supportFragmentManager.findFragmentByTag(getZFileConfig().fragmentTag) as? ZFileListFragment)?.showPermissionDialog()
    }

    override fun onDestroy() {
        mListener = null
        super.onDestroy()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.zfile_out_bottom)
    }

    private var mListener: ZFragmentListener? = object : ZFragmentListener() {

        override fun selectResult(selectList: MutableList<ZFileBean>?) {
            ZFileUtil.toResult(this@ZFileListActivity2, selectList)
//            getZFileHelp().setResult(this@ZFileListActivity2, selectList)
        }

    }
}