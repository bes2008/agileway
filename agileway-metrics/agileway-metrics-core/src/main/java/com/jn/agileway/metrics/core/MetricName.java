package com.jn.agileway.metrics.core;

import com.jn.agileway.metrics.core.tag.TagList;
import com.jn.agileway.metrics.core.tag.Tags;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;

import java.util.*;

/**
 * A metric name with the ability to include semantic tags.
 * <p>
 * This replaces the previous style where metric names where strictly
 * dot-separated strings.
 *
 * @since 4.1.0
 */
public class MetricName implements Comparable<MetricName> {

    /**
     * enum的数值不能为负数，且不能太大
     */
    public static enum MetricLevel {

        TRIVIAL, // 轻微指标

        MINOR,   // 次要指标

        NORMAL,  // 一般指标

        MAJOR,   // 重要指标

        CRITICAL; // 关键指标

        public static int getMaxValue() {
            MetricLevel[] levels = MetricLevel.values();
            int max = levels[0].ordinal();
            for (MetricLevel level : levels) {
                int value = level.ordinal();
                if (value > max) {
                    max = value;
                }
            }
            return max;
        }
    }

    public static final String SEPARATOR = ".";
    public static final Map<String, String> EMPTY_TAGS = Collections.emptyMap();
    public static final MetricName EMPTY = new MetricName();

    private final String key;
    private final TagList tags;
    // the level to indicate the importance of a metric
    private MetricLevel level;

    private int hashCode = 0;

    private boolean hashCodeCached = false;

    public MetricName() {
        this(null, (Map<String, String>) null, null);
    }

    public MetricName(String key) {
        this(key, (Map<String, String>) null, null);
    }

    public MetricName(String key, Map<String, String> tags) {
        this(key, tags, null);
    }

    public MetricName(String key, MetricLevel level) {
        this(key, (Map<String, String>) null, level);
    }

    public MetricName(String key, Map<String, String> tags, MetricLevel level) {
        this(key, Tags.listOf(tags), level);
    }

    public MetricName(String key, TagList tags, MetricLevel level) {
        this.key = key;
        this.tags = Tags.listOf(tags);
        this.level = level == null ? MetricLevel.NORMAL : level;
    }

    /**
     * Join the specified set of metric names.
     *
     * @param parts Multiple metric names to join using the separator.
     * @return A newly created metric name which has the name of the specified
     * parts and includes all tags of all child metric names.
     **/
    public static MetricName join(MetricName... parts) {
        final StringBuilder nameBuilder = new StringBuilder();
        final Map<String, String> tags = new HashMap<String, String>();

        boolean first = true;
        MetricName firstName = null;

        for (MetricName part : parts) {
            final String name = part.getKey();

            if (name != null && !name.isEmpty()) {
                if (first) {
                    first = false;
                    firstName = part;
                } else {
                    nameBuilder.append(SEPARATOR);
                }

                nameBuilder.append(name);
            }

            if (!part.getTags().isEmpty())
                tags.putAll(part.getTagsAsMap());
        }

        MetricLevel level = firstName == null ? null : firstName.getMetricLevel();
        return new MetricName(nameBuilder.toString(), tags, level);
    }

    /**
     * Build a new metric name using the specific path components.
     *
     * @param parts Path of the new metric name.
     * @return A newly created metric name with the specified path.
     **/
    public static MetricName build(String... parts) {
        if (parts == null || parts.length == 0)
            return MetricName.EMPTY;

        if (parts.length == 1)
            return new MetricName(parts[0], EMPTY_TAGS);

        return new MetricName(buildName(parts), EMPTY_TAGS);
    }

    private static String buildName(String... names) {
        final StringBuilder builder = new StringBuilder();
        boolean first = true;

        for (String name : names) {
            if (name == null || name.isEmpty())
                continue;

            if (first) {
                first = false;
            } else {
                builder.append(SEPARATOR);
            }

            builder.append(name);
        }

        return builder.toString();
    }


    public String getKey() {
        return key;
    }

    public TagList getTags() {
        return tags;
    }

