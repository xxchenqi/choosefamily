package com.eju.zejia.data.models;

/**
 * ----------------------------------------
 * 注释:用户登录bean
 * <p>
 * 作者: cq
 * <p>
 * 时间: on 2016/8/1 11:06
 * ----------------------------------------
 */
public class UserLoginBean {
    private String sessionId;
    private String nickname;
    private String photoUrl;
    private int inHome;

    public int getInHome() {
        return inHome;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
