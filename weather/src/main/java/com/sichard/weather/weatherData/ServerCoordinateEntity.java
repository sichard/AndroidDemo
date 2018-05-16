package com.sichard.weather.weatherData;

import com.sichard.weather.Interface.NotProGuard;

/**
 * <br> 服务器IP辅助定位的数据实体
 * Created by jinhui.shao on 2016/8/3.
 */
/*
{

    "status": "0",

    "msg": "Success",

    "data": {   // 已加密

        "countryCode": "CN",

        "longitude": "114.0683",

        "latitude": "22.54554"

    },

    "compress": 0

}
 */
public class ServerCoordinateEntity implements NotProGuard {
    public int status;
    public String msg;
    public String data; // 加密的data数据
    public int compress;
    public DataEntity dataEntity;   // 解密的data数据

    public static class DataEntity {
        public String countryCode;
        public double longitude;    // double和String都行
        public double latitude;

        @Override
        public String toString() {
            return "DataEntity [countryCode=" + countryCode + ",longitude=" + longitude + ",latitude=" + latitude + "]";
        }
    }


    @Override
    public String toString() {
        return "ServerCoordinateEntity [status=" + status + ",msg=" + msg
                + ",data=" + data + ",compress=" + compress
                + ",dataEntity=" + dataEntity
                + "]";
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCompress() {
        return compress;
    }

    public void setCompress(int compress) {
        this.compress = compress;
    }

    public DataEntity getDataEntity() {
        return dataEntity;
    }

    public void setDataEntity(DataEntity dataEntity) {
        this.dataEntity = dataEntity;
    }
}