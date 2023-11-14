package com.zp.zfile_manager.fm

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.zp.z_file.content.*
import com.zp.z_file.content.ZFileConfiguration.Companion.TITLE_CENTER
import com.zp.z_file.listener.ZFragmentListener
import com.zp.z_file.ui.ZFileListFragment
import com.zp.zfile_manager.R
import com.zp.zfile_manager.databinding.ActivityFragmentSample2Binding

class FragmentSampleActivity2 : AppCompatActivity() {

    companion object {
        fun jump(context: Context, type: Int) {
            context.startActivity(Intent(context, FragmentSampleActivity2::class.java).apply {
                putExtra("type", type)
            })
        }
    }

    private lateinit var vb: ActivityFragmentSample2Binding
    private var type = 1
    private var vpAdapter: VPAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityFragmentSample2Binding.inflate(layoutInflater)
        setContentView(vb.root)

        type = intent.getIntExtra("type", 1)
        when (type) {
            1 -> init1()
            2 -> init2()
            3 -> init3()
        }
    }

    private fun init1() {
        vb.fs2.visibility = View.VISIBLE
        vb.fs2Vp.visibility = View.GONE
        val TAG = "ZFileListFragmentTag"
        getZFileConfig().fragmentTag = TAG
        /*supportFragmentManager
            .beginTransaction()
            .add(R.id.fs2, getZFragment(), TAG)
            .commit()*/
        zfileInitAndStart(R.id.fs2, mListener)
    }

    private fun init2() {
        vb.fs2.visibility = View.GONE
        vb.fs2Vp.visibility = View.VISIBLE
        val list = arrayListOf<Fragment>()
        list.add(BlankFragment())
        list.add(getZFragment())
        list.add(BlankFragment())
        vpAdapter = VPAdapter(supportFragmentManager, list)
        getZFileConfig().apply {
            fragmentTag = getFragmentTagByVP(vpAdapter)
            showBackIcon = false
            titleGravity = TITLE_CENTER
            needLazy = true
        }

        vb.fs2Vp.offscreenPageLimit = list.size
        vb.fs2Vp.adapter = vpAdapter
    }

    private fun init3() {
        vb.fs2.visibility = View.VISIBLE
        vb.fs2Vp.visibility = View.GONE
        val TAG = "BlankFragment2"
        val fragment = BlankFragment2()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fs2, fragment, TAG)
            .commit()
    }

    override fun onBackPressed() {
        when (type) {
            1 -> {
//                (supportFragmentManager.findFragmentByTag(getZFileConfig().fragmentTag) as? ZFileListFragment)?.onBackPressed()
                zfileBackPressed()
            }
            3 -> {
                (supportFragmentManager.findFragmentByTag("BlankFragment2") as? BlankFragment2)?.onBackPressed()
            }
            else -> {
                if (vb.fs2Vp.currentItem == 1) {
//                    (supportFragmentManager.findFragmentByTag(getZFileConfig().fragmentTag) as? ZFileListFragment)?.onBackPressed()
                    zfileBackPressed()
                } else {
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (type == 3) {
            (supportFragmentManager.findFragmentByTag("BlankFragment2") as? BlankFragment2)?.showPermissionDialog()
        } else {
//            (supportFragmentManager.findFragmentByTag(getZFileConfig().fragmentTag) as? ZFileListFragment)?.showPermissionDialog()
            zfileResume()
        }
    }

    override fun onDestroy() {
        getZFileConfig().showBackIcon = true
        mListener = null
        super.onDestroy()
    }

    private var mListener: ZFragmentListener? = object : ZFragmentListener() {

        /**
         * 文件选择
         */
        override fun selectResult(selectList: MutableList<ZFileBean>?) {
            Toast.makeText(this@FragmentSampleActivity2, "选中了${selectList?.size}个，具体信息查看log", Toast.LENGTH_SHORT).show()
            Log.i("ZFileManager", "选中的值 ===>>> ")
            selectList?.forEach {
                Log.i("ZFileManager", it.toString())
            }
            finish()
        }


        /**
         * 获取 [Manifest.permission.WRITE_EXTERNAL_STORAGE] 权限失败
         * @param activity FragmentActivity
         */
        override fun onSDPermissionsFiled(activity: FragmentActivity) {
            Toast.makeText(this@FragmentSampleActivity2, "OPS，没有SD卡权限", Toast.LENGTH_SHORT).show()
        }

        /**
         * 获取 [Environment.isExternalStorageManager] (所有的文件管理) 权限 失败
         * 请注意：Android 11 及以上版本 才有
         */
        override fun onExternalStorageManagerFiled(activity: FragmentActivity) {
            Toast.makeText(this@FragmentSampleActivity2, "Environment.isExternalStorageManager = false", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getZFragment(): ZFileListFragment {
        val fragment = ZFileListFragment.newInstance()
        fragment.zFragmentListener = mListener
        return fragment
    }

    private fun getFragmentTagByVP(vpAdapter: FragmentPagerAdapter?, position: Int = 1): String {
        val fragmentId = vpAdapter?.getItemId(position)
        return "android:switcher:${vb.fs2Vp.id}:$fragmentId"
    }

    private class VPAdapter(
        fragmentManager: FragmentManager,
        private var fragments: MutableList<Fragment>
    ) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount() = fragments.size
        override fun getItem(position: Int) = fragments[position]
        override fun getItemPosition(any: Any) = PagerAdapter.POSITION_NONE
        override fun getPageTitle(position: Int) = ""
    }

}