package com.hzy.wan.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.AbsListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hzy.baselib.base.BaseApp
import com.hzy.baselib.base.BaseFragment
import com.hzy.baselib.base.showToast
import com.hzy.baselib.widget.TopSmoothScroller
import com.hzy.baselib.base.jump
import com.hzy.wan.App
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
import java.text.FieldPosition

class NaviFragment : BaseFragment() {
    private val adapter: MyAdapter by lazy { MyAdapter(null) }
    private val leftAdapter: AdapterLeft by lazy { AdapterLeft(null) }
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var scroller: TopSmoothScroller
    lateinit var mViewModel: NaviViewModel
    override fun getLayoutId(): Int {
        return R.layout.fragment_navi
    }

    override fun init() {

    }

    override fun initView(view: View) {

    }

    override fun initData() {
        title_bar.setPageTitle("导航")
        scroller = TopSmoothScroller(context);
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
        recyclerViewLeft.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerViewLeft.adapter = leftAdapter

        mViewModel = ViewModelProvider(this)[NaviViewModel::class.java]
        mViewModel?.apply {
            this.getData()
            this.naviLiveData.observe(viewLifecycleOwner, Observer {
                it.showSuccess?.run {
                    bindTabAndPager(this.data)
                    leftAdapter.setNewData(this.data)
                    adapter.setNewData(this.data)
                }
                it.showError?.run {
                    showToast(this)
                }
            })
        }

        leftAdapter.setOnItemClickListener { adapter, view, position ->
            leftAdapter.setSelected(position)
            scroller.targetPosition = position
            recyclerView.layoutManager?.startSmoothScroll(scroller)
        }
    }

    private fun bindTabAndPager(list: List<NaviBean.DataBean>?) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                when (newState) {
                    AbsListView.OnScrollListener.SCROLL_STATE_IDLE -> {
                        val position = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                        recyclerViewLeft.smoothScrollToPosition(position)
                        leftAdapter.setSelected(position)
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

    class AdapterLeft(var list: List<NaviBean.DataBean>?) :
            BaseQuickAdapter<NaviBean.DataBean, BaseViewHolder>(R.layout.item_simple_text, list) {
        var position: Int = 0
        override fun convert(helper: BaseViewHolder?, item: NaviBean.DataBean) {
            helper?.setText(R.id.tv_title, item.name)
            var p = helper?.layoutPosition
            if (position == p) {
                helper?.setTextColor(R.id.tv_title, ContextCompat.getColor(BaseApp.instance, R.color.colorPrimary))
            } else {
                helper?.setTextColor(R.id.tv_title, ContextCompat.getColor(BaseApp.instance, R.color.black_text))
            }
        }

        fun setSelected(position: Int) {
            this.position = position
            notifyDataSetChanged()
        }
    }
}
