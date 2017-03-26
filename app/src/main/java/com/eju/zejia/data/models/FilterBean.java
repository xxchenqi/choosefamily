package com.eju.zejia.data.models;


import java.util.List;

public class FilterBean {

    private FilterValueBean filterValue;
    private FilterCheckBean filterCheck;

    public FilterValueBean getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(FilterValueBean filterValue) {
        this.filterValue = filterValue;
    }

    public FilterCheckBean getFilterCheck() {
        return filterCheck;
    }

    public void setFilterCheck(FilterCheckBean filterCheck) {
        this.filterCheck = filterCheck;
    }
    
    public static class FilterCheckBean {
        private String areaCompose;
        private String buildAgeCompose;
        private String communityCompose;
        private String familyCompose;
        private String houseLifeCompose;
        private String houseTypeCompose;
        private String infrastructureCompose;
        private String layoutCompose;
        private String lifeCompose;
        private String orderComposes;
        private String priceCompose;
        private String regionComposes;
        private String supportComposes;

        public String getAreaCompose() {
            return areaCompose;
        }

        public void setAreaCompose(String areaCompose) {
            this.areaCompose = areaCompose;
        }

        public String getBuildAgeCompose() {
            return buildAgeCompose;
        }

        public void setBuildAgeCompose(String buildAgeCompose) {
            this.buildAgeCompose = buildAgeCompose;
        }

        public String getCommunityCompose() {
            return communityCompose;
        }

        public void setCommunityCompose(String communityCompose) {
            this.communityCompose = communityCompose;
        }

        public String getFamilyCompose() {
            return familyCompose;
        }

        public void setFamilyCompose(String familyCompose) {
            this.familyCompose = familyCompose;
        }

        public String getHouseLifeCompose() {
            return houseLifeCompose;
        }

        public void setHouseLifeCompose(String houseLifeCompose) {
            this.houseLifeCompose = houseLifeCompose;
        }

        public String getHouseTypeCompose() {
            return houseTypeCompose;
        }

        public void setHouseTypeCompose(String houseTypeCompose) {
            this.houseTypeCompose = houseTypeCompose;
        }

        public String getInfrastructureCompose() {
            return infrastructureCompose;
        }

        public void setInfrastructureCompose(String infrastructureCompose) {
            this.infrastructureCompose = infrastructureCompose;
        }

        public String getLayoutCompose() {
            return layoutCompose;
        }

        public void setLayoutCompose(String layoutCompose) {
            this.layoutCompose = layoutCompose;
        }

        public String getLifeCompose() {
            return lifeCompose;
        }

        public void setLifeCompose(String lifeCompose) {
            this.lifeCompose = lifeCompose;
        }

        public String getOrderComposes() {
            return orderComposes;
        }

        public void setOrderComposes(String orderComposes) {
            this.orderComposes = orderComposes;
        }

        public String getPriceCompose() {
            return priceCompose;
        }

        public void setPriceCompose(String priceCompose) {
            this.priceCompose = priceCompose;
        }

        public String getRegionComposes() {
            return regionComposes;
        }

        public void setRegionComposes(String regionComposes) {
            this.regionComposes = regionComposes;
        }

        public String getSupportComposes() {
            return supportComposes;
        }

        public void setSupportComposes(String supportComposes) {
            this.supportComposes = supportComposes;
        }
    }

    public static class FilterValueBean {
        private List<FilterItemBean> areaCompose;
        private List<FilterItemBean> buildAgeCompose;
        private List<FilterItemBean> communityCompose;
        private List<FilterItemBean> familyCompose;
        private List<FilterItemBean> houseLifeCompose;
        private List<FilterItemBean> houseTypeCompose;
        private List<FilterItemBean> infrastructureCompose;
        private List<FilterItemBean> layoutCompose;
        private List<FilterItemBean> lifeCompose;
        private List<FilterItemBean> orderComposes;
        private List<FilterItemBean> priceCompose;
        private List<FilterItemBean> regionComposes;
        private List<FilterItemBean> supportComposes;

        public List<FilterItemBean> getAreaCompose() {
            return areaCompose;
        }

        public void setAreaCompose(List<FilterItemBean> areaCompose) {
            this.areaCompose = areaCompose;
        }

        public List<FilterItemBean> getBuildAgeCompose() {
            return buildAgeCompose;
        }

        public void setBuildAgeCompose(List<FilterItemBean> buildAgeCompose) {
            this.buildAgeCompose = buildAgeCompose;
        }

        public List<FilterItemBean> getCommunityCompose() {
            return communityCompose;
        }

        public void setCommunityCompose(List<FilterItemBean> communityCompose) {
            this.communityCompose = communityCompose;
        }

        public List<FilterItemBean> getFamilyCompose() {
            return familyCompose;
        }

        public void setFamilyCompose(List<FilterItemBean> familyCompose) {
            this.familyCompose = familyCompose;
        }

        public List<FilterItemBean> getHouseLifeCompose() {
            return houseLifeCompose;
        }

        public void setHouseLifeCompose(List<FilterItemBean> houseLifeCompose) {
            this.houseLifeCompose = houseLifeCompose;
        }

        public List<FilterItemBean> getHouseTypeCompose() {
            return houseTypeCompose;
        }

        public void setHouseTypeCompose(List<FilterItemBean> houseTypeCompose) {
            this.houseTypeCompose = houseTypeCompose;
        }

        public List<FilterItemBean> getInfrastructureCompose() {
            return infrastructureCompose;
        }

        public void setInfrastructureCompose(List<FilterItemBean> infrastructureCompose) {
            this.infrastructureCompose = infrastructureCompose;
        }

        public List<FilterItemBean> getLayoutCompose() {
            return layoutCompose;
        }

        public void setLayoutCompose(List<FilterItemBean> layoutCompose) {
            this.layoutCompose = layoutCompose;
        }

        public List<FilterItemBean> getLifeCompose() {
            return lifeCompose;
        }

        public void setLifeCompose(List<FilterItemBean> lifeCompose) {
            this.lifeCompose = lifeCompose;
        }

        public List<FilterItemBean> getOrderComposes() {
            return orderComposes;
        }

        public void setOrderComposes(List<FilterItemBean> orderComposes) {
            this.orderComposes = orderComposes;
        }

        public List<FilterItemBean> getPriceCompose() {
            return priceCompose;
        }

        public void setPriceCompose(List<FilterItemBean> priceCompose) {
            this.priceCompose = priceCompose;
        }

        public List<FilterItemBean> getRegionComposes() {
            return regionComposes;
        }

        public void setRegionComposes(List<FilterItemBean> regionComposes) {
            this.regionComposes = regionComposes;
        }

        public List<FilterItemBean> getSupportComposes() {
            return supportComposes;
        }

        public void setSupportComposes(List<FilterItemBean> supportComposes) {
            this.supportComposes = supportComposes;
        }
    }

    public static class FilterItemBean {
        private String name;
        private int code;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
}
