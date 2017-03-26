package com.eju.zejia.data.models;

/**
 * Created by Sidney on 2016/7/28.
 */
public class Follow {

    private long id;
    private String name;
    private String thumbUrl;
    private String region;
    private String plate;
    private int saleNum;
    private float avgPrice;
    private String buildAge;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Follow{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", thumbUrl='").append(thumbUrl).append('\'');
        sb.append(", region='").append(region).append('\'');
        sb.append(", plate='").append(plate).append('\'');
        sb.append(", saleNum=").append(saleNum);
        sb.append(", avgPrice=").append(avgPrice);
        sb.append(", buildAge=").append(buildAge);
        sb.append('}');
        return sb.toString();
    }

    public String getBuildAge() {
        return buildAge;
    }

    public void setBuildAge(String buildAge) {
        this.buildAge = buildAge;
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

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
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

    public int getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(int saleNum) {
        this.saleNum = saleNum;
    }

    public float getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(float avgPrice) {
        this.avgPrice = avgPrice;
    }
}
