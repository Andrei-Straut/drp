package com.andreistraut.drp.core.communicator;

import io.netty.handler.codec.http.HttpHeaderNames;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;

public class RequestDispatcher {

    private static final int TIMEOUT_MILLIS = 60_000;

    /**
     * Dispatch (forward) the given request to its URL
     * @param proxyRequest The request to forward
     * @return The response of the remote endpoint
     * @throws UnsupportedEncodingException
     * @throws IOException Thrown by HttpComponents on client request execution
     */
    public static HttpResponse dispatch(HttpRequestBase proxyRequest) throws UnsupportedEncodingException, IOException {
	RequestConfig.Builder requestBuilder = RequestConfig.custom()
		.setConnectTimeout(TIMEOUT_MILLIS)
		.setConnectionRequestTimeout(TIMEOUT_MILLIS)
		.setSocketTimeout(TIMEOUT_MILLIS);

	/**
	 * Build the HTTP Request, Clean all default request headers
	 */
	HttpClient client = HttpClientBuilder.create()
		.setDefaultRequestConfig(requestBuilder.build())
		.build();

	Logger.getLogger(RequestDispatcher.class.getName()).log(Level.INFO,
		String.format("Dispatching Request:%s%s",
			System.lineSeparator(),
			proxyRequest.toString()));

	/**
	 * Execute the request and get the response content
	 */
	HttpResponse response = client.execute(proxyRequest);

	/**
	 * Add CORS headers
	 */
	response.addHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN.toString(), "*");
	response.addHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS.toString(), "*");
	response.addHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS.toString(), "*");

	Logger.getLogger(RequestDispatcher.class.getName()).log(Level.INFO,
		String.format("Request dispatched, response received:%s%s",
			System.lineSeparator(),
			response.toString()));

	return response;
    }
}
