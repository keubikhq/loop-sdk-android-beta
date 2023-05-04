package com.lib.loopsdk.core.customViews

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.*
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.LayoutTriggerButtonBinding
import com.lib.loopsdk.ui.feature_landing_home.presentation.LandingHomeActivity
import com.lib.loopsdk.ui.feature_loyalty_intro.presentation.IntroductionActivity
import com.lib.loopsdk.ui.feature_loyalty_intro.presentation.SignUpIntroActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.internal.schedulers.RxThreadFactory
import io.reactivex.rxjava3.schedulers.Schedulers

class TriggerButton
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var binding: LayoutTriggerButtonBinding

    private val disposables by lazy { CompositeDisposable() }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus && ::binding.isInitialized) {
            binding.root.startAnimation(AnimationUtils.loadAnimation(this@TriggerButton.context, R.anim.scale_up))
        }
        else {

        }
    }

    override fun onDetachedFromWindow() {
        disposables.clear()
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        disposables.add(RxBusInitializeRelay.subscribe{ msg->
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.layout_trigger_button,
                this,
                true
            )
            if(!msg.contains("success", true)){
                binding.root.hideView()
            }
            else{
                val brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
                binding.brandTheme = brandTheme
                binding.brandThemeColors = Colors
                binding.root.showView()
                this.layoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                this.layoutParams.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                this.layoutParams = this.layoutParams

                if (brandTheme.launcherMobile.placement != null){
                    when (brandTheme.launcherMobile.placement.label) {
                        "Right" -> {
                            val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                            params.gravity = Gravity.RIGHT
                            params.marginEnd = Utils.dpToPx(this.context, 20)
                            params.marginStart = Utils.dpToPx(this.context, 20)
                            binding.cvRoot.layoutParams = params
                        }
                        "Left" -> {
                            val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                            params.gravity = Gravity.LEFT
                            params.marginEnd = Utils.dpToPx(this.context, 20)
                            params.marginStart = Utils.dpToPx(this.context, 20)
                            binding.cvRoot.layoutParams = params
                        }
                    }
                }

                if (brandTheme.launcherMobile.display != null) {
                    if(brandTheme.launcherMobile.display.label.contains("icon only", true)){
                        binding.tvLabel.hideView()
                        binding.viewSpacing.hideView()
                        binding.ivLauncherIcon.showView()
                        if(brandTheme.launcherMobileIcon.isNullOrEmpty()){
                            binding.root.hideView()
                        }else{
                            Glide.with(context)
                                .load(brandTheme.launcherMobileIcon)
                                .into(binding.ivLauncherIcon)
                        }
                    }else if (brandTheme.launcherMobile.display.label.contains("text only", true)){
                        binding.ivLauncherIcon.hideView()
                        binding.viewSpacing.hideView()
                        binding.tvLabel.showView()
                        if(brandTheme.launcherMobile.label.isNullOrEmpty()){
                            binding.root.hideView()
                        }else{
                            binding.tvLabel.text = brandTheme.launcherMobile.label
                        }
                    }else if (brandTheme.launcherMobile.display.label.contains("text with icon", true)){
                        binding.ivLauncherIcon.showView()
                        binding.viewSpacing.showView()
                        binding.tvLabel.showView()
                        binding.tvLabel.text = brandTheme.launcherMobile.label
                        Glide.with(context)
                            .load(brandTheme.launcherMobileIcon)
                            .into(binding.ivLauncherIcon)
                    }
                }


                binding.root.setOnClickListener {
                    val authToken = Prefs.getString("AUTH_TOKEN")
                    val isNewCustomer = Prefs.getInt("IS_NEW_USER")
                    val isSignupConsentRequired = Prefs.getInt("IS_SIGNUP_CONSENT_REQUIRED")
                    val isJoinNowClicked = Prefs.getBoolean("IS_JOIN_NOW_CLICKED", false)
                    val hasSignedUp = Prefs.getInt("HAS_SIGNED_UP", 0)
                    if(authToken.isNullOrEmpty()){
                        Handler().postDelayed({
                            Intent(context, SignUpIntroActivity::class.java).apply {
                                context.startActivity(this)
                                (this@TriggerButton.context as Activity).overridePendingTransition( android.R.anim.fade_in, android.R.anim.fade_out)
                            }
                        }, 100)
                        binding.root.startAnimation(AnimationUtils.loadAnimation(this@TriggerButton.context, R.anim.scale_down))
                    }else if (hasSignedUp == 0 && isSignupConsentRequired == 1){
                        Handler().postDelayed({
                            Intent(context, IntroductionActivity::class.java).apply {
                                context.startActivity(this)
                                (this@TriggerButton.context as Activity).overridePendingTransition( android.R.anim.fade_in, android.R.anim.fade_out)
                            }
                        },100)
                        binding.root.startAnimation(AnimationUtils.loadAnimation(this@TriggerButton.context, R.anim.scale_down))
                    }else {
                        Handler().postDelayed({
                            Intent(context, LandingHomeActivity::class.java).apply {
                                context.startActivity(this)
                                (this@TriggerButton.context as Activity).overridePendingTransition( android.R.anim.fade_in, android.R.anim.fade_out)
                            }
                        }, 100)
                        binding.root.startAnimation(AnimationUtils.loadAnimation(this@TriggerButton.context, R.anim.scale_down))
                    }
                }
            }

        })

    }


}