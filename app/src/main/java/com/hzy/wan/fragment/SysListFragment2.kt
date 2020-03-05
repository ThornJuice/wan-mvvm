package com.hzy.wan.fragment


import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hzy.baselib.base.BaseLazyFragment
import com.hzy.baselib.base.initLoadDialog
import com.hzy.baselib.base.jump
import com.hzy.baselib.listener.RetryClickListener
import com.hzy.wan.R
import com.hzy.wan.activity.AgentWebView
import com.hzy.wan.bean.SystemArticleBean
import com.hzy.wan.viewmodel.SystemViewModel
import kotlinx.android.synthetic.main.fragment_sys_list.*
import me.jingbin.library.adapter.BaseByViewHolder
import me.jingbin.library.adapter.BaseRecyclerAdapter


/**
 * 体系列表
 */
class SysListFragment2 : BaseLazyFragment() {


    lateinit var mViewModel: SystemViewModel
    lateinit var adapter:OneTypeAdapter
    var mList = ArrayList<SystemArticleBean.DataBean.DatasBean>()
    var page = 0
    var mId: Int = 0
    override fun init() {
        mId = arguments!!.getInt("id")
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_sys_list
    }

    override fun initView(view: View) {
        mLoadHolder = swipeRefreshLayout.initLoadDialog(object : RetryClickListener {
            override fun retry() {
                mViewModel.getArticle(page, mId)
            }
        })
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext!!, R.color.theme))
        mViewModel = ViewModelProvider(this)[SystemViewModel::class.java]
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        adapter = OneTypeAdapter(null)
        recyclerView.adapter = adapter
        recyclerView.setLoadMoreEnabled(true)
        recyclerView.setOnLoadMoreListener({
            mViewModel.getArticle(page, mId)
        },1000)// delayMillis: 延迟多少毫秒调用接口
        recyclerView.setOnItemClickListener { v, position ->
            val bundle = Bundle()
            bundle.putString("url",adapter?.getItemData(position)?.link)
            jump(AgentWebView::class.java, bundle)
        }
        swipeRefreshLayout.setOnRefreshListener {
            page = 1
            mViewModel.getArticle(page, mId)
        }
    }

    override fun lazyLoad() {
        //showLoadDialog()
        mLoadHolder?.showLoading()
        mViewModel.getArticle(page, mId)
        mViewModel.systemArticleBean.observe(this, Observer {
            swipeRefreshLayout.isRefreshing = false
           //dismissLoadDialog()
            mLoadHolder?.showLoadSuccess()
            if (page == 0) {
                mList.clear()
                if (it.data.datas.size > 0) {
                    mList.addAll(it.data.datas)
                    adapter?.setNewData(mList)
                    page++
                    recyclerView.loadMoreComplete()
                } else {
                    adapter?.setNewData(null)
                    recyclerView.loadMoreEnd()
                }
            } else {
                if (it.data.datas.size > 0) {
                    mList.addAll(it.data.datas)
                    adapter?.setNewData(mList)
                    recyclerView.loadMoreComplete()
                    page++
                } else {
                    recyclerView.loadMoreEnd()
                }
            }
        })
    }

    class OneTypeAdapter(data: List<SystemArticleBean.DataBean.DatasBean>?) : BaseRecyclerAdapter<SystemArticleBean.DataBean.DatasBean>(R.layout.item_official_accounts, data) {

        override fun bindView(holder: BaseByViewHolder<SystemArticleBean.DataBean.DatasBean>, item: SystemArticleBean.DataBean.DatasBean, position: Int) {
            holder?.setText(R.id.tv_title, item?.title)
            holder?.setText(R.id.tv_date, item?.niceDate)
        }
    }

}