package com.eju.zejia.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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
import com.eju.zejia.databinding.ActivityBaiduMapBinding;
import com.eju.zejia.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class BaiduMapActivity extends BaseActivity {

    private ActivityBaiduMapBinding binding;
    private BaiduMap mBaiduMap;
    private List<Marker> mMarkers = new ArrayList<>();
    private InfoWindow mInfoWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_baidu_map);
        setUpToolbar(R.id.toolbar);
        actionBar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.bg_btn_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        MapView mMapView = (MapView) findViewById(R.id.bmapView);
        if (mMapView == null) {
            return;
        }
        mBaiduMap = mMapView.getMap();
        initData();
        mBaiduMap.setOnMarkerClickListener(marker -> {
            for (Marker maker_info : mMarkers) {
                if (maker_info.getPosition() == marker.getPosition()) {
                    TextView textView = new TextView(getApplicationContext());
                    textView.setBackground(UIUtils.getDrawable(R.drawable.around_window));
                    textView.setTextColor(UIUtils.getColor(R.color.white));
                    textView.setText(maker_info.getTitle());
                    textView.setGravity(Gravity.CENTER);
                    textView.setText(maker_info.getTitle());
                    LatLng ll = maker_info.getPosition();
                    mInfoWindow = new InfoWindow(textView, ll, -200);
                    mBaiduMap.showInfoWindow(mInfoWindow);
//                        UIUtils.showToastSafe(marker.getTitle());
                    break;
                }
            }
            return true;
        });
    }

    private void initData() {
        Intent intent = getIntent();
        if (null != intent) {
            String name = intent.getStringExtra("NAME");
            String latitude = intent.getStringExtra("LATITUDE");
            String longitude = intent.getStringExtra("LONGITUDE");
            binding.tvMapName.setText(name);
            LatLng center = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
            MarkerOptions options = new MarkerOptions().position(center)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.map_lsign_icon));
            mBaiduMap.addOverlay(options);
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(center));
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(center).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            Marker mMarker = (Marker) (mBaiduMap.addOverlay(options));
            mMarker.setTitle(name);
            mMarkers.add(mMarker);
        }
    }


    @Override
    public String getPageName() {
        return UIUtils.getString(R.string.page_act_baidu_map);
    }
}
