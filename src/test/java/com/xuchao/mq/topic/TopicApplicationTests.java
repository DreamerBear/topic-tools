package com.xuchao.mq.topic;

import com.xuchao.mq.topic.merge.DingDingSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TopicApplicationTests {

    @Autowired
    DingDingSender dingDingSender;

    @Test
    public void testGetAccessToken() {
        String accessToken = dingDingSender.getAccessToken();
        System.out.println(accessToken);
    }

    @Test
    public void testSaveFile(){
        dingDingSender.saveFile("/Users/xuchao/Documents/topics.json");
    }

    @Test
    public void testSendFileToUser(){
        dingDingSender.sendFileToUser("/Users/xuchao/Documents/topics.json","15286823019673998","hello");
    }

}
