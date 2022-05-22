package com.jn.agileway.eimessage.core.router;

import com.jn.agileway.eimessage.core.channel.ChannelResolver;
import com.jn.agileway.eimessage.core.channel.OutboundChannel;
import com.jn.agileway.eimessage.core.endpoint.dispatcher.AbstractMessageHandler;
import com.jn.agileway.eimessage.core.message.Message;
import com.jn.agileway.eimessage.core.message.MessageBuilder;
import com.jn.agileway.eimessage.core.message.MessagingException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.converter.ConverterService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractMessageRouter extends AbstractMessageHandler implements MessageRouter {
    private volatile OutboundChannel defaultOutputChannel;


    private volatile boolean ignoreSendFailures;

    private volatile boolean applySequence;

    private ConverterService converterService;
    private volatile String prefix;

    private volatile String suffix;

    private volatile ChannelResolver channelResolver;

    private volatile boolean ignoreChannelNameResolutionFailures;

    protected volatile Map<String, String> channelIdentifierMap = new ConcurrentHashMap<String, String>();


    public void setConverterService(ConverterService converterService) {
        this.converterService = converterService;
    }


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
     *
     * @param channelIdentifierMap
     */
    public void setChannelIdentifierMap(Map<String, String> channelIdentifierMap) {
        this.channelIdentifierMap.clear();
        this.channelIdentifierMap.putAll(channelIdentifierMap);
    }

    public void setChannelMapping(String channelIdentifier, String channelName) {
        this.channelIdentifierMap.put(channelIdentifier, channelName);
    }

    /**
     * Removes channel mapping for a give channel identifier
     *
     * @param channelIdentifier
     */
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
    protected void handleMessageInternal(Message<?> message) {
        boolean sent = false;
        Collection<OutboundChannel> results = this.determineTargetChannels(message);
        if (results != null) {
            int sequenceSize = results.size();
            int sequenceNumber = 1;
            for (OutboundChannel channel : results) {
                final Message<?> messageToSend = (!this.applySequence) ? message : MessageBuilder.fromMessage(message)
                        .pushSequenceDetails(message.getHeaders().getId(), sequenceNumber++, sequenceSize).build();
                if (channel != null) {
                    try {
                        channel.send(messageToSend);
                        sent = true;
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
        if (!sent) {
            if (this.defaultOutputChannel != null) {
                this.defaultOutputChannel.send(message);
            }
        }
    }

    private Collection<OutboundChannel> determineTargetChannels(Message<?> message) {
        Collection<OutboundChannel> channels = new ArrayList<OutboundChannel>();
        Collection<Object> channelsReturned = this.getChannelIdentifiers(message);
        addToCollection(channels, channelsReturned, message);
        return channels;
    }

    private OutboundChannel resolveChannelForName(String channelName, Message<?> message) {
        if (this.channelResolver == null) {
            this.init();
        }
        Preconditions.checkState(this.channelResolver != null,
                "unable to resolve channel names, no ChannelResolver available");
        OutboundChannel channel = this.channelResolver.resolveOutboundChannel(channelName);
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
            if (channelIndicator == null) {
                continue;
            } else if (channelIndicator instanceof OutboundChannel) {
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
