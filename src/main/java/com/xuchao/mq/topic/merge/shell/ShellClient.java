package com.xuchao.mq.topic.merge.shell;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * All rights Reserved, Designed By www.xxx.com
 *
 * @Package com.xuchao.mq.topic.merge.shell
 * @author: xuchao（xuchao@xxx.com）
 * @date: 2019-07-19 10:44
 * @Copyright: 2017-2020 www.xxx.com Inc. All rights reserved.
 * 注意：本内容仅限于xxx内部传阅，禁止外泄以及用于其他的商业目
 */
@Component
@Slf4j
public class ShellClient {

    @SneakyThrows
    public void executeShell(String shellLocalPath, String shellContent) {
        FileUtil.writeUtf8String(shellContent, shellLocalPath);
        RuntimeUtil.execForStr("chmod 777 " + shellLocalPath);
        Process process = RuntimeUtil.exec("sh " + shellLocalPath);
        @Cleanup InputStream inputStream = process.getInputStream();
        @Cleanup BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = br.readLine()) != null) {
            log.info(line);
        }
    }
}
