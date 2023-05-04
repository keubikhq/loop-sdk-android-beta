package com.lib.loopsdk.core.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Constants.RC_NETWORK_ERROR_SCREEN
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.databinding.LayoutNetworkErrorBinding

class FullScreenNetworkErrorActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var myContext: Context
    private lateinit var binding: LayoutNetworkErrorBinding

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, FullScreenNetworkErrorActivity::class.java)
            activity.startActivityForResult(intent, RC_NETWORK_ERROR_SCREEN)
        }

        fun startActivity(fragment: Fragment, context: Context) {
            val intent = Intent(context, FullScreenNetworkErrorActivity::class.java)
            fragment.startActivityForResult(intent, RC_NETWORK_ERROR_SCREEN)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable()) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()

        } else {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_network_error)
        binding.brandTheme = Prefs.getNonPrimitiveData<InitializeDto.Data.BrandTheme>(object: TypeToken<InitializeDto.Data.BrandTheme>(){}.type, "BRAND_THEME")
        binding.brandThemeColors = Colors
        initializeVariables()
        initViews()
        setupListeners()
    }

    private fun initializeVariables() {
        myContext = this
        binding.clEmptyStateNoInternet.showView()
    }

    private fun initViews() {
        supportActionBar?.hide()
    }

    private fun setupListeners() {
        binding.btnReload.setOnClickListener(this)
 //       binding.btnGoToSettings.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnReload -> {
                if (isNetworkAvailable()) {
                    val intent = Intent()
                    setResult(RESULT_OK, intent)
                    finish()
                } else {

                }
            }
//            R.id.btnGoToSettings -> {
//                myContext.redirectToNetworkSettings()
//            }
        }
    }

    override fun onBackPressed() {
        if (isNetworkAvailable()) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        } else {
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
    }
}