package com.eju.zejia.map;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.eju.zejia.R;
import com.eju.zejia.control.CountControl;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.UIUtils;

/**
 * location新单例
 */
public class LocaterSingle {

    public static final int KeepLocation = -1;
    public static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

    private static LocaterSingle _instance;

    private LocationClient mLocClient;
    private MyLocationListenner myListener = new MyLocationListenner();
    private BDLocation curr_location;

    private boolean isStartApp;
    private ILocationRunnable iLocationRunnable;

    public interface ILocationRunnable {
        void locationResult(BDLocation bdLocation);
    }

    public static LocaterSingle getInstance() {
        if (_instance == null) {
            _instance = new LocaterSingle();
        }
        return _instance;
    }

    public BDLocation getCurrentLocation() {
        return curr_location;
    }

    public void setCurrentLocation(BDLocation location) {
        this.curr_location = location;
    }

    public double getLatitude() {
        if (curr_location != null) {
            return curr_location.getLatitude();
        }
        return 0.0;
    }

    public double getLongitude() {
        if (curr_location != null) {
            return curr_location.getLongitude();
        }
        return 0.0;
    }

    public void init(Context context, boolean isStartApp, ILocationRunnable runnable) {
        if (runnable != null) {
            iLocationRunnable = runnable;
        }

        this.isStartApp = isStartApp;
        if (mLocClient != null && isStartApp)
            return;
        mLocClient = new LocationClient(context);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setAddrType("all");
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型bd09ll
//		option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    public void init(Context context, boolean isStartApp) {
        init(context, isStartApp, null);
    }


    /*
     * 定位SDK监听函数
     */
    private class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location != null) {
                curr_location = location;
                if (isStartApp) {
                    CountControl.getInstance().startApp();
                    String uid = PrefUtil.getString(UIUtils.getContext(),
                            UIUtils.getString(R.string.prefs_sessionId), "");
                    CountControl.getInstance().loginSuccess(uid);
                }
                if (iLocationRunnable != null) {
                    iLocationRunnable.locationResult(curr_location);
                }
                mLocClient.stop();
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

}
