package com.jn.agileway.eipchannel.topic.consumer;

import com.jn.langx.event.DomainEvent;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.regexp.Regexp;
import com.jn.langx.util.regexp.Regexps;

import java.util.List;
import java.util.Map;

/**
 * source 为 topic group name
 */
public class TopicSubscribeEvent extends DomainEvent<String> {
    private TopicSubscribeEventType type;

    /**
     * 每一个元素的格式：{clusterAddress}/{topic1},{topic2}
     */
    private List<String> topicUrls;

    public TopicSubscribeEvent() {
        super();
    }

    public TopicSubscribeEvent(String eventDomain, TopicSubscribeEventType type, String source) {
        super(eventDomain, source);
        this.setType(type);
    }

    public void addTopics(String cluster, List<String> topics) {
        if (topicUrls == null) {
            this.topicUrls = Collects.emptyArrayList();
        }
        topicUrls.add(cluster + "/" + Strings.join(",", topics));
    }

    public void setType(TopicSubscribeEventType type) {
        this.type = type;
    }

    public TopicSubscribeEventType getType() {
        return type;
    }

    public List<String> getTopicUrls() {
        return topicUrls;
    }

    public List<TopicURLComponents> getTopicComponents() {
        return Pipeline.of(this.topicUrls)
                .map(new Function<String, TopicURLComponents>() {
                    @Override
                    public TopicURLComponents apply(String topicUrl) {
                        return TopicURLComponents.extractUrl(topicUrl);
                    }
                }).clearNulls().asList();
    }

    public void setTopicUrls(List<String> topicUrls) {
        this.topicUrls = topicUrls;
    }

    public static class TopicURLComponents {
        private static Regexp URL_REGEXP = Regexps.createRegexp("(?<cluster>.*)/(?<topics>.*)");
        private String cluster;
        private List<String> topics;

        public String getCluster() {
            return cluster;
        }

        public void setCluster(String cluster) {
            this.cluster = cluster;
        }

        public List<String> getTopics() {
            return topics;
        }

        public void setTopics(List<String> topics) {
            this.topics = topics;
        }

        public static TopicURLComponents extractUrl(String topicURL) {
            TopicURLComponents components = null;
            if (Emptys.isNotEmpty(topicURL)) {
                Map<String, String> groups = Regexps.findNamedGroup(URL_REGEXP, topicURL);
                if (groups != null) {
                    String cluster = groups.get("cluster");
                    String topicsString = groups.get("topics");
                    if (Emptys.isNotEmpty(topicsString)) {
                        List<String> topics = Collects.asList(Strings.split(topicsString, ","));
                        if (Emptys.isNoneEmpty(topics, cluster)) {
                            components = new TopicURLComponents();
                            components.cluster = cluster;
                            components.topics = topics;
                        }
                    }
                }
            }
            return components;
        }

    }

}
