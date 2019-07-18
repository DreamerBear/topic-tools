package com.xuchao.mq.topic.domain;

import lombok.*;

/**
 * All rights Reserved, Designed By www.maihaoche.com
 *
 * @Package com.xuchao.mq.topic.add.domain
 * @author: xuchao（xuchao@maihaoche.com）
 * @date: 2019-07-18 17:27
 * @Copyright: 2017-2020 www.maihaoche.com Inc. All rights reserved.
 * 注意：本内容仅限于卖好车内部传阅，禁止外泄以及用于其他的商业目
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class GitCommit {
    private String gitFilePath;
    private String localFilePath;
    private String branch;
    private String author;
    private String authorEmail;
    private String commitMessage;
}
