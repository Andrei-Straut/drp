package com.andreistraut.drp.local.server;

import com.andreistraut.drp.core.communicator.RequestDispatcher;
import com.andreistraut.drp.core.communicator.RequestTranslator;
import com.andreistraut.drp.core.model.Messages;
import com.google.common.collect.Maps;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static io.netty.handler.codec.http.HttpVersion.*;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;

/**
 * Handler for local Netty-based server
 */
public class LocalHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
	ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws IOException {

	if (request.method() != HttpMethod.POST) {
	    HttpResponse response = new BasicHttpResponse(
		    new ProtocolVersion(
			    request.protocolVersion().protocolName(),
			    request.protocolVersion().majorVersion(),
			    request.protocolVersion().minorVersion()),
		    HttpResponseStatus.FORBIDDEN.code(),
		    String.format(Messages.UNSUPPORTED_HTTP_METHOD, HttpMethod.POST.toString()));
	    writeResponse(request, response, ctx);
	    return;
	}

	Optional<Map<String, String>> requestHeaders = parseHeaders(request);
	Optional<String> requestContent = parseContent(request);

	try {
	    HttpRequestBase proxyRequest = new RequestTranslator().fromJsonString(
		    requestHeaders.orElse(null), requestContent.orElse(null));
	    HttpResponse response = RequestDispatcher.dispatch(proxyRequest);

	    writeResponse(request, response, ctx);

	} catch(JsonSyntaxException | MalformedJsonException e) {
	    Logger.getLogger(LocalHttpServerHandler.class.getName()).log(Level.WARNING,
		    String.format("Invalid JSON Object submitted, exception raised: %s", e.getMessage()), e);
	    
	    HttpResponse response = new BasicHttpResponse(
		    new ProtocolVersion(
			    request.protocolVersion().protocolName(),
			    request.protocolVersion().majorVersion(),
			    request.protocolVersion().minorVersion()),
		    HttpResponseStatus.FORBIDDEN.code(),
		    HttpResponseStatus.FORBIDDEN.reasonPhrase());
	    response.setEntity(new StringEntity(String.format("Invalid JSON Object submitted: %s", e.getMessage())));
	    writeResponse(request, response, ctx);
	    
	} catch (IllegalStateException | IllegalArgumentException e) {
	    Logger.getLogger(LocalHttpServerHandler.class.getName()).log(Level.WARNING,
		    String.format("Invalid request content submitted: %s", e.getMessage()), e);
	    
	    HttpResponse response = new BasicHttpResponse(
		    new ProtocolVersion(
			    request.protocolVersion().protocolName(),
			    request.protocolVersion().majorVersion(),
			    request.protocolVersion().minorVersion()),
		    HttpResponseStatus.FORBIDDEN.code(),
		    HttpResponseStatus.FORBIDDEN.reasonPhrase());
	    response.setEntity(new StringEntity(e.getMessage()));
	    writeResponse(request, response, ctx);
	    
	} catch(IOException e) {
	    Logger.getLogger(LocalHttpServerHandler.class.getName()).log(Level.SEVERE,
		    String.format("Exception raised: %s", e.getMessage()), e);
	    
	    HttpResponse response = new BasicHttpResponse(
		    new ProtocolVersion(
			    request.protocolVersion().protocolName(),
			    request.protocolVersion().majorVersion(),
			    request.protocolVersion().minorVersion()),
		    HttpResponseStatus.INTERNAL_SERVER_ERROR.code(),
		    HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase());
	    
	    response.setEntity(new StringEntity(String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage())));
	    writeResponse(request, response, ctx);
	}
    }

    private Optional<Map<String, String>> parseHeaders(FullHttpRequest message) {

	Map<String, String> headers = Maps.<String, String>newHashMap();

	if (!message.headers().isEmpty()) {
	    for (Map.Entry<String, String> header : message.headers()) {
		headers.put(header.getKey(), header.getValue());
	    }
	}

	return Optional.ofNullable(headers);
    }

    private Optional<Map<String, List<String>>> parseParameters(FullHttpRequest message) {

	Map<String, List<String>> params = Maps.<String, List<String>>newHashMap();

	QueryStringDecoder queryStringDecoder = new QueryStringDecoder(message.uri());
	Map<String, List<String>> queryParams = queryStringDecoder.parameters();

	if (!queryParams.isEmpty()) {
	    for (Entry<String, List<String>> param : queryParams.entrySet()) {
		params.put(param.getKey(), param.getValue());
	    }
	}

	return Optional.ofNullable(params);
    }

    private Optional<String> parseContent(FullHttpRequest request) {
	if (request.content().isReadable()) {
	    return Optional.ofNullable(request.content().toString(CharsetUtil.UTF_8));
	}

	return Optional.empty();
    }

    private void writeResponse(FullHttpRequest request, HttpResponse proxyResponse, ChannelHandlerContext ctx) throws IOException {

	boolean keepAlive = false;
	RequestTranslator translator = new RequestTranslator();
	translator.getHeaderValue(request, HttpHeaderNames.CONNECTION.toString());
	
	if (translator.getHeader(request, HttpHeaderNames.CONNECTION.toString()).isPresent()) {
	    Map.Entry<String, String> keepAliveHeader = translator.getHeader(request, HttpHeaderNames.CONNECTION.toString()).get();
	    keepAlive = keepAliveHeader.getValue().equals(HttpHeaderValues.KEEP_ALIVE.toString().toLowerCase().trim());
	}
	
	String messageContent = translator.getBody(proxyResponse).orElse("");

	FullHttpResponse response = new DefaultFullHttpResponse(
		HTTP_1_1, HttpResponseStatus.parseLine(proxyResponse.getStatusLine().getStatusCode() + ""),
		Unpooled.copiedBuffer(translator.getBody(proxyResponse).orElse(""), CharsetUtil.UTF_8));

	for (Header header : proxyResponse.getAllHeaders()) {
	    response.headers().set(header.getName(), header.getValue());
	}
	
	response.headers().set(HttpHeaderNames.CONTENT_LENGTH, messageContent.length());

	ctx.write(response);
	
	if (!keepAlive) {
	    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
	cause.printStackTrace();
	ctx.close();
    }
}
