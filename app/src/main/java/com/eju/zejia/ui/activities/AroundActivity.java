package com.eju.zejia.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.eju.zejia.R;
import com.eju.zejia.config.Constants;
import com.eju.zejia.data.DataManager;
import com.eju.zejia.data.models.AroundLife;
import com.eju.zejia.data.models.AroundLife.ResponseBean;
import com.eju.zejia.data.models.AroundLifeDetail;
import com.eju.zejia.data.remote.ZejiaService;
import com.eju.zejia.databinding.ActivityAroundBinding;
import com.eju.zejia.utils.GsonFactory;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.UIUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sandy on 2016/8/5/0005.
 */
public class AroundActivity extends BaseActivity {

    private ActivityAroundBinding aroundBinding;
    private BaiduMap mBaiduMap;
    private ZejiaService zejiaService;
    private ResponseBean aroundLifeData;
    private List<TextView> textViews;
    private List<Drawable> orangeDrawables;
    private List<Drawable> whiteDrawables;
    private List<AroundLifeDetail> eatDetails = new ArrayList<>();
    private List<AroundLifeDetail> playDetails = new ArrayList<>();
    private List<AroundLifeDetail> studyDetails = new ArrayList<>();
    private List<AroundLifeDetail> buyDetails = new ArrayList<>();
    private List<AroundLifeDetail> cureDetails = new ArrayList<>();
    private List<Marker> mMarkers = new ArrayList<>();
    public final static int EAT = 0;
    public final static int PLAY = 1;
    public final static int STUDY = 2;
    public final static int BUY = 3;
    public final static int CURE = 4;
    private int STATE = Integer.MAX_VALUE;
    private InfoWindow mInfoWindow;
    private LatLng home_latLng;
    private String name;
    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aroundBinding = DataBindingUtil.setContentView(this, R.layout.activity_around);
        zejiaService = DataManager.getInstance().getZejiaService();
        initView();
        initData();

    }

    private void initView() {
        setUpToolbar(R.id.toolbar);
        actionBar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.bg_btn_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        TextView tv_around_eat = (TextView) findViewById(R.id.tv_around_eat);
        TextView tv_around_play = (TextView) findViewById(R.id.tv_around_play);
        TextView tv_around_study = (TextView) findViewById(R.id.tv_around_study);
        TextView tv_around_buy = (TextView) findViewById(R.id.tv_around_buy);
        TextView tv_around_cure = (TextView) findViewById(R.id.tv_around_cure);
        aroundBinding.tvAroundEat.setOnClickListener(view -> aroundEat());
        aroundBinding.tvAroundPlay.setOnClickListener(view -> aroundPlay());
        aroundBinding.tvAroundStudy.setOnClickListener(view -> aroundStudy());
        aroundBinding.tvAroundBuy.setOnClickListener(view -> aroundBuy());
        aroundBinding.tvAroundCure.setOnClickListener(view -> aroundCure());
        Drawable orange_eat = getResources().getDrawable(R.drawable.map_eat_selected_icon);
        Drawable orange_play = getResources().getDrawable(R.drawable.map_play_selected_icon);
        Drawable orange_study = getResources().getDrawable(R.drawable.map_school_selected_icon);
        Drawable orange_buy = getResources().getDrawable(R.drawable.map_shop_selected_icon);
        Drawable orange_cure = getResources().getDrawable(R.drawable.map_hospital_selected_icon);

        Drawable white_eat = getResources().getDrawable(R.drawable.map_eat_default_icon);
        Drawable white_play = getResources().getDrawable(R.drawable.map_play_default_icon);
        Drawable white_study = getResources().getDrawable(R.drawable.map_school_default_icon);
        Drawable white_buy = getResources().getDrawable(R.drawable.map_shop_default_icon);
        Drawable white_cure = getResources().getDrawable(R.drawable.map_hospital_default_icon);

        textViews = new ArrayList<>();
        orangeDrawables = new ArrayList<>();
        whiteDrawables = new ArrayList<>();
        textViews.add(tv_around_eat);
        textViews.add(tv_around_play);
        textViews.add(tv_around_study);
        textViews.add(tv_around_buy);
        textViews.add(tv_around_cure);
        orangeDrawables.add(orange_eat);
        orangeDrawables.add(orange_play);
        orangeDrawables.add(orange_study);
        orangeDrawables.add(orange_buy);
        orangeDrawables.add(orange_cure);
        whiteDrawables.add(white_eat);
        whiteDrawables.add(white_play);
        whiteDrawables.add(white_study);
        whiteDrawables.add(white_buy);
        whiteDrawables.add(white_cure);
        MapView mMapView = (MapView) findViewById(R.id.bmapView);
        if (mMapView == null) {
            return;
        }
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setOnMarkerClickListener(marker -> {
            for (Marker maker_info : mMarkers) {
                if (maker_info.getPosition() == marker.getPosition()) {
                    int height = 0;
                    String title = maker_info.getTitle();
                    TextView textView = new TextView(getApplicationContext());
                    textView.setBackground(UIUtils.getDrawable(R.drawable.around_window));
                    textView.setTextColor(UIUtils.getColor(R.color.white));
                    textView.setText(title);
                    textView.setGravity(Gravity.CENTER);
                    LatLng ll = maker_info.getPosition();
                    if (title.equals(name)) {
                        height = -200;
                    } else {
                        height = -120;
                    }
                    mInfoWindow = new InfoWindow(textView, ll, height);
                    mBaiduMap.showInfoWindow(mInfoWindow);
//                        UIUtils.showToastSafe(marker.getTitle());
                    break;
                }
            }
            return true;
        });
    }

    /**
     * 获取数据
     */
    private void initData() {
//        LocaterSingle.getInstance().init(this, false, myListener);
        Intent intent = getIntent();
        long communityid = intent.getLongExtra("COMMUNITYID", Long.MAX_VALUE);
        name = intent.getStringExtra("NAME");
        type = intent.getIntExtra("TYPE", Integer.MAX_VALUE);
        String latitude = intent.getStringExtra("LATITUDE");
        String longitude = intent.getStringExtra("LONGITUDE");
        home_latLng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
        goPosition(home_latLng, R.drawable.map_lsign_icon, true);
//        communityid = 4567;
        String sessionId = PrefUtil.getString(this,
                UIUtils.getString(R.string.prefs_sessionId),
                "");
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .putAlways("communityId", communityid)
                .build();
        zejiaService.doRequest(this, Constants.API.GET_COMM_PERIPHERY, data,
                jsonObject -> {

                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");
                    JSONObject response = jsonObject.optJSONObject("response");

                    if (null == response) {
                        return;
                    }
                    if (code == 200) {
                        AroundLife aroundLife = GsonFactory
                                .changeGsonToBean(jsonObject.toString(), AroundLife.class);

                        if (null != aroundLife) {
                            aroundLifeData = aroundLife.getResponse();
                            if (null != aroundLifeData) {
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
        //吃
        List<AroundLifeDetail> haveList= new ArrayList<> ();
        eatDetails.clear();
        playDetails.clear();
        studyDetails.clear();
        buyDetails.clear();
        cureDetails.clear();
        List<AroundLifeDetail> foodList = aroundLifeData.getFoodList();
        List<AroundLifeDetail> vegetableList = aroundLifeData.getVegetableList();
        eatDetails.addAll(foodList);
        eatDetails.addAll(vegetableList);
        //玩
        List<AroundLifeDetail> movieList = aroundLifeData.getMovieList();
        List<AroundLifeDetail> parkList = aroundLifeData.getParkList();
        playDetails.addAll(movieList);
        playDetails.addAll(parkList);
        //学
        List<AroundLifeDetail> schoolList = aroundLifeData.getSchoolList();
        studyDetails.addAll(schoolList);
        //买
        List<AroundLifeDetail> businessList = aroundLifeData.getBusinessList();
        List<AroundLifeDetail> marketList = aroundLifeData.getMarketList();
        buyDetails.addAll(businessList);
        buyDetails.addAll(marketList);
        //医
        List<AroundLifeDetail> hospitalList = aroundLifeData.getHospitalList();
        cureDetails.addAll(hospitalList);
        switch (type) {
            case EAT:
                haveList = eatDetails;
                break;
            case PLAY:
                haveList = playDetails;
                break;
            case STUDY:
                haveList = studyDetails;
                break;
            case BUY:
                haveList = buyDetails;
                break;
            case CURE:
                haveList = cureDetails;
                break;
            default:
                break;
        }
        changeColor(type, haveList);
    }

    private void aroundEat() {
        STATE = EAT;
        changeColor(STATE, eatDetails);
    }

    private void aroundPlay() {
        STATE = PLAY;
        changeColor(STATE, playDetails);
    }

    private void aroundStudy() {
        STATE = STUDY;
        changeColor(STATE, studyDetails);
    }

    private void aroundBuy() {
        STATE = BUY;
        changeColor(STATE, buyDetails);
    }

    private void aroundCure() {
        STATE = CURE;
        changeColor(STATE, cureDetails);
    }

    private void changeColor(int state, List<AroundLifeDetail> details) {
        Drawable orangeDrawable = orangeDrawables.get(state);
        TextView textView = textViews.get(state);

        if (textView.getCurrentTextColor() == UIUtils.getColor(R.color.white)) {
            orangeDrawable.setBounds(0, 0, orangeDrawable.getMinimumWidth(), orangeDrawable.getMinimumHeight());
            textView.setCompoundDrawables(null, orangeDrawable, null, null);
            textView.setTextColor(UIUtils.getColor(R.color.orange_FF7800));
        }/*else {
            whiteDrawable.setBounds(0, 0, whiteDrawable.getMinimumWidth(), whiteDrawable.getMinimumHeight());
            textView.setCompoundDrawables(null,whiteDrawable,null,null);
            textView.setTextColor(UIUtils.getColor(R.color.white));
        }*/
        for (int i = 0; i < textViews.size(); i++) {
            if (i != state) {
                Drawable d = whiteDrawables.get(i);
                TextView t = textViews.get(i);
                d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
                t.setCompoundDrawables(null, d, null, null);
                t.setTextColor(UIUtils.getColor(R.color.white));
            }
        }
        mBaiduMap.clear();
        mMarkers.clear();
        if (null != details && details.size() > 0) {
            for (AroundLifeDetail detail : details) {
                LatLng center = new LatLng(Double.valueOf(detail.getLatitude()),
                        Double.valueOf(detail.getLongitude()));
                int drawable = 0;
                String type = detail.getType();

                switch (type) {
                    case "food":
                        drawable = R.drawable.food_list_icon;
                        break;
                    case "vegetable":
                        drawable = R.drawable.vegetable_list_icon;
                        break;
                    case "movie":
                        drawable = R.drawable.movie_list_icon;
                        break;
                    case "cstore":
                        drawable = R.drawable.cstore;
                        break;
                    case "park":
                        drawable = R.drawable.park_list_icon;
                        break;
                    case "school":
                        drawable = R.drawable.school_list_icon;
                        break;
                    case "preSchool":
                        drawable = R.drawable.kindergarten_around_icon;
                        break;
                    case "business":
                        drawable = R.drawable.business_list_icon;
                        break;
                    case "market":
                        drawable = R.drawable.market_list_icon;
                        break;
                    case "hospital":
                        drawable = R.drawable.hospital_around_icon;
                        break;
                    default:
                        break;
                }
                MarkerOptions options = goPosition(center, drawable, false);
                Marker mMarker = (Marker) (mBaiduMap.addOverlay(options));
                mMarker.setTitle(detail.getName() + "\n距离小区直线距离" + detail.getDistance() + "m");
                mMarkers.add(mMarker);
            }
        } else {
            UIUtils.showToastSafe("暂无推荐");
        }
        goPosition(home_latLng, R.drawable.map_lsign_icon, true);
    }

    @NonNull
    private MarkerOptions goPosition(LatLng center, int drawable, boolean flag) {
        MarkerOptions options = new MarkerOptions().position(center)
                .icon(BitmapDescriptorFactory
                        .fromResource(drawable));
        mBaiduMap.addOverlay(options);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(center));
        if (flag) {
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(center).zoom(16.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            Marker mMarker = (Marker) (mBaiduMap.addOverlay(options));
            mMarker.setTitle(name);
            mMarkers.add(mMarker);
        }
        return options;
    }

    @Override
    public String getPageName() {
        return UIUtils.getString(R.string.page_act_around);
    }
}
