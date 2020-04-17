package com.hzy.wan.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsListView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hzy.baselib.base.BaseFragment
import com.hzy.baselib.base.showToast
import com.hzy.baselib.widget.TopSmoothScroller
import com.hzy.baselib.base.jump
import com.hzy.wan.R
import com.hzy.wan.activity.AgentWebView
import com.hzy.wan.adapter.ClassfiyMenuTabAdapter
import com.hzy.wan.bean.NaviBean
import com.hzy.wan.viewmodel.NaviViewModel
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout
import kotlinx.android.synthetic.main.fragment_navi.*
import q.rorbin.verticaltablayout.VerticalTabLayout
import q.rorbin.verticaltablayout.widget.TabView

class NaviFragment : BaseFragment() {
    private val adapter: MyAdapter by lazy { MyAdapter(null) }
    private lateinit var layoutManager: LinearLayoutManager
    lateinit var mViewModel: NaviViewModel
    private var selectedPosition = 0
    override fun getLayoutId(): Int {
        return R.layout.fragment_navi
    }

    override fun init() {

    }

    override fun initView(view: View) {

    }

    override fun initData() {
        title_bar.setPageTitle("导航")
        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        mViewModel = ViewModelProvider(this)[NaviViewModel::class.java]
        mViewModel?.apply {
            this.getData()
            this.naviLiveData.observe(viewLifecycleOwner, Observer {
                it.showSuccess?.run {
                    bindTabAndPager(this.data)
                    adapter.setNewData(this.data)
                }
                it.showError?.run {
                    showToast(this)
                }
            })
        }
    }

    private fun bindTabAndPager(list: List<NaviBean.DataBean>?) {
        val scroller = TopSmoothScroller(context);
        val classfiyMenuTabAdapter = ClassfiyMenuTabAdapter(list)
        tabLayout.setTabAdapter(classfiyMenuTabAdapter)

        tabLayout.addOnTabSelectedListener(object : VerticalTabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabView?, position: Int) {
            }

            override fun onTabSelected(tab: TabView?, position: Int) {
                // recyclerView.smoothScrollToPosition(position)
                scroller.targetPosition = position
                recyclerView.layoutManager?.startSmoothScroll(scroller)
            }

        })
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    AbsListView.OnScrollListener.SCROLL_STATE_IDLE -> {
                        val position = layoutManager.findFirstVisibleItemPosition()
                        if (selectedPosition != position) {
                            tabLayout.setTabSelected(position)
                            selectedPosition = position
                        }
                    }
                }
            }
        })
    }

    class MyAdapter(var list: List<NaviBean.DataBean>?) :
            BaseQuickAdapter<NaviBean.DataBean, BaseViewHolder>(R.layout.item_navi_content, list) {

        override fun convert(helper: BaseViewHolder?, item: NaviBean.DataBean) {
            helper?.setText(R.id.tv_title, item.name)
            val flowLayout = helper?.getView<TagFlowLayout>(R.id.flowLayout)
            flowLayout?.run {
                this.adapter = object : TagAdapter<NaviBean.DataBean.ArticlesBean>(item.articles) {
                    override fun getCount(): Int {
                        return item.articles!!.size
                    }

                    override fun getView(parent: FlowLayout, position: Int, t: NaviBean.DataBean.ArticlesBean): View {
                        val tv = LayoutInflater.from(parent.context).inflate(R.layout.item_tag,
                                parent, false) as TextView
                        tv.text = t.title
                        return tv
                    }
                }
                this.setOnTagClickListener { view, position, parent ->
                    val bundle = Bundle()
                    bundle.putString("url", item.articles?.get(position)?.link)
                    context.jump(AgentWebView::class.java, bundle)
                    true
                }
            }
        }
    }
}
