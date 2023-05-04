package com.lib.loopsdk.core.customViews

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.hideView
import com.lib.loopsdk.core.util.showView
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.LayoutSwipeToUnlockBinding
import timber.log.Timber

class SwipeToUnlockView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr), View.OnTouchListener {

    private var yDelta = 0
    private var xDelta = 0
    private var dragViewXCoordinate: Float = 0.0F
    private lateinit var binding: LayoutSwipeToUnlockBinding
    private var mInteraction: Interaction? = null

    init {
        if(!isInEditMode) {
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.layout_swipe_to_unlock,
                this,
                true
            )
            val brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
            binding.brandTheme = brandTheme
            binding.brandThemeColors = Colors

            binding.cvSwipeToUnlock.setOnTouchListener(this)
        }

    }

    fun setSwipeToUnlockListener(interaction: Interaction){
        mInteraction = interaction
    }

    fun resetSwipeToUnlock(){
        binding.cvLoad.hideView()
        binding.tvSwipeToUnlock.text = "Swipe to unlock"
        binding.cvSwipeToUnlock.x = dragViewXCoordinate
        binding.cvSwipeToUnlock.showView()
        resetView(binding.cvSwipeToUnlock)
    }

    fun disableSwipeToUnlock(){
        binding.cvDisableHighlight.showView()
    }

    fun enableSwipeToUnlock(){
        binding.cvDisableHighlight.hideView()
    }

    interface Interaction {
        fun onSwipeEndReachedAndReleased()
        fun onSwipeStartReachedAndReleased()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (v?.id) {
            binding.cvSwipeToUnlock.id -> {
                val y = event!!.rawY
                val x = event.rawX
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        xDelta = (v.x - x).toInt()
                        yDelta = (v.y - y).toInt()
                    }
                    MotionEvent.ACTION_UP -> {
                        val newX = x + xDelta
                        Timber.d("ACTION_UP"+newX)
                        if(getScreenSize()){
                            if (newX >= (550)) {
                               swipToUnlockState(v)
                            }else {
                                resetView(v)
                            }
                        }else{
                            if (newX >= (700)) {
                                swipToUnlockState(v)
                            }else {
                                resetView(v)
                            }
                        }

                    }
                    MotionEvent.ACTION_MOVE -> {
                        val newX = x + xDelta
                        if (newX >= binding.btnEmpty.x && newX <= binding.cvLoad.x) {
                            v.animate().x(newX).setDuration(0).start()
                        }
                    }
                    else -> return false
                }
            }
        }
        return true
    }
    fun swipToUnlockState(v: View){
        binding.cvSwipeToUnlock.hideView()
        binding.tvSwipeToUnlock.text="Unlocking"
        binding.cvLoad.showView()
        v.animate().x(binding.cvSwipeToUnlock.x).setDuration(1000).start()
        mInteraction?.onSwipeEndReachedAndReleased()
    }

    fun resetView(v: View) {
        v.animate().x(dragViewXCoordinate).setDuration(1000).start()
        mInteraction?.onSwipeStartReachedAndReleased()
    }

    fun getScreenSize():Boolean
    {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        if(width<=720 || height<=1500)
            return true
        else
            return false
    }


}