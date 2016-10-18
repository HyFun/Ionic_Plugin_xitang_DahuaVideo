package com.sdr.xitang;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdr.xitang.adapter.CollectAdapter;
import com.sdr.xitang.adapter.DahuaGridViewAdapter;
import com.sdr.xitang.adapter.DeviceTreeAdapter;
import com.sdr.xitang.bean.ChannelInfoExt;
import com.sdr.xitang.bean.DeviceInfo;
import com.sdr.xitang.bean.GridViewChannel;
import com.sdr.xitang.bean.NodeBean;
import com.sdr.xitang.utils.PrefUtils;
import com.sdr.xitang.utils.Utility;
import com.sdr.xitang.utils.VideoControllerUtils;
import com.dh.DpsdkCore.Dep_Info_t;
import com.dh.DpsdkCore.Device_Info_Ex_t;
import com.dh.DpsdkCore.Enc_Channel_Info_Ex_t;
import com.dh.DpsdkCore.Get_Channel_Info_Ex_t;
import com.dh.DpsdkCore.Get_Dep_Count_Info_t;
import com.dh.DpsdkCore.Get_Dep_Info_t;
import com.dh.DpsdkCore.IDpsdkCore;
import com.dh.DpsdkCore.Login_Info_t;
import com.dh.DpsdkCore.Return_Value_Info_t;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DahuaMainActivity extends AppCompatActivity implements View.OnClickListener{

    //正在加载的Dialog
    private ProgressDialog progressDialog;

    private String ipAddress="hcece.8800.org";
    private String port="9000";
    private String userName="hangz";
    private String passWord="12345678";

    //SDK句柄
    private Return_Value_Info_t sdkHandleInfo ;
    private int sdkHandle;
    //超时时间
    private final int TIME_OUT = 60000;
    //登录成功
    private final int LOGIN_SUCCESS = 1;
    //登录失败
    private final int LOGIN_FAILED = 0;

    // 返回的结果
    private int ret = -1;
    // 节点code
    private byte[] szCoding = null;

    private int nodeId;
    private int rootNodeId;
    private int groupNodeId;
    //存放节点的集合
    private List<NodeBean> nodeList;


    //主页面的布局控件
    private RelativeLayout rl_gv;
    public GridView gv_gridView;
    public DrawerLayout mDrawerLayout;
    public ScrollView mScrollView;
    public ListView lv_deviceListView;
    //点击当前的位置
    public int clickGridViewPosition = -1;

    private DeviceTreeAdapter treeAdapter;
    private ImageView iv_collect,iv_history,btn_1x1,btn_2x2,btn_3x3,btn_4x4;

    private Intent intent;
    //GridView的列数,默认是1x1的布局
    private int rowCount = 1;

    //初始化Gridview时用的List
    public List<View> gridViewList;
    //放入用户选择通道后的集合
    private List<GridViewChannel> gridViewChannelList;
    public Map<String,VideoControllerUtils> controllerMap;
    private VideoControllerUtils controller;

    private List<String> chushihuaList;

    private int screenWidth;
    private DahuaGridViewAdapter gridViewAdapter;

    public static int GET_CHANNEL_REQ_CODE = 1;
    public static int GET_CHANNEL_RES_CODE = 0;
    public static int GET_CTRL_RES_CODE = 3;

    public static DahuaMainActivity mainActivity;

    //历史纪录集合
    private List<GridViewChannel> historyList;
    //点击历史纪录的类型,0表示当前是clear,1表示当前是历史纪录
    private int historyType = 0;

    //收藏列表的集合
    private List<GridViewChannel> collectList;
    //收藏列表的listview
    public ListView lv_collect;
    //收藏列表的Adapter
    private CollectAdapter collectAdapter;

    //判断用户是否登录成功
    private boolean isLoginSuccess = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dahua_main);
        mainActivity = this;
        initActionBar();
        //初始化用户登录
        initUserLogin(ipAddress,port,userName,passWord);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.dahuavideo_actionbar_custom);
        ((TextView)actionBar.getCustomView().findViewById(R.id.tv_actionbar_title)).setText("实时监控");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#72D3E4")));
        actionBar.getCustomView().findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initUserLogin(String ipAddress, String port, String userName, String passWord) {
        //显示对话框
        showProgressDialog("初始化","正在加载....");

        sdkHandleInfo = new Return_Value_Info_t();
        IDpsdkCore.DPSDK_Create(1,sdkHandleInfo);
        final Login_Info_t loginInfo = new Login_Info_t();
        loginInfo.szIp = ipAddress.getBytes();
        loginInfo.nPort = Integer.parseInt(port);
        loginInfo.szUsername = userName.getBytes();
        loginInfo.szPassword = passWord.getBytes();

        new Thread(){
            @Override
            public void run() {
                int retCode = IDpsdkCore.DPSDK_Login(sdkHandleInfo.nReturnValue, loginInfo, TIME_OUT);
                Log.e("登录返回码",""+retCode);
                if (retCode==0){
                    //说明登录成功
                    sdkHandle = sdkHandleInfo.nReturnValue;
                    mHandler.sendEmptyMessage(LOGIN_SUCCESS);
                }else {
                    mHandler.sendEmptyMessage(LOGIN_FAILED);
                }
            }
        }.start();
    }

    private void initView() {
        screenWidth = getScreenWidth();

        rl_gv= (RelativeLayout) findViewById(R.id.rl_gv);
        gv_gridView= (GridView) findViewById(R.id.gv_gridView);

        mScrollView = (ScrollView) findViewById(R.id.sv_leftScrollView);

        lv_deviceListView = (ListView) findViewById(R.id.lv_deviceListView);
        lv_collect = (ListView) findViewById(R.id.lv_collect);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

//        ib_switch= (ImageButton) findViewById(R.id.ib_switch);
//        ib_video= (ImageButton) findViewById(R.id.ib_video);
//        ib_camera= (ImageButton) findViewById(R.id.ib_camera);
//        ib_voice= (ImageButton) findViewById(R.id.ib_voice);
//
//        ib_left_top= (ImageButton) findViewById(R.id.ib_left_top);
//        ib_top= (ImageButton) findViewById(R.id.ib_top);
//        ib_right_top= (ImageButton) findViewById(R.id.ib_right_top);
//        ib_left= (ImageButton) findViewById(R.id.ib_left);
//        ib_middle= (ImageButton) findViewById(R.id.ib_middle);
//        ib_right= (ImageButton) findViewById(R.id.ib_right);
//        ib_left_bottom= (ImageButton) findViewById(R.id.ib_left_bottom);
//        ib_bottom= (ImageButton) findViewById(R.id.ib_bottom);
//        ib_right_bottom= (ImageButton) findViewById(R.id.ib_right_bottom);
//
//        ib_video_big= (ImageButton) findViewById(R.id.ib_video_big);
//        ib_video_small= (ImageButton) findViewById(R.id.ib_video_small);
//        ib_focus_big= (ImageButton) findViewById(R.id.ib_focus_big);
//        ib_focus_small= (ImageButton) findViewById(R.id.ib_focus_small);

        iv_history = (ImageView) findViewById(R.id.iv_history);
        iv_collect = (ImageView) findViewById(R.id.iv_collect);

        btn_1x1 = (ImageView) findViewById(R.id.btn_one);
        btn_2x2 = (ImageView) findViewById(R.id.btn_two);
        btn_3x3 = (ImageView) findViewById(R.id.btn_three);
        btn_4x4 = (ImageView) findViewById(R.id.btn_four);
        //动态设置RelativeLayout的高度
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth,screenWidth+3);
        rl_gv.setLayoutParams(params);
    }

    private void initData() {
        nodeList = new ArrayList<NodeBean>();
        gridViewList=new ArrayList<View>();
        initgridViewList();
        gridViewChannelList = new ArrayList<GridViewChannel>();
        controllerMap = new HashMap<String,VideoControllerUtils>();
        chushihuaList = new ArrayList<String>();
        chushihuaList.add("1");

        historyList = new ArrayList<GridViewChannel>();
//        gridViewAdapter = new DahuaGridViewAdapter(this,gridViewList, screenWidth,gridViewChannelList,chushihuaList);
        gridViewAdapter = new DahuaGridViewAdapter(this,gridViewList, screenWidth,controllerMap,chushihuaList);
        gv_gridView.setNumColumns(rowCount);
        gv_gridView.setAdapter(gridViewAdapter);

        //收藏的列表
        collectList = new ArrayList<GridViewChannel>();
        collectAdapter = new CollectAdapter(this,collectList);
        lv_collect.setAdapter(collectAdapter);
        loadCollectFromLocal();

    }

    private void initListener() {
        btn_1x1.setOnClickListener(this);
        btn_2x2.setOnClickListener(this);
        btn_3x3.setOnClickListener(this);
        btn_4x4.setOnClickListener(this);
        iv_history.setOnClickListener(this);
        iv_collect.setOnClickListener(this);
    }

    private void initgridViewList() {
        for (int i = 0; i < 16; i++) {
            gridViewList.add(getEmptyView());
        }
    }

    /*
     * 提示加载
     */
    public void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, title,
                    message, true, false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }
        progressDialog.show();
    }

    /*
     * 隐藏提示加载
     */
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    //获取屏幕宽度
    private int getScreenWidth(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    //监听事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.ib_switch:
//                createSingleChoiceDialog();
//            break;
            case R.id.btn_one:
                switchView(1);
                break;
            case R.id.btn_two:
                switchView(2);
                break;
            case R.id.btn_three:
                switchView(3);
                break;
            case R.id.btn_four:
                switchView(4);
                break;
            case R.id.iv_history:
                if (historyType==0){
                    //就开启历史
                    //先清空
                    if (!controllerMap.isEmpty()){
                        clearCurrentVideo();
                    }
                    //在添加
                    addHistoryVideo();
                    iv_history.setImageResource(R.drawable.clear);
                    historyType = 1;
                }else if(historyType==1){
                    //清空
                    clearCurrentVideo();
                    iv_history.setImageResource(R.drawable.history);
                    historyType = 0;
                }
            break;
            //点击收藏
            case R.id.iv_collect:
                    //说明已经被选中了
                    if (gridViewAdapter.rl!=null){
                        int position = gridViewAdapter.currentPosition;
                        String channelId = new String(controllerMap.get(position+"").getChannelId());
                        String channelName = controllerMap.get(position+"").getChannelName();
                        if (!collectList.isEmpty()) {
                            for (int i = 0; i < collectList.size(); i++) {
                                if (collectList.get(i).getChannelId().equals(channelId)) {
                                    Toast.makeText(DahuaMainActivity.this, "您已经收藏过了", Toast.LENGTH_SHORT).show();
                                } else {
                                    collectList.add(new GridViewChannel(0,channelId,channelName));
                                    Toast.makeText(DahuaMainActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                                    collectAdapter.notifyDataSetChanged();
                                    Utility.setListViewHeightBasedOnChildren(lv_collect);
                                }
                            }
                        }else {
                            collectList.add(new GridViewChannel(0,channelId,channelName));
                            Toast.makeText(DahuaMainActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                            collectAdapter.notifyDataSetChanged();
                            Utility.setListViewHeightBasedOnChildren(lv_collect);
                        }
                    }else {
                        Toast.makeText(DahuaMainActivity.this, "请先选中一个摄像头", Toast.LENGTH_SHORT).show();
                    }
                break;
        }
    }

    private void clearCurrentVideo(){
        Iterator<Map.Entry<String, VideoControllerUtils>> iterator = controllerMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, VideoControllerUtils> entry = iterator.next();
            int position = Integer.parseInt(entry.getKey());
            controllerMap.get(position+"").stopPlayVideo();
            gridViewList.set(position,getEmptyView());
        }
        controllerMap.clear();
        reSetGridAdapter();
    }

    //添加历史纪录的摄像头
    private void addHistoryVideo(){
        List<GridViewChannel> list = loadHistoryFromLocal();
        if (list!=null) {
            for (int i = 0; i < list.size(); i++) {
                addControllerMap(list.get(i).getClickPositon(), list.get(i).getChannelId(), list.get(i).getChannelName());
            }
        }
    }

    //切换视图
    private void switchView(int rowCount){
        if (this.rowCount!=rowCount){
            chushihuaList.clear();
            this.rowCount = rowCount;
            for (int i = 0; i < rowCount; i++) {
                chushihuaList.add("1");
            }
            reSetGridAdapter();
            gv_gridView.setNumColumns(rowCount);
        }
    }

    //创建单选列表项对话框
