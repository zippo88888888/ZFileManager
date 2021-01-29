package com.zp.z_file.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.zp.z_file.R
import com.zp.z_file.common.ZFileActivity
import com.zp.z_file.common.ZFileAdapter
import com.zp.z_file.common.ZFileViewHolder
import com.zp.z_file.content.*
import com.zp.z_file.ui.adapter.ZFileListAdapter
import com.zp.z_file.ui.dialog.ZFileSelectFolderDialog
import com.zp.z_file.ui.dialog.ZFileSortDialog
import com.zp.z_file.util.ZFileLog
import com.zp.z_file.util.ZFilePermissionUtil
import com.zp.z_file.util.ZFileUtil
import kotlinx.android.synthetic.main.activity_zfile_list.*
import java.io.File


internal class ZFileListActivity : ZFileActivity() {

    private var barShow = false
    private lateinit var filePathAdapter: ZFileAdapter<ZFilePathBean>
    private var fileListAdapter: ZFileListAdapter? = null

    private var index = 0
    private var rootPath = "" // 根目录
    private var specifyPath: String? = "" // 指定目录
    private var nowPath: String? = "" // 当前目录

    private val titleArray by lazy {
        if (getZFileConfig().longClickOperateTitles.isNullOrEmpty()) {
            arrayOf(
                ZFileConfiguration.RENAME,
                ZFileConfiguration.COPY,
                ZFileConfiguration.MOVE,
                ZFileConfiguration.DELETE,
                ZFileConfiguration.INFO
            )
        } else getZFileConfig().longClickOperateTitles
    }

    private val backList by lazy {
        ArrayList<String>()
    }

    private var sortSelectId = R.id.zfile_sort_by_default // 排序方式选中的ID
    private var sequenceSelectId = R.id.zfile_sequence_asc // 顺序选中的ID

    /** 返回当前的路径 */
    private fun getThisFilePath() = if (backList.isEmpty()) null else backList[backList.size - 1]

    override fun getContentView() = R.layout.activity_zfile_list

    private fun setMenuState() {
        zfile_list_toolBar.menu.apply {
            findItem(R.id.menu_zfile_down).isVisible = barShow
            findItem(R.id.menu_zfile_px).isVisible = !barShow
            findItem(R.id.menu_zfile_show).isVisible = !barShow
            findItem(R.id.menu_zfile_hidden).isVisible = !barShow
        }
    }

    private fun menuItemClick(menu: MenuItem?): Boolean {
        when (menu?.itemId) {
            R.id.menu_zfile_down -> {
                val list = fileListAdapter?.selectData
                if (list.isNullOrEmpty()) {
                    zfile_list_toolBar.title = "文件管理"
                    fileListAdapter?.isManage = false
                    barShow = false
                    setMenuState()
                } else {
                    setResult(ZFILE_RESULT_CODE, Intent().apply {
                        putParcelableArrayListExtra(
                            ZFILE_SELECT_DATA_KEY,
                            list as java.util.ArrayList<out Parcelable>
                        )
                    })
                    finish()
                }
            }
            R.id.menu_zfile_px -> showSortDialog()
            R.id.menu_zfile_show -> {
                menu.isChecked = true
                getZFileConfig().showHiddenFile = true
                getData(nowPath)
            }
            R.id.menu_zfile_hidden -> {
                menu.isChecked = true
                getZFileConfig().showHiddenFile = false
                getData(nowPath)
            }
        }
        return true
    }

    override fun init(savedInstanceState: Bundle?) {
        setSortSelectId()
        specifyPath = intent.getStringExtra(FILE_START_PATH_KEY)
        getZFileConfig().filePath = specifyPath
        rootPath = specifyPath ?: ""
        backList.add(rootPath)
        nowPath = rootPath
        zfile_list_toolBar.apply {
            inflateMenu(R.menu.zfile_list_menu)
            setOnMenuItemClickListener { menu -> menuItemClick(menu) }
            setNavigationOnClickListener { onBackPressed() }
        }
        zfile_list_emptyPic.setImageResource(emptyRes)
        setHiddenState()
        callPermission()

    }

    private fun initAll() {
        zfile_list_refreshLayout.property({
            getData(nowPath)
        })
        initPathRecyclerView()
        initListRecyclerView()
    }

