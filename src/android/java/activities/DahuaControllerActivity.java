package com.sdr.xitang.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdr.xitang.DahuaMainActivity;
import com.sdr.xitang.R;
import com.sdr.xitang.utils.VideoControllerUtils;

public class DahuaControllerActivity extends AppCompatActivity {

    private RelativeLayout rl_controller;
    //屏幕的宽
    private int screenWidth;
    private VideoControllerUtils controller;

    private ImageButton ib_switch,ib_video,ib_camera,ib_voice,ib_left_top,ib_top,ib_right_top,
            ib_left,ib_middle,ib_right,ib_left_bottom,ib_bottom,ib_right_bottom,
            ib_video_big,ib_video_small,ib_focus_big,ib_focus_small;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dahua_controller);
        initActionBar();
        initView();
        initData();
        initListener();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.dahuavideo_actionbar_custom);
        ((TextView)actionBar.getCustomView().findViewById(R.id.tv_actionbar_title)).setText("视频操控");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#72D3E4")));
        actionBar.getCustomView().findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        screenWidth = getScreenWidth();

        rl_controller = (RelativeLayout) findViewById(R.id.rl_controller);


        //动态设置RelativeLayout的高度
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth,screenWidth+3);
        rl_controller.setLayoutParams(params);


        ib_left_top= (ImageButton) findViewById(R.id.ib_left_top);
        ib_top= (ImageButton) findViewById(R.id.ib_top);
        ib_right_top= (ImageButton) findViewById(R.id.ib_right_top);
        ib_left= (ImageButton) findViewById(R.id.ib_left);
        ib_middle= (ImageButton) findViewById(R.id.ib_middle);
        ib_right= (ImageButton) findViewById(R.id.ib_right);
        ib_left_bottom= (ImageButton) findViewById(R.id.ib_left_bottom);
        ib_bottom= (ImageButton) findViewById(R.id.ib_bottom);
        ib_right_bottom= (ImageButton) findViewById(R.id.ib_right_bottom);


        ib_video_big= (ImageButton) findViewById(R.id.ib_video_big);
        ib_video_small= (ImageButton) findViewById(R.id.ib_video_small);
        ib_focus_big= (ImageButton) findViewById(R.id.ib_focus_big);
        ib_focus_small= (ImageButton) findViewById(R.id.ib_focus_small);
    }

    private void initData() {
        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        intent.putExtra("position",position);
        setResult(DahuaMainActivity.GET_CTRL_RES_CODE,intent);

        View view = DahuaMainActivity.mainActivity.gridViewList.get(position);
        DahuaMainActivity.mainActivity.gv_gridView.removeViewInLayout(view);
        controller = DahuaMainActivity.mainActivity.controllerMap.get(position+"");
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(screenWidth, screenWidth);
        view.setLayoutParams(params);
        view.setOnClickListener(null);
        view.setOnLongClickListener(null);
        rl_controller.addView(view);
    }

    private void initListener() {
        //控制八个方向
        controller.moveTop(ib_top);
        controller.moveBottom(ib_bottom);
        controller.moveLeft(ib_left);
        controller.moveRight(ib_right);
        controller.moveLeftTop(ib_left_top);
        controller.moveLeftBottom(ib_left_bottom);
        controller.moveRightTop(ib_right_top);
        controller.moveRightBottom(ib_right_bottom);

        //控制镜头
        controller.addZoom(ib_video_big);
        controller.reduceZoom(ib_video_small);
        controller.addFocus(ib_focus_big);
        controller.reduceFocus(ib_focus_small);
    }


    //获取屏幕宽度
    private int getScreenWidth(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
}
