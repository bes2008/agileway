# agileway-dmmq (Disruptor Memory Message Queue)

## 术语
+ Topic: 消息主题
+ Producer: 生产者，push消息到Topic
+ Consumer： 消费者，从topic中pull消息
+ MessageTopicDispatcher： 赋值对topic管理、调度， consumer订阅等等

## Producer, Topic, Consumer 关系
1. 消息流转：producer -> topic -> consumer
2. 一个producer可以push消息到多个topic, 一个topic可以接收多个producer发的消息
3. 一个consumer可以从多个topic拉取消息， 一个topic的消息可以被多个消费者重复的消费
4. 一个consumer可以依赖于从同一topic拉取消息的多个其他的consumer