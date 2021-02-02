
SSH https://www.ssh.com/ssh/

![data_stream](./img/data_stream.png)

目前开源的ssh client有很多，支持情况也都不一样，下面就做一些对比：

|project|License|JDK supports|maven|
|-------|-------|------------|-----|
| JSch  | BSD   |1.6+        | ```com.jcraft:jsch:0.1.55 ``` |
|sshj   |Apache 2.0|1.6+     | ```net.schmizz:sshj:0.10.0``` |
|ganymed-ssh2 |Apache 2.0|1.8+|```com.airlenet.yang:ganymed-ssh2:1.2.0```|
|j2ssh|||```sshtools:j2ssh-core:0.2.9```|


Java版开源ssh框架功能对比：

|对比项      |Jsch      | sshj     |ganymed-ssh2|j2ssh   |
|---------  |----------|----------|------------|--------|
|License    | BSD      |Apache 2.0| Apache 2.0 | 未知    |
|JDK supports | 1.6+   |1.6+      |1.8+        | JDK11 + |
|代码可读性  | C        | A        | A          | B       |



## 协议说明
ssh 协议的流程一般是这样的：
```text
1、创建TCP连接
2、进行身份认证
3、打开一个Channel，进行交互，关闭Channel；然后重复 3
4、关闭tcp连接
```

其中第3步，是循环执行的，完成一个channel后，可以继续打开另外一个channel。


Channel 分类：

+ Direct
  + Direct TCP-IP
  + Session
    + Shell
    + Subsystem
    + Command
+ Forwarded
  + X11
  + Forwarded TCP-IP













