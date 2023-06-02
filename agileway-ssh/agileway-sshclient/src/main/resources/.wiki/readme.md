
SSH https://www.ssh.com/ssh/



目前开源的ssh client有很多，支持情况也都不一样，下面就做一些对比：

|project|License|JDK supports|maven|项目状态|
|-------|-------|------------|-----|-------|
| JSch  | BSD   |1.6+        | ```com.jcraft:jsch:0.1.55 ``` | 不做特性开发，只做BugFix|
|sshj   |Apache 2.0|1.6+     | ```net.schmizz:sshj:0.10.0``` | 活跃 |
|ganymed-ssh2 |Apache 2.0|1.4+|```com.airlenet.yang:ganymed-ssh2:1.2.0```| 超过10年没有维护，有Bug也没有再修复 |
|trilead-ssh2 |Apache 2.0|1.4+|```com.trilead:trilead-ssh2:1.0.0-build222```|源自ganymed-ssh2,不做特性开发，只做BugFix|
|j2ssh|>=0.2.8 GPL ; <0.2.8 LGPL + Apache |1.6+|```sshtools:j2ssh-core:0.2.9```| 不做特性开发，只做BugFix， 经测试目前跑不起来|
|maverick-synergy|LGPL|JDK1.8+|com.sshtools:maverick-synergy-client:3.0.3-FINAL|活跃，它是j2ssh的替代品|



Java版开源ssh框架功能对比：

