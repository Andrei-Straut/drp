package com.andreistraut.drp.core.communicator;

import com.andreistraut.drp.core.LocalHttpTestServerHandler;
import com.andreistraut.drp.core.LocalHttpTestServerInitializer;
import com.andreistraut.drp.core.LocalHttpTestServerRunner;
import com.andreistraut.drp.core.TestResource;
import com.google.common.collect.Maps;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.CharsetUtil;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class RequestDispatcherTest {

    private static LocalHttpTestServerRunner runner;
    private static LocalHttpTestServerHandler handler;

    @BeforeClass
    public static void setUpClass() throws InterruptedException, ExecutionException {

	Callable<Boolean> serverTask = () -> {
	    handler = new LocalHttpTestServerHandler();
	    LocalHttpTestServerInitializer initializer = new LocalHttpTestServerInitializer(handler);
	    
	    runner = new LocalHttpTestServerRunner(TestResource.TEST_PORT);
	    runner.run(initializer);
	    return !runner.isStarting() && runner.isStarted();
	};

	ExecutorService executor = Executors.newSingleThreadExecutor();
	executor.submit(serverTask);
	
	Logger.getLogger(LocalHttpTestServerRunner.class.getName()).log(Level.INFO, "Awaiting for server...");
	TimeUnit.SECONDS.sleep(5);
	
	Logger.getLogger(LocalHttpTestServerRunner.class.getName()).log(Level.INFO, "Starting test execution");
    }

    @AfterClass
    public static void tearDownClass() throws InterruptedException, ExecutionException {
	
	runner.stop();
	Logger.getLogger(LocalHttpTestServerRunner.class.getName()).log(Level.INFO, "Stopping test execution");
    }

    @Test
    public void testGETRequestContentLengthHeader() throws Exception {

	Map<String, String> headers = Maps.newHashMap();
	headers.put(HttpHeaderNames.CONTENT_TYPE.toString(),
		HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString());
	headers.put(HttpHeaderNames.CONTENT_LENGTH.toString(), 0 + "");
	RequestTranslator translator = new RequestTranslator();
	
	HttpRequestBase request = new RequestTranslator().fromJsonString(
		headers, 
		TestResource.validGETRequestWithNoHeadersRequestStringAsString());

	RequestDispatcher.dispatch(request);
	
	Assert.assertTrue("Expected absent Content-Length header, but was not", 
		!translator.getHeaderValue(handler.getLastRequest(), HttpHeaderNames.CONTENT_LENGTH.toString()).isPresent());
    }

    @Test
    public void testPOSTRequestContentLengthHeader() throws Exception {

	Map<String, String> headers = Maps.newHashMap();
	headers.put(HttpHeaderNames.CONTENT_TYPE.toString(),
		HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString());
	headers.put(HttpHeaderNames.CONTENT_LENGTH.toString(), 0 + "");
	RequestTranslator translator = new RequestTranslator();
	
	HttpRequestBase request = new RequestTranslator().fromJsonString(
		headers, 
		TestResource.validPOSTRequestWithRequestStringAsString());

	RequestDispatcher.dispatch(request);
	
	Assert.assertTrue("Expected present Content-Length header, but was not", 
		!translator.getHeaderValue(handler.getLastRequest(), HttpHeaderNames.CONTENT_LENGTH.toString()).isPresent());
    }

    @Test
    public void testRequestHeaders() throws Exception {

	Map<String, String> headers = Maps.newHashMap();
	headers.put(HttpHeaderNames.CONTENT_TYPE.toString(),
		HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString());
	
	HttpRequestBase request = new RequestTranslator().fromJsonString(
		headers, 
		TestResource.validGETRequestWithNoHeadersRequestStringAsString());

	RequestDispatcher.dispatch(request);
	
	Assert.assertTrue("Expected present Content-Type header, but was not", 
		handler.getLastRequest().getHeaders(HttpHeaderNames.CONTENT_TYPE.toString()).length > 0);
	Assert.assertTrue("Expected present Content-Type header value, but was not", 
		handler.getLastRequest().getFirstHeader(HttpHeaderNames.CONTENT_TYPE.toString()).getValue()
			.equals(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString()));
    }

    @Test
    public void testRequestMethodGETRequest() throws Exception {
	
	HttpRequestBase request = new RequestTranslator().fromJsonString(
		Maps.<String, String>newHashMap(), 
		TestResource.validGETRequestWithRequestStringAsString());

	RequestDispatcher.dispatch(request);
	
	Assert.assertTrue("Expected Correct HTTP Method, but was not", 
		HttpMethod.valueOf(handler.getLastRequest().getMethod()).equals(HttpMethod.GET));
    }

    @Test
    public void testRequestMethodPOSTRequest() throws Exception {

	Map<String, String> headers = Maps.newHashMap();
	headers.put(HttpHeaderNames.CONTENT_TYPE.toString(),
		HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString());
	
	HttpRequestBase request = new RequestTranslator().fromJsonString(
		headers, 
		TestResource.validPOSTRequestWithRequestStringAsString());

	RequestDispatcher.dispatch(request);
	
	Assert.assertTrue("Expected Correct HTTP Method, but was not", 
		HttpMethod.valueOf(handler.getLastRequest().getMethod()).equals(HttpMethod.POST));
    }

    @Test
    public void testRequestContentsPOSTRequest() throws Exception {

	Map<String, String> headers = Maps.newHashMap();
	headers.put(HttpHeaderNames.CONTENT_TYPE.toString(),
		HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString());
	
	HttpRequestBase request = new RequestTranslator().fromJsonString(
		headers, 
		TestResource.validPOSTRequestWithRequestStringAsString());

	RequestDispatcher.dispatch(request);
	
	Assert.assertTrue("Expected an HTTP Request containing a body, but it did not", 
		HttpEntityEnclosingRequest.class.isInstance(handler.getLastRequest()));
		
	if(!HttpEntityEnclosingRequest.class.isInstance(handler.getLastRequest())) {
	    Assert.fail("Expected response with message body, but had not");
	}
	
	HttpEntityEnclosingRequest entityRequest = HttpEntityEnclosingRequest.class.cast(
		handler.getLastRequest());
	
	String requestContents = EntityUtils.toString(entityRequest.getEntity(), CharsetUtil.UTF_8);
	Assert.assertTrue("Expected Correct HTTP Content, but was not", requestContents.contains(TestResource.REQUEST_PARAM_NAME));
	Assert.assertTrue("Expected Correct HTTP Content, but was not", requestContents.contains(TestResource.REQUEST_PARAM_VALUE));
    }
}
