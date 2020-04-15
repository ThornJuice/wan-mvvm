package com.hzy.wan.fragment


import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hzy.baselib.base.*
import com.hzy.baselib.listener.RetryClickListener
import com.hzy.wan.*
import com.hzy.wan.activity.AgentWebView
import com.hzy.wan.adapter.BannerViewHolder
import com.hzy.wan.bean.BannerBean
import com.hzy.wan.bean.HomeArticleBean
import com.hzy.wan.viewmodel.HomeViewModel
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.adapter.OnPageChangeListenerAdapter
import com.zhpan.bannerview.constants.IndicatorSlideMode
import com.zhpan.bannerview.constants.IndicatorStyle
import com.zhpan.bannerview.constants.PageStyle
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * 首页
 *
 */
class HomeFragment : BaseFragment() {
    private var bannerViewPager: BannerViewPager<BannerBean.DataBean, BannerViewHolder>? = null
    lateinit var mViewModel: HomeViewModel
    private val adapter: MyAdapter by lazy { MyAdapter(null) }
    var bannerView: View? = null
    var mList = ArrayList<HomeArticleBean.DataBean.DatasBean>()
    var page = 1
    override fun init() {
        title_bar.setPageTitle("首页")
    }

    override fun getLayoutId(): Int {


        return R.layout.fragment_home
    }

    override fun initView(view: View) {
        mLoadHolder = swipeRefreshLayout.initLoadDialog(object : RetryClickListener {
            override fun retry() {
                mViewModel.getData(page)
            }
        })
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext!!, R.color.theme))
        mViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
        adapter?.setEnableLoadMore(true)
        adapter?.setOnLoadMoreListener({
            mViewModel.getData(page)
        }, recyclerView)
        adapter?.disableLoadMoreIfNotFullPage()
        swipeRefreshLayout.setOnRefreshListener {
            page = 1
            mViewModel.getData(page)
        }
        adapter?.setOnItemClickListener { _, view, position ->
            val bundle = Bundle()
            bundle.putString("url", adapter?.getItem(position)?.link)
            jump(AgentWebView::class.java, bundle)

        }
        initBanner()
        adapter?.addHeaderView(bannerView)

    }

    override fun initData() {
        mLoadHolder?.showLoading()
        mViewModel.apply {
            this.getData(page)
            this.getBanner()
            this.articleLd.observe(viewLifecycleOwner, Observer {
                swipeRefreshLayout.isRefreshing = false
                mLoadHolder?.showLoadSuccess()
                if (page == 1) {
                    it.showSuccess?.apply {
                        if (data.datas.size > 0) {
                            adapter.setNewData(data.datas)
                            page++
                        } else {
                            adapter.setNewData(null)
                            adapter.loadMoreEnd()
                        }
                    }

                } else {
                    it.showSuccess?.apply {
                        if (data.datas.size > 0) {
                            adapter.addData(data.datas)
                            adapter.loadMoreComplete()
                            page++
                        } else {
                            adapter.loadMoreEnd()
                        }
                    }
                }
                it.showError?.run {
                    showToast(this)
                }
            })

            this.bannerLd.observe(viewLifecycleOwner, Observer {
                it.showSuccess?.apply {
                    if(this is BannerBean) {
                        bannerViewPager?.create(data)
                    }
                }
            })
        }
    }

    class MyAdapter(var list: List<HomeArticleBean.DataBean.DatasBean>?) :
            BaseQuickAdapter<HomeArticleBean.DataBean.DatasBean, BaseViewHolder>(R.layout.item_home_article, list) {

        override fun convert(helper: BaseViewHolder?, item: HomeArticleBean.DataBean.DatasBean?) {
            helper?.setText(R.id.tv_title, item?.title)
            if (!item?.shareUser.isNullOrBlank()) {
                helper?.setText(R.id.tv_share_name, "分享者：" + item?.shareUser)
            } else {
                helper?.setText(R.id.tv_share_name, "作者：" + item?.author)
            }
            helper?.setText(R.id.tv_type, "分类：" + item?.superChapterName)
            helper?.setText(R.id.tv_date, item?.niceShareDate)
        }
    }

    private fun initBanner() {
        bannerView = layoutInflater.inflate(R.layout.layout_home_banner, null)
        bannerViewPager = bannerView?.findViewById(R.id.bannerViewPager)
        bannerViewPager?.run {
            setAutoPlay(true)
                    .setPageStyle(PageStyle.MULTI_PAGE_SCALE)
                    .setIndicatorSlideMode(IndicatorSlideMode.SMOOTH)
                    .setIndicatorStyle(IndicatorStyle.ROUND_RECT)
                    .setInterval(5000)
                    .setScrollDuration(1200)
                    .setIndicatorColor(ContextCompat.getColor(mContext!!, R.color.white7f), ContextCompat.getColor(mContext!!, R.color.white))
                    .setHolderCreator { BannerViewHolder() }
                    .setOnPageChangeListener(object : OnPageChangeListenerAdapter() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)

                        }
                    })
                    .setOnPageClickListener { position ->
                        val bundle = Bundle()
                        bundle.putString("url", bannerViewPager?.list?.get(position)?.url)
                        jump(AgentWebView::class.java, bundle)
                    }
        }
    }
}
