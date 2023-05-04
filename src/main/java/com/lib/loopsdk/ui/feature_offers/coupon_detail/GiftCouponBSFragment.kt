package com.lib.loopsdk.ui.feature_offers.coupon_detail

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.ContactsContract
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Colors
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.hideView
import com.lib.loopsdk.core.util.isValidEmail
import com.lib.loopsdk.core.util.showView
import com.lib.loopsdk.data.remote.dto.CountryCodesDto
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.data.remote.dto.TransferCouponDto
import com.lib.loopsdk.databinding.FragmentGiftCouponBSBinding


class GiftCouponBSFragment(
    private val listener: GiftCouponListener,
    private  val couponTransactionId: String
): BottomSheetDialogFragment() ,View.OnClickListener ,
    CountryCodeBSFragment.CountryCodeSelectedListener {

    private lateinit var binding: FragmentGiftCouponBSBinding
    private lateinit var themeData:InitializeDto.Data
    private var selectedCountryCode: CountryCodesDto.Data.CountryCode? = null
    private var defaultCountryCode: CountryCodesDto.Data.CountryCode? = null
    private var countryCodesList:List<CountryCodesDto.Data.CountryCode> = arrayListOf()
    private lateinit var inputFilter: InputFilter
    val PICK_CONTACT = 1
    private val contactPickerRequest = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val phone = getSelectedContact(it.data)
            binding.etGiftType.text!!.clear()
            binding.etGiftType.setText(phone)
        }
    }
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
        binding = FragmentGiftCouponBSBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVariables()
    }
    private fun initVariables(){
        binding.lifecycleOwner = viewLifecycleOwner
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        themeData = Prefs.getNonPrimitiveData<InitializeDto.Data>(object: TypeToken<InitializeDto.Data>(){}.type, "INIT_DATA")
        disableButton()

        //initial state
        if(themeData.primaryUserId==1){
            //gift via email
            binding.tvGiftDesc.text = "Enter the email address of whom you’d like to gift"
            binding.tvErrorMessage.text = "Incorrect format, enter valid email address"
            binding.tvGiftType.text="Email Address"
            binding.etGiftType.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            binding.tvCountryCode.hideView()
            binding.ivContact.hideView()
            binding.ivCountryCode.hideView()

        }else{
            //gift via number
            binding.tvGiftDesc.text = "Enter the mobile number of whom you’d like to gift"
            binding.tvErrorMessage.text = "Invalid number"
            binding.tvGiftType.text="Phone Number"
            binding.etGiftType.inputType = InputType.TYPE_CLASS_PHONE
            binding.tvCountryCode.showView()
            binding.ivContact.showView()
            binding.ivCountryCode.showView()
            setDefaultCountryCode()

            this.inputFilter = object : InputFilter {
                override fun filter(
                    source: CharSequence,
                    start: Int,
                    end: Int,
                    dest: Spanned?,
                    dstart: Int,
                    dend: Int
                ): CharSequence? {
                    for (i in start until end) {
                        if (!Character.isDigit(source[i])
                        ) {
                            Toast.makeText(binding.root.context, "Please remove special characters", Toast.LENGTH_SHORT).show()
                            return ""
                        }
                    }
                    return null
                }
            }

        }

        binding.etGiftType.addTextChangedListener {
            if(themeData.primaryUserId==1) {
                if (it.toString().isValidEmail()) {
                    enableButton()
                    showHideErrorState(false)
                } else {
                    if (it.toString().isEmpty()) {
                        showHideErrorState(false)
                    } else {
                        showHideErrorState(true)
                    }
                    disableButton()
                }
            }else{
                // for Number
                checkIfInputIsProperDigits()
            }
        }

        binding.btnSendGift.setOnClickListener {
            if(themeData.primaryUserId==1){
                //gift via email
                listener.onGiftViaEmail(binding.etGiftType.editableText.toString().trim(),couponTransactionId)
            }else{
                //gift via number
                listener.onGiftViaMobile(binding.etGiftType.editableText.toString().trim(),binding.tvCountryCode.text.toString(),couponTransactionId)
            }
        }
        binding.ivCountryCode.setOnClickListener {
            val countryCodeBSFragment =  CountryCodeBSFragment(this)
            if(!countryCodeBSFragment.isVisible)
                countryCodeBSFragment.show(childFragmentManager.beginTransaction(), countryCodeBSFragment.getTag())
        }
        binding.ivContact.setOnClickListener{
            openContactPicker()
        }
    }

    private fun openContactPicker() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS), 123)
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            contactPickerRequest.launch(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123 && grantResults.contains(
                PackageManager.PERMISSION_GRANTED)) {
            openContactPicker()
        }
    }
    private fun getSelectedContact(data: Intent?): String {
        val uri = data?.data
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts._ID
        )
        val cursor = uri?.let { requireContext().contentResolver?.query(it, projection, null, null, null) }
        cursor?.moveToFirst()
        var number = ""
        val temp = cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) ?: "0"
        if ( temp.toInt() > 0 && cursor != null) {
            val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val pCur = requireContext().contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null
            )
            pCur?.moveToFirst()
            number =
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            pCur?.close()
        }
        //val nameIndex = cursor?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val phone = number.trim().replace(" ", "").replace("+91","")
        //val name = cursor.getString(nameIndex).toString()
        cursor?.close()
        return phone
    }


    private fun setDefaultCountryCode() {

        countryCodesList = Prefs.getNonPrimitiveData<List<CountryCodesDto.Data.CountryCode>>(object: TypeToken<List<CountryCodesDto.Data.CountryCode>>(){}.type, "COUNTRY_CODE_LIST")

        for (country in countryCodesList) {
            if(country.name.contains("india", true)&& country.code.equals("+91"))
            {
                this.defaultCountryCode=country
                countryCodesList[countryCodesList.indexOf(this.defaultCountryCode!!)].isSelected = true
            }
        }

        Prefs.putNonPrimitiveData("COUNTRY_CODE_LIST", countryCodesList)
        binding.etGiftType.length()
        binding.etGiftType.filters= arrayOf(InputFilter.LengthFilter(this.defaultCountryCode!!.length))

        binding.tvCountryCode.text = this.defaultCountryCode!!.code
        this.selectedCountryCode = this.defaultCountryCode!!


    }
    private fun enableButton() {
        if(context == null) return
        binding.btnSendGift.isEnabled = true
        binding.btnSendGift.alpha = 1f
    }
    private fun disableButton() {
        if(context == null) return
        binding.btnSendGift.isEnabled = false
        binding.btnSendGift.alpha = 0.3f
    }


    interface  GiftCouponListener{
        fun onGiftViaEmail(email:String,couponTransactionId:String)
        fun onGiftViaMobile(mobile:String,countryCode:String,couponTransactionId:String)
    }

    override fun onCountryCodeSelected(countryCode: CountryCodesDto.Data.CountryCode) {
        //enable button

        countryCodesList.forEach { it.isSelected = false }
        for (country in countryCodesList) {
            if(country.name.contains(countryCode.name, true)&& country.code.equals(countryCode.code))
            {
                this.selectedCountryCode=country
                countryCodesList[countryCodesList.indexOf(this.selectedCountryCode!!)].isSelected = true
            }
        }
        binding.tvCountryCode.text = selectedCountryCode!!.code
        Prefs.putNonPrimitiveData("COUNTRY_CODE_LIST", countryCodesList)
        binding.etGiftType.filters= arrayOf(InputFilter.LengthFilter(this.selectedCountryCode!!.length))
    }
    private fun checkIfInputIsProperDigits() {
        if (binding.etGiftType.text.toString().isEmpty()) {
            showHideErrorState(false)
        }else if (binding.etGiftType.text.toString().length < this.selectedCountryCode!!.length){
            disableButton()
            showHideErrorState(true)
        }else if (binding.etGiftType.text.toString().length == this.selectedCountryCode!!.length){
            enableButton()
            showHideErrorState(false)
        }else if (binding.etGiftType.text.toString().length > this.selectedCountryCode!!.length){
            disableButton()
            showHideErrorState(true)
        }
    }
    private fun showHideErrorState(status:Boolean){
        if(status){
            //show
            binding.errorContainer.showView()
            binding.viewErrorLine.setBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.error_red))
        }else{
            //hide
            binding.errorContainer.hideView()
            binding.viewErrorLine.setBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.text_light_gray))
        }
    }
    fun showDetailAfterTransfer(couponData : TransferCouponDto.Data) {
        binding.clGiftLayout.hideView()
        binding.clSuccessGiftLayout.showView()
    }

}