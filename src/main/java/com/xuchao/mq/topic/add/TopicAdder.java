package com.xuchao.mq.topic.add;


import com.xuchao.mq.topic.domain.Topic;
import com.xuchao.mq.topic.add.http.CamaroApiClient;
import com.xuchao.mq.topic.add.http.CamaroApiClientFactory;
import com.xuchao.mq.topic.add.reader.TopicReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.maihaoche.com
 *
 * @Package com.xuchao.mq.topic.add
 * @author: xuchao（xuchao@maihaoche.com）
 * @date: 2019-07-18 11:53
 * @Copyright: 2017-2020 www.maihaoche.com Inc. All rights reserved.
 * 注意：本内容仅限于卖好车内部传阅，禁止外泄以及用于其他的商业目
 */
@Component
@Slf4j
public class TopicAdder {

    @Value("${topics.xlsx.path}")
    String topicsXlsxPath;

    public void addTopic(){
        log.info("/*=========== start addTopic ===========*/");
        List<Topic> topics = TopicReader.readTopicsFromExcel(topicsXlsxPath);
        CamaroApiClient camaroApiPreClient = CamaroApiClientFactory.getCamaroApiClient(CamaroApiClientFactory.Env.PRE);
        CamaroApiClient camaroApiOnlineClient = CamaroApiClientFactory.getCamaroApiClient(CamaroApiClientFactory.Env.ONLINE);
        List<Map<String, String>> result1 = camaroApiPreClient.doTopicSave(topics);
        log.info("pre: "+result1.toString());
        List<Map<String, String>> result2 = camaroApiOnlineClient.doTopicSave(topics);
        log.info("online: "+result2.toString());
        log.info("/*=========== finish addTopic ===========*/");
    }
}
