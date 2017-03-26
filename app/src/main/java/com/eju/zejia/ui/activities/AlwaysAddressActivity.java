package com.eju.zejia.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.eju.zejia.R;
import com.eju.zejia.config.Constants;
import com.eju.zejia.data.DataManager;
import com.eju.zejia.data.models.AlwaysAddress;
import com.eju.zejia.data.models.DataListBean;
import com.eju.zejia.data.remote.ZejiaService;
import com.eju.zejia.databinding.ActivityAlawysAddressBinding;
import com.eju.zejia.utils.GsonFactory;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.MyActivityManager;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.StringUtils;
import com.eju.zejia.utils.UIUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sandy on 2016/8/2/0002.
 */
public class AlwaysAddressActivity extends BaseActivity {

    private ActivityAlawysAddressBinding alwaysBinding;
    private ZejiaService zejiaService;
    private int WORK = 0;
    private int LIFE = 1;
    private List<DataListBean> dataList;
    private AlwaysAddress alwaysAddress;

    //数据绑定器
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushOneActivity(this);
        alwaysBinding = DataBindingUtil.setContentView(this, R.layout.activity_alawys_address);
        zejiaService = DataManager.getInstance().getZejiaService();
        setUpToolbar(R.id.toolbar);
        actionBar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.bg_btn_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        alwaysBinding.tvWorkTwo.setVisibility(View.GONE);
        alwaysBinding.tvLifeTwo.setVisibility(View.GONE);
        alwaysBinding.tvWorkOne.setTextColor(UIUtils.getColor(R.color.gray_999999));
        alwaysBinding.tvLifeOne.setTextColor(UIUtils.getColor(R.color.gray_999999));
        alwaysBinding.rlWorkEdit.setOnClickListener(view -> mapEdit(WORK));
        alwaysBinding.rlLifeEdit.setOnClickListener(view -> mapEdit(LIFE));
        alwaysBinding.tvFind.setOnClickListener(view -> findFit());
        alwaysBinding.tvFind.setClickable(false);
        initData();
    }

    /**
     * 获取数据
     */
    private void initData() {
        String sessionId = PrefUtil.getString(this,
                UIUtils.getString(R.string.prefs_sessionId),
                "");
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .build();
        zejiaService.doRequest(this, Constants.API.GET_ADDR, data,
                jsonObject -> {

                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");
                    JSONObject response = jsonObject.optJSONObject("response");

                    if (null == response) {
                        return;
                    }
                    if (code == 200) {
                        alwaysAddress = GsonFactory
                                .changeGsonToBean(jsonObject.toString(), AlwaysAddress.class);
                        if (null != alwaysAddress) {
                            AlwaysAddress.ResponseBean responseBean = alwaysAddress.getResponse();
                            if (null != responseBean) {
                                dataList = responseBean.getDataList();
                                initNetData();
                            }
                        }

                    } else {
                        UIUtils.showLongToastSafe(message);
                    }
                });
    }

    /**
     * 加载数据
     */
    private void initNetData() {
        int sum = 0;
        List<DataListBean> workLists = new ArrayList<>();
        List<DataListBean> lifeLists = new ArrayList<>();
        if (null != dataList && dataList.size() > 0) {
            for (DataListBean info : dataList) {
                if (info.getType().equals("work")) {
                    workLists.add(info);
                }
                if (info.getType().equals("life")) {
                    lifeLists.add(info);
                }
                if (StringUtils.isNotNullString(info.getAddrName())) {
                    sum++;
                }
            }
        }
        haveData(workLists, alwaysBinding.tvWorkOne, alwaysBinding.tvWorkTwo, "work");
        haveData(lifeLists, alwaysBinding.tvLifeOne, alwaysBinding.tvLifeTwo, "life");
        if (sum > 0) {
            alwaysBinding.tvFind.setClickable(true);
            alwaysBinding.tvFind.setBackgroundColor(UIUtils.getColor(R.color.colorPrimary));
            alwaysBinding.tvFind.setTextColor(UIUtils.getColor(R.color.black_69));
        } else {
            alwaysBinding.tvFind.setClickable(false);
            alwaysBinding.tvFind.setBackgroundColor(UIUtils.getColor(R.color.gray_DDDDDD));
            alwaysBinding.tvFind.setTextColor(UIUtils.getColor(R.color.gray_A0A0A0));
        }
    }


    private void haveData(List<DataListBean> lists, TextView tvOne, TextView tvTwo, String type) {
        if(null != lists && lists.size() == 2){
            DataListBean dataListOne = lists.get(0);
            DataListBean dataListTwo = lists.get(1);
            if(StringUtils.isNullString(dataListOne.getAddrName())){
                if(type.equals("work")){
                    tvOne.setText(UIUtils.getString(R.string.work_tip));
                }else if(type.equals("life")){
                    tvOne.setText(UIUtils.getString(R.string.life_tip));
                }
                tvOne.setTextColor(UIUtils.getColor(R.color.gray_999999));
                return;
            }
            tvOne.setTextColor(UIUtils.getColor(R.color.orange_FE7700));
            tvOne.setText(dataListOne.getAddrName());
            if(StringUtils.isNullString(dataListTwo.getAddrName())){
                return;
            }
            tvTwo.setTextColor(UIUtils.getColor(R.color.orange_FE7700));
            tvTwo.setVisibility(View.VISIBLE);
            tvTwo.setText(dataListTwo.getAddrName());
        }else if(null != lists && lists.size() == 1){
            DataListBean dataListOne = lists.get(0);
            if(StringUtils.isNullString(dataListOne.getAddrName())){
                if(type.equals("work")){
                    tvOne.setText(UIUtils.getString(R.string.work_tip));
                }else if(type.equals("life")){
                    tvOne.setText(UIUtils.getString(R.string.life_tip));
                }
                tvOne.setTextColor(UIUtils.getColor(R.color.gray_999999));
                return;
            }
            tvOne.setTextColor(UIUtils.getColor(R.color.orange_FE7700));
            tvTwo.setVisibility(View.GONE);
            tvOne.setText(dataListOne.getAddrName());
        }else{
            if(type.equals("work")){
                tvOne.setText(UIUtils.getString(R.string.work_tip));
            }else if(type.equals("life")){
                tvOne.setText(UIUtils.getString(R.string.life_tip));
            }
            tvOne.setTextColor(UIUtils.getColor(R.color.gray_999999));
            tvTwo.setVisibility(View.GONE);
        }

    }

    /**
     * 查看适合我的社区
     */
    private void findFit() {
        PrefUtil.putBoolean(this, UIUtils.getString(R.string.prefs_is_setting), true);
        MyActivityManager.getInstance().finishAllActivity();
        navigator.to(MainActivity.class);
    }

    /**
     * 跳转地图页面
     *
     * @param flag 区分工作和生活
     */
    private void mapEdit(int flag) {

        Intent map_intent = new Intent(this, AlwaysAddressMapActivity.class);
        map_intent.putExtra("FLAG", flag);
        if (null != dataList && dataList.size() > 0) {
            map_intent.putExtra("ADDRESS", alwaysAddress);
        }
        startActivityForResult(map_intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int RESULT = 1;
        if (resultCode == RESULT) {
            initData();
        }
    }

    @Override
    public String getPageName() {
        return UIUtils.getString(R.string.page_act_always_address);
    }
}
