package com.xuchao.mq.topic;

import com.xuchao.mq.topic.add.TopicAdder;
import com.xuchao.mq.topic.merge.TopicMerger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@Slf4j
public class TopicApplication implements ApplicationRunner {

    @Autowired
    TopicMerger topicMerger;

    @Autowired
    TopicAdder topicAdder;

    public static void main(String[] args) {
        SpringApplication.run(TopicApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String[] sourceArgs = args.getSourceArgs();
        log.info(Arrays.toString(sourceArgs));
        Set<String> argSet = new HashSet<>();
        CollectionUtils.addAll(argSet,sourceArgs);
        if (argSet.contains("add")) {
            topicAdder.addTopic();
        }
        if (argSet.contains("merge")) {
            topicMerger.mergeTopicsJson();
        }
    }
}
