package com.sdr.xitang.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdr.xitang.DahuaMainActivity;
import com.sdr.xitang.R;
import com.sdr.xitang.bean.NodeBean;
import com.sdr.xitang.tree.bean.Node;
import com.sdr.xitang.tree.bean.TreeListViewAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/9/27.
 */
public class DeviceTreeAdapter<T> extends TreeListViewAdapter<T> {

    private List<NodeBean> nodeBeanList;
    private Context context;

    public DeviceTreeAdapter(ListView mTree, Context context, List<T> datas, int defaultExpandLevel) throws IllegalArgumentException, IllegalAccessException {
        super(mTree, context, datas, defaultExpandLevel);
        nodeBeanList = (List<NodeBean>) datas;
        this.context = context;
    }

    @Override
    public View getConvertView(final Node node, final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.device_list_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView
                    .findViewById(R.id.id_treenode_icon);
            viewHolder.label = (TextView) convertView
                    .findViewById(R.id.id_treenode_label);
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (node.getIcon() == -1)
        {
            viewHolder.icon.setVisibility(View.INVISIBLE);
            if ("通道".equals(nodeBeanList.get(i).getNodeType())){
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if ("closed".equals(nodeBeanList.get(i).getChannelStatus())) {
//                        Log.e("当前的i:",i+"");
//                        Log.e("它父亲的id",node.getpId()+"");
//                        Log.e("当前的类型：",nodeBeanList.get(i).getNodeType());
                            //结束这个activity，返回通道信息
                            NodeBean nodeBean = nodeBeanList.get(i);

//                            Intent intent = ((DahuaDeviceListActivity) context).getIntent();
//                            intent.putExtra("clilkGridPosition", intent.getIntExtra("clilkGridPosition", 0));
//                            intent.putExtra("channelId", nodeBean.getChannelId());
//                            intent.putExtra("channelName", nodeBean.getChannelName());
//                            ((DahuaDeviceListActivity) context).setResult(DahuaMainActivity.GET_CHANNEL_RES_CODE, intent);
//                            ((DahuaDeviceListActivity) context).finish();

                                if (DahuaMainActivity.mainActivity.mDrawerLayout.isDrawerOpen(DahuaMainActivity.mainActivity.mScrollView)) {
                                    DahuaMainActivity.mainActivity.mDrawerLayout.closeDrawers();
                                }
                                if (DahuaMainActivity.mainActivity.clickGridViewPosition>=0) {
                                    DahuaMainActivity.mainActivity.addControllerMap(DahuaMainActivity.mainActivity.clickGridViewPosition, nodeBean.getChannelId(), nodeBean.getChannelName());
                                }
                        }else {
                            Toast.makeText(context, "该通道已经打开", Toast.LENGTH_SHORT).show();
                        }
                        }
                    });
            }
        } else
        {
            viewHolder.icon.setVisibility(View.VISIBLE);
            viewHolder.icon.setImageResource(node.getIcon());
        }
        viewHolder.label.setTextColor(Color.parseColor("#000000"));
        viewHolder.label.setText(node.getName());

        return convertView;
    }
    private final class ViewHolder
    {
        ImageView icon;
        TextView label;
    }
}
