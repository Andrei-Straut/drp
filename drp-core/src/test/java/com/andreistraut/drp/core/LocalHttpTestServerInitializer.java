package com.andreistraut.drp.core;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Initializer for local Netty-based test server
 */
public class LocalHttpTestServerInitializer extends ChannelInitializer<SocketChannel> {
    
    final ChannelInboundHandler handler;
    
    public LocalHttpTestServerInitializer(ChannelInboundHandler handler) {
	if(handler == null) {
	    throw new IllegalArgumentException("ChannelHandler cannot be null");
	}
	
	this.handler = handler;
    }

    @Override
    public void initChannel(SocketChannel ch) {
	ChannelPipeline p = ch.pipeline();
	p.addLast(new HttpRequestDecoder());
	// Uncomment the following line if you don't want to handle HttpChunks.
	p.addLast(new HttpObjectAggregator(1048576));
	p.addLast(new HttpResponseEncoder());
	// Remove the following line if you don't want automatic content compression.
	//p.addLast(new HttpContentCompressor());
	p.addLast(this.handler);
    }
}
