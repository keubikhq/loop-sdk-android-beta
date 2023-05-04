package com.lib.loopsdk.ui.feature_offers

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.lib.loopsdk.R
import com.lib.loopsdk.ui.feature_offers.available_coupons.AvailableCouponsFragment
import com.lib.loopsdk.ui.feature_offers.your_coupons.YourCouponsFragment

class OffersTabAdapter(
    private val context: Context, fm: FragmentManager, private val noOfTabs: Int
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> AvailableCouponsFragment()
            else -> YourCouponsFragment()
        }
    }
    override fun getCount(): Int {
        return noOfTabs
    }

    override fun getPageTitle(position: Int): CharSequence{
        return when (position) {
            0 -> context.getString(R.string.title_available_coupons)
            else -> context.getString(R.string.title_yours_coupons)
        }
    }
}