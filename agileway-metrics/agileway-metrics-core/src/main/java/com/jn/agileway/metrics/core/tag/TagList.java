/*
 * Copyright 2017 VMware, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jn.agileway.metrics.core.tag;


import com.jn.langx.annotation.Nullable;
import com.jn.langx.text.StringJoiner;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Function;

import java.util.*;


/**
 * An immutable collection of {@link Tag Tags} that are guaranteed to be sorted and
 * deduplicated by tag key.
 */
public final class TagList implements Iterable<Tag> {

    static final TagList EMPTY = new TagList(new Tag[]{});

    private final Tag[] tags;

    private int last;

    public TagList(List<Tag> tags) {
        this(Collects.asArray(tags, Tag.class));
    }
    public TagList() {
        this.tags = new ImmutableTag[0];
    }
    /**
     * Return a {@code Tags} instance that contains no elements.
     *
     * @return an empty {@code Tags} instance
     */
    public TagList(Tag[] tags) {
        this.tags = tags;
        Arrays.sort(this.tags);
        dedup();
    }

    private void dedup() {
        int n = tags.length;

        if (n == 0 || n == 1) {
            last = n;
            return;
        }

        // index of next unique element
        int j = 0;

        for (int i = 0; i < n - 1; i++)
            if (!tags[i].getKey().equals(tags[i + 1].getKey()))
                tags[j++] = tags[i];

        tags[j++] = tags[n - 1];
        last = j;
    }

    /**
     * Return a new {@code Tags} instance by merging this collection and the specified
     * key/value pair.
     *
     * @param key   the tag key to add
     * @param value the tag value to add
     * @return a new {@code Tags} instance
     */
    public TagList and(String key, String value) {
        return and(Tags.of(key, value));
    }

    /**
     * Return a new {@code Tags} instance by merging this collection and the specified
     * key/value pairs.
     *
     * @param keyValues the key/value pairs to add
     * @return a new {@code Tags} instance
     */
    public TagList and(@Nullable String... keyValues) {
        if (keyValues == null || keyValues.length == 0) {
            return this;
        }
        return and(Tags.listOf(keyValues));
    }

    /**
     * Return a new {@code Tags} instance by merging this collection and the specified
     * tags.
     *
     * @param tags the tags to add
     * @return a new {@code Tags} instance
     */
    public TagList and(@Nullable Tag... tags) {
        if (tags == null || tags.length == 0) {
            return this;
        }
        Tag[] newTags = new Tag[last + tags.length];
        System.arraycopy(this.tags, 0, newTags, 0, last);
        System.arraycopy(tags, 0, newTags, last, tags.length);
        return new TagList(newTags);
    }

    /**
     * Return a new {@code Tags} instance by merging this collection and the specified
     * tags.
     *
     * @param tags the tags to add
     * @return a new {@code Tags} instance
     */
    public TagList and(@Nullable Iterable<? extends Tag> tags) {
        if (tags == null || tags == EMPTY || !tags.iterator().hasNext()) {
            return this;
        }

        if (this.tags.length == 0) {
            return Tags.listOf(tags);
        }

        return and(Tags.listOf(tags).tags);
    }

    @Override
    public Iterator<Tag> iterator() {
        return new ArrayIterator();
    }


    public Map<String,String> asMap(){
        final Map<String, String> map = new LinkedHashMap<String,String>();
        Pipeline.of(this.tags).forEach(new Consumer2<Integer, Tag>() {
            @Override
            public void accept(Integer integer, Tag tag) {
                map.put(tag.getKey(), tag.getValue());
            }
        });
        return map;
    }

    public List<String> getKeys(){
        return Pipeline.of(this.tags).map(new Function<Tag, String>() {
            @Override
            public String apply(Tag tag) {
                return tag.getKey();
            }
        }).asList();
    }

    public List<String> getValues(){
        return Pipeline.of(this.tags).map(new Function<Tag, String>() {
            @Override
            public String apply(Tag tag) {
                return tag.getValue();
            }
        }).asList();
    }

    public int getLast() {
        return last;
    }

    private class ArrayIterator implements Iterator<Tag> {

        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < last;
        }

        @Override
        public Tag next() {
            return tags[currentIndex++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("cannot remove items from tags");
        }

    }

    public boolean isEmpty() {
        return tags==null || tags.length == 0;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < last; i++) {
            result = 31 * result + tags[i].hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass() && tagsEqual((TagList) obj);
    }

    private boolean tagsEqual(TagList obj) {
        if (tags == obj.tags)
            return true;

        if (last != obj.last)
            return false;

        for (int i = 0; i < last; i++) {
            if (!tags[i].equals(obj.tags[i]))
                return false;
        }

        return true;
    }


    @Override
    public String toString() {
        return new StringJoiner(",", "[", "]").append(this.tags).toString();
    }

}
