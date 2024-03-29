package com.xuchao.mq.topic.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * All rights Reserved, Designed By www.xxx.com
 *
 * @Package com.xuchao.mq.topic.domain
 * @author: xuchao（xuchao@xxx.com）
 * @date: 2019-07-18 10:20
 * @Copyright: 2017-2020 www.xxx.com Inc. All rights reserved.
 * 注意：本内容仅限于xxx内部传阅，禁止外泄以及用于其他的商业目
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicColumn {
    private String topicName;
    private String description;
    private Integer queueNum;
    private String creator;
    private String messageBodyDependence;
    private String messageBodyFullClassName;
    private String errorMessage;
}
