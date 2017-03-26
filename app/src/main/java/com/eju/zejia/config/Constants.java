package com.eju.zejia.config;

/**
 * Created by Sidney on 2016/7/18.
 */
public interface Constants {
    String TEST_URL = "http://172.29.32.104/api/";
//    String OLD_UAT_URL = "http://10.0.60.68/api/";
    String UAT_URL = "http://10.0.60.135/api/";
    String API_URL = "http://www.51zejia.com/api/";
    String MOCK_URL = "http://172.29.32.121:12306/";
    String UAT_H5_URL = "http://10.0.60.135/html/";

    String ZEJIA_API_URL = API_URL;
    String ZEJIA_API_H5_URL = UAT_H5_URL;
    String VERSION = "1.0";

    int TIME_OUT = 15_000;

    int PAGE_COUNT = 10;

    interface API {
        // 1.	获取最新版本接口
        String GET_VERSION = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "getVersion");

        // 2.	用户登录接口
        String USER_LOGIN = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "userLogin");

        // 3.	用户注册接口
        String USER_REG = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "userReg");

        // 4.	获取手机验证码
        String GET_VCODE = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "getVCode");

        // 校验手机验证码
        String CHECK_VCODE = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "checkVCode");

        // 5.	上传头像
        String UPLOAD_PHOTO = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "uploadPhoto");

        // 6.	获取用户信息
        String GET_USER_INFO = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "getUserInfo");

        // 7.	设置账号绑定
        String UPDATE_BIND_ACCOUNT = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "updateBindAccount");

        // 8.	修改用户昵称
        String UPDATE_USER_NAME = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "updateUserName");

        // 9.	保存常去地址
        String SAVE_ADDR = String.format("%s/%s/%s", ZEJIA_API_URL, VERSION, "saveAddr");

        // 10.	获取常去地址列表
        String GET_ADDR = String.format("%s/%s/%s", ZEJIA_API_URL, VERSION, "getAddrList");

        // 13.	获取单条地址记录
        String GET_ADDR_BY_ID = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "getAddrById");

        // 14.	我的关注接口
        String GET_FOLLOW_LIST = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "getFollowList");

        // 15.	新增关注接口
        String ADD_FOLLOW = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "addFollow");

        // 16.	移除关注接口
        String DELETE_FOLLOW = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "deleteFollow");

        // 17.	获取社区详情
        String GET_COMMUNITY_INFO = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "getCommunityInfo");

        // 23.	获取社区周边信息
        String GET_COMM_PERIPHERY = String.format("%s/%s/%s", ZEJIA_API_URL, VERSION, "getCommPeriphery");

        // 24.	获取匹配社区列表
        String GET_MATCH_COMMUNITY = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "getMatchCommunity");

        // 修改密码
        String MODIFY_PWD = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "modifyPwd");
        //修改昵称
        String UPDATE_USERNAME = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "updateNickname");
        // 第三方登录
        String USER_LOGIN_BY_OTHER = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "userLoginByOther");
        //获取标签列表
        String GET_TAG_LIST = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "getTagList");
        //登出
        String LOG_OFF = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "logoff");
        //保存标签
        String SAVE_CHOOSE_TAG = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "saveChooseTag");


        // 26.	获取筛选条件列表
        String GET_FILTER_LIST = String.format("%s/%s/%s", ZEJIA_API_URL, VERSION, "getFilterList");

        // 27.	保存筛选结果
        String SAVE_CHOOSE_FILTER = String.format("%s/%s/%s", ZEJIA_API_URL, VERSION, "saveChooseFilter");

        String GET_HOUSE_LIST = String.format("%s%s/%s", ZEJIA_API_URL, VERSION, "getHouseList");

        String AGREEMENT = String.format("%s%s", ZEJIA_API_H5_URL, "agreement.html");
        String ABOUT_US = String.format("%s%s", ZEJIA_API_H5_URL, "aboutUs.html");
    }

}
