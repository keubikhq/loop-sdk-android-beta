package com.lib.loopsdk.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.lib.loopsdk.core.helper.ConnectivityLiveData
import com.lib.loopsdk.core.util.showNetworkOnlineSnackBar

abstract class BaseActivity : AppCompatActivity() {

    var snackbar: Snackbar? = null

    var wasOfflineShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wasOfflineShown = false
        ConnectivityLiveData(this).observe(this, {
            if(!it.isConnected){
                wasOfflineShown = true
            }else{
                //snackbar?.dismiss()
                if(wasOfflineShown){
                    showNetworkOnlineSnackBar()
                    wasOfflineShown = false
                }
            }
        })
    }
}