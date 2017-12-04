package com.example.android.slidingtabsbasic

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_sample.*

class SlidingTabsBasicFragmentKt : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_sample, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        viewpager.adapter = SamplePagerAdapter()
        sliding_tabs.setCustomTabView(R.layout.pager_tab_item, R.id.tab_content)
        sliding_tabs.setViewPager(viewpager)
    }

    internal inner class SamplePagerAdapter : PagerAdapter() {

        override fun getCount(): Int {
            return 10
        }

        override fun isViewFromObject(view: View, o: Any): Boolean {
            return o === view
        }

        override fun getPageTitle(position: Int): CharSequence {
            return "" + (position + 1)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = activity.layoutInflater.inflate(R.layout.pager_item,
                    container, false)
            container.addView(view)

            val title = view.findViewById<View>(R.id.item_title) as TextView
            title.text = (position + 1).toString()
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

    }
}
