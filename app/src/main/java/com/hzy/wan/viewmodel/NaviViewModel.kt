package com.hzy.wan.viewmodel

import androidx.lifecycle.MutableLiveData
import com.hzy.wan.base.BaseViewModel
import com.hzy.wan.bean.NaviBean
import com.hzy.wan.http.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NaviViewModel : BaseViewModel() {
    var naviLiveData = MutableLiveData<UiModel<NaviBean>>()
    fun getData() {
        mCoroutineScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    RetrofitManager.getInstance().create().getNavi()
                }
                if (result.errorCode == 0) {
                    emitModel(showSuccess = result, isRefresh = false)
                } else {
                    emitModel(showError = result.errorMsg, isRefresh = false)
                }

            } catch (e: Exception) {
                emitModel(showError = errorHandle(e), isRefresh = false)
            }
        }
    }

    private fun emitModel(
            showLoading: Boolean = false,
            showError: String? = null,
            showSuccess: NaviBean? = null,
            showEnd: Boolean = false,
            isRefresh: Boolean = false
    ) {

        naviLiveData.value = UiModel(showLoading, showError, showSuccess, showEnd, isRefresh)
    }
}