package com.xuchao.mq.topic.merge;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiCspaceAddToSingleChatRequest;
import com.dingtalk.api.request.OapiFileUploadSingleRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.response.OapiCspaceAddToSingleChatResponse;
import com.dingtalk.api.response.OapiFileUploadSingleResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.google.common.cache.CacheBuilder;
import com.taobao.api.FileItem;
import com.taobao.api.internal.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * All rights Reserved, Designed By www.xxx.com
 *
 * @Package com.xuchao.mq.topic.util
 * @author: xuchao（xuchao@xxx.com）
 * @date: 2019-07-18 12:06
 * @Copyright: 2017-2020 www.xxx.com Inc. All rights reserved.
 * 注意：本内容仅限于xxx内部传阅，禁止外泄以及用于其他的商业目
 */
@Slf4j
@Component
public class DingDingSender {

    @Value("${dingtalk.corpId}")
    String corpId;
    @Value("${dingtalk.secret}")
    String secret;
    @Value("${dingtalk.ssoSecret}")
    String ssoSecret;
    @Value("${dingtalk.agentId}")
    String agentId;

    public static final String GET_TOKEN = "https://oapi.dingtalk.com/gettoken";

    public static String ACCESS_TOKEN_KEY = "camaro_ding_ak";

    ConcurrentMapCache concurrentMapCache = new ConcurrentMapCache("access_token", CacheBuilder.newBuilder().expireAfterWrite(7100, TimeUnit.SECONDS).build().asMap(), false);

    /**
     * 获取accessToken
     *
     * @return
     */
    public String getAccessToken() {
        if (concurrentMapCache.get(ACCESS_TOKEN_KEY) != null) {
            return concurrentMapCache.get(ACCESS_TOKEN_KEY).get().toString();
        }
        String accessToken = "";
        log.info("request dingtalk token");
        String url = GET_TOKEN + "?corpid=" + corpId + "&corpsecret=" + secret;
        HttpRequest getTokenGet = HttpUtil.createGet(url);
        HttpResponse getTokenResponse = getTokenGet.execute();
        String response = getTokenResponse.body();
        JSONObject jsonObject = JSONObject.parseObject(response);
        if (!"0".equals(jsonObject.getString("errcode"))) {
            log.error("Access ding talk error. Response is {}.", JSON.toJSONString(response));
        } else {
            if (jsonObject.containsKey("access_token")) {
                accessToken = jsonObject.getString("access_token");
                if (!StringUtils.isEmpty(accessToken)) {
                    // 正常情况下AccessToken有效期为7200秒，有效期内重复获取返回相同结果，并自动续期
                    concurrentMapCache.put(ACCESS_TOKEN_KEY, accessToken);
                    return accessToken;
                }
            }
        }
        log.error("Response is error. Response is {}.", JSON.toJSONString(response));
        throw new RuntimeException("request for aceesss token error");
    }

    public String saveFile(String filePath) {
        OapiFileUploadSingleRequest request = new OapiFileUploadSingleRequest();
        request.setFileSize(FileUtil.size(new File(filePath)));
        request.setAgentId(agentId);
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/file/upload/single?" + WebUtils.buildQuery(request.getTextParams(), "utf-8"));
            request = new OapiFileUploadSingleRequest();
            request.setFile(new FileItem(filePath));
            OapiFileUploadSingleResponse response = client.execute(request, getAccessToken());
            log.info("saveFile, isSuccess:{}, errCode:{}, errMsg:{}, mediaId:{}"
                    , response.isSuccess()
                    , response.getErrcode()
                    , response.getErrmsg()
                    , response.getMediaId()
            );
            return response.getMediaId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendFileToUser(String filePath, String userId, String message) {
        String mediaId = saveFile(filePath);
        String accessToken = getAccessToken();
        OapiCspaceAddToSingleChatRequest request = new OapiCspaceAddToSingleChatRequest();
        request.setAgentId(agentId);
        request.setUserid(userId);
        request.setMediaId(mediaId);
        request.setFileName(FileUtil.getName(filePath));
        OapiCspaceAddToSingleChatResponse response = null;

        try {
            sendMessageToUser(userId,message);
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/cspace/add_to_single_chat?" + WebUtils.buildQuery(request.getTextParams(), "utf-8"));
            response = client.execute(request, accessToken);
        } catch (Exception e) {
            log.error("sendFileToUser error, Response is {}.", JSON.toJSONString(response));
        }

    }

    public void sendMessageToUser(String userId, String message) {
        String accessToken = getAccessToken();
        OapiMessageCorpconversationAsyncsendV2Response response = null;
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");

            OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
            request.setUseridList(userId);
            request.setAgentId(Long.valueOf(agentId));
            request.setToAllUser(false);

            OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
            msg.setMsgtype("text");
            msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
            msg.getText().setContent(message);
            request.setMsg(msg);
            response = client.execute(request, accessToken);
        } catch (Exception e) {
            log.error("sendMessageToUser error, Response is {}.", JSON.toJSONString(response));
        }

    }
}
