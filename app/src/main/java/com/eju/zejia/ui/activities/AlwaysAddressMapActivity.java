package com.eju.zejia.ui.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.eju.zejia.R;
import com.eju.zejia.config.Constants;
import com.eju.zejia.data.DataManager;
import com.eju.zejia.data.models.AlwaysAddress;
import com.eju.zejia.data.models.DataListBean;
import com.eju.zejia.data.models.LocationAddress;
import com.eju.zejia.data.remote.ZejiaService;
import com.eju.zejia.databinding.ActivityAlawysAddressMapBinding;
import com.eju.zejia.map.LocaterSingle;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.KeyBoardManager;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.StringUtils;
import com.eju.zejia.utils.UIUtils;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sandy on 2016/8/2/0002.
 */
public class AlwaysAddressMapActivity extends BaseActivity implements OnGetPoiSearchResultListener, OnGetSuggestionResultListener {

    private MapView mMapView;
    private LocationClient mLocClient;
    //    private UiSettings mUiSettings;
    private ActivityAlawysAddressMapBinding mapBinding;
    private ZejiaService zejiaService;
    private final int WORK = 0;
    private final int LIFE = 1;
    private PoiSearch mPoiSearch;
    private SuggestionSearch mSuggestionSearch = null;
    private BaiduMap mBaiduMap;
    private LatLng location_center = new LatLng(39.92235, 116.380338);
    private LatLng position_two;
    private ArrayAdapter<String> sugAdapter;
    private List<String> suggest;
    public MyLocationListenner myListener = new MyLocationListenner();
    public MyOnItemClickListener myItmeListener = new MyOnItemClickListener();
    private boolean isFirstLoc = true; // 是否首次定位
    private String addrStr;
    private GeoCoder mSearch;
    private int RESULT = 1;
    private String type = null;
    private boolean flag = false;//两个文本框的焦点获取
    private boolean flagCheck = false;//检测地址是否有效
    private boolean moveflag = false;//地图是否可拖动
    private boolean haveflag = false;//已有地址是否改变
    private LatLng position_one;
    private List<LatLng> suggestLatLngs;
    private AlertDialog ad;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapBinding = DataBindingUtil.setContentView(this, R.layout.activity_alawys_address_map);
        zejiaService = DataManager.getInstance().getZejiaService();
        setUpToolbar(R.id.toolbar);
        actionBar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.bg_btn_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        mapBinding.tvSave.setOnClickListener(view -> saveState());
        mapBinding.ivLocation.setOnClickListener(view -> location());
        mMapView = (MapView) findViewById(R.id.bmapView);
        mapBinding.ivCenter.setVisibility(View.GONE);
        if (mMapView == null) {
            return;
        }
        mBaiduMap = mMapView.getMap();
        mSearch = GeoCoder.newInstance();
//        mUiSettings = mBaiduMap.getUiSettings();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        initData();
        initPOI();
        sugAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line);
        mapBinding.etOne.setAdapter(sugAdapter);
        mapBinding.etTwo.setAdapter(sugAdapter);
        mapBinding.etOne.setThreshold(1);
        mapBinding.etTwo.setThreshold(1);
        mapBinding.etOne.setOnFocusChangeListener((view, b) -> {
            if (b) {
                if(!StringUtils.isNullString(mapBinding.etOne.getText().toString())){
                    mapBinding.etOne.setCompoundDrawables(null,null,getDrawable(),null);
                }else {
                    mapBinding.etOne.setCompoundDrawables(null,null,null,null);
                }
                mapBinding.flBack.setVisibility(View.VISIBLE);
//                mUiSettings.setScrollGesturesEnabled(true);
                flag = false;
                moveflag = false;
            }else {
                mapBinding.etOne.setCompoundDrawables(null,null,null,null);
            }
        });
        mapBinding.etTwo.setOnFocusChangeListener((view, b) -> {
            if (b) {
                if(!StringUtils.isNullString(mapBinding.etTwo.getText().toString())){
                    mapBinding.etTwo.setCompoundDrawables(null,null,getDrawable(),null);
                }else {
                    mapBinding.etTwo.setCompoundDrawables(null,null,null,null);
                }
                mapBinding.flBack.setVisibility(View.VISIBLE);
//                mUiSettings.setScrollGesturesEnabled(false);
                flag = true;
                moveflag = false;
            }else {
                mapBinding.etTwo.setCompoundDrawables(null,null,null,null);
            }
        });
        mapBinding.flBack.setOnClickListener(view -> {
            if(flag){
                if(flagCheck){
                    mapBinding.etTwo.setTextColor(UIUtils.getColor(R.color.black_28));
                }else {
                    mapBinding.etTwo.setTextColor(UIUtils.getColor(R.color.orange_FF6000));
                }
            }else {
                if(flagCheck){
                    mapBinding.etOne.setTextColor(UIUtils.getColor(R.color.black_28));
                }else {
                    mapBinding.etOne.setTextColor(UIUtils.getColor(R.color.orange_FF6000));
                }
            }
            KeyBoardManager.closeKeyBoard(this);
            mapBinding.flBack.setVisibility(View.GONE);
            mapBinding.etOne.clearFocus();
            mapBinding.etTwo.clearFocus();
            mapBinding.etOne.setCompoundDrawables(null,null,null,null);
            mapBinding.etTwo.setCompoundDrawables(null,null,null,null);
        });
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                haveflag = true;
                if (charSequence.length() <= 0) {
                    if(flag){
                        mapBinding.etTwo.setCompoundDrawables(null,null,null,null);
                    }else {
                        mapBinding.etOne.setCompoundDrawables(null,null,null,null);
                    }
                    mBaiduMap.clear();
                    mapBinding.ivCenter.setVisibility(View.VISIBLE);
                    return;
                }
                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(charSequence.toString()).city("上海"));
                if(flag){
                    mapBinding.etTwo.setTextColor(UIUtils.getColor(R.color.black_28));
                    if (moveflag) {
                        mapBinding.etTwo.setCompoundDrawables(null,null,null,null);
                    }else {
                        mapBinding.etTwo.setCompoundDrawables(null,null,getDrawable(),null);
                    }
                    mPoiSearch.searchInCity((new PoiCitySearchOption())
                            .city("上海").keyword(charSequence.toString()).pageNum(0));
                }else{
                    mapBinding.etOne.setTextColor(UIUtils.getColor(R.color.black_28));
                    if (!moveflag) {
                        mapBinding.etOne.setCompoundDrawables(null,null,getDrawable(),null);
                        mPoiSearch.searchInCity((new PoiCitySearchOption())
                                .city("上海").keyword(charSequence.toString()).pageNum(0));
                    }else {
                        mapBinding.etOne.setCompoundDrawables(null,null,null,null);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        mapBinding.etOne.addTextChangedListener(textWatcher);
        mapBinding.etTwo.addTextChangedListener(textWatcher);

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
                moveflag = true;
                mBaiduMap.clear();
                mapBinding.ivCenter.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                updateMapState(mapStatus);
            }
        });
        mapBinding.flBack.setVisibility(View.GONE);

        OnTouchListener touchListener = new OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // 左上右下
                Drawable drawable ;
                EditText et;
                if(flag){
                    drawable = mapBinding.etTwo.getCompoundDrawables()[2];
                    et = mapBinding.etTwo;
                }else{
                    et = mapBinding.etOne;
                    drawable = mapBinding.etOne.getCompoundDrawables()[2];
                }
                /*if(StringUtils.isNullString(et.getText().toString())){
                    et.setCompoundDrawables(null,null,getDrawable(),null);
                }else {
                    et.setCompoundDrawables(null,null,null,null);
                }*/
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                if (motionEvent.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (motionEvent.getX() > et.getWidth()
                        - et.getPaddingRight()
                        - drawable.getIntrinsicWidth()){
                    et.setText("");
                    et.setCompoundDrawables(null,null,null,null);
                }
                return false;
            }
        };
        mapBinding.etOne.setOnTouchListener(touchListener);
        mapBinding.etTwo.setOnTouchListener(touchListener);
    }

    @NonNull
    private Drawable getDrawable() {
        Drawable drawable = UIUtils.getDrawable(R.drawable.map_delete_icon);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        return drawable;
    }

    private void updateMapState(MapStatus mapStatus) {
        mapBinding.etOne.setCompoundDrawables(null,null,null,null);
        mapBinding.etTwo.setCompoundDrawables(null,null,null,null);
        LatLng mCenterLatLng = mapStatus.target;
        double lat = mCenterLatLng.latitude;
        double lng = mCenterLatLng.longitude;
        LatLng center = new LatLng(lat, lng);
        position_one = center;
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(center));
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                    UIUtils.showToastSafe("没有检索到结果");
                    flagCheck = false;
                }
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                    UIUtils.showToastSafe("没有找到检索结果");
                }
                //获取反向地理编码结果
                assert reverseGeoCodeResult != null;
                String address = reverseGeoCodeResult.getAddress();
                mapBinding.etOne.setText(address);
                mapBinding.etOne.dismissDropDown();
                flagCheck = true;
