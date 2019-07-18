package com.xuchao.mq.topic.merge;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * All rights Reserved, Designed By www.maihaoche.com
 *
 * @Package com.xuchao.mq.topic.merge
 * @author: xuchao（xuchao@maihaoche.com）
 * @date: 2019-07-18 11:52
 * @Copyright: 2017-2020 www.maihaoche.com Inc. All rights reserved.
 * 注意：本内容仅限于卖好车内部传阅，禁止外泄以及用于其他的商业目
 */
@Component
@Slf4j
public class TopicMerger {

    @Autowired
    DingDingSender dingDingSender;

    @Value("${topics.json.1.path}")
    String topics1JsonPath;

    @Value("${topics.json.2.path}")
    String topics2sonPath;

    @Value("${topics.json.path}")
    String topicsJsonPath;

    @Value("${topics.json.notify.dingUserId}")
    String topicsNotifyDingUserId;

    public void mergeTopicsJson(){
        log.info("/*=========== start mergeTopicsJson ===========*/");
        String topics1Str = FileUtil.readUtf8String(new File(topics1JsonPath));
        JSONObject topics1 = JSON.parseObject(topics1Str);
        String topics2Str = FileUtil.readUtf8String(new File(topics2sonPath));
        JSONObject topics2 = JSON.parseObject(topics2Str);


        JSONObject dataVersion;
        JSONObject dataVersion1 = topics1.getJSONObject("dataVersion");
        JSONObject dataVersion2 = topics2.getJSONObject("dataVersion");

        if (dataVersion1.getLong("timestamp") > dataVersion2.getLong("timestamp")) {
            dataVersion = dataVersion1;
        } else {
            dataVersion = dataVersion2;
        }

        Set<String> topicSet = new HashSet<>();
        Set<Map.Entry<String, Object>> topicConfigTableEntries = new HashSet<>();
        Set<Map.Entry<String, Object>> topicConfigTableEntries1 = topics1.getJSONObject("topicConfigTable").entrySet();
        Set<Map.Entry<String, Object>> topicConfigTableEntries2 = topics2.getJSONObject("topicConfigTable").entrySet();

        topicConfigTableEntries1.forEach(entry -> {
            if (!topicSet.contains(entry.getKey())) {
                topicConfigTableEntries.add(entry);
                topicSet.add(entry.getKey());
            }
        });

        topicConfigTableEntries2.forEach(entry -> {
            if (!topicSet.contains(entry.getKey())) {
                topicConfigTableEntries.add(entry);
                topicSet.add(entry.getKey());
            }
        });

        JSONObject topicConfigTable = new JSONObject();
        topicConfigTableEntries.forEach(entry -> {
            topicConfigTable.put(entry.getKey(), entry.getValue());
        });


        JSONObject topics = new JSONObject();
        topics.put("dataVersion",dataVersion);
        topics.put("topicConfigTable",topicConfigTable);
        String topicsStr = JSON.toJSONString(topics, true);
        FileUtil.writeUtf8String(topicsStr,topicsJsonPath);

        dingDingSender.sendFileToUser(
                topicsJsonPath,
                topicsNotifyDingUserId,
                "topics.json更新了,请及时同步到git仓库, 时间:" + DateUtil.formatDateTime(new Date()));
        log.info("/*=========== finish mergeTopicsJson ===========*/");
    }
}