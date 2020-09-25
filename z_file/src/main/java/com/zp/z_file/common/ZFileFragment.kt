package com.zp.z_file.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

internal abstract class ZFileFragment : Fragment() {

    private var isFirstLoad = true
    protected var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (rootView == null) {
            rootView = inflater.inflate(getContentView(), container, false)
        }
        return rootView
    }

    abstract fun getContentView(): Int

    abstract fun initAll()

    override fun onResume() {
        super.onResume()
        if (isFirstLoad) {
            initAll()
            isFirstLoad = false
        }
    }

}