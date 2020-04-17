package com.hzy.wan.adapter;

import com.hzy.wan.bean.NaviBean;

import java.util.List;

import q.rorbin.verticaltablayout.adapter.SimpleTabAdapter;
import q.rorbin.verticaltablayout.widget.QTabView;
import q.rorbin.verticaltablayout.widget.TabView;

/**
 * VerticalTabLayout适配器
 */
public class ClassfiyMenuTabAdapter extends SimpleTabAdapter {
    List<NaviBean.DataBean> menus;
    public ClassfiyMenuTabAdapter(List<NaviBean.DataBean> menus) {
        this.menus = menus;
    }
    @Override
    public int getCount() {
        return menus.size();
    }
    @Override
    public TabView.TabBadge getBadge(int position) {
        return null;
    }
    @Override
    public TabView.TabIcon getIcon(int position) {
        return  null;
    }
    @Override
    public TabView.TabTitle getTitle(int position) {
        String classfiy = menus.get(position).getName();
        //自定义Tab选择器的字体大小颜色
        return new QTabView.TabTitle.Builder()
                .setTextColor(0xFF1E90FF,0xFF2A2323)
                .setTextSize(14)
                .setContent(classfiy.toString())
                .build();
    }
    @Override
    public int getBackground(int position) {
        return -1;
    }
}