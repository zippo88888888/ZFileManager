package com.zp.z_file.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.RadioGroup
import com.zp.z_file.R
import com.zp.z_file.common.ZFileManageDialog
import com.zp.z_file.content.setNeedWH
import kotlinx.android.synthetic.main.dialog_zfile_sort.*

internal class ZFileSortDialog : ZFileManageDialog(), RadioGroup.OnCheckedChangeListener {

    companion object {
        fun newInstance(sortSelectId: Int, sequenceSelectId: Int) = ZFileSortDialog().apply {
            arguments = Bundle().run {
                putInt("sortSelectId", sortSelectId)
                putInt("sequenceSelectId", sequenceSelectId)
                this
            }
        }
    }

    private var sortSelectId = 0
    private var sequenceSelectId = 0

    var checkedChangedListener: ((Int, Int) -> Unit)? = null

    override fun getContentView() = R.layout.dialog_zfile_sort

    override fun createDialog(savedInstanceState: Bundle?) = Dialog(context!!, R.style.ZFile_Common_Dialog).apply {
        window?.setGravity(Gravity.CENTER)
    }

    override fun init(savedInstanceState: Bundle?) {
        sortSelectId = arguments?.getInt("sortSelectId", 0) ?: 0
        sequenceSelectId = arguments?.getInt("sequenceSelectId", 0) ?: 0
        check()
        when (sortSelectId) {
            R.id.zfile_sort_by_default -> zfile_sort_by_default.isChecked = true
            R.id.zfile_sort_by_name -> zfile_sort_by_name.isChecked = true
            R.id.zfile_sort_by_date -> zfile_sort_by_date.isChecked = true
            R.id.zfile_sort_by_size -> zfile_sort_by_size.isChecked = true
            else -> zfile_sort_by_default.isChecked = true
        }
        when (sequenceSelectId) {
            R.id.zfile_sequence_asc -> zfile_sequence_asc.isChecked = true
            R.id.zfile_sequence_desc -> zfile_sequence_desc.isChecked = true
            else -> zfile_sequence_asc.isChecked = true
        }
        zfile_sortGroup.setOnCheckedChangeListener(this)
        zfile_sequenceGroup.setOnCheckedChangeListener(this)
        zfile_dialog_sort_cancel.setOnClickListener { dismiss() }
        zfile_dialog_sort_down.setOnClickListener {
            checkedChangedListener?.invoke(sortSelectId, sequenceSelectId)
            dismiss()
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        if (group?.id == R.id.zfile_sortGroup) { // 方式
            sortSelectId = checkedId
            check()
        } else { // 顺序
            sequenceSelectId = checkedId
        }
    }

    private fun check() {
        zfile_sequenceLayout.visibility = if (sortSelectId == R.id.zfile_sort_by_default) View.GONE else View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        setNeedWH()
    }
}