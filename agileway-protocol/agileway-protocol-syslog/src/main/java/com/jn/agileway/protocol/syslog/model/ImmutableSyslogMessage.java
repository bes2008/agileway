package com.jn.agileway.protocol.syslog.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.jcustenborder.netty.syslog.Message;
import com.jn.agileway.protocol.syslog.MessageType;
import org.apache.avro.reflect.Nullable;

import javax.annotation.Generated;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Immutable implementation of {@link SyslogMessage}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableSyslogMessage.builder()}.
 */
@SuppressWarnings({"all"})
@Generated({"Immutables.generator", "SyslogMessage"})
public final class ImmutableSyslogMessage{
  private final LocalDateTime date;
  private final InetAddress remoteAddress;
  private final String rawMessage;
  private final MessageType type;
  private final @Nullable Integer level;
  private final @Nullable Integer version;
  private final @Nullable Integer facility;
  private final @Nullable String host;
  private final @Nullable String message;
  private final @Nullable String processId;
  private final @Nullable String tag;
  private final @Nullable String messageId;
  private final @Nullable String appName;
  private final @Nullable List<Message.StructuredData> structuredData;
  private final @Nullable String deviceVendor;
  private final @Nullable String deviceProduct;
  private final @Nullable String deviceVersion;
  private final @Nullable String deviceEventClassId;
  private final @Nullable String name;
  private final @Nullable String severity;
  private final @Nullable Map<String, String> extension;

  private ImmutableSyslogMessage(
      LocalDateTime date,
      InetAddress remoteAddress,
      String rawMessage,
      MessageType type,
      @Nullable Integer level,
      @Nullable Integer version,
      @Nullable Integer facility,
      @Nullable String host,
      @Nullable String message,
      @Nullable String processId,
      @Nullable String tag,
      @Nullable String messageId,
      @Nullable String appName,
      @Nullable List<Message.StructuredData> structuredData,
      @Nullable String deviceVendor,
      @Nullable String deviceProduct,
      @Nullable String deviceVersion,
      @Nullable String deviceEventClassId,
      @Nullable String name,
      @Nullable String severity,
      @Nullable Map<String, String> extension) {
    this.date = date;
    this.remoteAddress = remoteAddress;
    this.rawMessage = rawMessage;
    this.type = type;
    this.level = level;
    this.version = version;
    this.facility = facility;
    this.host = host;
    this.message = message;
    this.processId = processId;
    this.tag = tag;
    this.messageId = messageId;
    this.appName = appName;
    this.structuredData = structuredData;
    this.deviceVendor = deviceVendor;
    this.deviceProduct = deviceProduct;
    this.deviceVersion = deviceVersion;
    this.deviceEventClassId = deviceEventClassId;
    this.name = name;
    this.severity = severity;
    this.extension = extension;
  }

  /**
   * Date of the message. This is the parsed date from the client.
   * @return Date of the message.
   */
  @JsonProperty(required = true)
  @Override
  public LocalDateTime date() {
    return date;
  }

  /**
   * IP Address for the sender of the message.
   * @return Sender IP Address.
   */
  @JsonProperty(required = true)
  @Override
  public InetAddress remoteAddress() {
    return remoteAddress;
  }

  /**
   * Unprocessed copy of the message.
   * @return Unprocessed message.
   */
  @JsonProperty(required = true)
  @Override
  public String rawMessage() {
    return rawMessage;
  }

  /**
   * @return
   */
  @JsonProperty(required = true)
  @Override
  public MessageType type() {
    return type;
  }

  /**
   * Level for the message. Parsed from the message.
   * @return Message Level
   */
  @JsonProperty("level")
  @Override
  public @Nullable Integer level() {
    return level;
  }

  /**
   * Version of the message.
   * @return Message version
   */
  @JsonProperty("version")
  @Override
  public @Nullable Integer version() {
    return version;
  }

  /**
   * Facility of the message.
   * @return Message facility.
   */
  @JsonProperty("facility")
  @Override
  public @Nullable Integer facility() {
    return facility;
  }

  /**
   * Host of the message. This is the value from the message.
   * @return Message host.
   */
  @JsonProperty("host")
  @Override
  public @Nullable String host() {
    return host;
  }

  /**
   * Message part of the overall syslog message.
   * @return Message part of the overall syslog message.
   */
  @JsonProperty("message")
  @Override
  public @Nullable String message() {
    return message;
  }

  /**
   * @return The value of the {@code processId} attribute
   */
  @JsonProperty("processId")
  @Override
  public @Nullable String processId() {
    return processId;
  }

  /**
   * @return The value of the {@code tag} attribute
   */
  @JsonProperty("tag")
  @Override
  public @Nullable String tag() {
    return tag;
  }

  /**
   * @return The value of the {@code messageId} attribute
   */
  @JsonProperty("messageId")
  @Override
  public @Nullable String messageId() {
    return messageId;
  }

  /**
   * @return The value of the {@code appName} attribute
   */
  @JsonProperty("appName")
  @Override
  public @Nullable String appName() {
    return appName;
  }

  /**
   * @return The value of the {@code structuredData} attribute
   */
  @JsonProperty("structuredData")
  @Override
  public @Nullable List<StructuredData> structuredData() {
    return structuredData;
  }

