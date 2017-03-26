package com.eju.zejia.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sidney on 2016/7/25.
 */
public class Community {

    public static final int IS_FOLLOW = 1;

    @SerializedName("communityId")
    private long id;
    @SerializedName("communityName")
    private String name;
    private String panoUrl;
    private String longitude;
    private String latitude;
    private String address;
    private float capacityRate;
    private float greenRate;
    private String buildAge;
    private int houseCount;
    private int area;
    private String parkCount;
    private float avgPrice;
    private int chi;
    private String chiName;
    private int wan;
    private String wanName;
    private int xue;
    private String xueName;
    private int mai;
    private String maiName;
    private int yi;
    private String yiName;
    private List<Site> siteList;
    private String region;
    private String plate;
    private String feature;
    private int isFollow;
    private String pano360Url;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Community{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", panoUrl='").append(panoUrl).append('\'');
        sb.append(", longitude='").append(longitude).append('\'');
        sb.append(", latitude='").append(latitude).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append(", capacityRate=").append(capacityRate);
        sb.append(", greenRate=").append(greenRate);
        sb.append(", buildAge='").append(buildAge).append('\'');
        sb.append(", houseCount=").append(houseCount);
        sb.append(", area=").append(area);
        sb.append(", parkCount='").append(parkCount).append('\'');
        sb.append(", avgPrice=").append(avgPrice);
        sb.append(", chi=").append(chi);
        sb.append(", chiName='").append(chiName).append('\'');
        sb.append(", wan=").append(wan);
        sb.append(", wanName='").append(wanName).append('\'');
        sb.append(", xue=").append(xue);
        sb.append(", xueName='").append(xueName).append('\'');
        sb.append(", mai=").append(mai);
        sb.append(", maiName='").append(maiName).append('\'');
        sb.append(", yi=").append(yi);
        sb.append(", yiName='").append(yiName).append('\'');
        sb.append(", siteList=").append(siteList);
        sb.append(", region='").append(region).append('\'');
        sb.append(", plate='").append(plate).append('\'');
        sb.append(", feature='").append(feature).append('\'');
        sb.append(", isFollow=").append(isFollow);
        sb.append('}');
        return sb.toString();
    }

    public String getPano360Url() {
        return pano360Url;
    }

    public void setPano360Url(String pano360Url) {
        this.pano360Url = pano360Url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPanoUrl() {
        return panoUrl;
    }

    public void setPanoUrl(String panoUrl) {
        this.panoUrl = panoUrl;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getCapacityRate() {
        return capacityRate;
    }

    public void setCapacityRate(float capacityRate) {
        this.capacityRate = capacityRate;
    }

    public float getGreenRate() {
        return greenRate;
    }

    public void setGreenRate(float greenRate) {
        this.greenRate = greenRate;
    }

    public String getBuildAge() {
        return buildAge;
    }

    public void setBuildAge(String buildAge) {
        this.buildAge = buildAge;
    }

    public int getHouseCount() {
        return houseCount;
    }

    public void setHouseCount(int houseCount) {
        this.houseCount = houseCount;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getParkCount() {
        return parkCount;
    }

    public void setParkCount(String parkCount) {
        this.parkCount = parkCount;
    }

    public float getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(float avgPrice) {
        this.avgPrice = avgPrice;
    }

    public int getChi() {
        return chi;
    }

    public void setChi(int chi) {
        this.chi = chi;
    }

    public String getChiName() {
        return chiName;
    }

    public void setChiName(String chiName) {
        this.chiName = chiName;
    }

    public int getWan() {
        return wan;
    }

    public void setWan(int wan) {
        this.wan = wan;
    }

    public String getWanName() {
        return wanName;
    }

    public void setWanName(String wanName) {
        this.wanName = wanName;
    }

    public int getXue() {
        return xue;
    }

    public void setXue(int xue) {
        this.xue = xue;
    }

    public String getXueName() {
        return xueName;
    }

    public void setXueName(String xueName) {
        this.xueName = xueName;
    }

    public int getMai() {
        return mai;
    }

    public void setMai(int mai) {
        this.mai = mai;
    }

    public String getMaiName() {
        return maiName;
    }

    public void setMaiName(String maiName) {
        this.maiName = maiName;
    }

    public int getYi() {
        return yi;
    }

    public void setYi(int yi) {
        this.yi = yi;
    }

    public String getYiName() {
        return yiName;
    }

    public void setYiName(String yiName) {
        this.yiName = yiName;
    }

    public List<Site> getSiteList() {
        return siteList;
    }

    public void setSiteList(List<Site> siteList) {
        this.siteList = siteList;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public int getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(int isFollow) {
        this.isFollow = isFollow;
    }
}
