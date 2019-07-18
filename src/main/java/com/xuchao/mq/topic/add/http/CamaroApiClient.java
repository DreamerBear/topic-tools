package com.xuchao.mq.topic.add.http;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xuchao.mq.topic.domain.Topic;
import com.xuchao.mq.topic.util.ValidatorUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.net.HttpCookie;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * All rights Reserved, Designed By www.maihaoche.com
 *
 * @Package com.xuchao.mq.topic.http
 * @author: xuchao（xuchao@maihaoche.com）
 * @date: 2019-07-17 18:12
 * @Copyright: 2017-2020 www.maihaoche.com Inc. All rights reserved.
 * 注意：本内容仅限于卖好车内部传阅，禁止外泄以及用于其他的商业目
 */
@AllArgsConstructor
@NoArgsConstructor
public class CamaroApiClient {

    private static final String LOGIN_URL = "https://{0}/login.json";
    private static final String TOPIC_SAVE_URL = "https://{1}/topic/";
    private static final String TOPIC_QUERY_URL = "https://{1}/topic/list";

    private CamaroApiClientFactory.Env env;

    /**
     * 执行topic保存
     *
     * @param topics
     * @return
     */
    public List<Map<String, String>> doTopicSave(List<Topic> topics) {
        validateTopics(topics);

        List<Map<String, String>> result = new ArrayList<>();
        HttpRequest loginPost = HttpUtil.createPost(env.getURL(LOGIN_URL));
        loginPost.form("username", env.userName);
        loginPost.form("password", env.password);
        loginPost.form("appid", env.appId);
        HttpResponse loginResponse = loginPost.execute();

        String validateUrl = loginResponse.header("Location");
        HttpRequest validateGet = HttpUtil.createGet(validateUrl);
        HttpResponse validateResponse = validateGet.execute();
        HttpCookie camaro_sid = validateResponse.getCookie("camaro_sid");

        topics.forEach(topic -> {
            String topicSaveResponse = null;
            int retries = 0;
            while (retries < 8) {
                HttpRequest topicSavePost = HttpUtil.createPost(env.getURL(TOPIC_SAVE_URL));
                topicSavePost.cookie(camaro_sid);
                topicSavePost.header("content-type", "application/json; charset=UTF-8");
                topicSavePost.body(JSON.toJSONString(topic));
                topicSaveResponse = topicSavePost.execute().body();

                HttpRequest topicQueryGet = HttpUtil.createGet(env.getURL(TOPIC_QUERY_URL));
                topicQueryGet.form("topic", topic.getTopicName());
                HttpResponse topicQueryResponse = topicQueryGet.execute();
                String topicQueryJson = topicQueryResponse.body();
                JSONArray jsonArray = JSON.parseArray(topicQueryJson);
                if (!jsonArray.isEmpty()) {
                    Set<String> topicNameSet = new HashSet<>();
                    jsonArray.forEach(json->{
                        topicNameSet.add(((JSONObject)json).getString("topicName"));
                    });
                    if(topicNameSet.contains(topic.getTopicName())){
                        break;
                    }
                }
                try {
                    TimeUnit.MILLISECONDS.sleep((int)(Math.random()*(5000))+1000);
                } catch (InterruptedException e) {
                }
                retries++;
            }

            Map<String, String> res = new HashMap<>();
            res.put(topic.getTopicName(), topicSaveResponse + "[retries:"+retries+"]");
            result.add(res);
        });

        return result;
    }

    private void validateTopics(List<Topic> topics) {
        if (topics == null || topics.isEmpty()) {
            throw new IllegalArgumentException("topics is empty");
        }
        topics.forEach(topic -> ValidatorUtil.throwExceptionIfError(topic));
    }

}