|对比项      |Jsch      | sshj     |ganymed-ssh2|trilead-ssh2|j2ssh   | synergy|
|---------  |----------|----------|------------|------------|--------|
|License    | BSD      |Apache 2.0| Apache 2.0 |Trilead AG  | >=0.2.8 GPL ; <0.2.8 LGPL + Apache  | LGPL, GPL|
|JDK supports | 1.6+   |1.6+      |1.4+        |1.4+      | 1.6+ |1.8+|
|代码可读性  | C        | A        | A          |A           | B       |A|
|算法支持情况| 比较全    |比较全     |比较全       |比较全       | 需要手动添加最新算法 [最新算法下载地址](https://www.oracle.com/java/technologies/javase-jce8-downloads.html#)   |比较全|
|SFTP       |支持      |支持       |支持         |支持        | 支持，目前没法用    |支持|

注意：
+ ganymed-ssh2 与 trilead-ssh2 代码基本是一致的
+ j2ssh 的License不友好，算法支持也不全面，如果出现连接失败时，最好是把 debug日志打开，看看到底是什么情况，是由于算法两边支持不能协商一致还是怎么的
+ openssh 配置文件说明 https://www.ssh.com/ssh/sshd/#configuration-file



## 1、协议说明

[标准规范](https://datatracker.ietf.org/wg/secsh/documents/)

![data_stream](./img/data_stream.png)

[参考连接](http://web.mit.edu/rhel-doc/4/RH-DOCS/rhel-rg-en-4/s1-ssh-conn.html)

### 1.1 流程

ssh 协议的流程一般是这样的：
```text
1、创建TCP连接、key exchange
2、进行身份认证
3、打开一个Channel，进行交互，关闭Channel；然后重复 3
4、关闭tcp连接
```

其中第3步，是循环执行的，完成一个channel后，可以继续打开另外一个channel。

### 1.2 Key Exchange
key exchange 是在ssh client 通过tcp连接到server后发生的，流程如下：

+ Keys are exchanged
+ The public key encryption algorithm is determined
+ The symmetric encryption algorithm is determined
+ The message authentication algorithm is determined
+ The hash algorithm is determined

在 key exchange 期间，server 用唯一的 host_key 来识别它自己到client的关系。
如果client 之前从未连接过某个server，那么server的 host_key 对于client来说就是 unknown的，这种情况下就不能连接的。
如果client 在连到一个从未连接过的server时，server 会回传 host_key 到client，client 如果验证通过后，则会写入到known_hosts中。
如果client 在连到一个已连接过的server时，会将 known_hosts 中的host_key和server 的host_key进行验证。
验证通过后，连接才真正建立。

在服务端，如果使用的是OpenSSH时，通常 host keys会被存在在 /etc/ssh 目录（该目录可配置）下，以ssh_host_<rsa/dsa/ecdsa/ed25519>_key 为开始的文件里。

在客户端，通常存在known_hosts文件，文件位置可以是在：/etc/ssh/known_hosts 或者 是在 .ssh/known_hosts 


### 1.3 Authentication
连接建立后，server 会告诉client 所有支持的方式，例如使用 private key进行的签名，或者使用password 。client 选择一种方式去进行认证。


### 1.4 Channel
在认证通过后，基于一个连接，可以打开多个 channel，这是通过多路复用技术完成的。

```
   All terminal sessions, forwarded connections, etc., are channels.
   Either side may open a channel.  Multiple channels are multiplexed
   into a single connection.

   Channels are identified by numbers at each end.  The number referring
   to a channel may be different on each side.  Requests to open a
   channel contain the sender's channel number.  Any other channel-
   related messages contain the recipient's channel number for the
   channel.

   Channels are flow-controlled.  No data may be sent to a channel until
   a message is received to indicate that window space is available.
```


Channel 分类：

+ Direct
  + Direct TCP/IP PORT
  + Interactive Sessions
    + Shell
    + Subsystem
    + Command
+ Forwarded
  + X11
  + Forwarded TCP/IP PORT


## 2、OpenSSH Channel Multiplexing 

### 2.1 功能简介

在 OpenSSH 中 Multiplexing 功能支持同一主机的多个 SSH 会话共享单一 TCP 连接进行通讯，一旦第一个连接建立，后续连接就不再需要凭证，从而消除了每次连接同一机器都需要键入密码的麻烦并且大幅度节省了服务器端的资源。

### 2.2 Channel

[参考连接](https://www.ibm.com/developerworks/cn/opensource/os-cn-openssh-multiplexing/index.html)

常规的tcp连接，在建立后，我们会把整个tcp 连接称为一个 channel。那么这个tcp连接的服务端，就只能针对监听该port的进程有效。tcp的客户端，也只是针对一个进程有效。

OpenSSH 的Channel，跟常规的Channel 并不是一个层面的。它是建立在请求级别的，也就是说，Client每发起一个请求，会建立或复用通过一个channel，这个channel 不是连接级别的，是一个虚拟的。

简而言之，Channel 可以被理解为对 SSH 连接的一次再分割，每一个基于 SSH 的通信会话都可以利用不同的 Channel 来共享同一条连接，以保证对网络资源的最大化利用。

在 SSH 中，所有一切的通信会话，包括 terminal session，connection forward 都是利用 Channel 实现的。

控制终端是进程的一个属性。通过 fork 系统调用创建的子进程会从父进程那里继承控制终端。这样，session 中的所有进程都从 session 领头进程那里继承控制终端。Session 的领头进程称为终端的控制进程(controlling process)。
简单点说就是：一个 session 只能与一个终端关联，这个终端被称为 session 的控制终端(controlling terminal)。同时只能由 session 的领头进程来建立或者改变终端与 session 的联系。



# 3、SFTP

SFTP 是基于 SSH协议的 subsystem Channel，并提供ftp相关的功能。

因为基于SSH协议，所以端口也是22。

SFTP支持的数据协议：

```text
INIT: sends client version numbers and extensions to the server

VERSION: returns server version number and extensions to the client

OPEN: opens or creates a file, returning a file handle

CLOSE: closes a file handle

READ: reads data from a file

WRITE: writes data to a file

OPENDIR: opens a directory for reading, returning a directory handle

READDIR: reads file names and attributes from a directory handle

MKDIR: creates a directory

RMDIR: removes a directory

REMOVE: removes a file

RENAME: renames a file

STAT: returns file attributes given a path, following symlinks

LSTAT: returns file attributes given a path, without following symlinks

FSTAT: returns file attributes given a file handle

SETSTAT: modifies file attributes given a path

FSETSTAT: modifies file attributes given a file handle

READLINK: reads the value of a symbolic link

SYMLINK: creates a symbolic link

REALPATH: canonicalizes server-size relative path to an absolute path

The following response packets are returned by the server:

STATUS: indicates success or failure of an operation

HANDLE: returns a file handle upon success

DATA: returns data upon success

ATTRS: returns file attributes upon success

There is also an extension mechanism for arbitrary vendor-specific extensions. The extensions that are supported are negotiated using the INIT and VERSION packets.

EXTENDED: sends a vendor-specific request from client to server

EXTENDED_REPLY: sends a vendor-specific response from server to client.

```


Java SSH Client 对 SFTP的支持情况

|SFTP Protocol| JSch       | sshj               | ganymed-ssh2 | j2ssh |
|-------------|------------|--------------------|--------------|-------|
|Java Class   | com.jcraft.jsch.ChannelSftp |net.schmizz.sshj.sftp.SFTPClient | ch.ethz.ssh2.SFTPv3Client |       |
|INIT         | 创建ChannelSftp时自动发起 |创建 SFTPClient时自动发起|创建 SFTPClient时自动发起||
|VERSION      | #version() | #version()         | #getProtocolVersion()||
|OPEN         | #get()     | #open()            | #openFileRO(), #openFileRW()||
|CLOSE        |            | RemoteFile#close() | #closeFile() ||
|READ         | #get()     | RemoteFile#read()  | #read()      ||
|WRITE        | #put()     | RemoteFile#write() | #write()     ||
|OPENDIR      |            | #open()            | #openFileRO()||
|READDIR      | #ls()      | #ls()              | #ls()        ||
|MKDIR        | #mkdir()   | #mkdir()           | #mkdir()     ||
|RMDIR        | #rmdir()   | #rmdir()           | #rmdir()     ||
|REMOVE       | #rm()      | #rm()              | #rm()        ||
|RENAME       | #rename()  | #rename()          | #mv()        ||
|STAT         | #stat()    | #stat()            | #stat()      ||
|LSTAT        | #lstat()   | #lstat()           | #lstat()     ||
|FSTAT        |            | RemoteFile#fetchAttributes()| #fstat()     ||
|SETSTAT      | #setStat() | #setAttributes()   | #setstat()   ||
|FSETSTAT     |            | RemoteFile#setAttributes()| #fsetstat()  ||
|READLINK     | #readlink()| #readlink()        | #readLink()  ||
|SYMLINK      | #symlink() | #symlink()         | #createSymlink()||
|REALPATH     | #realpath()| #canonicalize()    | #canonicalPath()||


Java SSH Client 对 SFTP的支持的额外功能：

|SFTP Protocol| JSch       | sshj | ganymed-ssh2 | j2ssh |
|-------------|------------|------|--------------|-------|
|chgrp        | #chgrp()| #chgrp()|||
|chown        | #chown()| #chown()|||
|chmod        | #chmod()| #chmod()|||
|mtime        | |#mtime()|||
|atime        | |#atime()|||
|size         | SftpATTRS#getSize()| #size()|||


推荐选择顺序：sshj > trilead-ssh2 > jsch 
sshtools 下的推荐：synergy


4、SCP

SCP 是Linux里一个的 command program， 它是基于sftp 协议的一个程序。用于提供文件在多个机器上的copy。


|SCP     | JSch             |   sshj             |  ganymed-ssh2 | j2ssh |
|--------|------------------|--------------------|---------------|-------|
|put     | ChannelSftp#put()| SFTPClient#put()   |SCPClient#put()|       |
|get     | ChannelSftp#get()| SFTPClient#get()   |SCPClient#get()|       |


经过整合后，建议的做法是：Sftps#copy, Sftps.reverseCopy















