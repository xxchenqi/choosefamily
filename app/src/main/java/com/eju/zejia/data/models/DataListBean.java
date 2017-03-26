package com.eju.zejia.data.models;

import java.io.Serializable;

/**
 * Created by Sandy on 2016/8/5/0005.
 */
public class DataListBean implements Serializable {
    private String addrName;
    private String id;
    private String latitude;
    private String longitude;
    private String type;

    public String getAddrName() {
        return addrName;
    }

    public void setAddrName(String addrName) {
        this.addrName = addrName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
