package com.zp.zfile_manager.fm

import android.Manifest
import android.app.Activity
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
import androidx.viewpager.widget.ViewPager
import com.zp.z_file.content.ZFileBean
import com.zp.z_file.content.ZFileConfiguration.Companion.TITLE_CENTER
import com.zp.z_file.content.getZFileConfig
import com.zp.z_file.listener.ZFragmentListener
import com.zp.z_file.ui.ZFileListFragment
import com.zp.zfile_manager.R
import kotlinx.android.synthetic.main.activity_fragment_sample2.*

class FragmentSampleActivity2 : AppCompatActivity() {

    companion object {
        fun jump(context: Context, type: Int) {
            context.startActivity(Intent(context, FragmentSampleActivity2::class.java).apply {
                putExtra("type", type)
            })
        }
    }

    private var type = 1
    private var vpAdapter: VPAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_sample2)

        type = intent.getIntExtra("type", 1)
        when (type) {
            1 -> init1()
            2 -> init2()
            3 -> init3()
        }
    }

    private fun init1() {
        fs2.visibility = View.VISIBLE
        fs2_vp.visibility = View.GONE
        val TAG = "ZFileListFragmentTag"
        getZFileConfig().fragmentTag = TAG
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fs2, getZFragment(), TAG)
            .commit()
    }

    private fun init2() {
        fs2.visibility = View.GONE
        fs2_vp.visibility = View.VISIBLE
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

        fs2_vp.offscreenPageLimit = list.size
        fs2_vp.adapter = vpAdapter
    }

    private fun init3() {
        fs2.visibility = View.VISIBLE
        fs2_vp.visibility = View.GONE
        val TAG = "BlankFragment2"
        val fragment = BlankFragment2()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fs2, fragment, TAG)
            .commit()
    }

    override fun onBackPressed() {
        if (type == 3) {
            (supportFragmentManager.findFragmentByTag("BlankFragment2") as? BlankFragment2)?.onBackPressed()
        } else {
            (supportFragmentManager.findFragmentByTag(getZFileConfig().fragmentTag) as? ZFileListFragment)?.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        if (type == 3) {
            (supportFragmentManager.findFragmentByTag("BlankFragment2") as? BlankFragment2)?.showPermissionDialog()
        } else {
            (supportFragmentManager.findFragmentByTag(getZFileConfig().fragmentTag) as? ZFileListFragment)?.showPermissionDialog()
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
            Toast.makeText(this@FragmentSampleActivity2, "选中了${selectList?.size}个", Toast.LENGTH_SHORT).show()
            Log.i("ZFileManager", "选中的值 ===>>> ")
            selectList?.forEach {
                Log.i("ZFileManager", it.toString())
            }
            finish()
        }

        /**
         * 直接调用 [Activity.finish] 即可
         */
        override fun onActivityBackPressed() {
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
        return "android:switcher:${fs2_vp.id}:$fragmentId"
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