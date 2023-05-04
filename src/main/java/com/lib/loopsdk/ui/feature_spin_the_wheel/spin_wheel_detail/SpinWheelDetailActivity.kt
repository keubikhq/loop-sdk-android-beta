package com.lib.loopsdk.ui.feature_spin_the_wheel.spin_wheel_detail

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.DisplayMetrics
import android.view.View
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.customViews.SwipeToUnlockView
import com.lib.loopsdk.core.rubikstudio_library.LuckyItem
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.CommonDialogUtils
import com.lib.loopsdk.core.util.FullScreenNetworkErrorActivity
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.UIState
import com.lib.loopsdk.core.util.Utils
import com.lib.loopsdk.core.util.getFormattedPoints
import com.lib.loopsdk.core.util.hideView
import com.lib.loopsdk.core.util.invisibleView
import com.lib.loopsdk.core.util.observeInLifecycle
import com.lib.loopsdk.core.util.showView
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.RedeemSpinWheelDto
import com.lib.loopsdk.data.remote.dto.SpinWheelDetailDto
import com.lib.loopsdk.data.remote.dto.UnlockSpinWheelDto
import com.lib.loopsdk.databinding.ActivityWheelOfFortuneBinding
import com.lib.loopsdk.ui.BaseActivity
import com.lib.loopsdk.ui.feature_spin_the_wheel.SpinWheelHelpBSFragment
import com.lib.loopsdk.ui.feature_tier_details_points_wallet.presentation.TierDetailsPointsWalletActivity
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class SpinWheelDetailActivity : BaseActivity(),
    SwipeToUnlockView.Interaction,
    View.OnClickListener {

    private lateinit var binding: ActivityWheelOfFortuneBinding
    private lateinit var viewModel: SpinWheelDetailViewModel
    private var spinWheelTransactionId = ""
    private var spinWheelId = ""
    private var userPoints: String = ""
    var mediaPlayer: MediaPlayer? = null
    private var index: Int = -1
    private var themeData: InitializeDto.Data?=null
    private var data: ArrayList<LuckyItem> = ArrayList()
    private lateinit var spinWheelData: SpinWheelDetailDto.Data
    private lateinit var redeemedSpinWheelData: RedeemSpinWheelDto.Data
    //    private lateinit var buySpinWheelData: BuySpinWheelDto.Data
    private lateinit var benefitsData: SpinWheelDetailDto.Data.Benefit


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_wheel_of_fortune
        )
        initVariables()
        setUpListener()
        if (null != intent?.getStringExtra("spinWheelId")) {
            spinWheelId = intent.getStringExtra("spinWheelId")!!
            getSpinWin()
        }
        if (null != intent?.getStringExtra("spinWheelTransactionId")) {
            spinWheelTransactionId = intent.getStringExtra("spinWheelTransactionId")!!
        }
        setUpViewModel()
    }
    private fun getSpinWin() {
        if (spinWheelId.isNotBlank()) viewModel.getSingleSpinWheelDetails(spinWheelId)
    }
    private fun initVariables() {
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this)[SpinWheelDetailViewModel::class.java]
        val brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandTheme = brandTheme
        binding.brandThemeColors = Colors
        themeData = Prefs.getNonPrimitiveData<InitializeDto.Data>(object: TypeToken<InitializeDto.Data>(){}.type, "INIT_DATA")
    }
    private fun setUpListener() {
        binding.tvTitle.setOnClickListener(this)
        binding.btnTnC.setOnClickListener(this)
        binding.ivClose.setOnClickListener(this)
        binding.btnSpinWheel.setOnClickListener(this)
        binding.btnSwipeToUnlock.setOnClickListener(this)
        binding.cvPoints.setOnClickListener(this)
    }

    private fun setUpViewModel() {
        viewModel.uiStateFlow.onEach {
            when (it) {
                is UIState.Loading -> {
                    Timber.d("onCreate: loading")
                }
                is UIState.Success<*> -> {
                    when (it.data) {
                        is SpinWheelDetailDto.Data -> {
                            this.spinWheelData = it.data
                            userPoints = it.data.totalPoints
                            initWheelView()
                        }
                        is UnlockSpinWheelDto.Data -> {
                            this.spinWheelTransactionId=it.data.spinTransactionId
                            updatePointsAndAttemptsAfterUnlock()
                            showUnlockedState()
                        }
                        is RedeemSpinWheelDto.Data -> {
                            redeemedSpinWheelData = it.data
                            startSpinWheel()
                        }

                    }
                }
                is UIState.Error -> {
                    if (!it.message.isNullOrEmpty()){
                        if (it.message.contains("token", true) ||
                            it.message.contains("unauthorized", true) ||
                            it.message.contains("details not found", true)) {
                            CommonDialogUtils.showSingleButtonLogoutDialog(this, it.message, null)
                        }else if(it.message.contains("Expired", true)){
                            CommonDialogUtils.showSingleButtonAlertDialog(
                                this,
                                it.message,
                                object : CommonDialogUtils.SingleButtonClickListener {
                                    override fun onButtonClicked() {
                                        finish()
                                    }
                                })
                        }
                        else if(it.message == "NetworkError"){
                            FullScreenNetworkErrorActivity.startActivity(this)
                        }else if(it.message.contains("limit", true)){
                            //daily limit exceeds
                            showOutOfSpinUi("Daily attempts used")
                        }
                        else {
                            CommonDialogUtils.showSingleButtonAlertDialog(this, it.message)
                        }
                    }
                }
                else->{}
            }
        }.observeInLifecycle(this)

        viewModel.fragmentToActivityEventFlow.onEach { event ->
            when (event) {
                is SpinWheelDetailViewModel.Event.OnBenefitDialogCloseClicked ->{
                    supportFragmentManager.popBackStackImmediate()
                    onBackPressed()
                }
                is SpinWheelDetailViewModel.Event.OnTryAgainButtonClicked ->{
                    supportFragmentManager.popBackStackImmediate()
                    this.recreate()
                }
            }
        }.observeInLifecycle(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ivClose -> {
                onBackPressed()
            }
            R.id.tvTitle,R.id.btnTnC -> {
                SpinWheelHelpBSFragment(spinWheelData.termsAndConditions).show(
                    supportFragmentManager,
                    "SpinWheelHelpBSFragment"
                )
            }
            R.id.btnSpinWheel -> {
                if(!spinWheelTransactionId.isNullOrEmpty())
                    viewModel.redeemSpinWheel(spinWheelTransactionId)
                binding.btnSpinWheel.isEnabled=false
            }
            R.id.cvPoints ->{
                TierDetailsPointsWalletActivity.startActivity(this,2)
            }
            R.id.btnSwipeToUnlock ->{
                if(!spinWheelId.isNullOrEmpty())
                    viewModel.unlockSpinWheel(spinWheelId)
            }
        }
    }
    override fun onSwipeEndReachedAndReleased() {
//        if(!spinWheelId.isNullOrEmpty())
//            viewModel.unlockSpinWheel(spinWheelId)
    }
    override fun onSwipeStartReachedAndReleased() {
    }
    fun getScreenSize():Boolean
    {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        if(width<=720 || height<=1500)
            return true
        else
            return false
    }
    fun startMediaPlayer(view: String) {
        if (view == "success") {
            mediaPlayer = MediaPlayer.create(this, R.raw.wheel_success_state_sound)
            mediaPlayer!!.start()
        } else if (view == "spinning") {
            mediaPlayer = MediaPlayer.create(this, R.raw.wheel_spinning_sound)
            mediaPlayer!!.start()
        } else {
            mediaPlayer = MediaPlayer.create(this, R.raw.negative_state_sound)
            mediaPlayer!!.start()
        }
    }
    fun stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }
    fun initWheelView() {
        data.clear()
        binding.luckyWheelView.setData(data)
        binding.luckyWheelView.isTouchEnabled = false
        binding.luckyWheelView.setBorderColor(resources.getColor(android.R.color.transparent))
        binding.luckyWheelView.setLuckyWheelBackgrouldColor(resources.getColor(R.color.bg_gray))
        //set data in Wheel
        if (spinWheelData.benefits.size == 3) {

            val wheelData = spinWheelData.benefits
            for (i in 0 until wheelData.size) {
                val androidColors = resources.getIntArray(R.array.spin_background_3)
                drawItemsOnWheel(wheelData[i], androidColors[i],i+1)
            }
        }else if (spinWheelData.benefits.size == 4) {
            val wheelData = spinWheelData.benefits
            for (i in 0 until wheelData.size) {
                val androidColors = resources.getIntArray(R.array.spin_background_4)
                drawItemsOnWheel(wheelData[i], androidColors[i],i+1)
            }
        }else if (spinWheelData.benefits.size == 5) {
            val wheelData = spinWheelData.benefits
            for (i in 0 until wheelData.size) {
                val androidColors = resources.getIntArray(R.array.spin_background_5)
                drawItemsOnWheel(wheelData[i], androidColors[i],i+1)
            }
        }
        else if (spinWheelData.benefits.size == 6) {
            val wheelData = spinWheelData.benefits
            for (i in 0 until wheelData.size) {
                val androidColors = resources.getIntArray(R.array.spin_background_6)
                drawItemsOnWheel(wheelData[i], androidColors[i],i+1)
            }
        }
        else if (spinWheelData.benefits.size == 7) {
            val wheelData = spinWheelData.benefits
            for (i in 0 until wheelData.size) {
                val androidColors = resources.getIntArray(R.array.spin_background_7)
                drawItemsOnWheel(wheelData[i], androidColors[i],i+1)
            }
        }
        updatePointsAndAttempts()
        //updateTitleUI(spinWheelData.name)
        binding.tvTitle.text = spinWheelData.name
        if(spinWheelTransactionId.isNullOrEmpty()) setInitialState(spinWheelData)
        else showUnlockedState()
    }
    private fun drawItemsOnWheel(
        wheelData: SpinWheelDetailDto.Data.Benefit,
        androidColors: Int,
        blockIndex: Int
    ) {
        when (wheelData.benefitType) {
            1 -> {
                val luckyItem = LuckyItem()
                luckyItem.color = androidColors
                when(wheelData.benefitDetails.couponClassification){
                    1,2->{
                        luckyItem.topText =themeData!!.defaultCurrency.symbol+ wheelData.benefitDetails.maxDiscountValue.toString()
                        luckyItem.secondaryText ="voucher"
                    }
                    3,4->{
                        luckyItem.topText =wheelData.benefitDetails.discountPercent.toString()+"%"
                        luckyItem.secondaryText = "discount"
                    }
                    5->{
                        //free shipping
                        luckyItem.topText ="free"
                        luckyItem.secondaryText ="delivery"
                    }
                    6->{
                        //Other
                        luckyItem.icon =
                            BitmapFactory.decodeResource(resources, R.drawable.ic_gift_on_wheel)
                    }
                }
                luckyItem.isSmallDevice = getScreenSize()
                data.add(luckyItem)
            }
            2-> {
                val luckyItem = LuckyItem()
                luckyItem.color = androidColors
                luckyItem.topText =
                    getFormattedPoints(Math.round(wheelData.amount.toString().toFloat()).toString())
                if(wheelData.amount.toString().toInt()>1){
                    luckyItem.secondaryText =(themeData!!.pointsIdentifier.pointsLabelPlural).toLowerCase()
                }else{
                    luckyItem.secondaryText =(themeData!!.pointsIdentifier.pointsLabelSingular).toLowerCase()
                }
                luckyItem.isSmallDevice = getScreenSize()
                data.add(luckyItem)
            }
            3 -> {
                val luckyItem = LuckyItem()
                luckyItem.color = androidColors
                luckyItem.icon =
                    BitmapFactory.decodeResource(resources, R.drawable.ic_message_on_wheel)
                luckyItem.isSmallDevice = getScreenSize()
                data.add(luckyItem)
            }
        }
        Handler().postDelayed({
            binding.luckyWheelView.setData(data)
            binding.luckyWheelView.setRound(2)
        }, 400)
    }

    private fun updatePointsAndAttempts(){
        binding.tvUserPoints.text = getFormattedPoints(userPoints)
        binding.tvAttempts.text = spinWheelData.remaingAttempts.toString()
    }
    private fun updatePointsAndAttemptsAfterUnlock(){
        // update points  after unlock
        if (spinWheelData.pointsToUnlock !=0) {
            val roundedPoints: Int = userPoints.split(".")[0].toInt()
            userPoints = (roundedPoints - spinWheelData.pointsToUnlock).toString()
        }
        binding.tvUserPoints.text = getFormattedPoints(userPoints)
        binding.tvAttempts.text = (spinWheelData.remaingAttempts-1).toString()
    }
    private fun updateTitleUI(title:String){
        Timber.d("updateTitleUI"+title)
        Timber.d("updateTitleUI"+title.length)
            val primaryColor: Int= Color.parseColor(binding.brandThemeColors!!.PRIMARY_BRAND_COLOR)
            val drawable: Drawable = resources.getDrawable(R.drawable.ic_info)
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            drawable.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(primaryColor,
            BlendModeCompat.SRC_IN)
            val image = ImageSpan(drawable, ImageSpan.ALIGN_BASELINE)
            val ss1 = SpannableString(title + " |")
            ss1.setSpan(image, ss1.indexOf("|"), ss1.indexOf("|") + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            if(title.length > 30){
                binding.tvTitle.hideView()

            }else{
                binding.tvTitle.showView()
                binding.tvTitle.text = ss1
            }
    }
    private fun setInitialState(data: SpinWheelDetailDto.Data) {
        binding.ivDivider.showView()
        binding.btnSpinWheel.isEnabled=false
        binding.llUnlock.showView()
        if (data.remaingAttempts > 0 && data.canAttempt!=0) {
            if (data.pointsToUnlock == 0) {
                // free spin
                binding.tvUnlockFor.text = "Unlock to Spin the Wheel"
                binding.ivPointIcon.hideView()
                binding.tvPoints.hideView()
                showInActiveWheelState(true)
            } else if (data.remaningPoints != 0) {
                // limited points
                binding.tvUnlockFor.text = "Earn"
                binding.ivPointIcon.showView()
                binding.tvPoints.showView()
                if (data.pointsToUnlock >1) {
                    binding.tvPoints.text =
                        data.remaningPoints.toString() + " " + themeData!!.pointsIdentifier.pointsLabelPlural+" more to unlock"
                } else {
                    binding.tvPoints.text =
                        data.remaningPoints.toString() + " " + themeData!!.pointsIdentifier.pointsLabelSingular+" more to unlock"
                }
                showInActiveWheelState(false)
            } else {
                // unlock with points
                binding.tvUnlockFor.text = "Unlock for"
                binding.ivPointIcon.showView()
                binding.tvPoints.showView()
                if (data.pointsToUnlock >1) {
                    binding.tvPoints.text =
                        data.pointsToUnlock.toString() + " " + themeData!!.pointsIdentifier.pointsLabelPlural
                } else {
                    binding.tvPoints.text =
                        data.pointsToUnlock.toString() + " " + themeData!!.pointsIdentifier.pointsLabelSingular
                }
                showInActiveWheelState(true)
            }
        }else if (data.remaingAttempts > 0 && data.canAttempt==0) {
            showOutOfSpinUi("Daily attempts used")
        }
        else {
            showOutOfSpinUi("All attempts used")
        }
    }
    private fun showInActiveWheelState(isActive: Boolean) {
//        binding.swipeToUnlock.showView()
        binding.btnSwipeToUnlock.showView()
        if (isActive) {
//            binding.swipeToUnlock.enableSwipeToUnlock()
            binding.clspinwheellayout.alpha = 1f
            binding.ivWheelArrow.alpha = 1f
            enableUnlockButton()
        } else {
            binding.ivLock.showView()
            binding.tvSpin.hideView()
            binding.ivWheelArrow.alpha = 0.5f
            binding.clspinwheellayout.alpha = 0.5f
            disableUnlockButton()
//            binding.swipeToUnlock.disableSwipeToUnlock()
        }
    }
    private fun showOutOfSpinUi(title: String) {
        binding.btnSwipeToUnlock.hideView()
//        binding.swipeToUnlock.hideView()
//        binding.swipeToUnlock.resetSwipeToUnlock()
        binding.tvOutOfSpin.showView()
        binding.tvUnlockFor.text=title
        binding.ivPointIcon.hideView()
        binding.tvPoints.hideView()
        binding.ivWheelArrow.alpha = 0.5f
        binding.clspinwheellayout.alpha = 0.5f
        binding.ivLock.showView()
        binding.tvSpin.hideView()
    }

    private fun showUnlockedState() {
        binding.btnSpinWheel.isEnabled=true
        binding.llUnlockedState.showView()
        binding.llAttemps.invisibleView()
        binding.llUnlock.hideView()
        binding.tvOutOfSpin.hideView()
        binding.ivDivider.invisibleView()
//        binding.swipeToUnlock.resetSwipeToUnlock()
//        binding.swipeToUnlock.hideView()
        binding.btnSwipeToUnlock.hideView()
    }

    fun startSpinWheel(){
        var tempIndex = -1
        for (i in spinWheelData.benefits) {
            tempIndex += 1
            if (i.id == redeemedSpinWheelData.benefitId) {
                benefitsData = i
                index = tempIndex
                break
            }
        }
        binding.luckyWheelView.startLuckyWheelWithTargetIndex(index)
        //startMediaPlayer("spinning")
        binding.luckyWheelView.setLuckyRoundItemSelectedListener {
            Handler().postDelayed({
                //donot show success anim on message
                //stopMediaPlayer()
                Utils.showFragment(
                    supportFragmentManager,
                    SpinWheelSuccessDialogFragment(
                        redeemedSpinWheelData,
                        binding.tvAttempts.text.toString().toInt(),
                        !intent?.getStringExtra("spinWheelTransactionId").isNullOrEmpty()
                    ),
                    "SUCESS_DIALOG_FRAGMENT"
                )
            }, 1500)
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 1) {
            //do nothing
        } else {
            super.onBackPressed()
        }
    }

    private fun enableUnlockButton() {
        binding.btnSwipeToUnlock.isEnabled=true
        binding.btnSwipeToUnlock.alpha=1f
    }

    private fun disableUnlockButton() {
        binding.btnSwipeToUnlock.isEnabled=false
        binding.btnSwipeToUnlock.alpha=0.5f
    }
}