package com.xuchao.mq.topic.add.http;

import com.xuchao.mq.topic.domain.Topic;
import lombok.AllArgsConstructor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *   
 *   
 *  @author: 寻欢（xunhuan@maihaoche.com）
 *  @date: 2019/7/9 2:15 PM
 *
 * @since V1.0
 *  
 */
public class CamaroApiClientFactory {

    @AllArgsConstructor
    public enum Env {
        PRE("login-pre.maihaoche.com", "camaro-pre.maihaoche.com", "tutu", "123123", "94086228382605312"),
        ONLINE("login.maihaoche.com", "camaro.maihaoche.com", "tutu", "123123", "93743219128426496");

        public final String acuraHostName;
        public final String camaroHostName;
        public final String userName;
        public final String password;
        public final String appId;

        public String getURL(String template) {
            return MessageFormat.format(template, acuraHostName, camaroHostName);
        }
    }

    public static CamaroApiClient getCamaroApiClient(Env env) {
        return new CamaroApiClient(env);
    }

    public static void main(String[] args) {
        CamaroApiClient camaroApiPreClient = CamaroApiClientFactory.getCamaroApiClient(Env.PRE);
        CamaroApiClient camaroApiOnlineClient = CamaroApiClientFactory.getCamaroApiClient(Env.ONLINE);

        List<Topic> topics = new ArrayList<>();
        topics.add(Topic.builder().topicName("tp_trade_ferrari_tsOrderControl").description("帮我买topic").creator("马赫").build());
        List<Map<String, String>> result1 = camaroApiPreClient.doTopicSave(topics);
        System.out.println(result1);
        List<Map<String, String>> result2 = camaroApiOnlineClient.doTopicSave(topics);
        System.out.println(result2);
    }

}
