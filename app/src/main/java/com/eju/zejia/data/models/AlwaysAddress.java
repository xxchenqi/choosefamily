package com.eju.zejia.data.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Sandy on 2016/8/5/0005.
 */
public class AlwaysAddress implements Serializable{


    /**
     * code : 200
     * msg : 成功
     * response : {"dataList":[{"addrName":"aaaaa","id":"1","latitude":"31.21212","longitude":"31.21212","type":"work"},{"addrName":"bbbbb","id":"1","latitude":"33.21212","longitude":"33.21212","type":"life"}]}
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

    public static class ResponseBean implements Serializable{
        /**
         * addrName : aaaaa
         * id : 1
         * latitude : 31.21212
         * longitude : 31.21212
         * type : work
         */

        private List<DataListBean> dataList;

        public List<DataListBean> getDataList() {
            return dataList;
        }

        public void setDataList(List<DataListBean> dataList) {
            this.dataList = dataList;
        }
    }
}
