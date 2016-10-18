package com.sdr.xitang.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.sdr.xitang.R;
import com.sdr.xitang.adapter.DeviceTreeAdapter;
import com.sdr.xitang.bean.NodeBean;
import com.sdr.xitang.utils.PrefUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DahuaDeviceListActivity extends AppCompatActivity {

    private ListView deviceListView;
    private DeviceTreeAdapter adapter;

    private List<NodeBean> nodeBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dahua_device_list);
        initView();
        initData();
    }

    private void initView() {
        deviceListView = (ListView) findViewById(R.id.lv_deviceListView);
    }

    private void initData() {
        initList();


//        Intent intent = getIntent();
//        List<NodeBean> nodeBeanList = (List<NodeBean>) intent.getSerializableExtra("deviceList");
        Log.e("获取到的List",nodeBeanList.toString());
        try {
            adapter = new DeviceTreeAdapter<NodeBean>(deviceListView,this,nodeBeanList,0);
            deviceListView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initList() {
        nodeBeanList = new ArrayList<NodeBean>();
        Gson gson = new Gson();
        String nodeListString = PrefUtils.getString(this,"cameraDataList","");
        Type type = new TypeToken<List<NodeBean>>(){}.getType();
        List<NodeBean> list = gson.fromJson(nodeListString,type);

        for (int i = 0; i < list.size(); i++) {
            nodeBeanList.add(list.get(i));
        }
    }
}
