package com.lib.loopsdk.ui.feature_loyalty_intro.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.ListItemIntroSlideBinding

class IntroSliderAdapter(
    private val slidesList: ArrayList<IntroSlide>,
    private val interaction: Interaction? = null
): PagerAdapter() {

    override fun getCount(): Int {
        return slidesList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == (`object` as ListItemIntroSlideBinding).root
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ListItemIntroSlideBinding.inflate(
            LayoutInflater.from(container.rootView.context),
            container,
            false
        )
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        binding.executePendingBindings()
        binding.ivSliderPhoto.setImageResource(slidesList[position].drawableResource)
        binding.tvSlideText.text = slidesList[position].text

        binding.root.setOnClickListener { interaction?.onCoverImageItemClicked(position) }

        container.addView(binding.root, 0)

        return binding
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView((`object` as ListItemIntroSlideBinding).root)
    }

    interface Interaction{

        fun onCoverImageItemClicked(pos: Int)
    }
}
