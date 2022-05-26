package com.jn.agileway.kafka.consumer;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.struct.pair.NameValuePair;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class KafkaConsumerRef<K, V> extends NameValuePair<Consumer<K, V>> implements Consumer<K, V> {
    private Consumer<K, V> consumer;
    private TopicSubscribeContext subscribeContext;
    private long lastSubscribeTime = -1L;

    public boolean subscriptionModified() {
        return this.subscribeContext.getExpectedTopicsLastModified() > this.lastSubscribeTime;
    }

    public TopicSubscribeContext getSubscribeContext() {
        return subscribeContext;
    }

    public KafkaConsumerRef(@NonNull Consumer<K, V> consumer) {
        this.consumer = consumer;
        this.subscribeContext = new TopicSubscribeContext();
        afterSubscribe(false);
    }

    @Override
    public Set<TopicPartition> assignment() {
        return consumer.assignment();
    }

    @Override
    public Set<String> subscription() {
        return consumer.subscription();
    }

    @Override
    public void subscribe(Collection<String> topics) {
        consumer.subscribe(topics);
    }

    private void afterSubscribe() {
        afterSubscribe(true);
    }

    private void afterSubscribe(boolean updateTime) {
        if (updateTime) {
            this.lastSubscribeTime = System.currentTimeMillis();
        }
        this.subscribeContext.replaceTopics(consumer.subscription());
    }

    @Override
    public void subscribe(Collection<String> topics, ConsumerRebalanceListener callback) {
        consumer.subscribe(topics, callback);
        afterSubscribe();
    }

    @Override
    public void assign(Collection<TopicPartition> partitions) {
        consumer.assign(partitions);
    }

    @Override
    public void subscribe(Pattern pattern, ConsumerRebalanceListener callback) {
        consumer.subscribe(pattern, callback);
        afterSubscribe();
    }

    @Override
    public void subscribe(Pattern pattern) {
        consumer.subscribe(pattern);
        afterSubscribe();
    }

    @Override
    public void unsubscribe() {
        consumer.unsubscribe();
        afterSubscribe();
    }


    @Override
    @Deprecated
    public ConsumerRecords<K, V> poll(long timeout) {
        return consumer.poll(timeout);
    }

    @Override
    public ConsumerRecords<K, V> poll(Duration timeout) {
        return consumer.poll(timeout);
    }

    @Override
    public void commitSync() {
        consumer.commitSync();
    }

    @Override
    public void commitSync(Duration timeout) {
        consumer.commitSync(timeout);
    }

    @Override
    public void commitSync(Map<TopicPartition, OffsetAndMetadata> offsets) {
        consumer.commitSync(offsets);
    }

    @Override
    public void commitSync(Map<TopicPartition, OffsetAndMetadata> offsets, Duration timeout) {
        consumer.commitSync(offsets, timeout);
    }

    @Override
    public void commitAsync() {
        consumer.commitAsync();
    }

    @Override
    public void commitAsync(OffsetCommitCallback callback) {
        consumer.commitAsync(callback);
    }

    @Override
    public void commitAsync(Map<TopicPartition, OffsetAndMetadata> offsets, OffsetCommitCallback callback) {
        consumer.commitAsync(offsets, callback);
    }

    @Override
    public void seek(TopicPartition partition, long offset) {
        consumer.seek(partition, offset);
    }

    @Override
    public void seek(TopicPartition partition, OffsetAndMetadata offsetAndMetadata) {
        consumer.seek(partition, offsetAndMetadata);
    }

    @Override
    public void seekToBeginning(Collection<TopicPartition> partitions) {
        consumer.seekToBeginning(partitions);
    }

    @Override
    public void seekToEnd(Collection<TopicPartition> partitions) {
        consumer.seekToEnd(partitions);
    }

    @Override
    public long position(TopicPartition partition) {
        return consumer.position(partition);
    }

    @Override
    public long position(TopicPartition partition, Duration timeout) {
        return consumer.position(partition, timeout);
    }

    @Override
    @Deprecated
    public OffsetAndMetadata committed(TopicPartition partition) {
        return consumer.committed(partition);
    }

    @Override
    @Deprecated
    public OffsetAndMetadata committed(TopicPartition partition, Duration timeout) {
        return consumer.committed(partition, timeout);
    }

    @Override
    public Map<TopicPartition, OffsetAndMetadata> committed(Set<TopicPartition> partitions) {
        return consumer.committed(partitions);
    }

    @Override
    public Map<TopicPartition, OffsetAndMetadata> committed(Set<TopicPartition> partitions, Duration timeout) {
        return consumer.committed(partitions, timeout);
    }

    @Override
    public Map<MetricName, ? extends Metric> metrics() {
        return consumer.metrics();
    }

    @Override
    public List<PartitionInfo> partitionsFor(String topic) {
        return consumer.partitionsFor(topic);
    }

    @Override
    public List<PartitionInfo> partitionsFor(String topic, Duration timeout) {
        return consumer.partitionsFor(topic, timeout);
    }

    @Override
    public Map<String, List<PartitionInfo>> listTopics() {
        return consumer.listTopics();
    }

    @Override
    public Map<String, List<PartitionInfo>> listTopics(Duration timeout) {
        return consumer.listTopics(timeout);
    }

    @Override
    public Set<TopicPartition> paused() {
        return consumer.paused();
    }

    @Override
    public void pause(Collection<TopicPartition> partitions) {
        consumer.pause(partitions);
    }

    @Override
    public void resume(Collection<TopicPartition> partitions) {
        consumer.resume(partitions);
    }

    @Override
    public Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes(Map<TopicPartition, Long> timestampsToSearch) {
        return consumer.offsetsForTimes(timestampsToSearch);
    }

    @Override
    public Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes(Map<TopicPartition, Long> timestampsToSearch, Duration timeout) {
        return consumer.offsetsForTimes(timestampsToSearch, timeout);
    }

    @Override
    public Map<TopicPartition, Long> beginningOffsets(Collection<TopicPartition> partitions) {
        return consumer.beginningOffsets(partitions);
    }

    @Override
    public Map<TopicPartition, Long> beginningOffsets(Collection<TopicPartition> partitions, Duration timeout) {
        return consumer.beginningOffsets(partitions, timeout);
    }

    @Override
    public Map<TopicPartition, Long> endOffsets(Collection<TopicPartition> partitions) {
        return consumer.endOffsets(partitions);
    }

    @Override
    public Map<TopicPartition, Long> endOffsets(Collection<TopicPartition> partitions, Duration timeout) {
        return consumer.endOffsets(partitions, timeout);
    }

    @Override
    public void close() {
        consumer.close();
    }

    @Override
    @Deprecated
    public void close(long timeout, TimeUnit unit) {
        consumer.close(timeout, unit);
    }

    @Override
    public void close(Duration timeout) {
        consumer.close(timeout);
    }

    @Override
    public void wakeup() {
        consumer.wakeup();
    }
}
