package com.hzy.wan.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hzy.baselib.base.BaseActivity
import com.hzy.wan.R
import com.hzy.wan.bean.SystemBean
import com.hzy.wan.fragment.SysListFragment2
import kotlinx.android.synthetic.main.activity_sys2.*

class SysActivity2 : BaseActivity() {
    var mList: List<SystemBean.DataBean.ChildrenBean>? = null
    var fragmentsList = ArrayList<SysListFragment2>()
    override fun getLayoutId(): Int {
        return R.layout.activity_sys2
    }

    override fun initView() {
        intent?.extras?.getString("title")?.let { setPageTitle(it) }
    }

    override fun initData() {
        mList = intent?.extras?.getSerializable("child") as List<SystemBean.DataBean.ChildrenBean>
        mList?.run {
            setVpData(this)
        }

    }

    private fun setVpData(list: List<SystemBean.DataBean.ChildrenBean>) {
        if (list.isEmpty()) return
        val tabs = ArrayList<String>()
        for (i in list.indices) {
            tabs.add(list[i].name)
            val fragment = SysListFragment2()
            val bundle = Bundle()
            bundle.putInt("id", list[i].id)
            fragment.arguments = bundle
            fragmentsList.add(fragment)
        }
        viewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                // 返回集合的长度，Fragment的数量就是根据这个集合来创建的
                return fragmentsList.size
            }

            override fun createFragment(position: Int): Fragment {
                // 实例化Fragment
                return fragmentsList[position]
            }
        }
        viewPager2.offscreenPageLimit = fragmentsList.size
        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = tabs[position]
        }.attach()


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })
    }

}
