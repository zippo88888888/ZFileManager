package com.zp.zfile_manager.fm

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.zp.z_file.content.ZFileBean
import com.zp.z_file.content.getZFileConfig
import com.zp.z_file.listener.ZFragmentListener
import com.zp.z_file.ui.ZFileListFragment
import com.zp.zfile_manager.R

class BlankFragment2 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blank2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val TAG = "ZFileListFragmentTag"
        getZFileConfig().fragmentTag = TAG
        childFragmentManager
            .beginTransaction()
            .add(R.id.fb2Layout, getZFragment(), TAG)
            .commit()

        // Fragment + (VP + Fragment) 同 Activity + (VP + Fragment)
        // 区别在于 使用 childFragmentManager or supportFragmentManager 以及 找到正确的 FragmentTag
    }

    fun showPermissionDialog() {
        (childFragmentManager.findFragmentByTag(getZFileConfig().fragmentTag) as? ZFileListFragment)?.showPermissionDialog()
    }

    fun onBackPressed() {
        (childFragmentManager.findFragmentByTag(getZFileConfig().fragmentTag) as? ZFileListFragment)?.onBackPressed()
    }

    private var mListener: ZFragmentListener? = object : ZFragmentListener() {

        /**
         * 文件选择
         */
        override fun selectResult(selectList: MutableList<ZFileBean>?) {
            Toast.makeText(requireContext(), "选中了${selectList?.size}个", Toast.LENGTH_SHORT).show()
            Log.i("ZFileManager", "选中的值 ===>>> ")
            selectList?.forEach {
                Log.i("ZFileManager", it.toString())
            }
            activity?.finish()
        }

        /**
         * 直接调用 [Activity.finish] 即可
         */
//        override fun onActivityBackPressed() {
//            activity?.finish()
//        }

        /**
         * 获取 [Manifest.permission.WRITE_EXTERNAL_STORAGE] 权限失败
         * @param activity FragmentActivity
         */
        override fun onSDPermissionsFiled(activity: FragmentActivity) {
            Toast.makeText(requireContext(), "OPS，没有SD卡权限", Toast.LENGTH_SHORT).show()
        }

        /**
         * 获取 [Environment.isExternalStorageManager] (所有的文件管理) 权限 失败
         * 请注意：Android 11 及以上版本 才有
         */
        override fun onExternalStorageManagerFiled(activity: FragmentActivity) {
            Toast.makeText(requireContext(), "Environment.isExternalStorageManager = false", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getZFragment(): ZFileListFragment {
        val fragment = ZFileListFragment.newInstance()
        fragment.zFragmentListener = mListener
        return fragment
    }

}
