package com.andreistraut.drp.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple Netty-based HTTP server to handle requests and proxy them
 */
public class LocalHttpTestServerRunner {

    private final int port;
    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;
    
    private boolean isStarting = false;
    private boolean isStarted = false;

    public LocalHttpTestServerRunner(int port) {

	if (port <= 0) {
	    throw new IllegalArgumentException("Port number must be an integer larger than 0");
	}

	this.port = port;
    }

    public void run(ChannelInitializer<SocketChannel> initializer) throws InterruptedException {
	if(initializer == null) {
	    throw new IllegalArgumentException("ChannelInitializer cannot be null");
	}
	
	this.isStarting = true;
	this.isStarted = false;
	
	// Configure the server.
	bossGroup = new NioEventLoopGroup(1);
	workerGroup = new NioEventLoopGroup(1);
	
	ServerBootstrap b = new ServerBootstrap();
	b.group(bossGroup, workerGroup)
		.channel(NioServerSocketChannel.class)
		.handler(new LoggingHandler(LogLevel.INFO))
		.childHandler(initializer);
	
	Logger.getLogger(LocalHttpTestServerRunner.class.getName()).log(Level.INFO,
		String.format("Starting server on port %s", port));

	this.isStarting = false;
	this.isStarted = true;
	
	Channel ch = b.bind(port).sync().channel();
	ch.closeFuture().sync();
    }

    public boolean stop() {
	if (bossGroup != null) {
	    bossGroup.shutdownGracefully();
	}

	if (workerGroup != null) {
	    workerGroup.shutdownGracefully();
	}

	Logger.getLogger(LocalHttpTestServerRunner.class.getName()).log(Level.INFO, "Server stopped successfully");
	
	return true;
    }

    public boolean isStarting() {
	return isStarting;
    }

    public boolean isStarted() {
	return isStarted;
    }
}