//                UIUtils.showToastSafe(address + "  " + lat + "  " + lng);
            }
        });

    }

    /**
     * 定位
     */
    private void location() {
//        mapBinding.ivCenter.setVisibility(View.GONE);
        LocaterSingle.getInstance().init(this, false, myListener);
        mapBinding.etOne.setText(addrStr);
        mapBinding.etOne.dismissDropDown();
        goLocation(location_center);
    }

    /**
     * 去已存在位置
     */
    private void goLocation(LatLng position) {
        mapBinding.ivCenter.setVisibility(View.GONE);
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(position)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.map_lsign_icon)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(position));
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(position).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(position));
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                    UIUtils.showToastSafe("没有检索到结果");
                }
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                    UIUtils.showToastSafe("没有找到检索结果");
                }
                //获取反向地理编码结果
                assert reverseGeoCodeResult != null;
            }
        });
    }

    /**
     * 检索
     */
    private void initPOI() {
        mBaiduMap.setMyLocationEnabled(true);
        mLocClient = new LocationClient(this);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
    }

    /**
     * 加载数据
     */
    private void initData() {
        Intent intent = getIntent();
        if (null == intent) {
            return;
        }
        int flag = intent.getIntExtra("FLAG", Integer.MAX_VALUE);
        switch (flag) {
            case WORK:
                type = "work";
                mapBinding.tvFlag.setText(UIUtils.getString(R.string.set_work));
                mapBinding.etOne.setHint(UIUtils.getString(R.string.add_work_one));
                mapBinding.etTwo.setHint(UIUtils.getString(R.string.add_work_two));
                break;
            case LIFE:
                type = "life";
                mapBinding.tvFlag.setText(UIUtils.getString(R.string.set_life));
                mapBinding.etOne.setHint(UIUtils.getString(R.string.add_life_one));
                mapBinding.etTwo.setHint(UIUtils.getString(R.string.add_life_two));
                break;
            default:
                break;
        }
        AlwaysAddress address = (AlwaysAddress) intent.getSerializableExtra("ADDRESS");
        if (null != address) {
            AlwaysAddress.ResponseBean response = address.getResponse();
            if (null != response) {
                List<DataListBean> dataList = response.getDataList();
                if (null != dataList && dataList.size() > 0) {
                    List<DataListBean> typeLists = new ArrayList<>();
                    for (DataListBean info : dataList) {
                        if (info.getType().equals(type)) {
                            typeLists.add(info);
                        }
                    }
                    initHavaData(typeLists);
                } else {
                    LocaterSingle.getInstance().init(this, false, myListener);
                }
            } else {
                LocaterSingle.getInstance().init(this, false, myListener);
            }
        } else {
            LocaterSingle.getInstance().init(this, false, myListener);
        }

    }

    private void initHavaData(List<DataListBean> lists) {
        if (null != lists && lists.size() == 1) {
            DataListBean dataListOne = lists.get(0);
            if (StringUtils.isNullString(dataListOne.getAddrName()) ||
                    StringUtils.isNullString(dataListOne.getLatitude()) ||
                    StringUtils.isNullString(dataListOne.getLongitude())) {
                LocaterSingle.getInstance().init(this, false, myListener);
                return;
            }
            position_one = new LatLng(Double.valueOf(dataListOne.getLatitude()),
                    Double.valueOf(dataListOne.getLongitude()));
            goLocation(position_one);
            mapBinding.etOne.setText(dataListOne.getAddrName());
            flagCheck = true;
        } else if (null != lists && lists.size() == 2) {
            DataListBean dataListOne = lists.get(0);
            DataListBean dataListTwo = lists.get(1);
            if (StringUtils.isNullString(dataListOne.getAddrName()) ||
                    StringUtils.isNullString(dataListOne.getLatitude()) ||
                    StringUtils.isNullString(dataListOne.getLongitude())) {
                LocaterSingle.getInstance().init(this, false, myListener);
                return;
            }
            position_one = new LatLng(Double.valueOf(dataListOne.getLatitude()),
                    Double.valueOf(dataListOne.getLongitude()));
            goLocation(position_one);
            mapBinding.etOne.setText(dataListOne.getAddrName());
            if (StringUtils.isNullString(dataListTwo.getAddrName()) ||
                    StringUtils.isNullString(dataListTwo.getLatitude()) ||
                    StringUtils.isNullString(dataListTwo.getLongitude())) {
                return;
            }
            mapBinding.etTwo.setText(dataListTwo.getAddrName());
            position_two = new LatLng(Double.valueOf(dataListTwo.getLatitude()),
                    Double.valueOf(dataListTwo.getLongitude()));
            flagCheck = true;
        } else {
            LocaterSingle.getInstance().init(this, false, myListener);
        }
    }

    /**
     * 保存
     */
    private void saveState() {
        String etOne = mapBinding.etOne.getText().toString().trim();
        String etTwo = mapBinding.etTwo.getText().toString().trim();
        if (checkAddress(etOne, position_one)) return;
        if (checkAddress(etTwo, position_two)) return;
        String sessionId = PrefUtil.getString(this,
                UIUtils.getString(R.string.prefs_sessionId),
                "");
        List<LocationAddress> dataList = new ArrayList<>();
        LocationAddress locationAddressOne = new LocationAddress();
        LocationAddress locationAddressTwo = new LocationAddress();
        bornNewData(dataList, locationAddressOne, etOne, position_one);
        bornNewData(dataList, locationAddressTwo, etTwo, position_two);
        Gson gson = new Gson();
        String dataListJson = gson.toJson(dataList);
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .putAlways("type", type)
                .putAlways("dataList", dataListJson)
                .build();
        zejiaService.doRequest(this, Constants.API.SAVE_ADDR, data,
                jsonObject -> {

                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");
                    if (code == 200) {
                        UIUtils.showToastSafe("保存成功");
                        Intent intent = new Intent();
                        setResult(RESULT, intent);
                        finish();
                    } else {
                        UIUtils.showLongToastSafe(message);
                    }
                });

    }

    private boolean checkAddress(String content, LatLng laglng) {

        if (!StringUtils.isNullString(content)) {
            if (flagCheck) {
                if (laglng != null) {
                    return false;
                } else {
                    noneAddressColor();
                    UIUtils.showToastSafe("未找到地址");
                    return true;
                }
            } else {
                UIUtils.showToastSafe("请从列表选择正确地址");
                return true;
            }
        }
        return false;
    }

    private void noneAddressColor() {
        if (flag) {
            mapBinding.etTwo.setTextColor(UIUtils.getColor(R.color.orange_FF7800));
        } else {
            mapBinding.etOne.setTextColor(UIUtils.getColor(R.color.orange_FF7800));
        }
    }

    private void bornNewData(List<LocationAddress> list, LocationAddress locationAddress, String content, LatLng center) {
        if (!StringUtils.isNullString(content)) {
            locationAddress.setAddrName(content);
            locationAddress.setLatitude(String.valueOf(center.latitude));
            locationAddress.setLongitude(String.valueOf(center.longitude));
            list.add(locationAddress);
        }
    }

    @Override
    public String getPageName() {
        if ("work".equals(type)) {
            return UIUtils.getString(R.string.page_act_always_address_work);
        } else if ("life".equals(type)) {
            return UIUtils.getString(R.string.page_act_always_address_life);
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mLocClient.stop();
        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
        mMapView.onDestroy();
        mMapView = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    /**
     * 获取POI搜索结果，包括searchInCity，searchNearby，searchInBound返回的搜索结果
     *
     * @param result 结果
     */
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(this, "未找到结果", Toast.LENGTH_LONG)
                    .show();
            flagCheck = false;
            Log.d("结果","未找到结果");
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            Log.d("结果","结果");
            flagCheck = true;
            List<PoiInfo> allPoi = result.getAllPoi();
            if (flag) {
                position_two = allPoi.get(0).location;
            } else {
                position_one = allPoi.get(0).location;
                goLocation(position_one);
            }
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
            Log.d("结果","未找到结果");
            flagCheck = false;
            Toast.makeText(this, "未找到结果", Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * 获取POI详情搜索结果，得到searchPoiDetail返回的搜索结果
     *
     * @param result 结果
     */
    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    /**
     * 获取在线建议搜索结果，得到requestSuggestion返回的搜索结果
     *
     * @param res 结果
     */
    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }
        suggest = new ArrayList<>();
        suggestLatLngs = new ArrayList<>();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null) {
                suggest.add(info.key);
                suggestLatLngs.add(info.pt);
            }
        }
        sugAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggest);
        mapBinding.etOne.setAdapter(sugAdapter);
        mapBinding.etTwo.setAdapter(sugAdapter);
        sugAdapter.notifyDataSetChanged();
        mapBinding.etOne.setOnItemClickListener(myItmeListener);
        mapBinding.etTwo.setOnItemClickListener(myItmeListener);
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements LocaterSingle.ILocationRunnable {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void locationResult(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            addrStr = location.getAddrStr();
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                location_center = ll;
                position_one = ll;
                flagCheck = true;
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

    }

    public class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            /*String address = suggest.get(i);
            int loadIndex = 0;
            mPoiSearch.searchInCity((new PoiCitySearchOption())
                    .city("上海").keyword(address).pageNum(loadIndex));*/
            flagCheck = true;
            if (flag) {
                position_two = suggestLatLngs.get(i);
            } else {
                position_one = suggestLatLngs.get(i);
                goLocation(position_one);
            }
            KeyBoardManager.closeKeyBoard(AlwaysAddressMapActivity.this);
            mapBinding.flBack.setVisibility(View.GONE);
            mapBinding.etOne.clearFocus();
            mapBinding.etTwo.clearFocus();
        }
    }

    @Override
    public void onBackPressed() {
        if(haveflag){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("地址已改变，确定离开？");
            builder.setPositiveButton("是",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            ad.dismiss();
                            finish();
                        }
                    });
            builder.setNegativeButton("否",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            ad.dismiss();
                        }
                    });

            ad = builder.create();
            ad.show();
        }else {
            finish();
        }
    }
}
