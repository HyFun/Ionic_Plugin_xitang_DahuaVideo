package com.sdr.xitang.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdr.xitang.DahuaMainActivity;
import com.sdr.xitang.R;
import com.sdr.xitang.bean.GridViewChannel;
import com.sdr.xitang.utils.Utility;

import java.util.List;

/**
 * Created by HeYongFeng on 2016/10/10.
 */
public class CollectAdapter extends BaseAdapter {
    private Context context;
    private List<GridViewChannel> collectList;

    public CollectAdapter(Context context, List<GridViewChannel> collectList) {
        this.context = context;
        this.collectList = collectList;
    }

    @Override
    public int getCount() {
        return collectList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(context, R.layout.dahua_collect_item,null);
        TextView tv_item_collect = (TextView) convertView.findViewById(R.id.tv_item_collect);
        ImageView iv_collect_delete = (ImageView) convertView.findViewById(R.id.iv_collect_delete);
        iv_collect_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "您点击了删除", Toast.LENGTH_SHORT).show();
                collectList.remove(position);
                notifyDataSetChanged();
                Utility.setListViewHeightBasedOnChildren(DahuaMainActivity.mainActivity.lv_collect);
            }
        });
        tv_item_collect.setText((position+1)+"."+collectList.get(position).getChannelName());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DahuaMainActivity.mainActivity.mDrawerLayout.isDrawerOpen(DahuaMainActivity.mainActivity.mScrollView)) {
                    DahuaMainActivity.mainActivity.mDrawerLayout.closeDrawers();
                }
                if (DahuaMainActivity.mainActivity.clickGridViewPosition>=0) {
                    DahuaMainActivity.mainActivity.addControllerMap(DahuaMainActivity.mainActivity.clickGridViewPosition, collectList.get(position).getChannelId(), collectList.get(position).getChannelName());
                }
            }
        });
        return convertView;
    }
}
