package com.andreistraut.drp.core;

import com.andreistraut.drp.core.communicator.RequestTranslator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.util.CharsetUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Handler for test server. Echoes the last received request, and guards it as a
 * class property
 */
@ChannelHandler.Sharable
public class LocalHttpTestServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static HttpRequestBase lastRequest;

    public HttpRequestBase getLastRequest() {
	return lastRequest;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
	ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws UnsupportedEncodingException, IOException {
	Logger.getLogger(LocalHttpTestServerHandler.class.getName()).log(Level.INFO,
		String.format("Test server received request: %s", request));

	synchronized (this) {
	    lastRequest = new RequestTranslator().fromFullHttpRequest(request);
	}
	
	RequestTranslator translator = new RequestTranslator();
	String responseBody = translator.getBody(lastRequest).orElse("");
	    
	FullHttpResponse response = new DefaultFullHttpResponse(
		HTTP_1_1, HttpResponseStatus.OK,
		Unpooled.copiedBuffer(responseBody, CharsetUtil.UTF_8));
	
	ctx.write(response);
	ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
	cause.printStackTrace();
	ctx.close();
	ctx.channel().close();
    }
}
