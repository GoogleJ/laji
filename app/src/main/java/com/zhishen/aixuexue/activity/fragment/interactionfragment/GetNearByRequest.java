package com.zhishen.aixuexue.activity.fragment.interactionfragment;

import com.zhishen.aixuexue.http.BaseAesRequestData;

public class GetNearByRequest extends BaseAesRequestData {

    /**
     * userVo : {"id":"123","lon":108.948711,"lat":34.251527,"raidus":30000}
     * listStatus : companyListKey
     */

    private UserVoBean userVo;
    private String listStatus;

    public UserVoBean getUserVo() {
        return userVo;
    }

    public void setUserVo(UserVoBean userVo) {
        this.userVo = userVo;
    }

    public String getListStatus() {
        return listStatus;
    }

    public void setListStatus(String listStatus) {
        this.listStatus = listStatus;
    }

    public static class UserVoBean {
        /**
         * id : 123
         * lon : 108.948711
         * lat : 34.251527
         * raidus : 30000
         */

        private String id;
        private double lon;
        private double lat;
        private int raidus;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public int getRaidus() {
            return raidus;
        }

        public void setRaidus(int raidus) {
            this.raidus = raidus;
        }
    }
}
