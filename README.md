## 添加topic半自动化小工具

### 添加topic操作流程

1. <font color="#0000dd">业务开发</font> 添加topic 需要发邮件或钉钉消息给 <font color="#dd0000">绪超</font> （<font color="#dddd00">手动</font>）
   - topic规范见 [topic规范](https://cf.dawanju.net/pages/viewpage.action?pageId=15271227)
2. <font color="#dd0000">绪超</font> 审核 topic 并 去预发和线上camaro后台添加topic（<font color="#00dd00">已自动化</font>）   
   - 在/Users/xuchao/Documents/topics.xlsx 文件中录入topic列表   
   - 在local shell 中执行   
      - java -jar /Users/xuchao/Documents/topic-0.0.1-SNAPSHOT.jar add
      - 观察控制台输出
         - 如果返回错误，检查/Users/xuchao/Documents/topics.xlsx ，最后一栏有错误提示
         - 如果返回成功，则表示topic 已经添加到预发和线上
3. <font color="#dd0000">绪超</font> 登录线上broker机器，下载topics.json文件（<font color="#dddd00">手动</font>）
   - 用secureCRT 打开两个local shell
      - 在第一个shell中依次键入
         - ssh-add -K ~/.ssh/jms_xuchao.pem
         - ssh jms_xuchao@login.dawanju.net
         - 10.0.1.3
         - sudo su admin
         - sz /mqdata/mq/store/config/topics.json
      - 在第二个shell中键入
         - mv /Users/xuchao/Documents/topics.json /Users/xuchao/Documents/topics1.json
      - 在第一个shell中依次键入
         - exit
         - exit
         - 10.0.1.4
         - sudo su admin
         - sz /mqdata/mq/store/config/topics.json
      - 在第二个shell中键入
         - mv /Users/xuchao/Documents/topics.json /Users/xuchao/Documents/topics2.json
4. <font color="#dd0000">绪超</font> 合并topic文件并提交至git仓库,通知白起（<font color="#00dd00">已自动化</font>）
   - 在第二个shell中键入
      - java -jar /Users/xuchao/Documents/topic-0.0.1-SNAPSHOT.jar merge
5. <font color="#dd0000">白起</font> 重启 k8s环境的 nameserver和broker
6. <font color="#dd0000">白起</font> 重启任意k8s环境的camaro

---
### 项目结构说明

#### add
- TopicAdder 读取excel并校验topic命名规则, 并添加到 预发和线上环境

#### merge
- TopicMerger 合并从线上机器sz下来的两个topics.json文件,提交至git仓库,并钉钉提醒白起

#### application.properties
- topics.json.notify.dingUserId(将topics.json发送给哪个用户 绪超:15286823019673998  白起:15219728585798597)
- topics.json.path(topics.json文件地址)
- topics.json.1.path(topics1.json文件地址)
- topics.json.2.path(topics2.json文件地址)
- topics.xlsx.path(添加topic的excel文件地址)

---

## Contribute

Please do contribute! Issues and pull requests are welcome.

Thank you for your help improving our performance at a time!

Any questions contact with xuchao@maihaoche.com please.