  /**
   * @return The value of the {@code deviceVendor} attribute
   */
  @JsonProperty("deviceVendor")
  @Override
  public @Nullable String deviceVendor() {
    return deviceVendor;
  }

  /**
   * @return The value of the {@code deviceProduct} attribute
   */
  @JsonProperty("deviceProduct")
  @Override
  public @Nullable String deviceProduct() {
    return deviceProduct;
  }

  /**
   * @return The value of the {@code deviceVersion} attribute
   */
  @JsonProperty("deviceVersion")
  @Override
  public @Nullable String deviceVersion() {
    return deviceVersion;
  }

  /**
   * @return The value of the {@code deviceEventClassId} attribute
   */
  @JsonProperty("deviceEventClassId")
  @Override
  public @Nullable String deviceEventClassId() {
    return deviceEventClassId;
  }

  /**
   * @return The value of the {@code name} attribute
   */
  @JsonProperty("name")
  @Override
  public @Nullable String name() {
    return name;
  }

  /**
   * @return The value of the {@code severity} attribute
   */
  @JsonProperty("severity")
  @Override
  public @Nullable String severity() {
    return severity;
  }

  /**
   * @return The value of the {@code extension} attribute
   */
  @JsonProperty("extension")
  @Override
  public @Nullable Map<String, String> extension() {
    return extension;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#date() date} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for date
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withDate(LocalDateTime value) {
    if (this.date == value) return this;
    LocalDateTime newValue = Objects.requireNonNull(value, "date");
    return new ImmutableSyslogMessage(
        newValue,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        this.level,
        this.version,
        this.facility,
        this.host,
        this.message,
        this.processId,
        this.tag,
        this.messageId,
        this.appName,
        this.structuredData,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#remoteAddress() remoteAddress} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for remoteAddress
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withRemoteAddress(InetAddress value) {
    if (this.remoteAddress == value) return this;
    InetAddress newValue = Objects.requireNonNull(value, "remoteAddress");
    return new ImmutableSyslogMessage(
        this.date,
        newValue,
        this.rawMessage,
        this.type,
        this.level,
        this.version,
        this.facility,
        this.host,
        this.message,
        this.processId,
        this.tag,
        this.messageId,
        this.appName,
        this.structuredData,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#rawMessage() rawMessage} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for rawMessage
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withRawMessage(String value) {
    if (this.rawMessage.equals(value)) return this;
    String newValue = Objects.requireNonNull(value, "rawMessage");
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        newValue,
        this.type,
        this.level,
        this.version,
        this.facility,
        this.host,
        this.message,
        this.processId,
        this.tag,
        this.messageId,
        this.appName,
        this.structuredData,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#type() type} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for type
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withType(MessageType value) {
    if (this.type == value) return this;
    MessageType newValue = Objects.requireNonNull(value, "type");
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        newValue,
        this.level,
        this.version,
        this.facility,
        this.host,
        this.message,
        this.processId,
        this.tag,
        this.messageId,
        this.appName,
        this.structuredData,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#level() level} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for level (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withLevel(@Nullable Integer value) {
    if (Objects.equals(this.level, value)) return this;
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        value,
        this.version,
        this.facility,
        this.host,
        this.message,
        this.processId,
        this.tag,
        this.messageId,
        this.appName,
        this.structuredData,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#version() version} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for version (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withVersion(@Nullable Integer value) {
    if (Objects.equals(this.version, value)) return this;
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        this.level,
        value,
        this.facility,
        this.host,
        this.message,
        this.processId,
        this.tag,
        this.messageId,
        this.appName,
        this.structuredData,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#facility() facility} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for facility (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withFacility(@Nullable Integer value) {
    if (Objects.equals(this.facility, value)) return this;
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        this.level,
        this.version,
        value,
        this.host,
        this.message,
        this.processId,
        this.tag,
        this.messageId,
        this.appName,
        this.structuredData,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#host() host} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for host (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withHost(@Nullable String value) {
    if (Objects.equals(this.host, value)) return this;
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        this.level,
        this.version,
        this.facility,
        value,
        this.message,
        this.processId,
        this.tag,
        this.messageId,
        this.appName,
        this.structuredData,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#message() message} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for message (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withMessage(@Nullable String value) {
    if (Objects.equals(this.message, value)) return this;
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        this.level,
        this.version,
        this.facility,
        this.host,
        value,
        this.processId,
        this.tag,
        this.messageId,
        this.appName,
        this.structuredData,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#processId() processId} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for processId (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withProcessId(@Nullable String value) {
    if (Objects.equals(this.processId, value)) return this;
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        this.level,
        this.version,
        this.facility,
        this.host,
        this.message,
        value,
        this.tag,
        this.messageId,
        this.appName,
        this.structuredData,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#tag() tag} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for tag (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withTag(@Nullable String value) {
    if (Objects.equals(this.tag, value)) return this;
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        this.level,
        this.version,
        this.facility,
        this.host,
        this.message,
        this.processId,
        value,
        this.messageId,
        this.appName,
        this.structuredData,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#messageId() messageId} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for messageId (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withMessageId(@Nullable String value) {
    if (Objects.equals(this.messageId, value)) return this;
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        this.level,
        this.version,
        this.facility,
        this.host,
        this.message,
        this.processId,
        this.tag,
        value,
        this.appName,
        this.structuredData,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#appName() appName} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for appName (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withAppName(@Nullable String value) {
    if (Objects.equals(this.appName, value)) return this;
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        this.level,
        this.version,
        this.facility,
        this.host,
        this.message,
        this.processId,
        this.tag,
        this.messageId,
        value,
        this.structuredData,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link SyslogMessage#structuredData() structuredData}.
   * @param elements The elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableSyslogMessage withStructuredData(@Nullable StructuredData... elements) {
    if (elements == null) {
      return new ImmutableSyslogMessage(
          this.date,
          this.remoteAddress,
          this.rawMessage,
          this.type,
          this.level,
          this.version,
          this.facility,
          this.host,
          this.message,
          this.processId,
          this.tag,
          this.messageId,
          this.appName,
          null,
          this.deviceVendor,
          this.deviceProduct,
          this.deviceVersion,
          this.deviceEventClassId,
          this.name,
          this.severity,
          this.extension);
    }
    List<StructuredData> newValue = Arrays.asList(elements) == null ? null : createUnmodifiableList(false, createSafeList(Arrays.asList(elements), true, false));
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        this.level,
        this.version,
        this.facility,
        this.host,
        this.message,
        this.processId,
        this.tag,
        this.messageId,
        this.appName,
        newValue,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link SyslogMessage#structuredData() structuredData}.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param elements An iterable of structuredData elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableSyslogMessage withStructuredData(@Nullable Iterable<? extends StructuredData> elements) {
    if (this.structuredData == elements) return this;
    List<StructuredData> newValue = elements == null ? null : createUnmodifiableList(false, createSafeList(elements, true, false));
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        this.level,
        this.version,
        this.facility,
        this.host,
        this.message,
        this.processId,
        this.tag,
        this.messageId,
        this.appName,
        newValue,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#deviceVendor() deviceVendor} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for deviceVendor (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withDeviceVendor(@Nullable String value) {
    if (Objects.equals(this.deviceVendor, value)) return this;
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        this.level,
        this.version,
        this.facility,
        this.host,
        this.message,
        this.processId,
        this.tag,
        this.messageId,
        this.appName,
        this.structuredData,
        value,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#deviceProduct() deviceProduct} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for deviceProduct (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withDeviceProduct(@Nullable String value) {
    if (Objects.equals(this.deviceProduct, value)) return this;
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        this.level,
        this.version,
        this.facility,
        this.host,
        this.message,
        this.processId,
        this.tag,
        this.messageId,
        this.appName,
        this.structuredData,
        this.deviceVendor,
        value,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#deviceVersion() deviceVersion} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for deviceVersion (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withDeviceVersion(@Nullable String value) {
    if (Objects.equals(this.deviceVersion, value)) return this;
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        this.level,
        this.version,
        this.facility,
        this.host,
        this.message,
        this.processId,
        this.tag,
        this.messageId,
        this.appName,
        this.structuredData,
        this.deviceVendor,
        this.deviceProduct,
        value,
        this.deviceEventClassId,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#deviceEventClassId() deviceEventClassId} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for deviceEventClassId (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withDeviceEventClassId(@Nullable String value) {
    if (Objects.equals(this.deviceEventClassId, value)) return this;
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        this.level,
        this.version,
        this.facility,
        this.host,
        this.message,
        this.processId,
        this.tag,
        this.messageId,
        this.appName,
        this.structuredData,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        value,
        this.name,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#name() name} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for name (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withName(@Nullable String value) {
    if (Objects.equals(this.name, value)) return this;
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        this.level,
        this.version,
        this.facility,
        this.host,
        this.message,
        this.processId,
        this.tag,
        this.messageId,
        this.appName,
        this.structuredData,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        value,
        this.severity,
        this.extension);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SyslogMessage#severity() severity} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for severity (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSyslogMessage withSeverity(@Nullable String value) {
    if (Objects.equals(this.severity, value)) return this;
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        this.level,
        this.version,
        this.facility,
        this.host,
        this.message,
        this.processId,
        this.tag,
        this.messageId,
        this.appName,
        this.structuredData,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        value,
        this.extension);
  }

  /**
   * Copy the current immutable object by replacing the {@link SyslogMessage#extension() extension} map with the specified map.
   * Nulls are not permitted as keys or values.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param entries The entries to be added to the extension map
   * @return A modified copy of {@code this} object
   */
  public final ImmutableSyslogMessage withExtension(@Nullable Map<String, ? extends String> entries) {
    if (this.extension == entries) return this;
    Map<String, String> newValue = entries == null ? null : createUnmodifiableMap(true, false, entries);
    return new ImmutableSyslogMessage(
        this.date,
        this.remoteAddress,
        this.rawMessage,
        this.type,
        this.level,
        this.version,
        this.facility,
        this.host,
        this.message,
        this.processId,
        this.tag,
        this.messageId,
        this.appName,
        this.structuredData,
        this.deviceVendor,
        this.deviceProduct,
        this.deviceVersion,
        this.deviceEventClassId,
        this.name,
        this.severity,
        newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableSyslogMessage} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    return another instanceof ImmutableSyslogMessage
        && equalTo((ImmutableSyslogMessage) another);
  }

  private boolean equalTo(ImmutableSyslogMessage another) {
    return date.equals(another.date)
        && remoteAddress.equals(another.remoteAddress)
        && rawMessage.equals(another.rawMessage)
        && type.equals(another.type)
        && Objects.equals(level, another.level)
        && Objects.equals(version, another.version)
        && Objects.equals(facility, another.facility)
        && Objects.equals(host, another.host)
        && Objects.equals(message, another.message)
        && Objects.equals(processId, another.processId)
        && Objects.equals(tag, another.tag)
        && Objects.equals(messageId, another.messageId)
        && Objects.equals(appName, another.appName)
        && Objects.equals(structuredData, another.structuredData)
        && Objects.equals(deviceVendor, another.deviceVendor)
        && Objects.equals(deviceProduct, another.deviceProduct)
        && Objects.equals(deviceVersion, another.deviceVersion)
        && Objects.equals(deviceEventClassId, another.deviceEventClassId)
        && Objects.equals(name, another.name)
        && Objects.equals(severity, another.severity)
        && Objects.equals(extension, another.extension);
  }

  /**
   * Computes a hash code from attributes: {@code date}, {@code remoteAddress}, {@code rawMessage}, {@code type}, {@code level}, {@code version}, {@code facility}, {@code host}, {@code message}, {@code processId}, {@code tag}, {@code messageId}, {@code appName}, {@code structuredData}, {@code deviceVendor}, {@code deviceProduct}, {@code deviceVersion}, {@code deviceEventClassId}, {@code name}, {@code severity}, {@code extension}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + date.hashCode();
    h += (h << 5) + remoteAddress.hashCode();
    h += (h << 5) + rawMessage.hashCode();
    h += (h << 5) + type.hashCode();
    h += (h << 5) + Objects.hashCode(level);
    h += (h << 5) + Objects.hashCode(version);
    h += (h << 5) + Objects.hashCode(facility);
    h += (h << 5) + Objects.hashCode(host);
    h += (h << 5) + Objects.hashCode(message);
    h += (h << 5) + Objects.hashCode(processId);
    h += (h << 5) + Objects.hashCode(tag);
    h += (h << 5) + Objects.hashCode(messageId);
    h += (h << 5) + Objects.hashCode(appName);
    h += (h << 5) + Objects.hashCode(structuredData);
    h += (h << 5) + Objects.hashCode(deviceVendor);
    h += (h << 5) + Objects.hashCode(deviceProduct);
    h += (h << 5) + Objects.hashCode(deviceVersion);
    h += (h << 5) + Objects.hashCode(deviceEventClassId);
    h += (h << 5) + Objects.hashCode(name);
    h += (h << 5) + Objects.hashCode(severity);
    h += (h << 5) + Objects.hashCode(extension);
    return h;
  }

  /**
   * Prints the immutable value {@code SyslogMessage} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return "SyslogMessage{"
        + "date=" + date
        + ", remoteAddress=" + remoteAddress
        + ", rawMessage=" + rawMessage
        + ", type=" + type
        + ", level=" + level
        + ", version=" + version
        + ", facility=" + facility
        + ", host=" + host
        + ", message=" + message
        + ", processId=" + processId
        + ", tag=" + tag
        + ", messageId=" + messageId
        + ", appName=" + appName
        + ", structuredData=" + structuredData
        + ", deviceVendor=" + deviceVendor
        + ", deviceProduct=" + deviceProduct
        + ", deviceVersion=" + deviceVersion
        + ", deviceEventClassId=" + deviceEventClassId
        + ", name=" + name
        + ", severity=" + severity
        + ", extension=" + extension
        + "}";
  }

  /**
   * Utility type used to correctly read immutable object from JSON representation.
   * @deprecated Do not use this type directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonDeserialize
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
  static final class Json implements SyslogMessage {
    LocalDateTime date;
    InetAddress remoteAddress;
    String rawMessage;
    MessageType type;
    Integer level;
    Integer version;
    Integer facility;
    String host;
    String message;
    String processId;
    String tag;
    String messageId;
    String appName;
    List<StructuredData> structuredData = null;
    String deviceVendor;
    String deviceProduct;
    String deviceVersion;
    String deviceEventClassId;
    String name;
    String severity;
    Map<String, String> extension;
    @JsonProperty(required = true)
    public void setDate(LocalDateTime date) {
      this.date = date;
    }
    @JsonProperty(required = true)
    public void setRemoteAddress(InetAddress remoteAddress) {
      this.remoteAddress = remoteAddress;
    }
    @JsonProperty(required = true)
    public void setRawMessage(String rawMessage) {
      this.rawMessage = rawMessage;
    }
    @JsonProperty(required = true)
    public void setType(MessageType type) {
      this.type = type;
    }
    @JsonProperty("level")
    public void setLevel(@Nullable Integer level) {
      this.level = level;
    }
    @JsonProperty("version")
    public void setVersion(@Nullable Integer version) {
      this.version = version;
    }
    @JsonProperty("facility")
    public void setFacility(@Nullable Integer facility) {
      this.facility = facility;
    }
    @JsonProperty("host")
    public void setHost(@Nullable String host) {
      this.host = host;
    }
    @JsonProperty("message")
    public void setMessage(@Nullable String message) {
      this.message = message;
    }
    @JsonProperty("processId")
    public void setProcessId(@Nullable String processId) {
      this.processId = processId;
    }
    @JsonProperty("tag")
    public void setTag(@Nullable String tag) {
      this.tag = tag;
    }
    @JsonProperty("messageId")
    public void setMessageId(@Nullable String messageId) {
      this.messageId = messageId;
    }
    @JsonProperty("appName")
    public void setAppName(@Nullable String appName) {
      this.appName = appName;
    }
    @JsonProperty("structuredData")
    public void setStructuredData(@Nullable List<StructuredData> structuredData) {
      this.structuredData = structuredData;
    }
    @JsonProperty("deviceVendor")
    public void setDeviceVendor(@Nullable String deviceVendor) {
      this.deviceVendor = deviceVendor;
    }
    @JsonProperty("deviceProduct")
    public void setDeviceProduct(@Nullable String deviceProduct) {
      this.deviceProduct = deviceProduct;
    }
    @JsonProperty("deviceVersion")
    public void setDeviceVersion(@Nullable String deviceVersion) {
      this.deviceVersion = deviceVersion;
    }
    @JsonProperty("deviceEventClassId")
    public void setDeviceEventClassId(@Nullable String deviceEventClassId) {
      this.deviceEventClassId = deviceEventClassId;
    }
    @JsonProperty("name")
    public void setName(@Nullable String name) {
      this.name = name;
    }
    @JsonProperty("severity")
    public void setSeverity(@Nullable String severity) {
      this.severity = severity;
    }
    @JsonProperty("extension")
    public void setExtension(@Nullable Map<String, String> extension) {
      this.extension = extension;
    }
    @Override
    public LocalDateTime date() { throw new UnsupportedOperationException(); }
    @Override
    public InetAddress remoteAddress() { throw new UnsupportedOperationException(); }
    @Override
    public String rawMessage() { throw new UnsupportedOperationException(); }
    @Override
    public MessageType type() { throw new UnsupportedOperationException(); }
    @Override
    public Integer level() { throw new UnsupportedOperationException(); }
    @Override
    public Integer version() { throw new UnsupportedOperationException(); }
    @Override
    public Integer facility() { throw new UnsupportedOperationException(); }
    @Override
    public String host() { throw new UnsupportedOperationException(); }
    @Override
    public String message() { throw new UnsupportedOperationException(); }
    @Override
    public String processId() { throw new UnsupportedOperationException(); }
    @Override
    public String tag() { throw new UnsupportedOperationException(); }
    @Override
    public String messageId() { throw new UnsupportedOperationException(); }
    @Override
    public String appName() { throw new UnsupportedOperationException(); }
    @Override
    public List<StructuredData> structuredData() { throw new UnsupportedOperationException(); }
    @Override
    public String deviceVendor() { throw new UnsupportedOperationException(); }
    @Override
    public String deviceProduct() { throw new UnsupportedOperationException(); }
    @Override
    public String deviceVersion() { throw new UnsupportedOperationException(); }
    @Override
    public String deviceEventClassId() { throw new UnsupportedOperationException(); }
    @Override
    public String name() { throw new UnsupportedOperationException(); }
    @Override
    public String severity() { throw new UnsupportedOperationException(); }
    @Override
    public Map<String, String> extension() { throw new UnsupportedOperationException(); }
  }

  /**
   * @param json A JSON-bindable data structure
   * @return An immutable value type
   * @deprecated Do not use this method directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  static ImmutableSyslogMessage fromJson(Json json) {
    ImmutableSyslogMessage.Builder builder = ImmutableSyslogMessage.builder();
    if (json.date != null) {
      builder.date(json.date);
    }
    if (json.remoteAddress != null) {
      builder.remoteAddress(json.remoteAddress);
    }
    if (json.rawMessage != null) {
      builder.rawMessage(json.rawMessage);
    }
    if (json.type != null) {
      builder.type(json.type);
    }
    if (json.level != null) {
      builder.level(json.level);
    }
    if (json.version != null) {
      builder.version(json.version);
    }
    if (json.facility != null) {
      builder.facility(json.facility);
    }
    if (json.host != null) {
      builder.host(json.host);
    }
    if (json.message != null) {
      builder.message(json.message);
    }
    if (json.processId != null) {
      builder.processId(json.processId);
    }
    if (json.tag != null) {
      builder.tag(json.tag);
    }
    if (json.messageId != null) {
      builder.messageId(json.messageId);
    }
    if (json.appName != null) {
      builder.appName(json.appName);
    }
    if (json.structuredData != null) {
      builder.addAllStructuredData(json.structuredData);
    }
    if (json.deviceVendor != null) {
      builder.deviceVendor(json.deviceVendor);
    }
    if (json.deviceProduct != null) {
      builder.deviceProduct(json.deviceProduct);
    }
    if (json.deviceVersion != null) {
      builder.deviceVersion(json.deviceVersion);
    }
    if (json.deviceEventClassId != null) {
      builder.deviceEventClassId(json.deviceEventClassId);
    }
    if (json.name != null) {
      builder.name(json.name);
    }
    if (json.severity != null) {
      builder.severity(json.severity);
    }
    if (json.extension != null) {
      builder.putAllExtension(json.extension);
    }
    return builder.build();
  }

  /**
   * Creates an immutable copy of a {@link SyslogMessage} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable SyslogMessage instance
   */
  public static ImmutableSyslogMessage copyOf(SyslogMessage instance) {
    if (instance instanceof ImmutableSyslogMessage) {
      return (ImmutableSyslogMessage) instance;
    }
    return ImmutableSyslogMessage.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableSyslogMessage ImmutableSyslogMessage}.
   * @return A new ImmutableSyslogMessage builder
   */
  public static ImmutableSyslogMessage.Builder builder() {
    return new ImmutableSyslogMessage.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableSyslogMessage ImmutableSyslogMessage}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  public static final class Builder {
    private static final long INIT_BIT_DATE = 0x1L;
    private static final long INIT_BIT_REMOTE_ADDRESS = 0x2L;
    private static final long INIT_BIT_RAW_MESSAGE = 0x4L;
    private static final long INIT_BIT_TYPE = 0x8L;
    private long initBits = 0xfL;

    private LocalDateTime date;
    private InetAddress remoteAddress;
    private String rawMessage;
    private MessageType type;
    private Integer level;
    private Integer version;
    private Integer facility;
    private String host;
    private String message;
    private String processId;
    private String tag;
    private String messageId;
    private String appName;
    private List<StructuredData> structuredData = null;
    private String deviceVendor;
    private String deviceProduct;
    private String deviceVersion;
    private String deviceEventClassId;
    private String name;
    private String severity;
    private Map<String, String> extension = null;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code com.github.jcustenborder.netty.syslog.Message} instance.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(Message instance) {
      Objects.requireNonNull(instance, "instance");
      from((Object) instance);
      return this;
    }

    /**
     * Fill a builder with attribute values from the provided {@code com.github.jcustenborder.netty.syslog.SyslogMessage} instance.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(SyslogMessage instance) {
      Objects.requireNonNull(instance, "instance");
      from((Object) instance);
      return this;
    }

    private void from(Object object) {
      if (object instanceof Message) {
        Message instance = (Message) object;
        date(instance.date());
        String severityValue = instance.severity();
        if (severityValue != null) {
          severity(severityValue);
        }
        Map<String, String> extensionValue = instance.extension();
        if (extensionValue != null) {
          putAllExtension(extensionValue);
        }
        Integer levelValue = instance.level();
        if (levelValue != null) {
          level(levelValue);
        }
        String appNameValue = instance.appName();
        if (appNameValue != null) {
          appName(appNameValue);
        }
        String deviceVendorValue = instance.deviceVendor();
        if (deviceVendorValue != null) {
          deviceVendor(deviceVendorValue);
        }
        String messageIdValue = instance.messageId();
        if (messageIdValue != null) {
          messageId(messageIdValue);
        }
        String deviceVersionValue = instance.deviceVersion();
        if (deviceVersionValue != null) {
          deviceVersion(deviceVersionValue);
        }
        type(instance.type());
        String messageValue = instance.message();
        if (messageValue != null) {
          message(messageValue);
        }
        Integer versionValue = instance.version();
        if (versionValue != null) {
          version(versionValue);
        }
        rawMessage(instance.rawMessage());
        List<StructuredData> structuredDataValue = instance.structuredData();
        if (structuredDataValue != null) {
          addAllStructuredData(structuredDataValue);
        }
        String deviceEventClassIdValue = instance.deviceEventClassId();
        if (deviceEventClassIdValue != null) {
          deviceEventClassId(deviceEventClassIdValue);
        }
        String processIdValue = instance.processId();
        if (processIdValue != null) {
          processId(processIdValue);
        }
        String hostValue = instance.host();
        if (hostValue != null) {
          host(hostValue);
        }
        String nameValue = instance.name();
        if (nameValue != null) {
          name(nameValue);
        }
        String tagValue = instance.tag();
        if (tagValue != null) {
          tag(tagValue);
        }
        String deviceProductValue = instance.deviceProduct();
        if (deviceProductValue != null) {
          deviceProduct(deviceProductValue);
        }
        Integer facilityValue = instance.facility();
        if (facilityValue != null) {
          facility(facilityValue);
        }
        remoteAddress(instance.remoteAddress());
      }
    }

    /**
     * Initializes the value for the {@link SyslogMessage#date() date} attribute.
     * @param date The value for date
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty(required = true)
    public final Builder date(LocalDateTime date) {
      this.date = Objects.requireNonNull(date, "date");
      initBits &= ~INIT_BIT_DATE;
      return this;
    }

    /**
     * Initializes the value for the {@link SyslogMessage#remoteAddress() remoteAddress} attribute.
     * @param remoteAddress The value for remoteAddress
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty(required = true)
    public final Builder remoteAddress(InetAddress remoteAddress) {
      this.remoteAddress = Objects.requireNonNull(remoteAddress, "remoteAddress");
      initBits &= ~INIT_BIT_REMOTE_ADDRESS;
      return this;
    }

    /**
     * Initializes the value for the {@link SyslogMessage#rawMessage() rawMessage} attribute.
     * @param rawMessage The value for rawMessage
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty(required = true)
    public final Builder rawMessage(String rawMessage) {
      this.rawMessage = Objects.requireNonNull(rawMessage, "rawMessage");
      initBits &= ~INIT_BIT_RAW_MESSAGE;
      return this;
    }

    /**
     * Initializes the value for the {@link SyslogMessage#type() type} attribute.
     * @param type The value for type
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty(required = true)
    public final Builder type(MessageType type) {
      this.type = Objects.requireNonNull(type, "type");
      initBits &= ~INIT_BIT_TYPE;
      return this;
    }

    /**
     * Initializes the value for the {@link SyslogMessage#level() level} attribute.
     * @param level The value for level (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("level")
    public final Builder level(@Nullable Integer level) {
      this.level = level;
      return this;
    }

    /**
     * Initializes the value for the {@link SyslogMessage#version() version} attribute.
     * @param version The value for version (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("version")
    public final Builder version(@Nullable Integer version) {
      this.version = version;
      return this;
    }

    /**
     * Initializes the value for the {@link SyslogMessage#facility() facility} attribute.
     * @param facility The value for facility (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("facility")
    public final Builder facility(@Nullable Integer facility) {
      this.facility = facility;
      return this;
    }

    /**
     * Initializes the value for the {@link SyslogMessage#host() host} attribute.
     * @param host The value for host (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("host")
    public final Builder host(@Nullable String host) {
      this.host = host;
      return this;
    }

    /**
     * Initializes the value for the {@link SyslogMessage#message() message} attribute.
     * @param message The value for message (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("message")
    public final Builder message(@Nullable String message) {
      this.message = message;
      return this;
    }

    /**
     * Initializes the value for the {@link SyslogMessage#processId() processId} attribute.
     * @param processId The value for processId (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("processId")
    public final Builder processId(@Nullable String processId) {
      this.processId = processId;
      return this;
    }

    /**
     * Initializes the value for the {@link SyslogMessage#tag() tag} attribute.
     * @param tag The value for tag (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("tag")
    public final Builder tag(@Nullable String tag) {
      this.tag = tag;
      return this;
    }

    /**
     * Initializes the value for the {@link SyslogMessage#messageId() messageId} attribute.
     * @param messageId The value for messageId (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("messageId")
    public final Builder messageId(@Nullable String messageId) {
      this.messageId = messageId;
      return this;
    }

    /**
     * Initializes the value for the {@link SyslogMessage#appName() appName} attribute.
     * @param appName The value for appName (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("appName")
    public final Builder appName(@Nullable String appName) {
      this.appName = appName;
      return this;
    }

    /**
     * Adds one element to {@link SyslogMessage#structuredData() structuredData} list.
     * @param element A structuredData element
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addStructuredData(StructuredData element) {
      if (this.structuredData == null) {
        this.structuredData = new ArrayList<StructuredData>();
      }
      this.structuredData.add(Objects.requireNonNull(element, "structuredData element"));
      return this;
    }

    /**
     * Adds elements to {@link SyslogMessage#structuredData() structuredData} list.
     * @param elements An array of structuredData elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addStructuredData(StructuredData... elements) {
      if (this.structuredData == null) {
        this.structuredData = new ArrayList<StructuredData>();
      }
      for (StructuredData element : elements) {
        this.structuredData.add(Objects.requireNonNull(element, "structuredData element"));
      }
      return this;
    }

    /**
     * Sets or replaces all elements for {@link SyslogMessage#structuredData() structuredData} list.
     * @param elements An iterable of structuredData elements
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("structuredData")
    public final Builder structuredData(@Nullable Iterable<? extends StructuredData> elements) {
      if (elements == null) {
        this.structuredData = null;
        return this;
      }
      this.structuredData = new ArrayList<StructuredData>();
      return addAllStructuredData(elements);
    }

    /**
     * Adds elements to {@link SyslogMessage#structuredData() structuredData} list.
     * @param elements An iterable of structuredData elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addAllStructuredData(Iterable<? extends StructuredData> elements) {
      Objects.requireNonNull(elements, "structuredData element");
      if (this.structuredData == null) {
        this.structuredData = new ArrayList<StructuredData>();
      }
      for (StructuredData element : elements) {
        this.structuredData.add(Objects.requireNonNull(element, "structuredData element"));
      }
      return this;
    }

    /**
     * Initializes the value for the {@link SyslogMessage#deviceVendor() deviceVendor} attribute.
     * @param deviceVendor The value for deviceVendor (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("deviceVendor")
    public final Builder deviceVendor(@Nullable String deviceVendor) {
      this.deviceVendor = deviceVendor;
      return this;
    }

    /**
     * Initializes the value for the {@link SyslogMessage#deviceProduct() deviceProduct} attribute.
     * @param deviceProduct The value for deviceProduct (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("deviceProduct")
    public final Builder deviceProduct(@Nullable String deviceProduct) {
      this.deviceProduct = deviceProduct;
      return this;
    }

    /**
     * Initializes the value for the {@link SyslogMessage#deviceVersion() deviceVersion} attribute.
     * @param deviceVersion The value for deviceVersion (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("deviceVersion")
    public final Builder deviceVersion(@Nullable String deviceVersion) {
      this.deviceVersion = deviceVersion;
      return this;
    }

    /**
     * Initializes the value for the {@link SyslogMessage#deviceEventClassId() deviceEventClassId} attribute.
     * @param deviceEventClassId The value for deviceEventClassId (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("deviceEventClassId")
    public final Builder deviceEventClassId(@Nullable String deviceEventClassId) {
      this.deviceEventClassId = deviceEventClassId;
      return this;
    }

    /**
     * Initializes the value for the {@link SyslogMessage#name() name} attribute.
     * @param name The value for name (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("name")
    public final Builder name(@Nullable String name) {
      this.name = name;
      return this;
    }

    /**
     * Initializes the value for the {@link SyslogMessage#severity() severity} attribute.
     * @param severity The value for severity (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("severity")
    public final Builder severity(@Nullable String severity) {
      this.severity = severity;
      return this;
    }

    /**
     * Put one entry to the {@link SyslogMessage#extension() extension} map.
     * @param key The key in the extension map
     * @param value The associated value in the extension map
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder putExtension(String key, String value) {
      if (this.extension == null) {
        this.extension = new LinkedHashMap<String, String>();
      }
      this.extension.put(
          Objects.requireNonNull(key, "extension key"),
          Objects.requireNonNull(value, "extension value"));
      return this;
    }

    /**
     * Put one entry to the {@link SyslogMessage#extension() extension} map. Nulls are not permitted
     * @param entry The key and value entry
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder putExtension(Map.Entry<String, ? extends String> entry) {
      if (this.extension == null) {
        this.extension = new LinkedHashMap<String, String>();
      }
      String k = entry.getKey();
      String v = entry.getValue();
      this.extension.put(
          Objects.requireNonNull(k, "extension key"),
          Objects.requireNonNull(v, "extension value"));
      return this;
    }

    /**
     * Sets or replaces all mappings from the specified map as entries for the {@link SyslogMessage#extension() extension} map. Nulls are not permitted as keys or values, but parameter itself can be null
     * @param extension The entries that will be added to the extension map
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("extension")
    public final Builder extension(@Nullable Map<String, ? extends String> extension) {
      if (extension == null) {
        this.extension = null;
        return this;
      }
      this.extension = new LinkedHashMap<String, String>();
      return putAllExtension(extension);
    }

    /**
     * Put all mappings from the specified map as entries to {@link SyslogMessage#extension() extension} map. Nulls are not permitted
     * @param extension The entries that will be added to the extension map
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder putAllExtension(Map<String, ? extends String> extension) {
      if (this.extension == null) {
        this.extension = new LinkedHashMap<String, String>();
      }
      for (Map.Entry<String, ? extends String> entry : extension.entrySet()) {
        String k = entry.getKey();
        String v = entry.getValue();
        this.extension.put(
            Objects.requireNonNull(k, "extension key"),
            Objects.requireNonNull(v, "extension value"));
      }
      return this;
    }

    /**
     * Builds a new {@link ImmutableSyslogMessage ImmutableSyslogMessage}.
     * @return An immutable instance of SyslogMessage
     * @throws IllegalStateException if any required attributes are missing
     */
    public ImmutableSyslogMessage build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableSyslogMessage(
          date,
          remoteAddress,
          rawMessage,
          type,
          level,
          version,
          facility,
          host,
          message,
          processId,
          tag,
          messageId,
          appName,
          structuredData == null ? null : createUnmodifiableList(true, structuredData),
          deviceVendor,
          deviceProduct,
          deviceVersion,
          deviceEventClassId,
          name,
          severity,
          extension == null ? null : createUnmodifiableMap(false, false, extension));
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = new ArrayList<String>();
      if ((initBits & INIT_BIT_DATE) != 0) attributes.add("date");
      if ((initBits & INIT_BIT_REMOTE_ADDRESS) != 0) attributes.add("remoteAddress");
      if ((initBits & INIT_BIT_RAW_MESSAGE) != 0) attributes.add("rawMessage");
      if ((initBits & INIT_BIT_TYPE) != 0) attributes.add("type");
      return "Cannot build SyslogMessage, some of required attributes are not set " + attributes;
    }
  }

  private static <T> List<T> createSafeList(Iterable<? extends T> iterable, boolean checkNulls, boolean skipNulls) {
    ArrayList<T> list;
    if (iterable instanceof Collection<?>) {
      int size = ((Collection<?>) iterable).size();
      if (size == 0) return Collections.emptyList();
      list = new ArrayList<T>();
    } else {
      list = new ArrayList<T>();
    }
    for (T element : iterable) {
      if (skipNulls && element == null) continue;
      if (checkNulls) Objects.requireNonNull(element, "element");
      list.add(element);
    }
    return list;
  }

  private static <T> List<T> createUnmodifiableList(boolean clone, List<T> list) {
    switch(list.size()) {
    case 0: return Collections.emptyList();
    case 1: return Collections.singletonList(list.get(0));
    default:
      if (clone) {
        return Collections.unmodifiableList(new ArrayList<T>(list));
      } else {
        if (list instanceof ArrayList<?>) {
          ((ArrayList<?>) list).trimToSize();
        }
        return Collections.unmodifiableList(list);
      }
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
