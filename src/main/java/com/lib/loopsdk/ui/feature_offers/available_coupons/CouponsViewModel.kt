package com.lib.loopsdk.ui.feature_offers.available_coupons

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lib.loopsdk.core.util.Constants
import com.lib.loopsdk.core.util.UIState
import com.lib.loopsdk.data.remote.ApiClient
import com.lib.loopsdk.data.remote.customnetworkadapter.NetworkResponse
import com.lib.loopsdk.data.remote.dto.CouponTypeData
import com.lib.loopsdk.data.remote.dto.GetWalletPointsDto
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class CouponsViewModel: ViewModel() {

    private val apiService = ApiClient.initRetrofit()

    val selectedChipList: ArrayList<CouponTypeData> = arrayListOf()
    val categoriesList: ArrayList<CouponTypeData> = arrayListOf()

    val selectedCategoriesList: ArrayList<String> = arrayListOf()

    var userBalance: Int=0

    var filterByBalance: Boolean=false
    var isPriceRangeSelected: Boolean=false


    var userSelectedMaxRange: Int=0
    var userSelectedMinRange: Int=0

    private val unlockingUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val unlockingUiStateFlow = unlockingUiStateChannel.receiveAsFlow()

    private val filterSheetEventChannel = Channel<Event> { Channel.BUFFERED }
    val filterSheetEventFlow = filterSheetEventChannel.receiveAsFlow()

    private val defaultListUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val defaultListUiStateFlow = defaultListUiStateChannel.receiveAsFlow()

    private val searchFilterUiStateChannel = Channel<UIState>(Channel.BUFFERED)
    val searchFilterUiStateFlow = searchFilterUiStateChannel.receiveAsFlow()


    private val fragmentToActivityEventChannel = Channel<Event>(Channel.BUFFERED)
    val fragmentToActivityEventFlow = fragmentToActivityEventChannel.receiveAsFlow()


    init {
//        getCountryCodes()
        addCouponTypeList()
    }
    fun clearAllListSelection(){
        categoriesList.forEach {
            it.isSelected = false
        }
    }

    fun addCouponTypeList(){
        val couponTypeData= CouponTypeData()
        couponTypeData.CouponTypeSerialNo=2
        couponTypeData.CouponTypeId="1,2"
        couponTypeData.CouponTypeTitle="Amount Off"
        categoriesList.add(couponTypeData)
        val couponTypeData1= CouponTypeData()
        couponTypeData1.CouponTypeSerialNo=3
        couponTypeData1.CouponTypeId="3,4"
        couponTypeData1.CouponTypeTitle="Percentage Discount"
        categoriesList.add(couponTypeData1)
        val couponTypeData2= CouponTypeData()
        couponTypeData2.CouponTypeSerialNo=4
        couponTypeData2.CouponTypeId="5"
        couponTypeData2.CouponTypeTitle="Shipping Discounts"
        categoriesList.add(couponTypeData2)
        val couponTypeData3= CouponTypeData()
        couponTypeData3.CouponTypeSerialNo=5
        couponTypeData3.CouponTypeId="6"
        couponTypeData3.CouponTypeTitle="Others"
        categoriesList.add(couponTypeData3)
    }





    fun getDefaultCouponsList(pageNo: Int){

        viewModelScope.launch {

            defaultListUiStateChannel.send(UIState.Loading)

            val response = apiService.getAvailableCoupons(pageNo)
            when(response){

                is NetworkResponse.Success ->{
                    if(response.body.status.code != 200){
                        defaultListUiStateChannel.send(UIState.Error(response.body.status.message))
                    }else{
                        defaultListUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.NetworkError ->{
                    defaultListUiStateChannel.send(UIState.Error("NetworkError"))
                }

                is NetworkResponse.ServerError ->{
                    defaultListUiStateChannel.send(UIState.Error(response.body?.status?.code.toString()))
                }

                is NetworkResponse.UnknownError ->{
                    defaultListUiStateChannel.send(UIState.Error("UnknownError"))
                }

            }
        }
    }


    var searchAndFilterJob: Job? = null
    fun searchAndFilterCouponsList(searchParam:String,pageNo: Int, context: Context){
        val hashMap = HashMap<String, String>()

        if(filterByBalance){
            hashMap["FilterByBalance"] = filterByBalance.toString()
        }
        if(isPriceRangeSelected){
            hashMap["FilterByRange"] = userSelectedMinRange.toString()+","+userSelectedMaxRange.toString()
        }


        if(!searchParam.isNullOrEmpty()){
            hashMap["searchParams"] = searchParam
        }

        if(!selectedCategoriesList.isNullOrEmpty()){
            if(selectedCategoriesList.size > 1){
                hashMap["classification"] = selectedCategoriesList.joinToString(",")
            }else{
                hashMap["classification"] = selectedCategoriesList[0].toString()
            }
        }

        hashMap["pageNo"] = pageNo.toString()
        hashMap["pageSize"] = Constants.IDEAL_PAGE_SIZE.toString()

        searchAndFilterJob = viewModelScope.launch {

            searchFilterUiStateChannel.send(UIState.Loading)

            val response = apiService.getSearchCouponList(hashMap)
            when(response){

                is NetworkResponse.Success ->{
                    if(response.body.status.code != 200){
                        searchFilterUiStateChannel.send(UIState.Error(response.body.status.message))
                    }else{
                        searchFilterUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.NetworkError ->{
                    searchFilterUiStateChannel.send(UIState.Error("NetworkError"))
                }

                is NetworkResponse.ServerError ->{
                    searchFilterUiStateChannel.send(UIState.Error(response.body?.status?.code.toString()))
                }

                is NetworkResponse.UnknownError ->{
                    searchFilterUiStateChannel.send(UIState.Error("UnknownError"))
                }

            }
        }
    }

    fun addRemoveSelectedCategory(shouldAdd: Boolean, item: CouponTypeData):Int{
        val categoryId=item.CouponTypeId
        if(shouldAdd){
            if(categoryId != null) {
                if(!selectedCategoriesList.contains(categoryId)){
                    selectedCategoriesList.add(categoryId)
                    selectedChipList.add(item)
                }
            }
        }else{
            if(selectedCategoriesList.contains(categoryId)){
                selectedCategoriesList.remove(categoryId)
                selectedChipList.remove(item)
            }
        }

        Timber.d("isPriceRangeSelected  "+selectedChipList.size)
        Timber.d("isPriceRangeSelected  "+selectedCategoriesList.size)

        return selectedCategoriesList.size
    }

    fun addRemoveSelectedRange(shouldAdd: Boolean, item: CouponTypeData){
        if(shouldAdd){
            for (chip in selectedChipList) {
                if(chip.CouponTypeId.equals("13"))
                {
                    selectedChipList.remove(chip)
                    selectedChipList.add(item)
                    return
                }
            }
            selectedChipList.add(item)
        }else{
            for (chip in selectedChipList) {
                if(chip.CouponTypeId.equals("13"))
                {
                    selectedChipList.remove(chip)
                    return
                }
            }
        }
    }

    fun clearAllFilterItems(){
        clearAllListSelection()
        selectedCategoriesList.clear()
        this.selectedChipList.clear()
        this.filterByBalance=false
        this.isPriceRangeSelected =false
    }

    fun onClearAllFiltersButtonClicked(context: Context){
        clearAllFilterItems()
        viewModelScope.launch { filterSheetEventChannel.send(Event.OnClearAllFilters) }
    }
    fun onApplyFiltersButtonClicked(context: Context){
        viewModelScope.launch { filterSheetEventChannel.send(Event.OnApplyFilters) }
    }


    sealed class Event {
        object OnApplyFilters: Event()
        object OnClearAllFilters: Event()
        data class OnPointsReceived(
            val pointsResponse: GetWalletPointsDto.Data?
        ): Event()

    }


    fun unlockCoupon(couponId:String) {
        viewModelScope.launch {
            viewModelScope.launch { unlockingUiStateChannel.send(UIState.Loading) }
            val response = apiService.unlockCoupon(couponId)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        unlockingUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        unlockingUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    unlockingUiStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                    Timber.d("Server Error")
                }

                is NetworkResponse.NetworkError -> {
                    unlockingUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    unlockingUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }

    fun redeemCoupon(couponTransactionId:String) {
        viewModelScope.launch {
            viewModelScope.launch { unlockingUiStateChannel.send(UIState.Loading) }
            val response = apiService.redeemCoupon(couponTransactionId)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        unlockingUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        unlockingUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    unlockingUiStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                    Timber.d("Server Error")
                }

                is NetworkResponse.NetworkError -> {
                    unlockingUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    unlockingUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }

    fun transferCouponEmail(couponTransactionId:String,email:String) {
        viewModelScope.launch {
            viewModelScope.launch { unlockingUiStateChannel.send(UIState.Loading) }
            val response = apiService.transferCouponViaEmail(couponTransactionId,email)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        unlockingUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        unlockingUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    unlockingUiStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                    Timber.d("Server Error")
                }

                is NetworkResponse.NetworkError -> {
                    unlockingUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    unlockingUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }

    fun transferCouponMobile(mobile:String,countryCode:String,couponTransactionId:String) {
        viewModelScope.launch {
            viewModelScope.launch { unlockingUiStateChannel.send(UIState.Loading) }
            val response = apiService.transferCouponViaMobile(couponTransactionId,mobile,countryCode)
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        unlockingUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                    } else {
                        unlockingUiStateChannel.send(UIState.Success(response.body.data))
                    }
                }

                is NetworkResponse.ServerError -> {
                    unlockingUiStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                    Timber.d("Server Error")
                }

                is NetworkResponse.NetworkError -> {
                    unlockingUiStateChannel.send(UIState.Error("NetworkError"))
                    Timber.d("Network Error")
                }

                is NetworkResponse.UnknownError -> {
                    unlockingUiStateChannel.send(UIState.Error("Unknown Error"))
                    Timber.d("Unknown Error")
                }

            }
        }
    }

    fun getPointsAndTier(){
        viewModelScope.launch {
            viewModelScope.launch { unlockingUiStateChannel.send(UIState.Loading) }
            val response = apiService.getWalletPoints()
            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        unlockingUiStateChannel.send(UIState.Error(response.body.status.message ?: ""))
                        fragmentToActivityEventChannel.send(CouponsViewModel.Event.OnPointsReceived(null))

                    } else {
                        unlockingUiStateChannel.send(UIState.Success(response.body.data))
                        fragmentToActivityEventChannel.send(CouponsViewModel.Event.OnPointsReceived(response.body.data))

                    }
                }

                is NetworkResponse.ServerError -> {
                    unlockingUiStateChannel.send(UIState.Error(response.body?.status?.message ?: ""))
                    fragmentToActivityEventChannel.send(CouponsViewModel.Event.OnPointsReceived(null))

                }

                is NetworkResponse.NetworkError -> {
                    unlockingUiStateChannel.send(UIState.Error("NetworkError"))
                    fragmentToActivityEventChannel.send(CouponsViewModel.Event.OnPointsReceived(null))

                }

                is NetworkResponse.UnknownError -> {
                    unlockingUiStateChannel.send(UIState.Error("UnknownError"))
                    fragmentToActivityEventChannel.send(CouponsViewModel.Event.OnPointsReceived(null))

                }

            }
        }

    }

}