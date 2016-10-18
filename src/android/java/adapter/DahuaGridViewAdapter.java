package com.sdr.xitang.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.sdr.xitang.DahuaMainActivity;
import com.sdr.xitang.R;
import com.sdr.xitang.activities.DahuaControllerActivity;
import com.sdr.xitang.bean.GridViewChannel;
import com.sdr.xitang.utils.VideoControllerUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by HeYongFeng on 2016/9/27.
 */
public class DahuaGridViewAdapter extends BaseAdapter{
    private Activity mActivity;
    private List<View> gridViewList;
    private int screenWidth;
    //放入用户选择通道后的集合
    private List<GridViewChannel> gridViewChannelList;
    //句柄
    private int DLLHandle;

    public int currentPosition = -1;
    public RelativeLayout rl=null;

    private List<String> chushihuaList;
    private Map<String,VideoControllerUtils> controllerMap;

    private Intent intent;


//    public DahuaGridViewAdapter(Activity activity, List<View> gridViewList, int screenWidth,List<GridViewChannel> gridViewChannelList,List<String> chushihuaList) {
    public DahuaGridViewAdapter(Activity activity, List<View> gridViewList, int screenWidth,Map<String,VideoControllerUtils> controllerMap,List<String> chushihuaList) {
        mActivity = activity;
        this.gridViewList = gridViewList;
        this.screenWidth = screenWidth;
//        this.gridViewChannelList = gridViewChannelList;
        this.controllerMap = controllerMap;
        this.chushihuaList = chushihuaList;
//        this.DLLHandle = DLLHandle;
    }

    @Override
    public int getCount() {
        return chushihuaList.size()*chushihuaList.size();
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView==null){
            convertView = gridViewList.get(position);
            int width = screenWidth/chushihuaList.size();
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(width,width);
            convertView.setLayoutParams(params);
        }
        final RelativeLayout relativeLayout = (RelativeLayout) gridViewList.get(position).findViewById(R.id.rl_item_Relative);
        /*
        判断该处是否是直播的视频
         */
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controllerMap.containsKey(position+"")){
                    if (currentPosition == position) {
                        intent = new Intent(mActivity, DahuaControllerActivity.class);
                        intent.putExtra("position", position);
                        mActivity.startActivityForResult(intent, DahuaMainActivity.GET_CHANNEL_REQ_CODE);
                    }else {
                        if (rl!=null){
                            rl.setBackgroundResource(R.drawable.bg_gridview_unselected);
                        }
                        relativeLayout.setBackgroundResource(R.drawable.bg_gridview_selected);
                        rl = relativeLayout;
                        currentPosition = position;
                    }
                }else {
                    //说明此处并没有存放视频那么就跳转到列表,携带该处的位置
//                    intent = new Intent(mActivity,DahuaDeviceListActivity.class);
//                    intent.putExtra("clilkGridPosition",position);
//                    mActivity.startActivityForResult(intent,DahuaMainActivity.GET_CHANNEL_REQ_CODE);
                    if (!DahuaMainActivity.mainActivity.mDrawerLayout.isDrawerOpen(DahuaMainActivity.mainActivity.mScrollView)) {
                        DahuaMainActivity.mainActivity.mDrawerLayout.openDrawer(DahuaMainActivity.mainActivity.mScrollView);
                    }
                    DahuaMainActivity.mainActivity.clickGridViewPosition = position;
                }
            }
        });



        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (controllerMap.containsKey(position+"")){
                    new AlertDialog.Builder(mActivity)
                            .setTitle("删除")
                            .setMessage("是否删除该处视频?")
                            .setNegativeButton("取消",null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    controllerMap.get(position+"").stopPlayVideo();
                                    ((DahuaMainActivity)mActivity).gridViewList.set(position,((DahuaMainActivity)mActivity).getEmptyView());
                                    controllerMap.remove(position+"");
                                    ((DahuaMainActivity)mActivity).reSetGridAdapter();
                                }
                            })
                            .create()
                            .show();
                }
                return true;
            }
        });
        return convertView;
    }

}
