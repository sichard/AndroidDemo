package com.sichard.weather.weatherData;

import android.text.TextUtils;

import com.sichard.weather.Interface.NotProGuard;

import java.util.Arrays;

/**
 * Created by jinhui.shao on 2016/7/27.
 * 根据经纬度查询得到的位置信息实体
 * fastjson 使用注意事项：
 * 1、要加入默认的构造函数
 * 2、要解析的字段需要加入set和get函数，且函数名第四个字母必须为大写or第3点
 * 3、要解析的字段要声明为public
 */

/*
GeoPosition（经度和纬度）在搜索：
http://api.accuweather.com/locations/v1/cities/geoposition/search.json?q=22.56,113.91&apikey=af7408e9f4d34fa6a411dd92028d4630

{
    "Version": 1,
    "Key": "58341",
    "Type": "City",
    "Rank": 15,
    "LocalizedName": "Bao'an District",
    "EnglishName": "Bao'an District",
    "PrimaryPostalCode": "",
    "Region": {
        "ID": "ASI",
        "LocalizedName": "Asia",
        "EnglishName": "Asia"
    },
    "Country": {
        "ID": "CN",
        "LocalizedName": "China",
        "EnglishName": "China"
    },
    "AdministrativeArea": {
        "ID": "44",
        "LocalizedName": "Guangdong",
        "EnglishName": "Guangdong",
        "Level": 1,
        "LocalizedType": "Province",
        "EnglishType": "Province",
        "CountryID": "CN"
    },
    "TimeZone": {
        "Code": "CST",
        "Name": "Asia/Shanghai",
        "GmtOffset": 8,
        "IsDaylightSaving": false,
        "NextOffsetChange": null
    },
    "GeoPosition": {
        "Latitude": 22.557,
        "Longitude": 113.905,
        "Elevation": {
            "Metric": {
                "Value": 2,
                "Unit": "m",
                "UnitType": 5
            },
            "Imperial": {
                "Value": 6,
                "Unit": "ft",
                "UnitType": 0
            }
        }
    },
    "IsAlias": false,
    "SupplementalAdminAreas": [
        {
            "Level": 2,
            "LocalizedName": "Shenzhen",
            "EnglishName": "Shenzhen"
        }
    ]
}
 */
public class LocationEntity implements NotProGuard {
    private static final String TAG = "LocationEntity";
    public String Key;
    public String Type;
    public String Rank;
    public String LocalizedName;
    public String EnglishName;
    public Country country;
    public AdministrativeArea administrativeArea;
    public SupplementalAdminAreas[] supplementalAdminAreasArray;
    public Details Details;
    // public String isautolocate;
    // public boolean isAdjustedByCanonicalKey;// 区分是否有调整地区到上一级

    public LocationEntity() {
    }

    public String getDisplayName() {
        if (TextUtils.isEmpty(LocalizedName)) {
            return EnglishName;
        } else {
            return LocalizedName;
        }
    }

    public String getAdministrativeAreaDisplayName() {
        if (administrativeArea != null) {
            if (TextUtils.isEmpty(administrativeArea.LocalizedName)) {
                return administrativeArea.EnglishName;
            } else {
                return administrativeArea.LocalizedName;
            }
        }

        return getDisplayName();
    }

    @Override
    public String toString() {
        return "LocationEntity [Key=" + Key + ", Type=" + Type + ", Rank=" + Rank
                + ", LocalizedName=" + LocalizedName + ", EnglishName=" + EnglishName + ", country=" + country
                + ", administrativeArea=" + administrativeArea
                + ", supplementalAdminAreasArray : " +  Arrays.toString(supplementalAdminAreasArray)
            //   + ", isAdjustedByCanonicalKey=" + isAdjustedByCanonicalKey
                + ", Details=" + Details
                + "]";
    }

    public static class Country {
        public String ID;  //fastjson规定要定义为public才能解析！！
        public String LocalizedName;
        public String EnglishName;

        public String getDisplayName() {
            if (TextUtils.isEmpty(LocalizedName)) {
                return EnglishName;
            } else {
                return LocalizedName;
            }
        }

        @Override
        public String toString() {
            return "Country [ID=" + ID + ", LocalizedName=" + LocalizedName + ", EnglishName=" + EnglishName + "]";
        }
    }

    public static class AdministrativeArea {// {"ID":"44","LocalizedName":"广东省","EnglishName":"Guangdong","Level":1,"LocalizedType":"省","EnglishType":"Province","CountryID":"CN"}
        public String ID;
        public String LocalizedName;
        public String EnglishName;

        public String getDisplayName() {
            if (TextUtils.isEmpty(LocalizedName)) {
                return EnglishName;
            } else {
                return LocalizedName;
            }
        }

        @Override
        public String toString() {
            return "AdministrativeArea [ID=" + ID + ", LocalizedName=" + LocalizedName + ", EnglishName=" + EnglishName
                    + "]";
        }
    }

    public static class SupplementalAdminAreas{
        public String LocalizedName;
        public String EnglishName;

        public String getDisplayName() {
            if (!TextUtils.isEmpty(LocalizedName)) {
                return LocalizedName;
            } else {
                return EnglishName;
            }
        }

        @Override
        public String toString() {
            return "SupplementalAdminAreas [LocalizedName=" + LocalizedName + ", EnglishName=" + EnglishName + "]";
        }
    }

    public static class Details {
        public String CanonicalLocationKey;

        @Override
        public String toString() {
            return "Details [CanonicalLocationKey=" + CanonicalLocationKey + "]";
        }
    }

    // get and set begin
    public String getKey() {
        return Key;
    }

    public SupplementalAdminAreas[] getSupplementalAdminAreasArray() {
        return supplementalAdminAreasArray;
    }

    public LocationEntity.AdministrativeArea getAdministrativeArea() {
        return administrativeArea;
    }

    public LocationEntity.Country getCountry() {
        return country;
    }

    public String getEnglishName() {
        return EnglishName;
    }

    public String getLocalizedName() {
        return LocalizedName;
    }

    public String getRank() {
        return Rank;
    }

    public String getType() {
        return Type;
    }

    public void setKey(String key) {
        Key = key;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setEnglishName(String englishName) {
        EnglishName = englishName;
    }

    public void setLocalizedName(String localizedName) {
        LocalizedName = localizedName;
    }

    public void setRank(String rank) {
        Rank = rank;
    }

    public void setCountry(LocationEntity.Country country) {
        this.country = country;
    }

    public void setAdministrativeArea(LocationEntity.AdministrativeArea administrativeArea) {
        this.administrativeArea = administrativeArea;
    }

    public void setSupplementalAdminAreas(SupplementalAdminAreas[] supplementalAdminAreasArray) {
        this.supplementalAdminAreasArray = supplementalAdminAreasArray;
    }

    public LocationEntity.Details getDetails() {
        return Details;
    }

    public void setDetails(LocationEntity.Details details) {
        Details = details;
    }
}
