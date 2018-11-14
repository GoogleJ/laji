package com.zhishen.aixuexue.util;

import com.amap.api.maps.model.LatLng;

import java.text.DecimalFormat;

/**
 * Created by Jerome on 2018/7/28
 */
public class LocationUtil {

    /**
     * 计算距离
     * @link {LatLng(lat,lon)}
     * @param start
     * @param end
     * @return  String
     */
    public static String calculateLineDistance(LatLng start,  LatLng end) {
        double R = 6371;
        double distance;
        double dLat = (end.latitude - start.latitude) * Math.PI / 180;
        double dLon = (end.longitude - start.longitude) * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(start.latitude * Math.PI / 180)
                * Math.cos(end.latitude * Math.PI / 180) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        distance = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * R;

        if (distance < 1000){
            return (int)distance+"m";
        }
        return new DecimalFormat("0.0").format(distance/1000)+"km";
    }
}
