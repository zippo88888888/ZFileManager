package com.zp.z_file.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.zp.z_file.R
import com.zp.z_file.common.ZFileAdapter
import com.zp.z_file.common.ZFileManageDialog
import com.zp.z_file.common.ZFileViewHolder
import com.zp.z_file.content.*
import com.zp.z_file.util.ZFileUtil
import kotlinx.android.synthetic.main.dialog_zfile_select_folder.*

internal class ZFileSelectFolderDialog : ZFileManageDialog() {

    companion object {
        fun newInstance(type: String) = ZFileSelectFolderDialog().apply {
            arguments = Bundle().run {
                putString("type", type)
                this
            }
        }
    }
    private var tipStr = ""
    private var filePath: String? = ""
    private var isOnlyFolder = false
    private var isOnlyFile = false
    private var folderAdapter: ZFileAdapter<ZFileBean>? = null

    var selectFolder: (String.() -> Unit)? = null

    private val backList by lazy {
        ArrayList<String>()
    }

    /** 返回当前的路径 */
    private fun getThisFilePath() = if (backList.isEmpty()) null else backList[backList.size - 1]

    override fun getContentView() = R.layout.dialog_zfile_select_folder

    override fun createDialog(savedInstanceState: Bundle?) =
        Dialog(context!!, R.style.Zfile_Select_Folder_Dialog).apply {
            window?.setGravity(Gravity.BOTTOM)
        }

    override fun init(savedInstanceState: Bundle?) {
        tipStr = arguments?.getString("type") ?: ZFileConfiguration.COPY
        // 先保存之前用户配置的数据
        filePath = getZFileConfig().filePath
        isOnlyFile = getZFileConfig().isOnlyFile
        isOnlyFolder = getZFileConfig().isOnlyFolder
        zfile_select_folder_closePic.setOnClickListener {
            dismiss()
        }
        zfile_select_folder_downPic.setOnClickListener {
            selectFolder?.invoke(if (getZFileConfig().filePath.isNullOrEmpty()) SD_ROOT else getZFileConfig().filePath!!)
            recoverData()
            dismiss()
        }
        zfile_select_folder_title.text = String.format("%s到根目录", tipStr)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        folderAdapter = object : ZFileAdapter<ZFileBean>(context!!, R.layout.item_zfile_list_folder) {
            override fun bindView(holder: ZFileViewHolder, item: ZFileBean, position: Int) {
                holder.apply {
                    setText(R.id.item_zfile_list_folderNameTxt, item.fileName)
                    setImageRes(R.id.item_zfile_list_folderPic, folderRes)
                    setBgColor(R.id.item_zfile_list_folder_line, lineColor)
                    setVisibility(R.id.item_zfile_list_folder_line, position < itemCount - 1)
                }
            }
        }
        folderAdapter?.itemClick = { _, _, item ->
            getZFileConfig().filePath = item.filePath
            backList.add(item.filePath)
            getData()
        }
        val lp = zfile_select_folder_recyclerView.layoutParams as LinearLayout.LayoutParams
        lp.apply {
            bottomMargin = context!!.getStatusBarHeight()
        }
        zfile_select_folder_recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = folderAdapter
            layoutParams = lp
        }
        getZFileConfig().apply {
            isOnlyFile = false
            isOnlyFolder = true
            filePath = ""
        }
        getData()
    }

    private fun getData() {
        val filePath = getZFileConfig().filePath
        if (filePath.isNullOrEmpty() || filePath == SD_ROOT) {
            zfile_select_folder_title.text = String.format("%s到根目录", tipStr)
        } else {
            zfile_select_folder_title.text = String.format("%s到%s", tipStr, filePath.toFile().name)
        }
        ZFileUtil.getList(context!!) {
            if (isNullOrEmpty()) {
                folderAdapter?.clear()
            } else {
                folderAdapter?.setDatas(this)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        val path = getThisFilePath()
        if (path == SD_ROOT || path.isNullOrEmpty()) { // 根目录
            dismiss()
        } else { // 返回上一级
            backList.removeAt(backList.size - 1)
            getZFileConfig().filePath = getThisFilePath()
            getData()
        }
        return true
    }

    private fun recoverData() {
        // 恢复之前用户配置的数据
        getZFileConfig().filePath = filePath
        getZFileConfig().isOnlyFile = isOnlyFile
        getZFileConfig().isOnlyFolder = isOnlyFolder
    }

    override fun onStart() {
        val display = context!!.getZDisplay()
        dialog?.window?.setLayout(display[0], display[1])
        super.onStart()
    }


}