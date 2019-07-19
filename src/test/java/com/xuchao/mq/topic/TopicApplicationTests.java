package com.xuchao.mq.topic;

import com.xuchao.mq.topic.merge.DingDingSender;
import com.xuchao.mq.topic.merge.http.GitApiClient;
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

    @Autowired
    GitApiClient gitApiClient;

    @Test
    public void testGetAccessToken() {
        String accessToken = dingDingSender.getAccessToken();
        System.out.println(accessToken);
    }

    @Test
    public void testSaveFile(){
        dingDingSender.saveFile("/Users/xxx/Documents/topics.json");
    }

    @Test
    public void testSendFileToUser(){
        dingDingSender.sendFileToUser("/Users/xxx/Documents/topics.json","xxx","hello");
    }

    @Test
    public void testGetGitFile(){
        System.out.println(gitApiClient.getGitFile("https://git.xxx.net/api/v4/projects/859/repository/files/middleware%2Fadd_topic.sh"));
    }

}
