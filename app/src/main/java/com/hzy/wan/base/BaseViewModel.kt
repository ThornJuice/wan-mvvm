package com.hzy.wan.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import retrofit2.HttpException
import java.io.EOFException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

open class BaseViewModel : ViewModel() {
    protected val viewModelJob = SupervisorJob()
    protected val mCoroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    protected fun errorHandle(e: Exception): String? {
        when (e) {
            is HttpException -> {
                return defaultError(e.code(), e.message())
            }
            is UnknownHostException -> {
                return defaultError(400, "无法连接到服务器")
            }
            is SocketTimeoutException -> {
                return defaultError(400, "链接超时")
            }
            is ConnectException -> {
                return defaultError(500, "链接失败")
            }
            is SocketException -> {
                return defaultError(500, "链接关闭")
            }
            is EOFException -> {
                return defaultError(500, "链接关闭")
            }
            is IllegalArgumentException -> {
                return defaultError(400, "参数错误")
            }
            is SSLException -> {
                return defaultError(500, "证书错误")
            }
            is NullPointerException -> {
                return defaultError(500, "数据为空")
            }
            else -> {
                return defaultError(500, "未知错误")
            }
        }
    }

    private val defaultError = fun(_: Int, msg: String?): String? {
        return msg;
    }


    data class UiModel<out T>(
            val showLoading: Boolean,
            val showError: String?,
            val showSuccess: T?,
            val showEnd: Boolean,
            val isRefresh: Boolean
    )

    override fun onCleared() {
        super.onCleared()
        mCoroutineScope.cancel()
    }
}
