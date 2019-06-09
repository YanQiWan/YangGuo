package com.example.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.application.SysApplication;
import com.example.bean.Record;
import com.example.service.UpdateRouteService;
import com.example.superdriver.R;
import com.example.utils.LocalFileUtils;

import org.gps.demo.CLocation;
import org.gps.demo.IBaseGpsListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class LocationFragment extends android.support.v4.app.Fragment{
    private final static String TAG = "LocationFragment";

    //定位相关
    private double mLatitude = 0;
    private double mLongtitude = 0;

    private LocationClient mLocationClient;
    public BDAbstractLocationListener myListener;

    public LocationFragment() {
    }

    //速度计算相关
    protected TextView tv_speed;
    private Date first_time;
    private Date second_time;

    /**
     * The layout identifier to inflate for this Fragment.
     */
    protected int layout;

    //存储路线相关
    protected FileOutputStream fileOutputStream = null;
    protected File record = null;
    protected Date startTime = null;
    protected int pointNum = 0;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected Record cur_record;
    protected String curPlace = "";
    private boolean isFirst = true;

    @SuppressLint("ValidFragment")
    public LocationFragment(int layout) {
        this.layout = layout;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout, null);
        initMyLocation();
        tv_speed = (TextView) view.findViewById(R.id.fragment_siri_tv_speed);
        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        //开启定位
        if (mLocationClient!=null&&!mLocationClient.isStarted())
            mLocationClient.start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        //停止定位
        if(mLocationClient!=null)
            mLocationClient.stop();
        //停止方向传感器
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    //定位
    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            if (mLatitude != location.getLatitude() || mLongtitude != location.getLongitude()) {
                if(isFirst){
                    first_time = new Date();
                }else{
                    Double distance= DistanceUtil.getDistance(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(mLatitude,mLongtitude));
                    second_time = new Date();
                    long total_time = (second_time.getTime()-first_time.getTime())/1000;
                    double speed = distance/total_time;
                    updateSpeed(speed);
                    first_time = second_time;
                }
                mLatitude = location.getLatitude();
                mLongtitude = location.getLongitude();
                if (fileOutputStream != null) {
                    //更新经纬度
                    pointNum++;
                    String str = mLatitude + "\n" + mLongtitude + "\n";
                    try {
                        fileOutputStream.write(str.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                // GPS定位结果
                curPlace = location.getAddrStr();
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                // 网络定位结果
                curPlace = location.getAddrStr();
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                // 离线定位结果
                curPlace = location.getAddrStr();
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                tv_speed.setText("定位:服务器错误");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                tv_speed.setText("定位:网络错误");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                tv_speed.setText("定位:手机模式错误，请检查是否飞行模式");
            }
            if (isFirst) {
                isFirst = false;
                cur_record.setStartPlace(curPlace);
            }
        }


    }

    //初始化定位
    private void initMyLocation() {
        //声明LocationClient类
        mLocationClient = new LocationClient(getContext());
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true);//设置是否需要地址信息
        option.setScanSpan(1000);
        //设置locationClientOption
        mLocationClient.setLocOption(option);
        myListener = new MyLocationListener();
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);
        //开始定位
        mLocationClient.start();
    }

    private void updateSpeed(double speed) {
        // TODO Auto-generated method stub
        double nCurrentSpeed = speed;
        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%.3f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        String strUnits = "meters/second";
        tv_speed.setText(strCurrentSpeed + " " + strUnits);
    }

    protected void startStroke() {
        startTime = new Date();
        String recordId = SysApplication.login_user.getPhoneNumber() + "_" + startTime.getTime();
        record = new File(LocalFileUtils.getSDPath() + "/SuperDriver/" + recordId + ".txt");
        try {
            fileOutputStream = new FileOutputStream(record);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        cur_record = new Record();
        cur_record.setPhoneNumber(SysApplication.login_user.getPhoneNumber());
        cur_record.setRecordId(recordId);
        cur_record.setStartTime(sdf.format(startTime));
    }

    protected void finishStroke() {
        try {
            Log.e(TAG, "finishStroke");
            fileOutputStream.close();
            Date endTime = new Date();
            cur_record.setEndTime(sdf.format(endTime));
            cur_record.setEndPlace(curPlace);
            Intent intent = new Intent(getActivity(), UpdateRouteService.class);
            intent.putExtra("record", cur_record);
            getActivity().startService(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
