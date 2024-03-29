package com.jn.agileway.eipchannel.core.router;

import com.jn.agileway.eipchannel.core.channel.ChannelDirect;
import com.jn.agileway.eipchannel.core.channel.ChannelResolver;
import com.jn.agileway.eipchannel.core.channel.OutboundChannel;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.agileway.eipchannel.core.message.MessageBuilder;
import com.jn.agileway.eipchannel.core.message.MessagingException;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractMessageRouter extends AbstractLifecycle implements MessageRouter {
    protected Logger logger = Loggers.getLogger(getClass());
    private volatile OutboundChannel defaultOutputChannel;

    private volatile boolean ignoreSendFailures;

    private volatile boolean applySequence;

    private volatile String prefix;

    private volatile String suffix;

    private volatile ChannelResolver channelResolver;

    private volatile boolean ignoreChannelNameResolutionFailures;

    protected volatile Map<String, String> channelIdentifierMap = new ConcurrentHashMap<String, String>();

    /**
     * Specify a prefix to be added to each channel name prior to resolution.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Specify a suffix to be added to each channel name prior to resolution.
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * Allows you to set the map which will map channel identifiers to channel names.
     * Channel names will be resolve via {@link ChannelResolver}
     */
    public void setChannelIdentifierMap(Map<String, String> channelIdentifierMap) {
        this.channelIdentifierMap.clear();
        this.channelIdentifierMap.putAll(channelIdentifierMap);
    }

    public void setChannelMapping(String channelIdentifier, String channelName) {
        this.channelIdentifierMap.put(channelIdentifier, channelName);
    }

    public void removeChannelMapping(String channelIdentifier) {
        this.channelIdentifierMap.remove(channelIdentifier);
    }

    /**
     * Set the default channel where Messages should be sent if channel resolution fails to return any channels. If no
     * default channel is provided, the router will either drop the Message or throw an Exception depending on the value
     */
    public void setDefaultOutputChannel(OutboundChannel defaultOutputChannel) {
        this.defaultOutputChannel = defaultOutputChannel;
    }


    /**
     * Specify whether this router should ignore any failure to resolve a channel name to
     * an actual MessageChannel instance when delegating to the ChannelResolver strategy.
     */
    public void setIgnoreChannelNameResolutionFailures(boolean ignoreChannelNameResolutionFailures) {
        this.ignoreChannelNameResolutionFailures = ignoreChannelNameResolutionFailures;
    }

    /**
     * Specify whether send failures for one or more of the recipients should be ignored. By default this is
     * <code>false</code> meaning that an Exception will be thrown whenever a send fails. To override this and suppress
     * Exceptions, set the value to <code>true</code>.
     */
    public void setIgnoreSendFailures(boolean ignoreSendFailures) {
        this.ignoreSendFailures = ignoreSendFailures;
    }

    /**
     * Specify whether to apply the sequence number and size headers to the messages prior to sending to the recipient
     * channels. By default, this value is <code>false</code> meaning that sequence headers will <em>not</em> be
     * applied. If planning to use an Aggregator downstream with the default correlation and completion strategies, you
     * should set this flag to <code>true</code>.
     */
    public void setApplySequence(boolean applySequence) {
        this.applySequence = applySequence;
    }

    /**
     * Subclasses must implement this method to return the channel identifiers.
     */
    protected abstract List<Object> getChannelIdentifiers(Message<?> message);

    @Override
    public final void handle(Message<?> message) {
        route(message);
    }

    protected void route(Message<?> message) {
        boolean sent = false;
        Collection<OutboundChannel> expectedOutboundChannels = this.determineTargetChannels(message);
        List<OutboundChannel> successSentChannels = Collects.newArrayList();
        if (Objs.isNotEmpty(expectedOutboundChannels)) {
            int sequenceSize = expectedOutboundChannels.size();
            int sequenceNumber = 1;
            for (OutboundChannel channel : expectedOutboundChannels) {
                final Message<?> messageToSend = (!this.applySequence) ? message : MessageBuilder.fromMessage(message)
                        .pushSequenceDetails(message.getHeaders().getId(), sequenceNumber++, sequenceSize).build();
                if (channel != null) {
                    try {
                        channel.send(messageToSend);
                        sent = true;
                        successSentChannels.add(channel);
                    } catch (MessagingException e) {
                        if (!this.ignoreSendFailures) {
                            throw e;
                        } else if (this.logger.isDebugEnabled()) {
                            this.logger.debug(e.getMessage(), e);
                        }
                    }
                }
            }
        }

        if (this.defaultOutputChannel != null) {
            /**
             * 如果之前没有发送时，必然发送到默认channel
             * 如果之前发送了，根据 自定义的方式来决定是否发到 默认的
             */
            boolean sentToDefault = !sent || determineSentToDefaultOutputChannel(this.defaultOutputChannel, expectedOutboundChannels, successSentChannels);
            if (sentToDefault) {
                if (Collects.contains(successSentChannels, this.defaultOutputChannel)) {
                    sentToDefault = false;
                }
            }
            if (sentToDefault) {
                this.defaultOutputChannel.send(message);
            }

        }
    }

    protected boolean determineSentToDefaultOutputChannel(OutboundChannel defaultOutboundChannel, Collection<OutboundChannel> expectedOutboundChannels, Collection<OutboundChannel> successSentChannels) {
        return true;
    }

    private Collection<OutboundChannel> determineTargetChannels(Message<?> message) {
        Collection<OutboundChannel> channels = Collects.emptyArrayList();
        Collection<Object> channelsReturned = this.getChannelIdentifiers(message);
        addToCollection(channels, channelsReturned, message);
        return channels;
    }

    public void setChannelResolver(ChannelResolver channelResolver) {
        this.channelResolver = channelResolver;
    }

    private OutboundChannel resolveChannelForName(String channelName, Message<?> message) {
        if (this.channelResolver == null) {
            this.init();
        }
        Preconditions.checkState(this.channelResolver != null,
                "unable to resolve channel names, no ChannelResolver available");
        OutboundChannel channel = this.channelResolver.resolve(channelName, ChannelDirect.OUTBOUND);
        if (channel == null && !this.ignoreChannelNameResolutionFailures) {
            throw new MessagingException(message,
                    "failed to resolve channel name '" + channelName + "'");
        }
        return channel;
    }

    private void addChannelFromString(Collection<OutboundChannel> channels, String channelIdentifier, Message<?> message) {
        if (channelIdentifier.indexOf(',') != -1) {
            for (String name : Strings.split(channelIdentifier, ",")) {
                addChannelFromString(channels, name, message);
            }
            return;
        }

        // if the channelIdentifierMap contains a mapping, we'll use the mapped value
        // otherwise, the String-based channelIdentifier itself will be used as the channel name
        String channelName = channelIdentifier;
        if (!Objs.isEmpty(channelIdentifierMap) && channelIdentifierMap.containsKey(channelIdentifier)) {
            channelName = channelIdentifierMap.get(channelIdentifier);
        }
        if (this.prefix != null) {
            channelName = this.prefix + channelName;
        }
        if (this.suffix != null) {
            channelName = channelName + suffix;
        }
        OutboundChannel channel = resolveChannelForName(channelName, message);
        if (channel != null) {
            channels.add(channel);
        }
    }

    private void addToCollection(Collection<OutboundChannel> channels, Collection<?> channelIndicators, Message<?> message) {
        if (channelIndicators == null) {
            return;
        }
        for (Object channelIndicator : channelIndicators) {
            if (channelIndicator != null) {
                if (channelIndicator instanceof OutboundChannel) {
                    channels.add((OutboundChannel) channelIndicator);
                } else if (channelIndicator instanceof OutboundChannel[]) {
                    channels.addAll(Arrays.asList((OutboundChannel[]) channelIndicator));
                } else if (channelIndicator instanceof String) {
                    addChannelFromString(channels, (String) channelIndicator, message);
                } else if (channelIndicator instanceof String[]) {
                    for (String indicatorName : (String[]) channelIndicator) {
                        addChannelFromString(channels, indicatorName, message);
                    }
                } else if (channelIndicator instanceof Collection) {
                    addToCollection(channels, (Collection<?>) channelIndicator, message);
                } else {
                    throw new MessagingException(
                            "unsupported return type for router [" + channelIndicator.getClass() + "]");
                }
            }
        }
    }

    public OutboundChannel getDefaultOutputChannel() {
        return defaultOutputChannel;
    }

    @Override
    protected void doStart() {
        defaultOutputChannel.startup();
    }

    @Override
    protected void doStop() {
        super.doStop();
    }

    @Override
    protected void doInit() throws InitializationException {
        super.doInit();
    }
}
