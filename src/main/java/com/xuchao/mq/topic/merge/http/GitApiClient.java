package com.xuchao.mq.topic.merge.http;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import com.xuchao.mq.topic.domain.GitCommit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * All rights Reserved, Designed By www.xxx.com
 *
 * @Package com.xuchao.mq.topic.merge.http
 * @author: xuchao（xuchao@xxx.com）
 * @date: 2019-07-18 16:42
 * @Copyright: 2017-2020 www.xxx.com Inc. All rights reserved.
 * 注意：本内容仅限于xxx内部传阅，禁止外泄以及用于其他的商业目
 */
@Component
public class GitApiClient {

    @Value("${git.privateToken}")
    String privateToken;

    public void uploadGitFile(GitCommit gitCommit) {
        HttpRequest httpRequest = HttpRequest.put(gitCommit.getGitFilePath());
        httpRequest.setUrl(gitCommit.getGitFilePath());
        httpRequest.header("PRIVATE-TOKEN", privateToken);
        httpRequest.header("Content-Type", "application/json");
        JSON json = new JSONObject();
        json.putByPath("branch", gitCommit.getBranch());
        json.putByPath("author_email", gitCommit.getAuthorEmail());
        json.putByPath("author_name", gitCommit.getAuthor());
        json.putByPath("commit_message", gitCommit.getCommitMessage());
        json.putByPath("content", FileUtil.readUtf8String(gitCommit.getLocalFilePath()));
        httpRequest.body(json);
        HttpResponse response = httpRequest.execute();
        if (response.getStatus() != 200 || !response.body().contains("file_path")) {
            throw new RuntimeException(MessageFormat.format("提交git失败,gitCommit:{0}", gitCommit));
        }
    }

    public String getGitFile(String gitFilePath) {
        HttpRequest httpRequest = HttpRequest.get(gitFilePath + "/raw?ref=master");
        httpRequest.setUrl(gitFilePath + "/raw?ref=master");
        HttpResponse response = httpRequest
                .header("PRIVATE-TOKEN", privateToken)
                .execute();
        return response.body();
    }
}
