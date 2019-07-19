## 添加topic半自动化小工具
- 执行环境
   - 开发人员的mac电脑
   - 连接bigtoys内网
   - 跳板机权限
   - jdk8
   - sshpass

### 添加topic操作流程

1. <font color="#0000dd">业务开发</font> 添加topic 需要发邮件或钉钉消息给 <font color="#dd0000">xxx</font> （<font color="#dddd00">手动</font>）
   - topic规范
2. <font color="#dd0000">xxx</font> 审核 topic 并 去预发和线上camaro后台添加topic（<font color="#00dd00">已自动化</font>）   
   - 在/Users/xuchao/Documents/topics.xlsx 文件中录入topic列表   
   - 在local shell 中执行   
      - java -jar /Users/xxx/Documents/topic-0.0.1-SNAPSHOT.jar add
      - 观察控制台输出
         - 如果返回错误，检查/Users/xxx/Documents/topics.xlsx ，最后一栏有错误提示
         - 如果返回成功，则表示topic 已经添加到预发和线上
3. <font color="#dd0000">xxx</font> 登录线上broker机器，下载topics.json文件（<font color="#dddd00">手动</font>）
   - 用secureCRT 打开两个local shell
      - 在第一个shell中依次键入
         - ssh-add -K ~/.ssh/jms_xxx.pem
         - ssh jms_xxx@login.xxx.net
         - 10.0.1.3
         - sudo su admin
         - sz /mqdata/mq/store/config/topics.json
      - 在第二个shell中键入
         - mv /Users/xxx/Documents/topics.json /Users/xxx/Documents/topics1.json
      - 在第一个shell中依次键入
         - exit
         - exit
         - 10.0.1.4
         - sudo su admin
         - sz /mqdata/mq/store/config/topics.json
      - 在第二个shell中键入
         - mv /Users/xxx/Documents/topics.json /Users/xxx/Documents/topics2.json
4. <font color="#dd0000">xxx</font> 合并topic文件并提交至git仓库,执行重启脚本(1.重启k8s环境的 nameserver和broker 2.重启任意k8s环境的camaro),最后通知xxx（<font color="#00dd00">已自动化</font>）
   - 在第二个shell中键入
      - java -jar /Users/xuchao/Documents/topic-0.0.1-SNAPSHOT.jar merge

---
### 项目结构说明

#### add
- TopicAdder 读取excel并校验topic命名规则, 并添加到 预发和线上环境

#### merge
- TopicMerger 合并从线上机器sz下来的两个topics.json文件,提交至git仓库,执行重启脚本,并钉钉提醒xxx

#### application.properties
- username(本机用户名)
- topics.json.notify.dingUserId(将topics.json发送给哪个用户)
- topics.json.path(topics.json文件地址)
- topics.json.1.path(topics1.json文件地址)
- topics.json.2.path(topics2.json文件地址)
- topics.xlsx.path(添加topic的excel文件地址)
- git.privateToken(gitlab私钥)
- topics.json.git.path(topics.json的git地址)
- topics.add.sh.git.path(add_topics.sh脚本git路径)
---

## Contribute

Please do contribute! Issues and pull requests are welcome.

Thank you for your help improving our performance at a time!

Any questions contact with xuchao@xxx.com please.
