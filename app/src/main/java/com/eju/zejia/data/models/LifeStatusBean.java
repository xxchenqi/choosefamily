package com.eju.zejia.data.models;

import java.io.Serializable;

/**
 * ----------------------------------------
 * 注释: 生活状态bean
 * <p>
 * 作者: cq
 * <p>
 * 时间: on 2016/7/27 11:05
 * ----------------------------------------
 */
public class LifeStatusBean implements Serializable {


    private String id;
    private String name;
    private String content;
    private boolean flag;

    public LifeStatusBean(String id, String name, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
