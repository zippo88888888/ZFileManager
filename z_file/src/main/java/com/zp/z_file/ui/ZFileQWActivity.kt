package com.zp.z_file.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import androidx.collection.ArrayMap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.zp.z_file.R
import com.zp.z_file.common.ZFileActivity
import com.zp.z_file.content.*
import com.zp.z_file.util.ZFilePermissionUtil
import com.zp.z_file.util.ZFileUtil
import kotlinx.android.synthetic.main.activity_zfile_qw.*

internal class ZFileQWActivity : ZFileActivity(), ViewPager.OnPageChangeListener {

    private val selectArray by lazy {
        ArrayMap<String, ZFileBean>()
    }

    private var type = ZFileConfiguration.QQ

    private lateinit var vpAdapter: ZFileQWAdapter
    private var isManage = false

    override fun getContentView() = R.layout.activity_zfile_qw

    override fun init(savedInstanceState: Bundle?) {
        type = getZFileConfig().filePath!!
        zfile_qw_toolBar.title = if (type == ZFileConfiguration.QQ) "QQ文件" else "微信文件"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) checkHasPermission() else initAll()
    }

    private fun initAll() {
        zfile_qw_toolBar.apply {
            inflateMenu(R.menu.zfile_qw_menu)
            setOnMenuItemClickListener { menu -> menuItemClick(menu) }
            setNavigationOnClickListener { onBackPressed() }
        }
        zfile_qw_viewPager.addOnPageChangeListener(this)
        zfile_qw_tabLayout.setupWithViewPager(zfile_qw_viewPager)
        vpAdapter = ZFileQWAdapter(type, isManage, this, supportFragmentManager)
        zfile_qw_viewPager.adapter = vpAdapter
    }

    fun observer(bean: ZFileQWBean) {
        val item = bean.zFileBean!!
        if (bean.isSelected) {
            val size = selectArray.size
            if (size >= getZFileConfig().maxLength) {
                toast(getZFileConfig().maxLengthStr)
                getVPFragment(zfile_qw_viewPager.currentItem)?.removeLastSelectData(bean.zFileBean)
            } else {
                selectArray[item.filePath] = item
            }
        } else {
            if (selectArray.contains(item.filePath)) {
                selectArray.remove(item.filePath)
            }
        }
        zfile_qw_toolBar.title = "已选中${selectArray.size}个文件"
        isManage = true
        getMenu().isVisible = true
    }

    private fun getMenu() = zfile_qw_toolBar.menu.findItem(R.id.menu_zfile_qw_down)

    private fun menuItemClick(menu: MenuItem?): Boolean {
        when (menu?.itemId) {
            R.id.menu_zfile_qw_down -> {
                if (selectArray.isNullOrEmpty()) {
                    vpAdapter.list.indices.forEach {
                        getVPFragment(it)?.apply {
                            resetAll()
                        }
                    }
                    isManage = false
                    getMenu().isVisible = false
                    zfile_qw_toolBar.title = if (getZFileConfig().filePath!! == ZFileConfiguration.QQ) "QQ文件" else "微信文件"
                } else {
                    setResult(ZFILE_RESULT_CODE, Intent().apply {
                        putParcelableArrayListExtra(ZFILE_SELECT_DATA_KEY, selectArray.toFileList() as java.util.ArrayList<out Parcelable>)
                    })
                    finish()
                }
            }
        }
        return true
    }

    private fun getVPFragment(currentItem: Int): ZFileQWFragment? {
        val fragmentId = vpAdapter.getItemId(currentItem)
        val tag = "android:switcher:${zfile_qw_viewPager.id}:$fragmentId"
        return supportFragmentManager.findFragmentByTag(tag) as? ZFileQWFragment
    }

    override fun onPageScrollStateChanged(state: Int) = Unit
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
    override fun onPageSelected(position: Int) {
        getVPFragment(position)?.setManager(isManage)
    }

    private fun checkHasPermission() {
        val hasPermission = ZFilePermissionUtil.hasPermission(this, ZFilePermissionUtil.WRITE_EXTERNAL_STORAGE)
        if (hasPermission) {
            ZFilePermissionUtil.requestPermission(this, ZFilePermissionUtil.WRITE_EXTERNAL_CODE, ZFilePermissionUtil.WRITE_EXTERNAL_STORAGE)
        } else {
            initAll()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ZFilePermissionUtil.WRITE_EXTERNAL_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) initAll()
            else {
                toast("权限申请失败")
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isManage = false
        selectArray.clear()
        ZFileUtil.resetAll()
    }

    class ZFileQWAdapter(
        type: String,
        isManger: Boolean,
        context: Context,
        fragmentManager: FragmentManager
    ) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        var list = ArrayList<Fragment>()
        private val titles by lazy {
            arrayOf(
                    context.getStringById(R.string.zfile_pic),
                    context.getStringById(R.string.zfile_video),
                    context.getStringById(R.string.zfile_txt),
                    context.getStringById(R.string.zfile_other)
            )
        }

        init {
            list.add(ZFileQWFragment.newInstance(type, ZFILE_QW_PIC, isManger))
            list.add(ZFileQWFragment.newInstance(type, ZFILE_QW_MEDIA, isManger))
            list.add(ZFileQWFragment.newInstance(type, ZFILE_QW_DOCUMENT, isManger))
            list.add(ZFileQWFragment.newInstance(type, ZFILE_QW_OTHER, isManger))
        }

        override fun getItem(position: Int) = list[position]

        override fun getCount() = list.size

        override fun getItemPosition(any: Any) = PagerAdapter.POSITION_NONE

        override fun getPageTitle(position: Int): String? =
                getZFileHelp().getQWFileLoadListener()?.getTitles()?.get(position) ?: titles[position]

    }

}
