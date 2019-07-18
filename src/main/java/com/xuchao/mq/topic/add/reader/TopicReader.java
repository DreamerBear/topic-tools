package com.xuchao.mq.topic.add.reader;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.xuchao.mq.topic.add.domain.Topic;
import com.xuchao.mq.topic.add.domain.TopicColumn;
import com.xuchao.mq.topic.util.ValidatorUtil;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * All rights Reserved, Designed By www.maihaoche.com
 *
 * @Package com.xuchao.mq.topic.reader
 * @author: xuchao（xuchao@maihaoche.com）
 * @date: 2019-07-18 10:00
 * @Copyright: 2017-2020 www.maihaoche.com Inc. All rights reserved.
 * 注意：本内容仅限于卖好车内部传阅，禁止外泄以及用于其他的商业目
 */
public class TopicReader {

    /**
     * 从excel中读取topic列表
     *
     * @param excelFilePath
     * @return
     */
    public static List<Topic> readTopicsFromExcel(String excelFilePath) {
        ExcelReader reader = ExcelUtil.getReader(excelFilePath);
        List<Topic> topicList = reader.readAll(Topic.class);
        reader.close();
        List<TopicColumn> topicColumnList = new ArrayList<>();
        boolean passed = validateTopics(topicList, topicColumnList);
        ExcelWriter writer = ExcelUtil.getWriter(excelFilePath);
        writer.write(topicColumnList);
        writer.close();
        if(!passed){
            throw new IllegalArgumentException("topic校验失败,请检查");
        }
        return topicList;
    }

    private static boolean validateTopics(List<Topic> topics,List<TopicColumn> topicColumnList) {
        boolean passed = true;
        for (Topic topic:topics){
            TopicColumn topicColumn = BeanUtil.toBean(topic,TopicColumn.class);
            String errorMessage = ValidatorUtil.returnAnyMessageIfError(topic);
            if(!StringUtils.isEmpty(errorMessage)){
                topicColumn.setErrorMessage(errorMessage);
                passed = false;
            }
            topicColumnList.add(topicColumn);
        }
        return passed;
    }

    public static void main(String[] args) {
        System.out.println(readTopicsFromExcel("/Users/xuchao/Documents/topics.xlsx"));
    }
}
