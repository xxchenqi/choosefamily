package com.eju.zejia.data.models;

/**
 * 房源
 * <p>
 * Created by Sidney on 2016/7/26.
 */
public class House {

    private long id;

    /**
     * 房源缩略图地址
     */
    private String logo;
    /**
     * 名称
     */
    private String title;
    /**
     * 户型
     */
    private String layout;
    /**
     * 面积
     */
    private float area;
    /**
     * 朝向
     */
    private String orientation;
    /**
     * 房属性
     */
    private String feature;
    /**
     * 销售价格
     */
    private float price;
    private float avgPrice;
    private float sellPrice;

    /**
     * 详情网址
     */
    private String infoUrl;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("House{");
        sb.append("id=").append(id);
        sb.append(", logo='").append(logo).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", layout='").append(layout).append('\'');
        sb.append(", area=").append(area);
        sb.append(", orientation='").append(orientation).append('\'');
        sb.append(", feature='").append(feature).append('\'');
        sb.append(", price=").append(price);
        sb.append(", avgPrice=").append(avgPrice);
        sb.append(", sellPrice=").append(sellPrice);
        sb.append(", infoUrl='").append(infoUrl).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public float getArea() {
        return area;
    }

    public void setArea(float area) {
        this.area = area;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(float avgPrice) {
        this.avgPrice = avgPrice;
    }

    public float getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(float sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }
}