    private fun initPathRecyclerView() {
        filePathAdapter = object : ZFileAdapter<ZFilePathBean>(this, R.layout.item_zfile_path) {
            override fun bindView(holder: ZFileViewHolder, item: ZFilePathBean, position: Int) {
                holder.setText(R.id.item_zfile_path_title, item.fileName)
            }

            override fun addItem(position: Int, t: ZFilePathBean) {
                var hasData = false
                getDatas().forEach forEach@{
                    if (it.filePath == t.filePath) {
                        hasData = true
                        return@forEach
                    }
                }
                if (!(hasData || t.filePath == SD_ROOT)) {
                    super.addItem(position, t)
                }
            }

        }
        zfile_list_pathRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ZFileListActivity).run {
                orientation = LinearLayoutManager.HORIZONTAL
                this
            }
            adapter = filePathAdapter
        }
        getPathData()
    }

    private fun getPathData() {
        val filePath = getZFileConfig().filePath
        val pathList = ArrayList<ZFilePathBean>()
        if (filePath.isNullOrEmpty() || filePath == SD_ROOT) {
            pathList.add(ZFilePathBean("根目录", "root"))
        } else {
            pathList.add(ZFilePathBean("指定目录${filePath.getFileName()}", filePath))
        }
        filePathAdapter.addAll(pathList)
    }

    private fun initListRecyclerView() {
        fileListAdapter = ZFileListAdapter(this).run {
            itemClickByAnim = { v, position, item ->
                if (item.isFile) {
                    ZFileUtil.openFile(item.filePath, v)
                } else {
                    ZFileLog.i("进入 ${item.filePath}")
                    backList.add(item.filePath)
                    filePathAdapter.addItem(filePathAdapter.itemCount, item.toPathBean())
                    zfile_list_pathRecyclerView.scrollToPosition(filePathAdapter.itemCount - 1)
                    getData(item.filePath)
                    nowPath = item.filePath
                }
            }
            itemLongClick = { _, index, item ->
                if (fileListAdapter?.isManage == true) {
                    false
                } else {
                    if (getZFileConfig().needLongClick) {
                        if (getZFileConfig().isOnlyFileHasLongClick) {
                            if (item.isFile) showSelectDialog(index, item)
                            else false
                        } else {
                            showSelectDialog(index, item)
                        }
                    } else {
                        false
                    }
                }
            }
            changeListener = { isManage, size ->
                if (isManage) {
                    if (barShow) {
                        zfile_list_toolBar.title = "已选中${size}个文件"
                    } else {
                        barShow = true
                        zfile_list_toolBar.title = "已选中0个文件"
                        setMenuState()
                    }
                }
            }
            this
        }
        zfile_list_listRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ZFileListActivity)
            adapter = fileListAdapter
        }
        getData(getZFileConfig().filePath)
        index ++
    }

    private fun getData(filePath: String?) {
        zfile_list_refreshLayout.isRefreshing = true
        val key = if (filePath.isNullOrEmpty()) SD_ROOT else filePath
        if (rootPath.isEmpty()) {
            rootPath = key
        }
        getZFileConfig().filePath = filePath
        if (index != 0) {
            filePathAdapter.addItem(filePathAdapter.itemCount, File(key).toPathBean())
            zfile_list_pathRecyclerView.scrollToPosition(filePathAdapter.itemCount - 1)
        }
        ZFileUtil.getList(this) {
            if (isNullOrEmpty()) {
                fileListAdapter?.clear()
                zfile_list_emptyLayout.visibility = View.VISIBLE
            } else {
                fileListAdapter?.setDatas(this)
                zfile_list_emptyLayout.visibility = View.GONE
            }
            zfile_list_refreshLayout.isRefreshing = false
        }
    }

    private fun showSelectDialog(index: Int, item: ZFileBean): Boolean {
        AlertDialog.Builder(this).apply {
            setTitle("请选择")
            setItems(titleArray) { dialog, which ->
                jumpByWhich(item, which, index)
                dialog.dismiss()
            }
            setPositiveButton("取消") { dialog, _ -> dialog.dismiss() }
            show()
        }
        return true
    }

    private val TAG by lazy {
        ZFileSelectFolderDialog::class.java.simpleName
    }

    private fun jumpByWhich(item: ZFileBean, which: Int, index: Int) {
        when (titleArray!![which]) {
            ZFileConfiguration.RENAME -> {
                getZFileHelp().getFileOperateListener()
                    .renameFile(item.filePath, this) { isSuccess, newName ->
                        if (isSuccess) {
                            val oldFile = item.filePath.toFile()
                            val oldFileType = oldFile.getFileType()
                            val oldPath =
                                oldFile.path.substring(0, oldFile.path.lastIndexOf("/") + 1)
                            val newFilePath = "$oldPath$newName.$oldFileType"
                            fileListAdapter?.getItem(index)?.apply {
                                filePath = newFilePath
                                fileName = "$newName.$oldFileType"
                            }
                            fileListAdapter?.notifyItemChanged(index)
                        }
                    }
            }
            ZFileConfiguration.COPY, ZFileConfiguration.MOVE -> {
                checkFragmentByTag(TAG)
                ZFileSelectFolderDialog.newInstance(titleArray!![which]).apply {
                    selectFolder = {
                        doSth(item, this, titleArray!![which], index)
                    }
                }.show(supportFragmentManager, TAG)
            }
            ZFileConfiguration.DELETE -> getZFileHelp().getFileOperateListener().deleteFile(
                item.filePath,
                this
            ) {
                if (this) {
                    fileListAdapter?.remove(index)
                    ZFileLog.i("文件删除成功")
                } else {
                    ZFileLog.i("文件删除失败")
                }
            }
            ZFileConfiguration.INFO -> ZFileUtil.infoFile(item, this)
            else -> throw IllegalArgumentException("ZFileConfiguration longClickOperateTitles ERROR")
        }
    }

    private fun doSth(item: ZFileBean, targetPath: String, type: String, position: Int) {
        if (type == ZFileConfiguration.COPY) { // 复制文件
            getZFileHelp().getFileOperateListener().copyFile(item.filePath, targetPath, this) {
                if (this) {
                    ZFileLog.i("文件复制成功")
                } else {
                    ZFileLog.e("文件复制失败")
                }
            }
        } else { // 移动文件
            getZFileHelp().getFileOperateListener().moveFile(item.filePath, targetPath, this) {
                if (this) {
                    fileListAdapter?.remove(position)
                    ZFileLog.i("文件移动成功")
                } else {
                    ZFileLog.e("文件移动失败")
                }
            }
        }
    }

    private fun showSortDialog() {
        val tag = ZFileSortDialog::class.java.simpleName
        checkFragmentByTag(tag)
        ZFileSortDialog.newInstance(sortSelectId, sequenceSelectId).apply {
            checkedChangedListener = { sortId, sequenceId ->
                sortSelectId = sortId
                sequenceSelectId = sequenceId
                val sortordByWhat = when (sortId) {
                    R.id.zfile_sort_by_default -> ZFileConfiguration.BY_DEFAULT
                    R.id.zfile_sort_by_name -> ZFileConfiguration.BY_NAME
                    R.id.zfile_sort_by_date -> ZFileConfiguration.BY_DATE
                    R.id.zfile_sort_by_size -> ZFileConfiguration.BY_SIZE
                    else -> ZFileConfiguration.BY_DEFAULT
                }
                val sortord = when (sequenceId) {
                    R.id.zfile_sequence_asc -> ZFileConfiguration.ASC
                    R.id.zfile_sequence_desc -> ZFileConfiguration.DESC
                    else -> ZFileConfiguration.ASC
                }
                getZFileConfig().apply {
                    sortordBy = sortordByWhat
                    this.sortord = sortord
                }
                getData(nowPath)
            }
        }.show(supportFragmentManager, tag)
    }

    override fun onBackPressed() {
        val path = getThisFilePath()
        if (path == rootPath || path.isNullOrEmpty()) { // 根目录
            if (barShow) {  // 存在编辑状态
                zfile_list_toolBar.title = "文件管理"
                fileListAdapter?.isManage = false
                barShow = false
                setMenuState()
            } else {
                super.onBackPressed()
            }
        } else { // 返回上一级
            // 先清除当前一级的数据
            backList.removeAt(backList.size - 1)
            val lastPath = getThisFilePath()
            getData(lastPath)
            nowPath = lastPath
            filePathAdapter.remove(filePathAdapter.itemCount - 1)
            zfile_list_pathRecyclerView.scrollToPosition(filePathAdapter.itemCount - 1)
        }
    }

    private fun callPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) checkHasPermission() else initAll()
    }

    private fun checkHasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val hasPermission = ZFilePermissionUtil.hasPermission(
                this,
                ZFilePermissionUtil.WRITE_EXTERNAL_STORAGE
            )
            if (hasPermission) {
                ZFilePermissionUtil.requestPermission(
                    this,
                    ZFilePermissionUtil.WRITE_EXTERNAL_CODE,
                    ZFilePermissionUtil.WRITE_EXTERNAL_STORAGE
                )
            } else {
                initAll()
            }
        } else {
            initAll()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ZFilePermissionUtil.WRITE_EXTERNAL_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) initAll()
            else {
                toast("权限申请失败")
                finish()
            }
        }
    }

    private fun setSortSelectId() {
        sortSelectId = when (getZFileConfig().sortordBy) {
            ZFileConfiguration.BY_NAME -> R.id.zfile_sort_by_name
            ZFileConfiguration.BY_SIZE -> R.id.zfile_sort_by_size
            ZFileConfiguration.BY_DATE -> R.id.zfile_sort_by_date
            else -> R.id.zfile_sort_by_default
        }
        sequenceSelectId = when (getZFileConfig().sortord) {
            ZFileConfiguration.DESC -> R.id.zfile_sequence_desc
            else -> R.id.zfile_sequence_asc
        }
    }

    private fun setHiddenState() {
        zfile_list_toolBar.post {
            val menu = zfile_list_toolBar.menu
            val showMenuItem = menu.findItem(R.id.menu_zfile_show)
            val hiddenMenuItem = menu.findItem(R.id.menu_zfile_hidden)
            if (getZFileConfig().showHiddenFile) {
                showMenuItem.isChecked = true
            } else {
                hiddenMenuItem.isChecked = true
            }
        }
    }

    fun observer(isSuccess: Boolean) {
        if (isSuccess) getData(nowPath)
    }

    override fun onDestroy() {
        ZFileUtil.resetAll()
        super.onDestroy()
        fileListAdapter?.reset()
        backList.clear()
    }

}
