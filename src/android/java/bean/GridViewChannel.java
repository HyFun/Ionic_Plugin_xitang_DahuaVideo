package com.sdr.xitang.bean;

/**
 * Created by HeYongFeng on 2016/9/28.
 */
public class GridViewChannel {
    private int clickPositon;
    private String channelId;
    private String channelName;

    public GridViewChannel(int clickPositon, String channelId, String channelName) {
        this.clickPositon = clickPositon;
        this.channelId = channelId;
        this.channelName = channelName;
    }

    public int getClickPositon() {
        return clickPositon;
    }

    public void setClickPositon(int clickPositon) {
        this.clickPositon = clickPositon;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
