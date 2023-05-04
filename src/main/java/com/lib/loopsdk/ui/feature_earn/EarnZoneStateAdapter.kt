package com.lib.loopsdk.ui.feature_earn

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.lib.loopsdk.R
import com.lib.loopsdk.ui.feature_survey.presentation.quizzlet_list.QuizAvailableFragment
import com.lib.loopsdk.ui.feature_survey.presentation.quizzlet_list.SurveyAvailableFragment

class EarnZoneStateAdapter(private val context: Context,
                           fm: FragmentManager,
                           private val noOfTabs: Int):
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    override fun getCount(): Int {
        return noOfTabs
    }
    override fun getItemPosition(`object`: Any): Int {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return PagerAdapter.POSITION_NONE
    }
    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> EarnZoneAvailableFragment()
            else -> EarnZoneCompletedFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Available"
            else -> "Completed"
        }
    }
}