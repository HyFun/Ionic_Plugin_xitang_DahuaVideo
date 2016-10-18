package com.sdr.xitang.bean;


import com.sdr.xitang.tree.bean.TreeNodeId;
import com.sdr.xitang.tree.bean.TreeNodeLabel;
import com.sdr.xitang.tree.bean.TreeNodePid;

import java.io.Serializable;

/**
 * Created by HeYongFeng on 2016/9/27.
 */
public class NodeBean implements Serializable{
    @TreeNodeId
    private int nodeId;
    @TreeNodePid
    private int parentId;
    @TreeNodeLabel
    private String name;

    private String nodeType;

    private String channelId;
    private String channelName;

    //该摄像头是否在GridView中打开
    private String channelStatus;

    public NodeBean(int nodeId, int parentId, String name, String nodeType) {
        this.nodeId = nodeId;
        this.parentId = parentId;
        this.name = name;
        this.nodeType = nodeType;
    }

    public NodeBean(int nodeId, int parentId, String name, String nodeType, String channelId, String channelName,String channelStatus) {
        this.nodeId = nodeId;
        this.parentId = parentId;
        this.name = name;
        this.nodeType = nodeType;
        this.channelId = channelId;
        this.channelName = channelName;
        this.channelStatus = channelStatus;
    }

    public String getChannelStatus() {
        return channelStatus;
    }

    public void setChannelStatus(String channelStatus) {
        this.channelStatus = channelStatus;
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

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public String toString() {
        return "NodeBean{" +
                "nodeId=" + nodeId +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                ", nodeType='" + nodeType + '\'' +
                ", channelId='" + channelId + '\'' +
                ", channelName='" + channelName + '\'' +
                ", channelStatus='" + channelStatus + '\'' +
                '}';
    }
}
