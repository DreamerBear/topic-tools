package com.xuchao.mq.topic.merge;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuchao.mq.topic.domain.GitCommit;
import com.xuchao.mq.topic.merge.http.GitApiClient;
import com.xuchao.mq.topic.merge.shell.ShellClient;
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
 * All rights Reserved, Designed By www.xxx.com
 *
 * @Package com.xuchao.mq.topic.merge
 * @author: xuchao（xuchao@xxx.com）
 * @date: 2019-07-18 11:52
 * @Copyright: 2017-2020 www.xxx.com Inc. All rights reserved.
 * 注意：本内容仅限于xxx内部传阅，禁止外泄以及用于其他的商业目
 */
@Component
@Slf4j
public class TopicMerger {

    @Autowired
    DingDingSender dingDingSender;

    @Autowired
    GitApiClient gitApiClient;

    @Autowired
    ShellClient shellClient;

    @Value("${topics.json.1.path}")
    String topics1JsonPath;

    @Value("${topics.json.2.path}")
    String topics2sonPath;

    @Value("${topics.json.path}")
    String topicsJsonPath;

    @Value("${topics.json.notify.dingUserId}")
    String topicsNotifyDingUserId;

    @Value("${topics.json.git.path}")
    String topicsJsonGitPath;

    @Value("${topics.json.git.branch}")
    String topicsJsonGitBranch;

    @Value("${topics.json.git.author}")
    String topicsJsonGitAuthor;

    @Value("${topics.json.git.author.email}")
    String topicsJsonGitAuthorEmail;

    @Value("${topics.add.sh.git.path}")
    String topicsAddShGitPath;

    @Value("${topics.add.sh.local.path}")
    String topicsAddShLocalPath;

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

        Date now = new Date();
        gitApiClient.uploadGitFile(GitCommit.builder()
                .author(topicsJsonGitAuthor)
                .authorEmail(topicsJsonGitAuthorEmail)
                .branch(topicsJsonGitBranch)
                .gitFilePath(topicsJsonGitPath)
                .localFilePath(topicsJsonPath)
                .commitMessage("从线上同步topics,时间:" + DateUtil.formatDateTime(now))
                .build());

        String topicsAddShellContent = gitApiClient.getGitFile(topicsAddShGitPath);
        shellClient.executeShell(topicsAddShLocalPath,topicsAddShellContent);

        dingDingSender.sendMessageToUser(
                topicsNotifyDingUserId,
                "已将线上topics.json更新到k8s环境, 时间:" + DateUtil.formatDateTime(now));
        log.info("/*=========== finish mergeTopicsJson ===========*/");
    }
}
