package com.example.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.bean.Record;
import com.example.superdriver.R;
import com.example.utils.DateUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RecordFragment extends Fragment {
    private static final String TAG = "RecordFragment";
    private Record recordItem;

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private TextView tv_startTime;
    private TextView tv_startPlace;
    private TextView tv_endTime;
    private TextView tv_endPlace;

    //地图相关
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private Polyline mPolyline = null;
    private LatLng target = null;
    private List<LatLng> latLngs = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, null);
        initView(view);
        //获取地图控件引用
        initMyLocation();
        return view;
    }

    private void initMyLocation() {
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //获取定位点
        coordinateConvert();

        //设置缩放中点LatLng target，和缩放比例
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(target).zoom(18f);

        //地图设置缩放状态
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        //画起点
        OverlayOptions startPoint = new MarkerOptions().position(target)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps))
                .zIndex(5);

        mBaiduMap.addOverlay(startPoint);
        /**
         * 配置线段图层参数类： PolylineOptions
         * ooPolyline.width(13)：线宽
         * ooPolyline.color(0xAAFF0000)：线条颜色红色
         * ooPolyline.points(latLngs)：List<LatLng> latLngs位置点，将相邻点与点连成线就成了轨迹了
         */
        if(latLngs.size()>1){
            OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(latLngs);

            //在地图上画出线条图层，mPolyline：线条图层
            mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
            mPolyline.setZIndex(3);
            LatLng endLatlng = latLngs.get(latLngs.size()-1);
            OverlayOptions endPoint = new MarkerOptions().position(endLatlng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.finish_point))
                    .zIndex(5);
            mBaiduMap.addOverlay(endPoint);
        }
    }

    private void coordinateConvert() {
        String path = recordItem.getRecordId() + ".txt";
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SuperDriver/" + path);
        Log.e(TAG, file.getName());
        latLngs = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),
                    "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                double mLatitude = Double.parseDouble(lineTxt);
                lineTxt = br.readLine();
                double mLongtitude = Double.parseDouble(lineTxt);
                Log.e(TAG, mLatitude+" "+mLongtitude);
                LatLng latLng = new LatLng(mLatitude, mLongtitude);
                latLngs.add(latLng);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(latLngs.size()>0)
            target = latLngs.get(0);
        Log.e(TAG, target.latitude+" "+target.longitude+" "+latLngs.size());
    }

    private void initView(View view) {
        tv_startTime = (TextView)view.findViewById(R.id.fragment_record_tv_startTime);
        tv_endTime = (TextView)view.findViewById(R.id.fragment_record_tv_endTime);
        tv_startPlace = (TextView)view.findViewById(R.id.fragment_record_tv_startPlace);
        tv_endPlace = (TextView)view.findViewById(R.id.fragment_record_tv_endPlace);
        tv_startPlace.setText(recordItem.getStartPlace());
        tv_endPlace.setText(recordItem.getEndPlace());
        try {
            tv_startTime.setText(DateUtils.sdf.format(DateUtils.sdf_GMT.parse(recordItem.getStartTime())));
            tv_endTime.setText(DateUtils.sdf.format(DateUtils.sdf_GMT.parse(recordItem.getStartTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mMapView = (MapView)view.findViewById(R.id.fragment_record_map_view);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        recordItem = (Record) getArguments().getSerializable(TAG);
        Log.e(TAG, recordItem.getStartPlace());
        super.onCreate(savedInstanceState);
    }

    public static RecordFragment newInstance(Record orderItem) {
        Bundle args = new Bundle();
        args.putSerializable(TAG, orderItem);
        RecordFragment fragment = new RecordFragment();
        fragment.setArguments(args);
        return fragment;
    }


}
