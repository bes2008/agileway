package com.jn.agileway.httpclient.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class HttpClientChannelInitializer extends ChannelInitializer {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("http-message-codec", new HttpClientCodec());
        ch.pipeline().addLast("http-message-aggregator", new HttpObjectAggregator(Integer.MAX_VALUE));

    }
}
