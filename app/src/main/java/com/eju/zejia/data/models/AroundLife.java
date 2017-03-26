package com.eju.zejia.data.models;

import java.util.List;

/**
 * Created by Sandy on 2016/8/5/0005.
 */
public class AroundLife {

    /**
     * code : 200
     * msg : 成功
     * response : {"businessList":[{"address":"复兴东路663弄1-7号","distance":"1906","latitude":"31.243738","longitude":"121.490857","name":"南京东路"}],"foodList":[{"address":"中华路388号附近","costs":"33","distance":"733","latitude":"31.219454","longitude":"121.498840","name":"广式烧腊老铺"}],"hospitalList":[{"address":"黄家路163号边门","distance":"732","latitude":"31.216149","longitude":"121.494810","name":"上海市黄浦区中西医结合医院(黄家路分院)-中医楼"}],"marketList":[{"address":"傅家街65号","distance":"237","latitude":"31.223858","longitude":"121.493451","name":"农工商超市(傅家街)"}],"movieList":[{"address":"三牌楼路8号1层(近昼锦路)","distance":"285","latitude":"31.224778","longitude":"121.492065","name":"星影5D360度超立体影院(豫园店)"}],"parkList":[{"address":"河南南路519号附近","distance":"217","latitude":"31.221150","longitude":"121.489943","name":"司马秤"}],"schoolList":[{"address":"复兴东路123号(近盐码头路)","distance":"799","latitude":"31.221701","longitude":"121.500227","name":"市八初级中学"}],"vegetableList":[{"address":"蓬莱路207号","distance":"736","latitude":"31.218062","longitude":"121.490702","name":"MILK CLUB随心订"}]}
     */

    private String code;
    private String msg;
    private ResponseBean response;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResponseBean getResponse() {
        return response;
    }

    public void setResponse(ResponseBean response) {
        this.response = response;
    }

    public static class ResponseBean {
        /**
         * address : 复兴东路663弄1-7号
         * distance : 1906
         * latitude : 31.243738
         * longitude : 121.490857
         * name : 南京东路
         */

        private List<AroundLifeDetail> businessList;
        /**
         * address : 中华路388号附近
         * costs : 33
         * distance : 733
         * latitude : 31.219454
         * longitude : 121.498840
         * name : 广式烧腊老铺
         */

        private List<AroundLifeDetail> foodList;
        /**
         * address : 黄家路163号边门
         * distance : 732
         * latitude : 31.216149
         * longitude : 121.494810
         * name : 上海市黄浦区中西医结合医院(黄家路分院)-中医楼
         */

        private List<AroundLifeDetail> hospitalList;
        /**
         * address : 傅家街65号
         * distance : 237
         * latitude : 31.223858
         * longitude : 121.493451
         * name : 农工商超市(傅家街)
         */

        private List<AroundLifeDetail> marketList;
        /**
         * address : 三牌楼路8号1层(近昼锦路)
         * distance : 285
         * latitude : 31.224778
         * longitude : 121.492065
         * name : 星影5D360度超立体影院(豫园店)
         */

        private List<AroundLifeDetail> movieList;
        /**
         * address : 河南南路519号附近
         * distance : 217
         * latitude : 31.221150
         * longitude : 121.489943
         * name : 司马秤
         */

        private List<AroundLifeDetail> parkList;
        /**
         * address : 复兴东路123号(近盐码头路)
         * distance : 799
         * latitude : 31.221701
         * longitude : 121.500227
         * name : 市八初级中学
         */

        private List<AroundLifeDetail> schoolList;
        /**
         * address : 蓬莱路207号
         * distance : 736
         * latitude : 31.218062
         * longitude : 121.490702
         * name : MILK CLUB随心订
         */

        private List<AroundLifeDetail> vegetableList;

        public List<AroundLifeDetail> getBusinessList() {
            return businessList;
        }

        public void setBusinessList(List<AroundLifeDetail> businessList) {
            this.businessList = businessList;
        }

        public List<AroundLifeDetail> getFoodList() {
            return foodList;
        }

        public void setFoodList(List<AroundLifeDetail> foodList) {
            this.foodList = foodList;
        }

        public List<AroundLifeDetail> getHospitalList() {
            return hospitalList;
        }

        public void setHospitalList(List<AroundLifeDetail> hospitalList) {
            this.hospitalList = hospitalList;
        }

        public List<AroundLifeDetail> getMarketList() {
            return marketList;
        }

        public void setMarketList(List<AroundLifeDetail> marketList) {
            this.marketList = marketList;
        }

        public List<AroundLifeDetail> getMovieList() {
            return movieList;
        }

        public void setMovieList(List<AroundLifeDetail> movieList) {
            this.movieList = movieList;
        }

        public List<AroundLifeDetail> getParkList() {
            return parkList;
        }

        public void setParkList(List<AroundLifeDetail> parkList) {
            this.parkList = parkList;
        }

        public List<AroundLifeDetail> getSchoolList() {
            return schoolList;
        }

        public void setSchoolList(List<AroundLifeDetail> schoolList) {
            this.schoolList = schoolList;
        }

        public List<AroundLifeDetail> getVegetableList() {
            return vegetableList;
        }

        public void setVegetableList(List<AroundLifeDetail> vegetableList) {
            this.vegetableList = vegetableList;
        }

    }
}
