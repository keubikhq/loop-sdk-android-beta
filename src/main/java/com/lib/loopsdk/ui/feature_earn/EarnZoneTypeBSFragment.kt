package com.lib.loopsdk.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Constants
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.hideView
import com.lib.loopsdk.core.util.showView
import com.lib.loopsdk.data.remote.dto.EarnZoneTaskData
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.FragmentEarnZoneTypeBottmSheetBinding
import com.lib.loopsdk.ui.feature_earn.DatePickerDialogFragment

class EarnZoneTypeBottmSheetFragment(
    private val task: EarnZoneTaskData,
    private val listener: TaskCompleteListener
): BottomSheetDialogFragment() ,View.OnClickListener, DatePickerDialogFragment.DateSelectedListener{
    private lateinit var binding: FragmentEarnZoneTypeBottmSheetBinding

    var dateSelected:String=""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: BottomSheetDialog =
            super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            val mDialog: BottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet: FrameLayout = mDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        }
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ccffffff")))
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_NoActionBar_FullScreen)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEarnZoneTypeBottmSheetBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        initializeVariables()
        setUpListener()
    }
    fun initializeVariables(){
        when(task.type){
            Constants.EARN_ZONE_TYPE.BIRTHDAY.type->{
               showHideBirthAndAnniUI(true)
                binding.cvTypeIcon.setImageDrawable(
                    ContextCompat.getDrawable(binding.root.context,
                        R.drawable.ic_birthday_cake))
                binding.tvBirthOrAnniType.text="Enter Birthdate"
                binding.tvTypeName.text ="Add Birthday"

            }
            Constants.EARN_ZONE_TYPE.ANNIVERSARY.type->{
                showHideBirthAndAnniUI(true)
                binding.cvTypeIcon.setImageDrawable(
                    ContextCompat.getDrawable(binding.root.context,
                        R.drawable.ic_anniversary))
                binding.tvTypeName.text ="Add Anniversary"
                binding.tvBirthOrAnniType.text="Enter Anniversary Date"
            }
            Constants.EARN_ZONE_TYPE.YOUTUBE.type->{
                showHideBirthAndAnniUI(false)
                binding.cvTypeIcon.setImageDrawable(
                    ContextCompat.getDrawable(binding.root.context,
                        R.drawable.ic_youtube))
                binding.tvTypeName.text ="Like video on Youtube"
                binding.tvAddOrShare.text="Open Youtube"
                binding.tvShareDesc.text="click to open post and like"

            }
            Constants.EARN_ZONE_TYPE.FACEBOOK.type->{
                showHideBirthAndAnniUI(false)
                binding.cvTypeIcon.setImageDrawable(
                    ContextCompat.getDrawable(binding.root.context,
                        R.drawable.ic_fb))
                binding.tvTypeName.text ="Follow page on Facebook"
                binding.tvAddOrShare.text="Open Facebook"
                binding.tvShareDesc.text="click to open post and like"
            }
            Constants.EARN_ZONE_TYPE.LINKEDIN.type->{
                showHideBirthAndAnniUI(false)
                binding.cvTypeIcon.setImageDrawable(
                    ContextCompat.getDrawable(binding.root.context,
                        R.drawable.ic_linkedin))
                binding.tvTypeName.text ="Like post on LinkedIn"
                binding.tvAddOrShare.text="Open LinkedIn"
                binding.tvShareDesc.text="click to open post and like"
            }
            Constants.EARN_ZONE_TYPE.TWITTER.type->{
                showHideBirthAndAnniUI(false)
                binding.cvTypeIcon.setImageDrawable(
                    ContextCompat.getDrawable(binding.root.context,
                        R.drawable.ic_twitter))
                binding.tvTypeName.text ="Share post on Twitter"
                binding.tvAddOrShare.text="Share on Twitter"
                binding.tvShareDesc.text="click to share"
            }
            Constants.EARN_ZONE_TYPE.INSTALIKE.type->{
                showHideBirthAndAnniUI(false)
                binding.cvTypeIcon.setImageDrawable(
                    ContextCompat.getDrawable(binding.root.context,
                        R.drawable.ic_instagram))
                binding.tvTypeName.text ="Like post on Instagram"
                binding.tvAddOrShare.text="Open Instagram"
                binding.tvShareDesc.text="click to open post and like"
            }
            Constants.EARN_ZONE_TYPE.INSTAFOLLOW.type->{
                showHideBirthAndAnniUI(false)
                binding.cvTypeIcon.setImageDrawable(
                    ContextCompat.getDrawable(binding.root.context,
                        R.drawable.ic_instagram))
                binding.tvTypeName.text ="Follow page on Instagram"
                binding.tvAddOrShare.text="Open Instagram"
                binding.tvShareDesc.text="click to open page and follow"
            }
        }

    }
    private fun showHideBirthAndAnniUI(isActive:Boolean){
        if (isActive){
            binding.llBirthOrAnniType.showView()
            binding.viewDivider.showView()
            binding.tvBirthOrAnniType.showView()
            binding.tvAddOrShare.text="Add"
            binding.tvShareDesc.hideView()
            disableButton()
        }else{
            binding.llBirthOrAnniType.hideView()
            binding.viewDivider.hideView()
            binding.tvBirthOrAnniType.hideView()
            binding.tvShareDesc.showView()
            enableButton()
        }
    }
    private fun setUpListener() {
        binding.btnAddOrShare.setOnClickListener(this)
        binding.ivPickDate.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
      when(v?.id){
          R.id.btnAddOrShare->{
              when(task.type){
                  Constants.EARN_ZONE_TYPE.BIRTHDAY.type,Constants.EARN_ZONE_TYPE.ANNIVERSARY.type->{
                        listener.onTaskCompleteClicked(task,dateSelected)
                  }
                  else->{
                      listener.onTaskCompleteClicked(task)
                  }
              }
                closeBottomSheet()
          }
          R.id.ivPickDate->{
              val  datePickerDialogFragment =  DatePickerDialogFragment(this)
              if(!datePickerDialogFragment.isVisible)
                  datePickerDialogFragment.show(childFragmentManager.beginTransaction(), datePickerDialogFragment.getTag())
          }
      }
    }
    public fun closeBottomSheet() {
        this.dismiss()
    }
    interface TaskCompleteListener {
        fun onTaskCompleteClicked(type:EarnZoneTaskData,date:String?="")
    }
    private fun enableButton() {
        binding.btnAddOrShare.isEnabled=true
        binding.btnAddOrShare.alpha=1f
    }

    private fun disableButton() {
        binding.btnAddOrShare.isEnabled=false
        binding.btnAddOrShare.alpha=0.5f
    }

    override fun onDateSelectedListener(date: String, dateAPIFormat: String) {

        if(!date.isEmpty()){
            binding.tvDate.text=date
            this.dateSelected=dateAPIFormat
            enableButton()
        }
    }

}