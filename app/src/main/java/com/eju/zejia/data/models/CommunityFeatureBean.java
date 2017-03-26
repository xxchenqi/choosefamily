package com.eju.zejia.data.models;

import java.io.Serializable;
import java.util.List;

/**
 * ----------------------------------------
 * 注释: 社区特色bean
 * <p>
 * 作者: cq
 * <p>
 * 时间: on 2016/7/27 11:05
 * ----------------------------------------
 */
public class CommunityFeatureBean implements Serializable {

    private int id;
    private int isCheck;
    private int order;
    private String tagName;
    private String desc;
    private int type;
    private List<CommunityFeatureBean> children;
    private int parent_id;

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public String getDesc() {
        return desc;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }

    public int getId() {
        return id;
    }

    public int getIsCheck() {
        return isCheck;
    }

    public int getOrder() {
        return order;
    }

    public String getTagName() {
        return tagName;
    }

    public int getType() {
        return type;
    }

    public List<CommunityFeatureBean> getChildren() {
        return children;
    }
}
