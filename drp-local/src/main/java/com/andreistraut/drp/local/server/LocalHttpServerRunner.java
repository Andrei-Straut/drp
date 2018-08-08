
package com.andreistraut.drp.local.server;

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
public class LocalHttpServerRunner {

    private final int port;
    private final ChannelInitializer<SocketChannel> initializer;

    public LocalHttpServerRunner(int port, ChannelInitializer<SocketChannel> initializer) {

        if (port <= 0) {
            throw new IllegalArgumentException("Port number must be an integer larger than 0");
        }

        if (initializer == null) {
            throw new IllegalArgumentException("ChannelInitializer cannot be null");
        }

        this.port = port;
        this.initializer = initializer;
    }

    public void run() throws InterruptedException {

        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(initializer);

            Channel ch = b.bind(port).sync().channel();

            Logger.getLogger(LocalHttpServerRunner.class.getName()).log(Level.INFO,
                    String.format("Server started and listening on port", port));

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
        }
    }
}
