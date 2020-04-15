package com.hzy.wan.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hzy.baselib.util.LogUtils
import com.hzy.wan.bean.HomeArticleBean
import com.hzy.wan.http.RetrofitManager
import com.hzy.wan.http.subscribe
import kotlinx.coroutines.*
import okhttp3.ResponseBody

class HomeViewModel : ViewModel() {
    private val viewModelJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var articleLd = MutableLiveData<HomeArticleBean>()
    var bannerLd = MutableLiveData<ResponseBody>()
    fun getData(page: Int) {
        coroutineScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    RetrofitManager.getInstance().create().getHomeArticle(page)
                }
                articleLd.value = data


            } catch (e: Exception) {
                LogUtils.e("HomeViewModel","error"+e.message)
            }
        }
    }

    fun getBanner() {
        coroutineScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    RetrofitManager.getInstance().create().getHomeBanner2()
                }
                bannerLd.value = data

            } catch (e: Exception) {
                LogUtils.e("HomeViewModel","error")
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }
}

