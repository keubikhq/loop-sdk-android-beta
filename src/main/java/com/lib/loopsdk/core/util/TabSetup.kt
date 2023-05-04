package com.lib.loopsdk.core.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabSetup(
    var tabFragmentList: List<Fragment>,
    fragmentManager: FragmentManager?,
    var tabsTitle: Array<String>
) : FragmentPagerAdapter(fragmentManager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    override fun getCount(): Int {
        return tabFragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return tabFragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if (tabsTitle.isEmpty()){
            return null
        }else {
           return tabsTitle[position]
        }
    }
}