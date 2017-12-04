package com.example.android.common.view

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.LinearLayout

class SlidingTabStripKt(context: Context, attrs: AttributeSet? =  null)
    : LinearLayout(context, attrs) {
    private val DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS = 2
    private val DEFAULT_BOTTOM_BORDER_COLOR_ALPHA: Byte = 0x26
    private val SELECTED_INDICATOR_THICKNESS_DIPS = 5f
    private val DEFAULT_SELECTED_INDICATOR_COLOR = -0xcc4a1b

    private val DEFAULT_DIVIDER_THICKNESS_DIPS = 1
    private val DEFAULT_DIVIDER_COLOR_ALPHA: Byte = 0x20
    private val DEFAULT_DIVIDER_HEIGHT = 0.5f

    private var mBottomBorderThickness: Float = 0f
    private lateinit var mBottomBorderPaint: Paint

    private var mSelectedIndicatorThickness: Float = 0f
    private lateinit var mSelectedIndicatorPaint: Paint

    private var mDefaultBottomBorderColor: Int = 0

    private lateinit var mDividerPaint: Paint
    private var mDividerHeight: Float = 0f

    private var mSelectedPosition: Int = 0
    private var mSelectionOffset: Float = 0f

    private var mDefaultTabColorizer: SimpleTabColorizer

    init {
        setWillNotDraw(false)

        val density = resources.displayMetrics.density
        val outValue = TypedValue()

        context.theme.resolveAttribute(R.attr.colorForeground, outValue, true)
        val themeForegroundColor = outValue.data

        mDefaultBottomBorderColor = setColorAlpha(themeForegroundColor,
                DEFAULT_BOTTOM_BORDER_COLOR_ALPHA)

        mDefaultTabColorizer = SimpleTabColorizer()
        mDefaultTabColorizer.setIndicatorColors(DEFAULT_SELECTED_INDICATOR_COLOR)
        mDefaultTabColorizer.setDividerColors(setColorAlpha(themeForegroundColor,
                DEFAULT_DIVIDER_COLOR_ALPHA))

        mBottomBorderThickness = (DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS * density)
        mBottomBorderPaint = Paint()
        mBottomBorderPaint.color = mDefaultBottomBorderColor

        mSelectedIndicatorThickness = (SELECTED_INDICATOR_THICKNESS_DIPS * density)
        mSelectedIndicatorPaint = Paint()

        mDividerHeight = DEFAULT_DIVIDER_HEIGHT
        mDividerPaint = Paint()
        mDividerPaint.strokeWidth = (DEFAULT_DIVIDER_THICKNESS_DIPS * density).toInt().toFloat()
    }

    fun setSelectedIndicatorColors(colors: Int) {
        mDefaultTabColorizer.setIndicatorColors(colors)
        invalidate()
    }

    fun setDividerColors(colors: Int) {
        mDefaultTabColorizer.setDividerColors(colors)
        invalidate()
    }

    fun onViewPagerPageChanged(position: Int, positionOffset: Float) {
        mSelectedPosition = position
        mSelectionOffset = positionOffset
        invalidate()
    }

    protected override fun onDraw(canvas: Canvas) {
        val dividerHeightPx = (Math.min(Math.max(0f, mDividerHeight), 1f) * height).toInt()

        // Thick colored underline below the current selection
        if (childCount > 0) {
            val selectedTitle = getChildAt(mSelectedPosition)
            var left = selectedTitle.left
            var right = selectedTitle.right
            var color = mDefaultTabColorizer.getIndicatorColor(mSelectedPosition)

            if (mSelectionOffset > 0f && mSelectedPosition < childCount - 1) {
                // Draw the selection partway between the tabs
                val nextTitle = getChildAt(mSelectedPosition + 1)
                left = (mSelectionOffset * nextTitle.left + (1.0f - mSelectionOffset) * left).toInt()
                right = (mSelectionOffset * nextTitle.right + (1.0f - mSelectionOffset) * right).toInt()
            }

            mSelectedIndicatorPaint.color = color

            canvas.drawRect(left.toFloat(), (height - mSelectedIndicatorThickness), right.toFloat(),
                    (height-mBottomBorderThickness), mSelectedIndicatorPaint)
        }

        // Thin underline along the entire bottom edge
        canvas.drawRect(0f, (height - mBottomBorderThickness), width.toFloat(), height.toFloat(), mBottomBorderPaint)

        // Vertical separators between the titles
        val separatorTop = (height - dividerHeightPx) / 2
        for (i in 0 until childCount - 1) {
            val child = getChildAt(i)
            mDividerPaint.color = mDefaultTabColorizer.getDividerColor(i)
            canvas.drawLine(child.right.toFloat(), separatorTop.toFloat(), child.right.toFloat(),
                    (separatorTop + dividerHeightPx).toFloat(), mDividerPaint)
        }
    }

    private fun setColorAlpha(color: Int, alpha: Byte): Int {
        return Color.argb(alpha.toInt(), Color.red(color), Color.green(color), Color.blue(color))
    }

    private class SimpleTabColorizer : SlidingTabLayoutKt.TabColorizer {
        private var mIndicatorColors: Int = 0
        private var mDividerColors: Int = 0

        override fun getIndicatorColor(position: Int): Int {
            return mIndicatorColors
        }

        override fun getDividerColor(position: Int): Int {
            return mDividerColors
        }

        internal fun setIndicatorColors(colors: Int) {
            mIndicatorColors = colors
        }

        internal fun setDividerColors(colors: Int) {
            mDividerColors = colors
        }
    }
}
