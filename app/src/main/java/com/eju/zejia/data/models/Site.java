package com.eju.zejia.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 房源站点
 * <p>
 * Created by Sidney on 2016/7/26.
 */
public class Site {

    /**
     * 房源网站名称
     */
    @SerializedName("siteName")
    private String name;

    /**
     * 房源网站logo地址
     */
    @SerializedName("siteLogo")
    private String logo;

    /**
     * 房源数
     */
    private int houseCount;

    /**
     * 房源列表
     */
    private List<House> houseList;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Site{");
        sb.append("name='").append(name).append('\'');
        sb.append(", logo='").append(logo).append('\'');
        sb.append(", houseCount=").append(houseCount);
        sb.append(", houseList=").append(houseList);
        sb.append('}');
        return sb.toString();
    }

    public List<House> getHouseList() {
        return houseList;
    }

    public void setHouseList(List<House> houseList) {
        this.houseList = houseList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getHouseCount() {
        return houseCount;
    }

    public void setHouseCount(int houseCount) {
        this.houseCount = houseCount;
    }
}
