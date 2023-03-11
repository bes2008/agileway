package com.jn.agileway.protocol.syslog.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.jcustenborder.netty.syslog.Message;

import javax.annotation.Generated;
import java.util.*;

/**
 * Immutable implementation of {@link Message.StructuredData}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableStructuredData.builder()}.
 */
@SuppressWarnings({"all"})
@Generated({"Immutables.generator", "Message.StructuredData"})
final class ImmutableStructuredData
    implements Message.StructuredData {
  private final String id;
  private final Map<String, String> structuredDataElements;

  private ImmutableStructuredData(String id, Map<String, String> structuredDataElements) {
    this.id = id;
    this.structuredDataElements = structuredDataElements;
  }

  /**
   * @return The value of the {@code id} attribute
   */
  @JsonProperty("id")
  @Override
  public String id() {
    return id;
  }

  /**
   * @return The value of the {@code structuredDataElements} attribute
   */
  @JsonProperty("structuredDataElements")
  @Override
  public Map<String, String> structuredDataElements() {
    return structuredDataElements;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link Message.StructuredData#id() id} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for id
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableStructuredData withId(String value) {
    if (this.id.equals(value)) return this;
    String newValue = Objects.requireNonNull(value, "id");
    return new ImmutableStructuredData(newValue, this.structuredDataElements);
  }

  /**
   * Copy the current immutable object by replacing the {@link Message.StructuredData#structuredDataElements() structuredDataElements} map with the specified map.
   * Nulls are not permitted as keys or values.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param entries The entries to be added to the structuredDataElements map
   * @return A modified copy of {@code this} object
   */
  public final ImmutableStructuredData withStructuredDataElements(Map<String, ? extends String> entries) {
    if (this.structuredDataElements == entries) return this;
    Map<String, String> newValue = createUnmodifiableMap(true, false, entries);
    return new ImmutableStructuredData(this.id, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableStructuredData} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    return another instanceof ImmutableStructuredData
        && equalTo((ImmutableStructuredData) another);
  }

  private boolean equalTo(ImmutableStructuredData another) {
    return id.equals(another.id)
        && structuredDataElements.equals(another.structuredDataElements);
  }

  /**
   * Computes a hash code from attributes: {@code id}, {@code structuredDataElements}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + id.hashCode();
    h += (h << 5) + structuredDataElements.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code StructuredData} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return "StructuredData{"
        + "id=" + id
        + ", structuredDataElements=" + structuredDataElements
        + "}";
  }

  /**
   * Utility type used to correctly read immutable object from JSON representation.
   * @deprecated Do not use this type directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonDeserialize
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
  static final class Json implements Message.StructuredData {
    String id;
    Map<String, String> structuredDataElements;
    @JsonProperty("id")
    public void setId(String id) {
      this.id = id;
    }
    @JsonProperty("structuredDataElements")
    public void setStructuredDataElements(Map<String, String> structuredDataElements) {
      this.structuredDataElements = structuredDataElements;
    }
    @Override
    public String id() { throw new UnsupportedOperationException(); }
    @Override
    public Map<String, String> structuredDataElements() { throw new UnsupportedOperationException(); }
  }

  /**
   * @param json A JSON-bindable data structure
   * @return An immutable value type
   * @deprecated Do not use this method directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  static ImmutableStructuredData fromJson(Json json) {
    ImmutableStructuredData.Builder builder = ImmutableStructuredData.builder();
    if (json.id != null) {
      builder.id(json.id);
    }
    if (json.structuredDataElements != null) {
      builder.putAllStructuredDataElements(json.structuredDataElements);
    }
    return builder.build();
  }

  /**
   * Creates an immutable copy of a {@link Message.StructuredData} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable StructuredData instance
   */
  public static ImmutableStructuredData copyOf(Message.StructuredData instance) {
    if (instance instanceof ImmutableStructuredData) {
      return (ImmutableStructuredData) instance;
    }
    return ImmutableStructuredData.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableStructuredData ImmutableStructuredData}.
   * @return A new ImmutableStructuredData builder
   */
  public static ImmutableStructuredData.Builder builder() {
    return new ImmutableStructuredData.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableStructuredData ImmutableStructuredData}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  public static final class Builder {
    private static final long INIT_BIT_ID = 0x1L;
    private long initBits = 0x1L;

    private String id;
    private Map<String, String> structuredDataElements = new LinkedHashMap<String, String>();

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code StructuredData} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * Collection elements and entries will be added, not replaced.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(Message.StructuredData instance) {
      Objects.requireNonNull(instance, "instance");
      id(instance.id());
      putAllStructuredDataElements(instance.structuredDataElements());
      return this;
    }

    /**
     * Initializes the value for the {@link Message.StructuredData#id() id} attribute.
     * @param id The value for id
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("id")
    public final Builder id(String id) {
      this.id = Objects.requireNonNull(id, "id");
      initBits &= ~INIT_BIT_ID;
      return this;
    }

    /**
     * Put one entry to the {@link Message.StructuredData#structuredDataElements() structuredDataElements} map.
     * @param key The key in the structuredDataElements map
     * @param value The associated value in the structuredDataElements map
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder putStructuredDataElements(String key, String value) {
      this.structuredDataElements.put(
          Objects.requireNonNull(key, "structuredDataElements key"),
          Objects.requireNonNull(value, "structuredDataElements value"));
      return this;
    }

    /**
     * Put one entry to the {@link Message.StructuredData#structuredDataElements() structuredDataElements} map. Nulls are not permitted
     * @param entry The key and value entry
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder putStructuredDataElements(Map.Entry<String, ? extends String> entry) {
      String k = entry.getKey();
      String v = entry.getValue();
      this.structuredDataElements.put(
          Objects.requireNonNull(k, "structuredDataElements key"),
          Objects.requireNonNull(v, "structuredDataElements value"));
      return this;
    }

    /**
     * Sets or replaces all mappings from the specified map as entries for the {@link Message.StructuredData#structuredDataElements() structuredDataElements} map. Nulls are not permitted
     * @param structuredDataElements The entries that will be added to the structuredDataElements map
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("structuredDataElements")
    public final Builder structuredDataElements(Map<String, ? extends String> structuredDataElements) {
      this.structuredDataElements.clear();
      return putAllStructuredDataElements(structuredDataElements);
    }

    /**
     * Put all mappings from the specified map as entries to {@link Message.StructuredData#structuredDataElements() structuredDataElements} map. Nulls are not permitted
     * @param structuredDataElements The entries that will be added to the structuredDataElements map
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder putAllStructuredDataElements(Map<String, ? extends String> structuredDataElements) {
      for (Map.Entry<String, ? extends String> entry : structuredDataElements.entrySet()) {
        String k = entry.getKey();
        String v = entry.getValue();
        this.structuredDataElements.put(
            Objects.requireNonNull(k, "structuredDataElements key"),
            Objects.requireNonNull(v, "structuredDataElements value"));
      }
      return this;
    }

    /**
     * Builds a new {@link ImmutableStructuredData ImmutableStructuredData}.
     * @return An immutable instance of StructuredData
     * @throws IllegalStateException if any required attributes are missing
     */
    public ImmutableStructuredData build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableStructuredData(id, createUnmodifiableMap(false, false, structuredDataElements));
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = new ArrayList<String>();
      if ((initBits & INIT_BIT_ID) != 0) attributes.add("id");
      return "Cannot build StructuredData, some of required attributes are not set " + attributes;
    }
  }

  private static <K, V> Map<K, V> createUnmodifiableMap(boolean checkNulls, boolean skipNulls, Map<? extends K, ? extends V> map) {
    switch (map.size()) {
    case 0: return Collections.emptyMap();
    case 1: {
      Map.Entry<? extends K, ? extends V> e = map.entrySet().iterator().next();
      K k = e.getKey();
      V v = e.getValue();
      if (checkNulls) {
        Objects.requireNonNull(k, "key");
        Objects.requireNonNull(v, "value");
      }
      if (skipNulls && (k == null || v == null)) {
        return Collections.emptyMap();
      }
      return Collections.singletonMap(k, v);
    }
    default: {
      Map<K, V> linkedMap = new LinkedHashMap<K, V>(map.size());
      if (skipNulls || checkNulls) {
        for (Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
          K k = e.getKey();
          V v = e.getValue();
          if (skipNulls) {
            if (k == null || v == null) continue;
          } else if (checkNulls) {
            Objects.requireNonNull(k, "key");
            Objects.requireNonNull(v, "value");
          }
          linkedMap.put(k, v);
        }
      } else {
        linkedMap.putAll(map);
      }
      return Collections.unmodifiableMap(linkedMap);
    }
    }
  }
}
