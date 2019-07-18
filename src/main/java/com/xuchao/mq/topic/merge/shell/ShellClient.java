package com.xuchao.mq.topic.merge.shell;

import cn.hutool.core.util.RuntimeUtil;

/**
 * All rights Reserved, Designed By www.maihaoche.com
 *
 * @Package com.xuchao.mq.topic.merge.shell
 * @author: xuchao（xuchao@maihaoche.com）
 * @date: 2019-07-18 17:49
 * @Copyright: 2017-2020 www.maihaoche.com Inc. All rights reserved.
 * 注意：本内容仅限于卖好车内部传阅，禁止外泄以及用于其他的商业目
 */
public class ShellClient {
    public static void main(String[] args) {
        String pwd = RuntimeUtil.execForStr("ls -lh");
        System.out.println(pwd);
    }
}
