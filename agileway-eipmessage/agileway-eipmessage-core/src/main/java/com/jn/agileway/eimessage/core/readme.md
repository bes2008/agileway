# 常见通信模式

## 1、PubSub 模式 （event message常用）

Endpoint (Producer) -----> messageMapper -----> producer channel -----> router -----> buffer (queue, topic)
----->  consumer channel -----> messageMapper -----> Endpoint (Consumer)

常用单工通道, producer 走 outbound channel, consumer 走 inbound channel

## 2、Exchange 模式 （request reply message常用）

Endpoint (Requester) -----> messageMapper -----> sender channel -----> router -----> network , broker channel (queue,
topic) -----> request receiver -----> receiver channel -----> Endpoint (Replier)
Endpoint (Requester) <----- messageMapper <----- receiver channel <----- router <----- network , broker channel (queue,
topic) <----- reply sender     <----- send channel     <----- Endpoint (Replier)

常用 双工通道, request message 走 outbound channel, reply message 走 inbound channel

## 关键术语解释

## Endpoint

在 pub-sub 通信模式下， endpoint 代表了 producer (s), consumer (s)

在 exchange 模式下， endpoint 代表了 requester(s) (client), replier(s) (server)

## Endpoint 处理模型: MessageMapper, MessageDispatcher

### MessageMapper

如果message 在进入 outbound 之前， message 可以是任何形态、格式 的话，通常要先使用 message mapper将 任意格式的 message 转成 统一格式的message。 如果message 在离开
inbound 之前， message 必然是统一格式，通常要先使用 message mapper将 统一格式的message 转换成期望的格式的 message。

### MessageDispatcher

endpoint 常用的处理模型为dispatcher

## Channel

channel 是为了方便对message的处理，简历的一个处理通道而已

channel 按照流向分为2种： inbound channel, outbound channel, 如果将它俩结合，可以衍生出一种： duplex channel 一个channel 必有 两端: src,dest ,channel的

两端均可以有多种形式： 可以是另外一个或多个 channel, buffer (queue, topic)，所以会有point-to-point, pub-sub 


## Channel 处理模型：Pipeline, Chain

channel 上常用的处理模型为：pipeline, chain, 不论是 pipeline还是 chain，它们上面都有很多的message handler，并且 这些handler是串行执行。

### Pipeline 
Pipeline 上每一个 message handler 的输出将作为下一个 message handler的输入

### chain
chain 上每一个 message handler 都将接收到同一个 message


## MessageHandler