    public Map<String,String> getTagsAsMap(){
        if( Objs.isEmpty(this.tags)){
            return Collects.emptyHashMap(true);
        }
        return this.tags.asMap();
    }

    /**
     * Return the level of this metric
     * The level indicates the importance of the metric
     *
     * @return when level tag do not exist or illegal tag, will return null.
     */
    public MetricLevel getMetricLevel() {
        return level;
    }

    /**
     * Metric level can be changed during runtime
     *
     * @param level the level to set
     */
    public MetricName level(MetricLevel level) {
        this.level = level;
        return this;
    }

    public MetricName resolve(String p) {
        return resolve(p, true);
    }

    /**
     * Build the MetricName that is this with another path appended to it.
     * <p>
     * The new MetricName inherits the tags of this one.
     *
     * @param p           The extra path element to add to the new metric.
     * @param inheritTags if true, tags will be inherited
     * @return A new metric name relative to the original by the path specified
     * in p.
     */
    public MetricName resolve(String p, boolean inheritTags) {
        final String next;

        if (p != null && !p.isEmpty()) {
            if (key != null && !key.isEmpty()) {
                next = key + SEPARATOR + p;
            } else {
                next = p;
            }
        } else {
            next = this.key;
        }

        return inheritTags ? new MetricName(next, tags, level) : new MetricName(next, level);
    }


    /**
     * Add tags to a metric name and return the newly created MetricName.
     *
     * @param add Tags to add.
     * @return A newly created metric name with the specified tags associated with it.
     */
    public MetricName tags(Map<String, String> add) {
        final Map<String, String> tags = new HashMap<String, String>(add);
        tags.putAll(this.getTagsAsMap());
        return new MetricName(key, tags, level);
    }

    public MetricName tag(String tagKey, String tagValue) {
        return tags(tagKey, tagValue);
    }

    /**
     * Same as {@link #tags(Map)}, but takes a variadic list
     * of arguments.
     *
     * @param pairs An even list of strings acting as key-value pairs.
     * @return A newly created metric name with the specified tags associated
     * with it.
     * @see #tags(Map)
     */
    public MetricName tags(String... pairs) {
        if (pairs == null) {
            return this;
        }
        int len = pairs.length;
        if (pairs.length % 2 != 0) {
            len = pairs.length - 1;
        }
        if (len <= 0) {
            return this;
        }

        final Map<String, String> add = new HashMap<String, String>();

        for (int i = 0; i < len; i += 2) {
            add.put(pairs[i], pairs[i + 1]);
        }

        return tags(add);
    }

    @Override
    public String toString() {
        if (tags.isEmpty()) {
            return key;
        }

        return key + tags;
    }

    @Override
    public int hashCode() {

        if (!hashCodeCached) {
            hashCode = Objs.hash(key, tags);
            ;
            hashCodeCached = true;
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        MetricName other = (MetricName) obj;

        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;

        if (!tags.equals(other.tags))
            return false;

        return true;
    }

    @Override
    public int compareTo(MetricName o) {
        if (o == null)
            return -1;

        int c = compareName(key, o.getKey());

        if (c != 0)
            return c;

        return compareTags(this.getTagsAsMap(), o.getTagsAsMap());
    }

    private int compareName(String left, String right) {
        if (left == null && right == null)
            return 0;

        if (left == null)
            return 1;

        if (right == null)
            return -1;

        return left.compareTo(right);
    }

    private int compareTags(Map<String, String> left, Map<String, String> right) {
        if (left == null && right == null)
            return 0;

        if (left == null)
            return 1;

        if (right == null)
            return -1;

        final Iterable<String> keys = uniqueSortedKeys(left, right);

        for (final String key : keys) {
            final String a = left.get(key);
            final String b = right.get(key);

            if (a == null && b == null)
                continue;

            if (a == null)
                return -1;

            if (b == null)
                return 1;

            int c = a.compareTo(b);

            if (c != 0)
                return c;
        }

        return 0;
    }

    private Iterable<String> uniqueSortedKeys(Map<String, String> left, Map<String, String> right) {
        final Set<String> set = new TreeSet<String>(left.keySet());
        set.addAll(right.keySet());
        return set;
    }
}
