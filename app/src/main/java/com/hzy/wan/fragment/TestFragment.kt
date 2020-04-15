package com.hzy.wan.fragment

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class TestFragment :LifecycleObserver{
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onresume(){
        println("onresume")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onpause(){
        println("onpause")
    }
}