//    private void createSingleChoiceDialog(){
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this)
//                .setTitle("选择布局")
//                .setIcon(R.drawable.ios7_keypad_outline)
//                .setSingleChoiceItems(new String[]{"1x1布局","2x2布局","3x3布局","4x4布局"}, rowCount-1, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (rowCount-1!=which) {
//                            chushihuaList.clear();
//                            switch (which) {
//                                case 0:
//                                    rowCount = 1;
//                                    break;
//                                case 1:
//                                    rowCount = 2;
//                                    break;
//                                case 2:
//                                    rowCount = 3;
//                                    break;
//                                case 3:
//                                    rowCount = 4;
//                                    break;
//                            }
//                            for (int i = 0; i < rowCount; i++) {
//                                chushihuaList.add("1");
//                            }
//                            gridViewAdapter.notifyDataSetChanged();
//                            gv_gridView.setNumColumns(rowCount);
//                            gv_gridView.invalidate();
//                        }
//                        dialog.dismiss();
//                    }
//                });
//        builder.create().show();
//    }

    public View getEmptyView(){
        View view = View.inflate(this,R.layout.dahua_camera_item,null);
        //初始化控件
        SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.sv_item_liveVideo);
        ImageView iv_item_progressbar = (ImageView) view.findViewById(R.id.iv_item_progressbar);
        surfaceView.setVisibility(View.INVISIBLE);
        iv_item_progressbar.setVisibility(View.INVISIBLE);
        return view;
    }

    private View getVideoView(String channelId,String channelName){
        View view = View.inflate(this,R.layout.dahua_camera_item,null);

//        int width = screenWidth/count;
//        AbsListView.LayoutParams params = new AbsListView.LayoutParams(width,width);
//        view.setLayoutParams(params);

        //初始化控件
        SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.sv_item_liveVideo);
        ImageView iv_item_progressbar = (ImageView) view.findViewById(R.id.iv_item_progressbar);
        ImageView iv_item_ImageView = (ImageView) view.findViewById(R.id.iv_item_ImageView);
        iv_item_ImageView.setVisibility(View.INVISIBLE);
        controller = new VideoControllerUtils(this,sdkHandle,surfaceView,channelId,channelName,iv_item_progressbar);
        controller.startPlayVideo();
        return view;
    }

    //重新设置adapter
    public void reSetGridAdapter(){
        gv_gridView.setAdapter(gridViewAdapter);
    }

    /**
     * 当这个Activity销毁的时候需要做的事情
     *  关闭所有的视频流
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (controllerMap!=null) {
            for (String key : controllerMap.keySet()) {
                historyList.add(new GridViewChannel(Integer.parseInt(key),new String(controllerMap.get(key).getChannelId()),controllerMap.get(key).getChannelName()));
                controllerMap.get(key).stopPlayVideo();
            }
        }
        //将集合存储在Pre中
        saveHistoryToLocal();
        //将收藏列表保存在Pref中
        saveCollectToLocal();
        //用户退出并销毁句柄
        if (isLoginSuccess) {
            userLogOut();
        }
    }

    //用户退出
    private void userLogOut() {
        int ret = IDpsdkCore.DPSDK_Logout(sdkHandle,30*1000);
        if (ret==0){
            IDpsdkCore.DPSDK_Destroy(sdkHandle);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mScrollView)) {
            mDrawerLayout.closeDrawers();
        }else {
            finish();
        }
    }

    private void saveHistoryToLocal() {
        if (historyList != null){
            Gson gson = new Gson();
            String historyString = gson.toJson(historyList);
            PrefUtils.setString(this,"historyVideo",historyString);
        }
    }

    private void saveCollectToLocal() {
        if (collectList!=null){
            Gson gson = new Gson();
            String historyString = gson.toJson(collectList);
            PrefUtils.setString(this,"collectVideo",historyString);
        }
    }

    private List<GridViewChannel> loadHistoryFromLocal(){
        String historyString = PrefUtils.getString(this,"historyVideo",null);
        if (historyString!=null){
            Gson gson = new Gson();
            Type type = new TypeToken<List<GridViewChannel>>(){}.getType();
            List<GridViewChannel> list = gson.fromJson(historyString,type);
            return list;
        }
        return null;
    }

    private void loadCollectFromLocal() {
        String historyString = PrefUtils.getString(this,"collectVideo",null);
        if (historyString!=null){
            Gson gson = new Gson();
            Type type = new TypeToken<List<GridViewChannel>>(){}.getType();
            List<GridViewChannel> list = gson.fromJson(historyString,type);
            for (int i = 0; i < list.size(); i++) {
                collectList.add(list.get(i));
            }
            collectAdapter.notifyDataSetChanged();
            Utility.setListViewHeightBasedOnChildren(lv_collect);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null) {
            if (requestCode == GET_CHANNEL_REQ_CODE) {
                if (resultCode == GET_CHANNEL_RES_CODE) {
                    int position = data.getIntExtra("clilkGridPosition",0);
                    String channelId = data.getStringExtra("channelId");
                    String channelName = data.getStringExtra("channelName");
                    Toast.makeText(DahuaMainActivity.this, "点击的位置："+position+",返回的通道ID：" + channelId + ",通道名字:" + channelName, Toast.LENGTH_SHORT).show();
                    if (!controllerMap.containsKey(position+"")){
                        gridViewList.set(position,getVideoView(channelId,channelName));
                        controllerMap.put(position+"",controller);
                        gv_gridView.setAdapter(gridViewAdapter);
                        gridViewAdapter.notifyDataSetChanged();
                    }
                }
                if (resultCode == GET_CTRL_RES_CODE){
                    int position = data.getIntExtra("position",0);
                    View view = gridViewList.get(position);
                    ((RelativeLayout)gridViewList.get(position).getParent()).removeViewInLayout(view);
                    view.setClickable(true);
                    reSetGridAdapter();
                }
            }
        }
    }

    public void addControllerMap(int position,String channelId,String channelName){
        if (!controllerMap.containsKey(position+"")){
            gridViewList.set(position,getVideoView(channelId,channelName));
            controllerMap.put(position+"",controller);
            reSetGridAdapter();
        }
    }

    private void loadData() {
        MyTask task = new MyTask();
        task.execute();
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //登录成功后
                case LOGIN_SUCCESS:
                    isLoginSuccess = true;
                    initView();
                    initData();
                    initListener();
                    loadData();
                break;
                //登录失败后提示
                case LOGIN_FAILED:
                    hideProgressDialog();
                    Toast.makeText(DahuaMainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };


    class MyTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {

            szCoding = loadDGroupInfoLayered();
            getGroupList(szCoding,null,rootNodeId);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //隐藏对话框
            hideProgressDialog();
            //将数据存储在本地Sharepreferces中
//            Gson gson = new Gson();
//            String nodeListString = gson.toJson(nodeList);
//            PrefUtils.setString(DahuaMainActivity.this,"cameraDataList",nodeListString);
//            Log.e("集合中的信息是：",nodeList.toString());
            Toast.makeText(DahuaMainActivity.this, "获取数据完成", Toast.LENGTH_SHORT).show();

            try {
                treeAdapter = new DeviceTreeAdapter<NodeBean>(lv_deviceListView,mainActivity,nodeList,0);
                lv_deviceListView.setAdapter(treeAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    private synchronized byte[] loadDGroupInfoLayered(){
        //获取根节点的信息
        Dep_Info_t dInfo = new Dep_Info_t();
        ret = IDpsdkCore.DPSDK_GetDGroupRootInfo(sdkHandle, dInfo);
        Return_Value_Info_t rvInfo2 = new Return_Value_Info_t();
        ret = IDpsdkCore.DPSDK_LoadDGroupInfo(sdkHandle, rvInfo2, TIME_OUT);

        Log.e("获取的根节点信息",new String(dInfo.szDepName)+";"+new String(dInfo.szCoding));
        byte [] szCoding = dInfo.szCoding;
        return szCoding;
    }

    private synchronized void getGroupList(byte[] coding,byte [] gName,int rootId){
        // 获取组织设备信息串数量DPSDK_GetDGroupCount
        Get_Dep_Count_Info_t gdcInfo = new Get_Dep_Count_Info_t();
        gdcInfo.szCoding = coding;
        ret = IDpsdkCore.DPSDK_GetDGroupCount(sdkHandle, gdcInfo);


        // 获取组织设备信息串DPSDK_GetDGroupInfo
        Get_Dep_Info_t gdInfo = new Get_Dep_Info_t(gdcInfo.nDepCount, gdcInfo.nDeviceCount);
        gdInfo.szCoding = coding;
        ret = IDpsdkCore.DPSDK_GetDGroupInfo(sdkHandle, gdInfo);

        // 组织信息
        Dep_Info_t[] gInfo = gdInfo.pDepInfo;
        for (int i = 0; i < gInfo.length; i++) {
            nodeId++;
            nodeList.add(new NodeBean(nodeId,rootId,new String(gInfo[i].szDepName).trim(),"组织"));
            rootNodeId = nodeId;
//            depNode = new TreeNode(new String(dInfo[i].szDepName).trim(), new String(dInfo[i].szCoding).trim());
            Log.e("组织信息为：",new String(gInfo[i].szDepName).trim()+">>>>"+new String(gInfo[i].szCoding).trim());
        }


        // 设备信息
        Device_Info_Ex_t[] deviceInfo = gdInfo.pDeviceInfo;
        List<Device_Info_Ex_t> deviceList = new ArrayList<Device_Info_Ex_t>();

        Log.e("设备信息长度",deviceInfo.length+"");

        int port = 0;
        // 设备在线离线排序
        for (int i = 0; i < deviceInfo.length; i++) {
            if (deviceInfo[i].nStatus == 2) {
                deviceList.add(deviceInfo[i]);
            } else {
                deviceList.add(port, deviceInfo[i]);
                port++;
            }
        }


        //设备信息

        for (int i = 0; i < deviceList.size(); i++) {
            DeviceInfo deviceBean = new DeviceInfo();
            deviceBean.setDeviceId(deviceList.get(i).szId+"");
            deviceBean.setDeviceName(new String(deviceList.get(i).szName));
            deviceBean.setDeviceIp(new String(deviceList.get(i).szIP));
            deviceBean.setDevicePort(deviceList.get(i).nPort);
            deviceBean.setUserName(new String(deviceList.get(i).szUser));
            deviceBean.setPassWord(new String(deviceList.get(i).szPassword));
            deviceBean.setChannelCount(deviceList.get(i).nEncChannelChildCount);
            deviceBean.setFactory(deviceList.get(i).nFactory);
            deviceBean.setStatus(deviceList.get(i).nStatus);
            Log.e("设备信息：",deviceBean.toString());

            nodeId++;
            nodeList.add(new NodeBean(nodeId,rootId,new String(deviceList.get(i).szName),"设备"));
            groupNodeId = nodeId;

        }

        for (int i = 0; i < deviceList.size(); i++) {
            // 获取通道信息
            Get_Channel_Info_Ex_t channelInfo = new Get_Channel_Info_Ex_t(deviceList.get(i).nEncChannelChildCount);
            channelInfo.szDeviceId = deviceList.get(i).szId;
            ret = IDpsdkCore.DPSDK_GetChannelInfoEx(sdkHandle, channelInfo);
            Enc_Channel_Info_Ex_t[] ecInfo = channelInfo.pEncChannelnfo;
            String name = "";
            String szId = "";
            // int position = 0;
            for (int j = 0; j < ecInfo.length; j++) {
                // 处理如果通道名称为空，则默认显示：通道1
                name = new String(ecInfo[j].szName).trim();
                szId = new String(ecInfo[j].szId).trim();
                // 过滤szid为空的数据
                if (szId.equals("")) {
                    continue;
                }
                ChannelInfoExt channelBean = new ChannelInfoExt();
                channelBean.setDevType(ecInfo[j].nCameraType);
                channelBean.setSzId(szId);
                channelBean.setSzName(name);
                Log.e("通道信息:",szId+"--->>>"+name);

                nodeId++;
                nodeList.add(new NodeBean(nodeId,groupNodeId,name,"通道",szId,name,"closed"));
            }
        }

        for (int i = 0; i < gInfo.length; i++) {
            // 循环获取组和设备信息
            getGroupList(gInfo[i].szCoding,gInfo[i].szDepName,rootNodeId);
        }
    }
}
