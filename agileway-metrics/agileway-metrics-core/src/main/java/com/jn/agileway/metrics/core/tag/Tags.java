package com.jn.agileway.metrics.core.tag;

import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer2;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Tags {
    private Tags() {
    }

    public static Tag of(String key, String value) {
        return new ImmutableTag(key, value);
    }

    /**
     * Return a new {@code Tags} instance by concatenating the specified collections of
     * tags.
     *
     * @param tags      the first set of tags
     * @param otherTags the second set of tags
     * @return the merged tags
     */
    public static TagList concat(@Nullable Iterable<? extends Tag> tags, @Nullable Iterable<? extends Tag> otherTags) {
        return listOf(tags).and(otherTags);
    }

    /**
     * Return a new {@code Tags} instance by concatenating the specified tags and
     * key/value pairs.
     *
     * @param tags      the first set of tags
     * @param keyValues the additional key/value pairs to add
     * @return the merged tags
     */
    public static TagList concat(@Nullable Iterable<? extends Tag> tags, @Nullable String... keyValues) {
        return listOf(tags).and(keyValues);
    }

    /**
     * Return a new {@code Tags} instance containing tags constructed from the specified
     * source tags.
     *
     * @param tags the tags to add
     * @return a new {@code Tags} instance
     */
    public static TagList listOf(@Nullable Iterable<? extends Tag> tags) {
        if (tags == null || tags == TagList.EMPTY || !tags.iterator().hasNext()) {
            return TagList.EMPTY;
        } else if (tags instanceof TagList) {
            return (TagList) tags;
        } else if (tags instanceof Collection) {
            Collection<? extends Tag> tagsCollection = (Collection<? extends Tag>) tags;
            return new TagList(tagsCollection.toArray(new Tag[0]));
        } else {
            return new TagList(Pipeline.<Tag>of(tags).clearNulls().distinct().asList());
        }
    }

    /**
     * Return a new {@code Tags} instance containing tags constructed from the specified
     * key/value pair.
     *
     * @param key   the tag key to add
     * @param value the tag value to add
     * @return a new {@code Tags} instance
     */
    public static TagList listOf(String key, String value) {
        return new TagList(new Tag[]{Tags.of(key, value)});
    }

    public static TagList listOf(Map<String, String> tagMap) {
        final List<Tag> list = Collects.newArrayList();
        Collects.forEach(tagMap, new Consumer2<String, String>() {
            @Override
            public void accept(String key, String value) {
                list.add(of(key, value));
            }
        });
        return listOf(list);
    }

    /**
     * Return a new {@code Tags} instance containing tags constructed from the specified
     * key/value pairs.
     *
     * @param keyValues the key/value pairs to add
     * @return a new {@code Tags} instance
     */
    public static TagList listOf(@Nullable String... keyValues) {
        if (keyValues == null || keyValues.length == 0) {
            return TagList.EMPTY;
        }
        if (keyValues.length % 2 == 1) {
            throw new IllegalArgumentException("size must be even, it is a set of key=value pairs");
        }
        Tag[] tags = new Tag[keyValues.length / 2];
        for (int i = 0; i < keyValues.length; i += 2) {
            tags[i / 2] = Tags.of(keyValues[i], keyValues[i + 1]);
        }
        return new TagList(tags);
    }

    /**
     * Return a new {@code Tags} instance containing tags constructed from the specified
     * tags.
     *
     * @param tags the tags to add
     * @return a new {@code Tags} instance
     */
    public static TagList listOf(@Nullable Tag... tags) {
        return new TagList(tags);
    }


}
