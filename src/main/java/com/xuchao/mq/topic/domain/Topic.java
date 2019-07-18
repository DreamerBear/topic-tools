package com.xuchao.mq.topic.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Pattern;

/**
 * All rights Reserved, Designed By www.maihaoche.com
 *
 * @Package com.xuchao.mq.topic.domain
 * @author: xuchao（xuchao@maihaoche.com）
 * @date: 2019-07-17 18:16
 * @Copyright: 2017-2020 www.maihaoche.com Inc. All rights reserved.
 * 注意：本内容仅限于卖好车内部传阅，禁止外泄以及用于其他的商业目
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Topic {

    @NotBlank(message = "topicName不能为空")
    @Pattern(regexp="^(tp|TP)_.*",message = "topicName第一列必须是tp或者TP,完整命名规范:tp_{发送方所在业务线}_{发送方系统名}_{发送场景}")
    @Pattern(regexp="^(tp|TP)_(trade|wms|tms|loan|finance)_.*",message = "topicName第二列必须是业务线名称,完整命名规范:tp_{发送方所在业务线}_{发送方系统名}_{发送场景}")
    @Pattern(regexp="^(tp|TP)_(trade|wms|tms|loan|finance)_[a-zA-Z]+_.*",message = "topicName第三列必须是应用名称,完整命名规范:tp_{发送方所在业务线}_{发送方系统名}_{发送场景}")
    @Pattern(regexp="^(tp|TP)_(trade|wms|tms|loan|finance)_[a-zA-Z]+_[a-zA-Z]+",message = "topicName第四列必须是发送场景,完整命名规范:tp_{发送方所在业务线}_{发送方系统名}_{发送场景}")
    private String topicName;
    @Builder.Default
    private String description = "系统导入";
    @Builder.Default
    @Range(min = 4,max = 16,message = "队列数必须取[4,16]区间的整数")
    private Integer queueNum = 4;
    @Builder.Default
    private String creator = "绪超";
    @Builder.Default
    private String messageBodyDependence = "";
    @Builder.Default
    private String messageBodyFullClassName = "";
}
