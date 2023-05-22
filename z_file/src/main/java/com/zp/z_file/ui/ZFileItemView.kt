package com.zp.z_file.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import com.zp.z_file.content.QW_SIZE
import com.zp.z_file.content.ZFileException
import com.zp.z_file.content.getZFileHelp
import com.zp.z_file.databinding.LayoutZfileItemBinding
import com.zp.z_file.util.ZFileQWUtil

/**
 * 为了解决 aar 包中 com.google.android.material:material 引用失败的问题
 */
class ZFileItemView : LinearLayout {

    private val titles by lazy {
        ZFileQWUtil.getQWTitle(context)
    }

    private lateinit var vp: ViewPager

    private var vb: LayoutZfileItemBinding? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        vb = LayoutZfileItemBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private fun initAll() {
        vb?.zfileItemLayout1?.setOnClickListener {
            vp.setCurrentItem(0, false)
            setPageItemState()
        }
        vb?.zfileItemLayout2?.setOnClickListener {
            vp.setCurrentItem(1, false)
            setPageItemState()
        }
        vb?.zfileItemLayout3?.setOnClickListener {
            vp.setCurrentItem(2, false)
            setPageItemState()
        }
        vb?.zfileItemLayout4?.setOnClickListener {
            vp.setCurrentItem(3, false)
            setPageItemState()
        }
    }

    fun setupWithViewPager(viewPager: ViewPager) {
        this.vp = viewPager
        this.vp.addOnPageChangeListener(changeListener)

        val list = getZFileHelp().getQWFileLoadListener()?.getTitles() ?: titles
        if (list.size != QW_SIZE) {
            throw ZFileException("ZQWFileLoadListener.getTitles() size must be $QW_SIZE")
        }
        vb?.zfileItemTitleTxt1?.text = list[0]
        vb?.zfileItemTitleTxt2?.text = list[1]
        vb?.zfileItemTitleTxt3?.text = list[2]
        vb?.zfileItemTitleTxt4?.text = list[3]
    }

    private fun setPageItemState() {
        when (vp.currentItem) {
            0 -> {
                setShow(vb?.zfileItemTitleTxt1, vb?.zfileItemLine1)
                setHidden(vb?.zfileItemTitleTxt2, vb?.zfileItemLine2)
                setHidden(vb?.zfileItemTitleTxt3, vb?.zfileItemLine3)
                setHidden(vb?.zfileItemTitleTxt4, vb?.zfileItemLine4)
            }
            1 -> {
                setHidden(vb?.zfileItemTitleTxt1, vb?.zfileItemLine1)
                setShow(vb?.zfileItemTitleTxt2, vb?.zfileItemLine2)
                setHidden(vb?.zfileItemTitleTxt3, vb?.zfileItemLine3)
                setHidden(vb?.zfileItemTitleTxt4, vb?.zfileItemLine4)
            }
            2 -> {
                setHidden(vb?.zfileItemTitleTxt1, vb?.zfileItemLine1)
                setHidden(vb?.zfileItemTitleTxt2, vb?.zfileItemLine2)
                setShow(vb?.zfileItemTitleTxt3, vb?.zfileItemLine3)
                setHidden(vb?.zfileItemTitleTxt4, vb?.zfileItemLine4)
            }
            3 -> {
                setHidden(vb?.zfileItemTitleTxt1, vb?.zfileItemLine1)
                setHidden(vb?.zfileItemTitleTxt2, vb?.zfileItemLine2)
                setHidden(vb?.zfileItemTitleTxt3, vb?.zfileItemLine3)
                setShow(vb?.zfileItemTitleTxt4, vb?.zfileItemLine4)
            }
        }
    }

    private fun setShow(txt: View?, line: View?) {
        txt?.isSelected = true
        line?.visibility = View.VISIBLE
    }

    private fun setHidden(txt: View?, line: View?) {
        txt?.isSelected = false
        line?.visibility = View.GONE
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initAll()
    }

    override fun onDetachedFromWindow() {
        vb = null
        super.onDetachedFromWindow()
    }

    private var changeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) = Unit
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) = Unit

        override fun onPageSelected(position: Int) {
            setPageItemState()
        }

    }
}