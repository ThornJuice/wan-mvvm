package com.hzy.wan.viewmodel

import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSONObject
import com.hzy.wan.base.BaseViewModel
import com.hzy.wan.bean.BannerBean
import com.hzy.wan.bean.HomeArticleBean
import com.hzy.wan.http.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : BaseViewModel() {
    var bannerLd = MutableLiveData<UiModel<BannerBean>>()
    var articleLd = MutableLiveData<UiModel<HomeArticleBean>>()
    fun getData(page: Int) {
        mCoroutineScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    RetrofitManager.getInstance().create().getHomeArticle(page)
                }
                if (result.errorCode == 0) {
                    emitArticleModel(showSuccess = result, isRefresh = false)
                } else {
                    emitArticleModel(showError = result.errorMsg, isRefresh = false)
                }

            } catch (e: Exception) {
                emitArticleModel(showError = errorHandle(e), isRefresh = false)
            }
        }
    }

    fun getBanner() {
        mCoroutineScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    RetrofitManager.getInstance().create().getHomeBanner2()
                }
                val result = JSONObject.parseObject(data.string(), BannerBean::class.java)
                if (result.errorCode == 0) {
                    emitBannerModel(showSuccess = result, isRefresh = false)
                }

            } catch (e: Exception) {
                emitBannerModel(showError = errorHandle(e), isRefresh = false)
            }
        }

    }


    private fun emitArticleModel(
            showLoading: Boolean = false,
            showError: String? = null,
            showSuccess: HomeArticleBean? = null,
            showEnd: Boolean = false,
            isRefresh: Boolean = false
    ) {

        articleLd.value = UiModel(showLoading, showError, showSuccess, showEnd, isRefresh)
    }

    private fun emitBannerModel(
            showLoading: Boolean = false,
            showError: String? = null,
            showSuccess: BannerBean? = null,
            showEnd: Boolean = false,
            isRefresh: Boolean = false
    ) {
        bannerLd.value = UiModel(showLoading, showError, showSuccess, showEnd, isRefresh)
    }

}









