package com.example.android.common.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.TextView

class SlidingTabLayoutKt @JvmOverloads constructor (context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : HorizontalScrollView(context, attrs, defStyle) {

    interface TabColorizer {
        fun getIndicatorColor(position: Int): Int
        fun getDividerColor(position: Int): Int
    }

    private val DEVICE_WIDTH = context.resources.displayMetrics.widthPixels/2

    private val TITLE_OFFSET_DIPS = 24
    private var mTitleOffset: Int = 0

    private var mTabViewLayoutId: Int = 0
    private var mTabViewTextViewId: Int = 0

    private lateinit var mViewPager: ViewPager
    private var mViewPagerPageChangeListener: ViewPager.OnPageChangeListener? = null

    private lateinit var mTabStrip: SlidingTabStripKt

    init {
        isHorizontalScrollBarEnabled = false
        isFillViewport = true

        mTitleOffset = (TITLE_OFFSET_DIPS * resources.displayMetrics.density).toInt()

        mTabStrip = SlidingTabStripKt(context)
        addView(mTabStrip, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
    }

    fun setSelectedIndicatorColors(colors: Int) {
        mTabStrip.setSelectedIndicatorColors(colors)
    }

    fun setDividerColors(colors: Int) {
        mTabStrip.setDividerColors(colors)
    }

    fun setOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        mViewPagerPageChangeListener = listener
    }

    fun setCustomTabView(layoutResId: Int, textViewId: Int) {
        mTabViewLayoutId = layoutResId
        mTabViewTextViewId = textViewId
    }

    fun setViewPager(viewPager: ViewPager) {
        mTabStrip.removeAllViews()

        mViewPager = viewPager
        viewPager.addOnPageChangeListener(InternalViewPagerListener())
        populateTabStrip()

    }

    private fun populateTabStrip() {
        val adapter = mViewPager.adapter
        val tabClickListener = TabClickListener()

        for (i in 0 until adapter.count) {
            var tabView = LayoutInflater.from(context).inflate(mTabViewLayoutId, mTabStrip,false)

            tabView.let {
                it.findViewById<TextView>(mTabViewTextViewId)?.text = adapter.getPageTitle(i)
                it.setOnClickListener(tabClickListener)
            }
            mTabStrip.addView(tabView)
        }
    }

    protected override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        scrollToTab(mViewPager.currentItem, 0)
    }

    private fun scrollToTab(tabIndex: Int, positionOffset: Int) {
        val tabStripChildCount = mTabStrip.childCount
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return
        }

        val selectedChild = mTabStrip.getChildAt(tabIndex)
        if (selectedChild != null) {
            var targetScrollX = selectedChild.left

            if (tabIndex>0 || targetScrollX > DEVICE_WIDTH) {
                var tabSize = targetScrollX/tabIndex
                var stopTabNum = DEVICE_WIDTH/tabSize
                scrollTo(tabSize*(tabIndex-stopTabNum) + positionOffset, 0)
            }
        }
    }

    private inner class InternalViewPagerListener : ViewPager.OnPageChangeListener {
        private var mScrollState: Int = 0

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            val tabStripChildCount = mTabStrip.childCount
            if (tabStripChildCount == 0 || position < 0 || position >= tabStripChildCount) {
                return
            }

            mTabStrip.onViewPagerPageChanged(position, positionOffset)

            val selectedTitle = mTabStrip.getChildAt(position)
            val extraOffset = if (selectedTitle != null) (positionOffset * selectedTitle.width).toInt() else 0
            scrollToTab(position, extraOffset)

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener!!.onPageScrolled(position, positionOffset,
                        positionOffsetPixels)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            mScrollState = state

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener!!.onPageScrollStateChanged(state)
            }
        }

        override fun onPageSelected(position: Int) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f)
                scrollToTab(position, 0)
            }

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener!!.onPageSelected(position)
            }
        }

    }

    private inner class TabClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            for (i in 0 until mTabStrip.childCount) {
                if (v === mTabStrip.getChildAt(i)) {
                    mViewPager.currentItem = i
                    return
                }
            }
        }
    }

}
