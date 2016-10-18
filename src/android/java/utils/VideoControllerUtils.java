package com.sdr.xitang.utils;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.sdr.xitang.R;
import com.company.PlaySDK.IPlaySDK;
import com.dh.DpsdkCore.Enc_Channel_Info_Ex_t;
import com.dh.DpsdkCore.Get_RealStream_Info_t;
import com.dh.DpsdkCore.IDpsdkCore;
import com.dh.DpsdkCore.Ptz_Direct_Info_t;
import com.dh.DpsdkCore.Ptz_Operation_Info_t;
import com.dh.DpsdkCore.Return_Value_Info_t;
import com.dh.DpsdkCore.fMediaDataCallback;

/**
 * Created by HeYongFeng on 2016/9/28.
 */
public class VideoControllerUtils {

    //句柄，SurfaceView,通道id，通道名字,端口port
    private Context mContext;

    private int DLLHandle;

    private SurfaceView mSurfaceView;

    private byte [] channelId;

    private String channelName;

    private ImageView iv_item_progressbar;

    private int mPort;
    private fMediaDataCallback mFm;

    public byte[] getChannelId() {
        return channelId;
    }

    public void setChannelId(byte[] channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
    //    private List<NodeBean> nodeBeanList;

    public VideoControllerUtils(Context mContext,int DLLHandle, SurfaceView surfaceView, String channelId, String channelName,ImageView iv_item_progressbar) {
        this.mContext = mContext;
        this.DLLHandle = DLLHandle;
        mSurfaceView = surfaceView;
        this.channelId = channelId.getBytes();
        this.channelName = channelName;
        this.iv_item_progressbar = iv_item_progressbar;
        //初始化数据
        initDatas();
    }

    private void initDatas() {
//        initList();
        mPort = IPlaySDK.PLAYGetFreePort();
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder)
            {
                Log.e("xss", "surfaceCreated");
                IPlaySDK.InitSurface(mPort, mSurfaceView);
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height)
            {
                Log.e("xss", "surfaceChanged");
            }

            public void surfaceDestroyed(SurfaceHolder holder)
            {
                Log.e("xss", "surfaceDestroyed");
            }
        });

        mFm = new fMediaDataCallback() {

            @Override
            public void invoke(int nPDLLHandle, int nSeq, int nMediaType,
                               byte[] szNodeId, int nParamVal, byte[] szData, int nDataLen) {

                int ret = IPlaySDK.PLAYInputData(mPort, szData, nDataLen);
                if(ret == 0){
//                    Log.e("xss","playing success=" + nSeq + " package size=" + nDataLen);
                }else{
//                    Log.e("xss","playing failed=" + nSeq + " package size=" + nDataLen);
                }
            }
        };
    }

    public void startPlayVideo(){
        if(!StartRealPlay()){
            Log.e("xss", "StartRealPlay failed!");
//            Toast.makeText(mContext, "Open video failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (iv_item_progressbar!=null) {
            iv_item_progressbar.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
            LinearInterpolator lin = new LinearInterpolator();
            animation.setInterpolator(lin);
            iv_item_progressbar.setAnimation(animation);
        }

        try{
            Return_Value_Info_t retVal = new Return_Value_Info_t();

            Get_RealStream_Info_t getRealStreamInfo = new Get_RealStream_Info_t();
            //m_szCameraId = etCam.getText().toString().getBytes();

            System.arraycopy(channelId, 0, getRealStreamInfo.szCameraId, 0, channelId.length);
            //getRealStreamInfo.szCameraId = "1000096$1$0$0".getBytes();
            getRealStreamInfo.nMediaType = 1;
            getRealStreamInfo.nRight = 0;
            getRealStreamInfo.nStreamType = 1;
            getRealStreamInfo.nTransType = 1;
            Enc_Channel_Info_Ex_t ChannelInfo = new Enc_Channel_Info_Ex_t();
            IDpsdkCore.DPSDK_GetChannelInfoById(DLLHandle, channelId, ChannelInfo);
            int ret = IDpsdkCore.DPSDK_GetRealStream(DLLHandle, retVal, getRealStreamInfo, mFm, 10*1000);
            if(ret == 0){
                int m_nSeq = retVal.nReturnValue;
                Log.e("xss DPSDK_GetRealStream success!",ret+"");
//                Toast.makeText(mContext, "Open video success!", Toast.LENGTH_SHORT).show();
//                for (int i = 0; i < nodeBeanList.size(); i++) {
//                    if (new String(channelId).equals(nodeBeanList.get(i).getChannelId()) && !"opening".equals(nodeBeanList.get(i).getChannelStatus())){
//                        nodeBeanList.get(i).setChannelStatus("opening");
//                        //将集合存储起来
//                        //将数据存储在本地Sharepreferces中
//                        Gson gson = new Gson();
//                        String nodeListString = gson.toJson(nodeBeanList);
//                        PrefUtils.setString(mContext,"cameraDataList",nodeListString);
//                        break;
//                    }
//                }
                if (iv_item_progressbar!=null) {
                    iv_item_progressbar.clearAnimation();
                    iv_item_progressbar.setVisibility(View.INVISIBLE);
                }
//                Toast.makeText(mContext, "获取成功", Toast.LENGTH_SHORT).show();
            }else{
                StopRealPlay();
                Log.e("xss DPSDK_GetRealStream failed!",ret+"");
                if (iv_item_progressbar!=null) {
                    iv_item_progressbar.clearAnimation();
                    iv_item_progressbar.setVisibility(View.INVISIBLE);
                }
                Toast.makeText(mContext, "获取实时视频失败", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Log.e("xss", e.toString());
        }
    }

    public void stopPlayVideo(){
        int ret = IDpsdkCore.DPSDK_CloseRealStreamByCameraId(DLLHandle, channelId, 30*1000);
        if(ret == 0){
            Log.e("xss","DPSDK_CloseRealStreamByCameraId success!");
//            Toast.makeText(mContext, "Close video success!", Toast.LENGTH_SHORT).show();

        }else{
            Log.e("xss","DPSDK_CloseRealStreamByCameraId failed! ret = " + ret);
//            Toast.makeText(mContext, "Close video failed!", Toast.LENGTH_SHORT).show();
        }
        StopRealPlay();
    }

    //控制摄像头方向

    private void moveCamera(ImageButton imageButton, final int Direct){
        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Ptz_Direct_Info_t ptzDirectInfo1 = new Ptz_Direct_Info_t();
                        System.arraycopy(channelId, 0, ptzDirectInfo1.szCameraId, 0, channelId.length);
                        ptzDirectInfo1.bStop = false;
                        ptzDirectInfo1.nDirect = Direct;
                        ptzDirectInfo1.nStep = 4;

                        int ret1 = IDpsdkCore.DPSDK_PtzDirection(DLLHandle, ptzDirectInfo1, 10*1000);
                        if(ret1 == 0)
                        {
                            Log.e("xss","DPSDK_PtzDirection success!"+ret1);
                        }
                        else
                        {
                            Log.e("xss","DPSDK_PtzDirection failed!"+ret1);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Ptz_Direct_Info_t ptzDirectInfo2 = new Ptz_Direct_Info_t();
                        System.arraycopy(channelId, 0, ptzDirectInfo2.szCameraId, 0, channelId.length);
                        ptzDirectInfo2.bStop = false;
                        ptzDirectInfo2.nDirect = Direct;
                        ptzDirectInfo2.nStep = 4;

                        int ret2 = IDpsdkCore.DPSDK_PtzDirection(DLLHandle, ptzDirectInfo2, 10*1000);
                        if(ret2 == 0)
                        {
                            Log.e("xss","DPSDK_PtzDirection success!"+ret2);
                        }
                        else
                        {
                            Log.e("xss","DPSDK_PtzDirection failed!"+ret2);
                        }
                        break;
                }
                return true;
            }
        });
    }

    //上
    public void moveTop(ImageButton imageButton){
        moveCamera(imageButton,1);
    }
    //下
    public void moveBottom(ImageButton imageButton){
        moveCamera(imageButton,2);
    }
    //左
    public void moveLeft(ImageButton imageButton){
        moveCamera(imageButton,3);
    }
    //右
    public void moveRight(ImageButton imageButton){
        moveCamera(imageButton,4);
    }
    //左上
    public void moveLeftTop(ImageButton imageButton){
        moveCamera(imageButton,5);
    }

    //右上
    public void moveRightTop(ImageButton imageButton){
        moveCamera(imageButton,7);
    }
    //左下
    public void moveLeftBottom(ImageButton imageButton){
        moveCamera(imageButton,6);
    }
    //右下
    public void moveRightBottom(ImageButton imageButton){
        moveCamera(imageButton,8);
    }

    //控制摄像头镜头
    private void ctrlLens(ImageButton imageButton, final int code){
        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if(arg1.getAction() == MotionEvent.ACTION_DOWN)
                {
                    Ptz_Operation_Info_t ptzOperationInfo = new Ptz_Operation_Info_t();
                    System.arraycopy(channelId, 0, ptzOperationInfo.szCameraId, 0, channelId.length);
                    ptzOperationInfo.bStop = false;
                    ptzOperationInfo.nOperation = code;
                    ptzOperationInfo.nStep = 4;

                    int ret = IDpsdkCore.DPSDK_PtzCameraOperation(DLLHandle, ptzOperationInfo, 10*1000);
                    if(ret == 0)
                    {
                        Log.e("xss","DPSDK_PtzCameraOperation success!"+ret);
                    }
                    else
                    {
                        Log.e("xss","DPSDK_PtzCameraOperation failed!"+ret);
                    }
                }
                else if(arg1.getAction() == MotionEvent.ACTION_UP)
                {
                    Ptz_Operation_Info_t ptzOperationInfo = new Ptz_Operation_Info_t();
                    System.arraycopy(channelId, 0, ptzOperationInfo.szCameraId, 0, channelId.length);
                    ptzOperationInfo.bStop = true;
                    ptzOperationInfo.nOperation = code;
                    ptzOperationInfo.nStep = 4;

                    int ret = IDpsdkCore.DPSDK_PtzCameraOperation(DLLHandle, ptzOperationInfo, 10*1000);
                    if(ret == 0)
                    {
                        Log.e("xss","DPSDK_PtzCameraOperation success!"+ret);
                    }
                    else
                    {
                        Log.e("xss","DPSDK_PtzCameraOperation failed!"+ret);
                    }
                }

                return true;
            }
        });
    }

    //放大
    public void addZoom(ImageButton imageButton){
        ctrlLens(imageButton,0);
    }
    //缩小
    public void reduceZoom(ImageButton imageButton){
        ctrlLens(imageButton,3);
    }

    //焦距+
    public void addFocus(ImageButton imageButton){
        ctrlLens(imageButton,1);
    }
    //焦距-
    public void reduceFocus(ImageButton imageButton){
        ctrlLens(imageButton,4);
    }


    public synchronized boolean StartRealPlay()
    {
        if(mSurfaceView == null)
            return false;

        boolean bOpenRet = IPlaySDK.PLAYOpenStream(mPort,null,0,1500*1024) == 0? false : true;
        if(bOpenRet)
        {
            boolean bPlayRet = IPlaySDK.PLAYPlay(mPort, mSurfaceView) == 0 ? false : true;
            if(bPlayRet)
            {
                boolean bSuccess = IPlaySDK.PLAYPlaySoundShare(mPort) == 0 ? false : true;
                if(!bSuccess)
                {
                    IPlaySDK.PLAYStop(mPort);
                    IPlaySDK.PLAYCloseStream(mPort);
                    return false;
                }
            }
            else
            {
                IPlaySDK.PLAYCloseStream(mPort);
                return false;
            }
        }
        else
        {
            return false;
        }

        return true;
    }

    public synchronized void StopRealPlay()
    {
        try {
            IPlaySDK.PLAYStopSoundShare(mPort);
            IPlaySDK.PLAYStop(mPort);
            IPlaySDK.PLAYCloseStream(mPort);

        } catch (Exception e) {
            e.printStackTrace();
        }

//        for (int i = 0; i < nodeBeanList.size(); i++) {
//            if (new String(channelId).equals(nodeBeanList.get(i).getChannelId()) &&!"closed".equals(nodeBeanList.get(i).getChannelStatus())){
//                nodeBeanList.get(i).setChannelStatus("closed");
//                //将集合存储起来
//                //将数据存储在本地Sharepreferces中
//                Gson gson = new Gson();
//                String nodeListString = gson.toJson(nodeBeanList);
//                PrefUtils.setString(mContext,"cameraDataList",nodeListString);
//                break;
//            }
//        }
    }


//    private void initList() {
//        nodeBeanList = new ArrayList<>();
//        Gson gson = new Gson();
//        String nodeListString = PrefUtils.getString(mContext,"cameraDataList","");
//        Type type = new TypeToken<List<NodeBean>>(){}.getType();
//        List<NodeBean> list = gson.fromJson(nodeListString,type);
//
//        for (int i = 0; i < list.size(); i++) {
//            nodeBeanList.add(list.get(i));
//        }
//    }
}
