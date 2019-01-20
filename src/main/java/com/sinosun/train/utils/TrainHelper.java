package com.sinosun.train.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.sinosun.train.constants.UrlConstant;
import com.sinosun.train.model.response.Station;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created on 2019/1/16 20:42.
 *
 * @author caogu
 */
public class TrainHelper {
    private static final Logger logger = LoggerFactory.getLogger(TrainHelper.class);


    /**
     * 从12306获取最新的火车站点数据
     * @return 火车站点数据
     */
    public static List<Station> getTrainAllCityFromNet() {
        List<Station> stations = Lists.newArrayList();

        String httpRes = HttpUtil.request(UrlConstant.TRAIN_ALL_STATION_URL, Connection.Method.GET, null);
        if (StringUtils.isNotEmpty(httpRes)) {
            // var station_names ='@bjb|北京北|VAP|beijingbei|bjb|0@bjd|北京东|BOP|beijingdong|bjd|1'
            String originalStationStr = httpRes.substring(httpRes.indexOf("\'") + 1, httpRes.lastIndexOf("\'"));
            List<String> originalStations = Lists.newArrayList(Splitter.on("@").omitEmptyStrings().trimResults().split(originalStationStr));
            for (String originalStation : originalStations) {
                List<String> stationInfo = Lists.newArrayList(Splitter.on("|").omitEmptyStrings().trimResults().split(originalStation));
                if (stationInfo.size() > 3) {
                    Station station = new Station();
                    station.setPingYinShort(stationInfo.get(0));
                    station.setName(stationInfo.get(1));
                    station.setStationCode(stationInfo.get(2));
                    station.setPingYin(stationInfo.get(3));
                    stations.add(station);
                } else {
                    logger.error("从12306获取到的火车站点信息有误，stationInfo={}", originalStation);
                }
            }
        } else {
            logger.error("从12306获取到的火车站点信息为空");
        }
        return stations;
    }

    /**
     * 向12306发送get请求，并返回json对象
     * @param url 请求URL
     * @return json对象
     */
    public static JSONObject requestTo12306(String url) {
        return JsonUtil.parseObject(HttpUtil.request(url, Connection.Method.GET, null));
    }
}
