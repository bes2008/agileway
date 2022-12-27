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
import com.jn.langx.util.Objs;
/**
 * Key/value pair representing a dimension of a meter used to classify and drill into
 * measurements.
 */
public class Tag implements Comparable<Tag> {

    private final String key;

    private final String value;

    public Tag(String key, String value) {
        Objs.requireNonNull(key);
        Objs.requireNonNull(value);
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Tag that = (Tag) o;
        return Objs.equals(key, that.getKey()) && Objs.equals(value, that.getValue());
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return key + " = " + value;
    }

    @Override
    public int compareTo(Tag o) {
        return getKey().compareTo(o.getKey());
    }

}
