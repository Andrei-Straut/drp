package com.andreistraut.drp.core.communicator;

import com.andreistraut.drp.core.TestResource;
import com.andreistraut.drp.core.model.Fields;
import com.andreistraut.drp.core.model.Messages;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import java.util.Map;
import java.util.Optional;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.protocol.HttpCoreContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class RequestTranslatorTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();
	
    private final String EMPTY_STRING = "";
    private final String SPACE = " ";
    private final String QUOTE = "\"";
    private final String NEWLINE = System.lineSeparator();
    
    @Test
    public void testGetHeaderHttpRequestBaseAllLowercase() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	HttpRequestBase request = new HttpGet(TestResource.TEST_ENDPOINT);
	request.setHeader(
		HttpHeaderNames.CONNECTION.toString(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<Map.Entry<String, String>> header = translator.getHeader(
		request, HttpHeaderNames.CONNECTION.toString());
	Assert.assertTrue("Expected header was not present", header.isPresent());
    }
    
    @Test
    public void testGetHeaderHttpRequestBaseAllUppercase() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	HttpRequestBase request = new HttpGet(TestResource.TEST_ENDPOINT);
	request.setHeader(
		HttpHeaderNames.CONNECTION.toString().toUpperCase(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<Map.Entry<String, String>> header = translator.getHeader(
		request, HttpHeaderNames.CONNECTION.toString());
	Assert.assertTrue("Expected header was not present", header.isPresent());
    }
    
    @Test
    public void testGetHeaderHttpRequestBaseAllUppercaseInParameter() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	HttpRequestBase request = new HttpGet(TestResource.TEST_ENDPOINT);
	request.setHeader(
		HttpHeaderNames.CONNECTION.toString(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<Map.Entry<String, String>> header = translator.getHeader(
		request, HttpHeaderNames.CONNECTION.toString().toUpperCase());
	Assert.assertTrue("Expected header was not present", header.isPresent());
    }
    
    @Test
    public void testGetHeaderHttpRequestBaseCamelCaseInParameter() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	HttpRequestBase request = new HttpGet(TestResource.TEST_ENDPOINT);
	request.setHeader(
		HttpHeaderNames.CONNECTION.toString(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<Map.Entry<String, String>> header = translator.getHeader(
		request, HttpHeaderNames.CONNECTION.toString().replace("c", "C").replace("n", "N"));
	Assert.assertTrue("Expected header was not present", header.isPresent());
    }
    
    @Test
    public void testGetHeaderHttpRequestBaseWithStartAndTrailingSpaces() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	StringBuilder headerName = new StringBuilder(HttpHeaderNames.CONNECTION.toString());
	headerName.insert(0, SPACE);
	headerName.insert(headerName.length(), SPACE);
	
	HttpRequestBase request = new HttpGet(TestResource.TEST_ENDPOINT);
	request.setHeader(
		headerName.toString(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<Map.Entry<String, String>> header = translator.getHeader(
		request, HttpHeaderNames.CONNECTION.toString());
	Assert.assertTrue("Expected header was not present", header.isPresent());
    }
    
    @Test
    public void testGetHeaderHttpRequestBaseWithStartAndTrailingSpacesInParameter() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	StringBuilder headerName = new StringBuilder(HttpHeaderNames.CONNECTION.toString());
	headerName.insert(0, SPACE);
	headerName.insert(headerName.length(), SPACE);
	
	HttpRequestBase request = new HttpGet(TestResource.TEST_ENDPOINT);
	request.setHeader(
		HttpHeaderNames.CONNECTION.toString(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<Map.Entry<String, String>> header = translator.getHeader(
		request, headerName.toString());
	Assert.assertTrue("Expected header was not present", header.isPresent());
    }
    
    @Test
    public void testGetHeaderHttpRequestBaseAbsentHeader() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	HttpRequestBase request = new HttpGet(TestResource.TEST_ENDPOINT);
	
	Optional<Map.Entry<String, String>> header = translator.getHeader(
		request, HttpHeaderNames.CONNECTION.toString());
	Assert.assertTrue("Header was present when it shouldn't have been", !header.isPresent());
    }
    
    @Test
    public void testGetHeaderFullHttpRequestAllLowercase() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	FullHttpRequest request = new DefaultFullHttpRequest(
		HttpVersion.HTTP_1_0, HttpMethod.GET, TestResource.TEST_ENDPOINT);
	request.headers().add(
		HttpHeaderNames.CONNECTION.toString(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<Map.Entry<String, String>> header = translator.getHeader(
		request, HttpHeaderNames.CONNECTION.toString());
	Assert.assertTrue("Expected header was not present", header.isPresent());
    }
    
    @Test
    public void testGetHeaderFullHttpRequestAllUppercase() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	FullHttpRequest request = new DefaultFullHttpRequest(
		HttpVersion.HTTP_1_0, HttpMethod.GET, TestResource.TEST_ENDPOINT);
	request.headers().add(
		HttpHeaderNames.CONNECTION.toString().toUpperCase(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<Map.Entry<String, String>> header = translator.getHeader(
		request, HttpHeaderNames.CONNECTION.toString());
	Assert.assertTrue("Expected header was not present", header.isPresent());
    }
    
    @Test
    public void testGetHeaderFullHttpRequestAllUppercaseInParameter() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	FullHttpRequest request = new DefaultFullHttpRequest(
		HttpVersion.HTTP_1_0, HttpMethod.GET, TestResource.TEST_ENDPOINT);
	request.headers().add(
		HttpHeaderNames.CONNECTION.toString(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<Map.Entry<String, String>> header = translator.getHeader(
		request, HttpHeaderNames.CONNECTION.toString().toUpperCase());
	Assert.assertTrue("Expected header was not present", header.isPresent());
    }
    
    @Test
    public void testGetHeaderFullHttpRequestCamelCase() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	FullHttpRequest request = new DefaultFullHttpRequest(
		HttpVersion.HTTP_1_0, HttpMethod.GET, TestResource.TEST_ENDPOINT);
	request.headers().add(
		HttpHeaderNames.CONNECTION.toString().replace("c", "C").replace("n", "N"), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<Map.Entry<String, String>> header = translator.getHeader(
		request, HttpHeaderNames.CONNECTION.toString());
	Assert.assertTrue("Expected header was not present", header.isPresent());
    }
    
    @Test
    public void testGetHeaderFullHttpRequestCamelCaseInParameter() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	FullHttpRequest request = new DefaultFullHttpRequest(
		HttpVersion.HTTP_1_0, HttpMethod.GET, TestResource.TEST_ENDPOINT);
	request.headers().add(
		HttpHeaderNames.CONNECTION.toString(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<Map.Entry<String, String>> header = translator.getHeader(
		request, HttpHeaderNames.CONNECTION.toString().replace("c", "C").replace("n", "N"));
	Assert.assertTrue("Expected header was not present", header.isPresent());
    }
    
    @Test
    public void testGetHeaderFullHttpRequestWithStartAndTrailingSpacesAbsentHeader() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	StringBuilder headerName = new StringBuilder(HttpHeaderNames.CONNECTION.toString());
	headerName.insert(0, SPACE);
	headerName.insert(headerName.length(), SPACE);
	
	FullHttpRequest request = new DefaultFullHttpRequest(
		HttpVersion.HTTP_1_0, HttpMethod.GET, TestResource.TEST_ENDPOINT);
	
	/** DefaultFullHttpRequest checks header values on addition, make sure this functionality continues to work as expected*/
	try {
	    request.headers().add(
		headerName.toString(), 
		HttpHeaderValues.KEEP_ALIVE.toString());	
	} catch(IllegalArgumentException e) {
	    Optional<Map.Entry<String, String>> header = translator.getHeader(
		    request, HttpHeaderNames.CONNECTION.toString());
	    Assert.assertTrue("Expected header was present (it shouldn't - backward compatibility)", !header.isPresent());
	    return;
	}
	
	Assert.fail("IllegalArgumentException was expected on invalid header addition, but it did not occur");
    }
    
    @Test
    public void testGetHeaderFullHttpRequestAbsentHeader() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	FullHttpRequest request = new DefaultFullHttpRequest(
		HttpVersion.HTTP_1_0, HttpMethod.GET, TestResource.TEST_ENDPOINT);
	
	Optional<Map.Entry<String, String>> header = translator.getHeader(
		request, HttpHeaderNames.CONNECTION.toString());
	Assert.assertTrue("Header was present when it shouldn't have been", !header.isPresent());
    }
    
    @Test
    public void testGetHeaderValueHttpRequestBaseAllLowercase() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	HttpRequestBase request = new HttpGet(TestResource.TEST_ENDPOINT);
	request.setHeader(
		HttpHeaderNames.CONNECTION.toString(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<String> headerValue = translator.getHeaderValue(
		request, HttpHeaderNames.CONNECTION.toString());
	Assert.assertTrue("Expected header value was not present", headerValue.isPresent());
	Assert.assertTrue("Expected header value was not present", 
		headerValue.get().equals(HttpHeaderValues.KEEP_ALIVE.toString()));
    }
    
    @Test
    public void testGetHeaderValueHttpRequestBaseAllUppercase() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	HttpRequestBase request = new HttpGet(TestResource.TEST_ENDPOINT);
	request.setHeader(
		HttpHeaderNames.CONNECTION.toString().toUpperCase(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<String> headerValue = translator.getHeaderValue(
		request, HttpHeaderNames.CONNECTION.toString());
	Assert.assertTrue("Expected header value was not present", headerValue.isPresent());
	Assert.assertTrue("Expected header value was not present", 
		headerValue.get().equals(HttpHeaderValues.KEEP_ALIVE.toString()));
    }
    
    @Test
    public void testGetHeaderValueHttpRequestBaseAllUppercaseInParameter() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	HttpRequestBase request = new HttpGet(TestResource.TEST_ENDPOINT);
	request.setHeader(
		HttpHeaderNames.CONNECTION.toString(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<String> headerValue = translator.getHeaderValue(
		request, HttpHeaderNames.CONNECTION.toString().toUpperCase());
	Assert.assertTrue("Expected header value was not present", headerValue.isPresent());
	Assert.assertTrue("Expected header value was not present", 
		headerValue.get().equals(HttpHeaderValues.KEEP_ALIVE.toString()));
    }
    
    @Test
    public void testGetHeaderValueHttpRequestBaseCamelCase() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String camelCaseHeader = HttpHeaderNames.CONNECTION.toString().replace("c", "C").replace("n", "N");
	
	HttpRequestBase request = new HttpGet(TestResource.TEST_ENDPOINT);
	request.setHeader(
		camelCaseHeader, 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<String> headerValue = translator.getHeaderValue(
		request, HttpHeaderNames.CONNECTION.toString());
	Assert.assertTrue("Expected header value was not present", headerValue.isPresent());
	Assert.assertTrue("Expected header value was not present", 
		headerValue.get().equals(HttpHeaderValues.KEEP_ALIVE.toString()));
    }
    
    @Test
    public void testGetHeaderValueHttpRequestBaseCamelCaseInParameter() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String camelCaseHeader = HttpHeaderNames.CONNECTION.toString().replace("c", "C").replace("n", "N");
	
	HttpRequestBase request = new HttpGet(TestResource.TEST_ENDPOINT);
	request.setHeader(
		HttpHeaderNames.CONNECTION.toString(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<String> headerValue = translator.getHeaderValue(request,  camelCaseHeader);
	Assert.assertTrue("Expected header value was not present", headerValue.isPresent());
	Assert.assertTrue("Expected header value was not present", 
		headerValue.get().equals(HttpHeaderValues.KEEP_ALIVE.toString()));
    }
    
    @Test
    public void testGetHeaderValueHttpRequestBaseWithStartAndTrailingSpaces() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	StringBuilder headerName = new StringBuilder(HttpHeaderNames.CONNECTION.toString());
	headerName.insert(0, SPACE);
	headerName.insert(headerName.length(), SPACE);
	
	HttpRequestBase request = new HttpGet(TestResource.TEST_ENDPOINT);
	request.setHeader(
		headerName.toString(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<String> headerValue = translator.getHeaderValue(
		request, HttpHeaderNames.CONNECTION.toString());
	Assert.assertTrue("Expected header value was not present", headerValue.isPresent());
	Assert.assertTrue("Expected header value was not present", 
		headerValue.get().equals(HttpHeaderValues.KEEP_ALIVE.toString()));
    }
    
    @Test
    public void testGetHeaderValueHttpRequestBaseWithStartAndTrailingSpacesInParameter() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	StringBuilder headerName = new StringBuilder(HttpHeaderNames.CONNECTION.toString());
	headerName.insert(0, SPACE);
	headerName.insert(headerName.length(), SPACE);
	
	HttpRequestBase request = new HttpGet(TestResource.TEST_ENDPOINT);
	request.setHeader(
		HttpHeaderNames.CONNECTION.toString(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<String> headerValue = translator.getHeaderValue(
		request, headerName.toString());
	Assert.assertTrue("Expected header value was not present", headerValue.isPresent());
	Assert.assertTrue("Expected header value was not present", 
		headerValue.get().equals(HttpHeaderValues.KEEP_ALIVE.toString()));
    }
    
    @Test
    public void testGetHeaderValueHttpRequestBaseAbsentHeader() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	HttpRequestBase request = new HttpGet(TestResource.TEST_ENDPOINT);
	String headerName = "ASDFGH";
	request.setHeader(
		headerName, 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<String> headerValue = translator.getHeaderValue(
		request, HttpHeaderNames.CONNECTION.toString());
	Assert.assertTrue(
		"Header was present when it shouldn't have been", 
		!headerValue.isPresent());
    }
    
    @Test
    public void testGetHeaderValueFullHttpRequestAllLowercase() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	FullHttpRequest request = new DefaultFullHttpRequest(
		HttpVersion.HTTP_1_0, HttpMethod.GET, TestResource.TEST_ENDPOINT);
	request.headers().add(
		HttpHeaderNames.CONNECTION.toString(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<String> headerValue = translator.getHeaderValue(
		request, HttpHeaderNames.CONNECTION.toString());
	Assert.assertTrue("Expected header was not present", headerValue.isPresent());
	Assert.assertTrue("Expected header value was not present", 
		headerValue.get().equals(HttpHeaderValues.KEEP_ALIVE.toString()));
    }
    
    @Test
    public void testGetHeaderValueFullHttpRequestAllUppercase() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	FullHttpRequest request = new DefaultFullHttpRequest(
		HttpVersion.HTTP_1_0, HttpMethod.GET, TestResource.TEST_ENDPOINT);
	request.headers().add(
		HttpHeaderNames.CONNECTION.toString().toUpperCase(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<String> headerValue = translator.getHeaderValue(
		request, HttpHeaderNames.CONNECTION.toString());
	Assert.assertTrue("Expected header was not present", headerValue.isPresent());
	Assert.assertTrue("Expected header value was not present", 
		headerValue.get().equals(HttpHeaderValues.KEEP_ALIVE.toString()));
    }
    
    @Test
    public void testGetHeaderValueFullHttpRequestAllUppercaseInParameter() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	FullHttpRequest request = new DefaultFullHttpRequest(
		HttpVersion.HTTP_1_0, HttpMethod.GET, TestResource.TEST_ENDPOINT);
	request.headers().add(
		HttpHeaderNames.CONNECTION.toString(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<String> headerValue = translator.getHeaderValue(
		request, HttpHeaderNames.CONNECTION.toString().toUpperCase());
	Assert.assertTrue("Expected header was not present", headerValue.isPresent());
	Assert.assertTrue("Expected header value was not present", 
		headerValue.get().equals(HttpHeaderValues.KEEP_ALIVE.toString()));
    }
    
    @Test
    public void testGetHeaderValueFullHttpRequestCamelCase() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String camelCaseHeader = HttpHeaderNames.CONNECTION.toString().replace("c", "C").replace("n", "N");
	
	FullHttpRequest request = new DefaultFullHttpRequest(
		HttpVersion.HTTP_1_0, HttpMethod.GET, TestResource.TEST_ENDPOINT);
	request.headers().add(
		camelCaseHeader, 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<String> headerValue = translator.getHeaderValue(
		request, HttpHeaderNames.CONNECTION.toString());
	Assert.assertTrue("Expected header was not present", headerValue.isPresent());
	Assert.assertTrue("Expected header value was not present", 
		headerValue.get().equals(HttpHeaderValues.KEEP_ALIVE.toString()));
    }
    
    @Test
    public void testGetHeaderValueFullHttpRequestCamelCaseInParameter() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	FullHttpRequest request = new DefaultFullHttpRequest(
		HttpVersion.HTTP_1_0, HttpMethod.GET, TestResource.TEST_ENDPOINT);
	request.headers().add(
		HttpHeaderNames.CONNECTION.toString(), 
		HttpHeaderValues.KEEP_ALIVE.toString());
	
	Optional<String> headerValue = translator.getHeaderValue(
		request, HttpHeaderNames.CONNECTION.toString().replace("c", "C").replace("n", "N"));
	Assert.assertTrue("Expected header was not present", headerValue.isPresent());
	Assert.assertTrue("Expected header value was not present", 
		headerValue.get().equals(HttpHeaderValues.KEEP_ALIVE.toString()));
    }
    
    @Test
    public void testGetHeaderValueFullHttpRequestWithStartAndTrailingSpacesAbsentHeader() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	StringBuilder headerName = new StringBuilder(HttpHeaderNames.CONNECTION.toString());
	headerName.insert(0, SPACE);
	headerName.insert(headerName.length(), SPACE);
	
	FullHttpRequest request = new DefaultFullHttpRequest(
		HttpVersion.HTTP_1_0, HttpMethod.GET, TestResource.TEST_ENDPOINT);
	
	/** DefaultFullHttpRequest checks header values on addition, make sure this functionality continues to work as expected*/
	try {
	    request.headers().add(
		headerName.toString(), 
		HttpHeaderValues.KEEP_ALIVE.toString());	
	} catch(IllegalArgumentException e) {
	    Optional<String> header = translator.getHeaderValue(
		    request, HttpHeaderNames.CONNECTION.toString());
	    Assert.assertTrue(
		    "Expected header value was present (it shouldn't - backward compatibility)", 
		    !header.isPresent());
	    return;
	}
	
	Assert.fail("IllegalArgumentException was expected on invalid header addition, but it did not occur");
    }
    
    @Test
    public void testGetHeaderValueFullHttpRequestAbsentHeader() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	FullHttpRequest request = new DefaultFullHttpRequest(
		HttpVersion.HTTP_1_0, HttpMethod.GET, TestResource.TEST_ENDPOINT);
	
	Optional<String> headerValue = translator.getHeaderValue(
		request, HttpHeaderNames.CONNECTION.toString());
	Assert.assertTrue(
		"Header was present when it shouldn't have been", 
		!headerValue.isPresent());
    }
    
    @Test
    public void testGetBodyGETRequestWithoutBody() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	HttpRequestBase request = new HttpGet(TestResource.TEST_ENDPOINT);
	
	Assert.assertTrue(
		"Request body was present when it shouldn't have been", 
		!translator.getBody(request).isPresent());
    }
    
    @Test
    public void testGetBodyGETRequestWithBody() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	HttpRequestBase request = new HttpGet(TestResource.TEST_ENDPOINT);
	String requestBody = "RequestBody";
	
	try {
	    HttpEntityEnclosingRequest.class.cast(request).setEntity(new StringEntity(requestBody));
	} catch(ClassCastException e) {
	    Assert.assertTrue(
		    "Request body was present when it shouldn't have been", 
		    !translator.getBody(request).isPresent());
	    return;
	}
	
	Assert.fail("GET Request was successfully casted to HttpEntityEnclosingRequest - backward compatibility");
    }
    
    @Test
    public void testGetBodyPOSTRequestWithoutBody() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	HttpPost request = new HttpPost(TestResource.TEST_ENDPOINT);
	
	Assert.assertTrue(
		"Request body was present when it shouldn't have been", 
		!translator.getBody(request).isPresent());
    }
    
    @Test
    public void testGetBodyPOSTRequestWithEmptyCharacterBody() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String requestBodyContent = EMPTY_STRING;
	
	HttpPost request = new HttpPost(TestResource.TEST_ENDPOINT);
	request.setEntity(new StringEntity(requestBodyContent));
	
	Assert.assertTrue(
		"Request body value was present when it shouldn't have been",
		!translator.getBody(request).isPresent());
    }
    
    @Test
    public void testGetBodyPOSTRequestWithSpaceCharacterBody() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String requestBodyContent = SPACE;
	
	HttpPost request = new HttpPost(TestResource.TEST_ENDPOINT);
	request.setEntity(new StringEntity(requestBodyContent));
	
	Assert.assertTrue(
		"Request body value was present when it shouldn't have been",
		!translator.getBody(request).isPresent());
    }
    
    @Test
    public void testGetBodyPOSTRequestWithNewLineBody() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String requestBodyContent = NEWLINE;
	
	HttpPost request = new HttpPost(TestResource.TEST_ENDPOINT);
	request.setEntity(new StringEntity(requestBodyContent));
	
	Assert.assertTrue(
		"Request body value was present when it shouldn't have been",
		!translator.getBody(request).isPresent());
    }
    
    @Test
    public void testGetBodyPOSTRequestWithBody() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String requestBodyContent = "RequestBody";
	
	HttpPost request = new HttpPost(TestResource.TEST_ENDPOINT);
	request.setEntity(new StringEntity(requestBodyContent));
	
	Assert.assertTrue(
		"Request body value was not present",
		translator.getBody(request).isPresent());
	Assert.assertTrue(
		"Request body value was different than expected",
		translator.getBody(request).get().equals(requestBodyContent));
    }
    
    @Test
    public void testGetBodyPOSTRequestWithBodyCharsetUTF16WithUTF8Characters() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String requestBodyContent = "RequestBody";
	
	HttpPost request = new HttpPost(TestResource.TEST_ENDPOINT);
	request.setEntity(new StringEntity(requestBodyContent, CharsetUtil.UTF_16));
	
	Assert.assertTrue(
		"Request body value was not present",
		translator.getBody(request).isPresent());
	Assert.assertTrue(
		"Request body value was different than expected",
		translator.getBody(request).get().equals(requestBodyContent));
    }
    
    @Test
    public void testGetBodyHttpResponseWithoutBody() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	HttpResponse response = new DefaultHttpResponseFactory().newHttpResponse(
		new ProtocolVersion(org.apache.http.HttpVersion.HTTP, 1, 0), 
		HttpResponseStatus.OK.code(), 
		HttpCoreContext.create());
	
	Assert.assertTrue(
		"Request body value was present when it shouldn't have been",
		!translator.getBody(response).isPresent());
    }
    
    @Test
    public void testGetBodyHttpResponseWithEmptyCharacterBody() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String responseBodyContent = EMPTY_STRING;
	
	HttpResponse response = new DefaultHttpResponseFactory().newHttpResponse(
		new ProtocolVersion(org.apache.http.HttpVersion.HTTP, 1, 0), 
		HttpResponseStatus.OK.code(), 
		HttpCoreContext.create());
	
	response.setEntity(new StringEntity(responseBodyContent));
	
	Assert.assertTrue(
		"Request body value was present when it shouldn't have been",
		!translator.getBody(response).isPresent());
    }
    
    @Test
    public void testGetBodyHttpResponseWithNewLineBody() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String responseBodyContent = NEWLINE;
	
	HttpResponse response = new DefaultHttpResponseFactory().newHttpResponse(
		new ProtocolVersion(org.apache.http.HttpVersion.HTTP, 1, 0), 
		HttpResponseStatus.OK.code(), 
		HttpCoreContext.create());
	
	response.setEntity(new StringEntity(responseBodyContent));
	
	Assert.assertTrue(
		"Request body value was present when it shouldn't have been",
		!translator.getBody(response).isPresent());
    }
    
    @Test
    public void testGetBodyHttpResponseWithBody() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String responseBodyContent = "ResponseBody";
	
	HttpResponse response = new DefaultHttpResponseFactory().newHttpResponse(
		new ProtocolVersion(org.apache.http.HttpVersion.HTTP, 1, 0), 
		HttpResponseStatus.OK.code(), 
		HttpCoreContext.create());
	
	response.setEntity(new StringEntity(responseBodyContent));
	
	Assert.assertTrue(
		"Request body value was not present",
		translator.getBody(response).isPresent());
    }
    
    @Test
    public void testGetBodyResponseWithBodyCharsetUTF16WithUTF8Characters() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String responseBodyContent = "ResponseBody";
	
	HttpResponse response = new DefaultHttpResponseFactory().newHttpResponse(
		new ProtocolVersion(org.apache.http.HttpVersion.HTTP, 1, 0), 
		HttpResponseStatus.OK.code(), 
		HttpCoreContext.create());
	
	response.setEntity(new StringEntity(responseBodyContent, CharsetUtil.UTF_16));
	
	Assert.assertTrue(
		"Request body value was not present",
		translator.getBody(response).isPresent());
    }
    
    @Test
    public void testFromStringUrlUrlWithValue() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String url = TestResource.TEST_ENDPOINT;
	Map<String, String> requestHeaders = Maps.<String, String>newHashMap();
	HttpRequestBase request = translator.fromStringUrl(
		requestHeaders, 
		url);
	
	Assert.assertTrue(
		"Request URL different than expected",
		request.getURI().toURL().toString().equals(TestResource.TEST_ENDPOINT));
    }
    
    @Test
    public void testFromStringUrlUrlNull() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String url = null;
	Map<String, String> requestHeaders = Maps.<String, String>newHashMap();
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.ENDPOINT_URL_MUST_BE_PROVIDED);
	HttpRequestBase request = translator.fromStringUrl(
		requestHeaders, 
		url);
    }
    
    @Test
    public void testFromStringUrlEmptyCharacterUrl() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String url = EMPTY_STRING;
	Map<String, String> requestHeaders = Maps.<String, String>newHashMap();
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.ENDPOINT_URL_MUST_BE_PROVIDED);
	HttpRequestBase request = translator.fromStringUrl(
		requestHeaders, 
		url);
    }
    
    @Test
    public void testFromStringUrlUrlSpaceCharacter() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String url = SPACE;
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.ENDPOINT_URL_MUST_BE_PROVIDED);
	HttpRequestBase request = translator.fromStringUrl(
		Maps.<String, String> newHashMap(), 
		url);
    }
    
    @Test
    public void testFromStringUrlUrlNewLineCharacter() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String url = NEWLINE;
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.ENDPOINT_URL_MUST_BE_PROVIDED);
	HttpRequestBase request = translator.fromStringUrl(
		Maps.<String, String> newHashMap(), 
		url);
    }
    
    @Test
    public void testFromStringUrlHeadersNull() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String url = TestResource.TEST_ENDPOINT;
	Map<String, String> requestHeaders = null;
	
	HttpRequestBase request = translator.fromStringUrl(
		requestHeaders, 
		url);
	
	Assert.assertTrue(
		"Request had headers present when it shouldn't have had",
		request.getAllHeaders().length == 0);
    }
    
    @Test
    public void testFromStringUrlHeadersEmpty() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String url = TestResource.TEST_ENDPOINT;
	Map<String, String> requestHeaders = Maps.<String, String>newHashMap();
	
	HttpRequestBase request = translator.fromStringUrl(
		requestHeaders, 
		url);
	
	Assert.assertTrue(
		"Request had headers present when it shouldn't have had",
		request.getAllHeaders().length == 0);
    }
    
    @Test
    public void testFromStringUrlHeadersHeaderPresent() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String url = TestResource.TEST_ENDPOINT;
	Map<String, String> headers = Maps.<String, String>newHashMap();
	headers.put(
		HttpHeaderNames.CONTENT_TYPE.toString(), 
		HttpHeaderValues.APPLICATION_OCTET_STREAM.toString());
	
	HttpRequestBase request = translator.fromStringUrl(
		headers, 
		url);
	
	Assert.assertTrue(
		"Request did not have expected header present",
		translator.getHeader(request, HttpHeaderNames.CONTENT_TYPE.toString()).isPresent());
    }
    
    @Test
    public void testFromStringUrlHeadersEmptyHeader() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String url = TestResource.TEST_ENDPOINT;
	Map<String, String> headers = Maps.<String, String>newHashMap();
	headers.put(
		EMPTY_STRING, 
		HttpHeaderValues.APPLICATION_OCTET_STREAM.toString());
	
	HttpRequestBase request = translator.fromStringUrl(
		headers, 
		url);
	
	Assert.assertTrue(
		"Request had headers present when it shouldn't have had",
		request.getAllHeaders().length == 0);
    }
    
    @Test
    public void testFromStringUrlHeadersEmptyHeaderAndValidHeader() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String url = TestResource.TEST_ENDPOINT;
	Map<String, String> headers = Maps.<String, String>newHashMap();
	headers.put(
		EMPTY_STRING, 
		HttpHeaderValues.APPLICATION_OCTET_STREAM.toString());
	headers.put(
		HttpHeaderNames.ACCEPT_CHARSET.toString(), 
		CharsetUtil.UTF_8.name());
	
	HttpRequestBase request = translator.fromStringUrl(
		headers, 
		url);
	
	Assert.assertTrue(
		"Request had more headers present than expected",
		request.getAllHeaders().length == 1);
	Assert.assertTrue(
		"Request header expected was not present",
		translator.getHeader(request, HttpHeaderNames.ACCEPT_CHARSET.toString()).isPresent());
    }
    
    @Test
    public void testFromStringUrlHeadersEmptyCharacterHeader() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String url = TestResource.TEST_ENDPOINT;
	Map<String, String> headers = Maps.<String, String>newHashMap();
	headers.put(
		NEWLINE, 
		HttpHeaderValues.APPLICATION_OCTET_STREAM.toString());
	
	HttpRequestBase request = translator.fromStringUrl(
		headers, 
		url);
	
	Assert.assertTrue(
		"Request had headers present when it shouldn't have had",
		request.getAllHeaders().length == 0);
    }
    
    @Test
    public void testFromStringUrlHeadersNewLineHeader() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String url = TestResource.TEST_ENDPOINT;
	Map<String, String> headers = Maps.<String, String>newHashMap();
	headers.put(
		NEWLINE, 
		HttpHeaderValues.APPLICATION_OCTET_STREAM.toString());
	
	HttpRequestBase request = translator.fromStringUrl(
		headers, 
		url);
	
	Assert.assertTrue(
		"Request had headers present when it shouldn't have had",
		request.getAllHeaders().length == 0);
    }
    
    @Test
    public void testFromStringUrlHeadersIgnoreContentLengthHeader() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String url = TestResource.TEST_ENDPOINT;
	Map<String, String> headers = Maps.<String, String>newHashMap();
	headers.put(
		HttpHeaderNames.CONTENT_LENGTH.toString(), 
		String.valueOf(15));
	
	HttpRequestBase request = translator.fromStringUrl(
		headers, 
		url);
	
	Assert.assertTrue(
		"Request had headers present when it shouldn't have had",
		request.getAllHeaders().length == 0);
    }
    
    @Test
    public void testFromStringUrlHeadersHeaderValue() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String url = TestResource.TEST_ENDPOINT;
	Map<String, String> headers = Maps.<String, String>newHashMap();
	headers.put(
		HttpHeaderNames.CONTENT_TYPE.toString(), 
		HttpHeaderValues.APPLICATION_OCTET_STREAM.toString());
	
	HttpRequestBase request = translator.fromStringUrl(
		headers, 
		url);
	
	Assert.assertTrue(
		"Request did not have expected header value present",
		translator.getHeaderValue(request, HttpHeaderNames.CONTENT_TYPE.toString()).isPresent());	
	Assert.assertTrue(
		"Request did not have expected header value",
		translator.getHeaderValue(request, HttpHeaderNames.CONTENT_TYPE.toString()).get()
			.equals(HttpHeaderValues.APPLICATION_OCTET_STREAM.toString()));
    }
    
    @Test
    public void testFromJsonStringHttpHeadersNullHeaders() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	JsonObject request = new JsonObject();
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(Fields.METHOD, HttpMethod.GET.name());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	
	HttpHeaders headers = null;
	
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
	
	Assert.assertTrue("Expected empty headers, but was not", translatedRequest.getAllHeaders().length == 0);
    }
    
    @Test
    public void testFromJsonStringHttpHeadersEmptyHeaders() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	JsonObject request = new JsonObject();
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(Fields.METHOD, HttpMethod.GET.name());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
	
	Assert.assertTrue("Expected empty headers, but was not", translatedRequest.getAllHeaders().length == 0);
    }
    
    @Test
    public void testFromJsonStringHttpHeadersIgnoreContentLengthHeader() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	JsonObject request = new JsonObject();
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(Fields.METHOD, HttpMethod.GET.name());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	
	HttpHeaders headers = new DefaultHttpHeaders();
	headers.add(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
	
	Assert.assertTrue("Expected absent content-length header, but was not", 
		!translator.getHeader(translatedRequest, HttpHeaderNames.CONTENT_LENGTH.toString()).isPresent());
    }
    
    @Test
    public void testFromJsonStringHttpHeadersPresentJsonObjectEmptyHeaders() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	JsonObject request = new JsonObject();
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(Fields.METHOD, HttpMethod.GET.name());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	
	HttpHeaders headers = new DefaultHttpHeaders();
	headers.add(HttpHeaderNames.LAST_MODIFIED, TestResource.DATE);
	
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
	
	Assert.assertTrue("Expected header present, but was not", 
		translator.getHeader(translatedRequest, HttpHeaderNames.LAST_MODIFIED.toString()).isPresent());
	Assert.assertTrue("Expected header value present, but was not", 
		translator.getHeaderValue(translatedRequest, HttpHeaderNames.LAST_MODIFIED.toString()).isPresent());
	Assert.assertTrue("Header value was different than expected", 
		translator.getHeaderValue(
			translatedRequest, HttpHeaderNames.LAST_MODIFIED.toString()).get().equals(TestResource.DATE));
    }
    
    @Test
    public void testFromJsonStringHttpHeadersAbsentJsonObjectPresentHeaders() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.LAST_MODIFIED.toString(), TestResource.DATE);
	headersJson.addProperty(HttpHeaderNames.DATE.toString(), TestResource.DATE);
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(Fields.METHOD, HttpMethod.GET.name());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
	
	Assert.assertTrue("Expected header present, but was not", 
		translator.getHeader(translatedRequest, HttpHeaderNames.LAST_MODIFIED.toString()).isPresent());
	Assert.assertTrue("Expected header value present, but was not", 
		translator.getHeaderValue(translatedRequest, HttpHeaderNames.LAST_MODIFIED.toString()).isPresent());
	Assert.assertTrue("Header value was different than expected", 
		translator.getHeaderValue(
			translatedRequest, HttpHeaderNames.LAST_MODIFIED.toString()).get().equals(TestResource.DATE));
	
	Assert.assertTrue("Expected header present, but was not", 
		translator.getHeader(translatedRequest, HttpHeaderNames.DATE.toString()).isPresent());
	Assert.assertTrue("Expected header value present, but was not", 
		translator.getHeaderValue(translatedRequest, HttpHeaderNames.DATE.toString()).isPresent());
	Assert.assertTrue("Header value was different than expected", 
		translator.getHeaderValue(
			translatedRequest, HttpHeaderNames.DATE.toString()).get().equals(TestResource.DATE));
    }
    
    @Test
    public void testFromJsonStringHttpHeadersPresentJsonObjectPresentHeaders() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String expectedDate = TestResource.DATE;
	String notExpectedDate = "SomeDate";
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.LAST_MODIFIED.toString(), expectedDate);
	headersJson.addProperty(HttpHeaderNames.DATE.toString(), expectedDate);
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(Fields.METHOD, HttpMethod.GET.name());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	headers.add(HttpHeaderNames.LAST_MODIFIED, notExpectedDate);
	headers.add(HttpHeaderNames.DATE, notExpectedDate);
	
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
	
	Assert.assertTrue("Expected header present, but was not", 
		translator.getHeader(translatedRequest, HttpHeaderNames.LAST_MODIFIED.toString()).isPresent());
	Assert.assertTrue("Expected header value present, but was not", 
		translator.getHeaderValue(translatedRequest, HttpHeaderNames.LAST_MODIFIED.toString()).isPresent());
	Assert.assertTrue("Header value was different than expected", 
		translator.getHeaderValue(
			translatedRequest, HttpHeaderNames.LAST_MODIFIED.toString()).get().equals(TestResource.DATE));
	
	Assert.assertTrue("Expected header present, but was not", 
		translator.getHeader(translatedRequest, HttpHeaderNames.DATE.toString()).isPresent());
	Assert.assertTrue("Expected header value present, but was not", 
		translator.getHeaderValue(translatedRequest, HttpHeaderNames.DATE.toString()).isPresent());
	Assert.assertTrue("Header value was different than expected", 
		translator.getHeaderValue(
			translatedRequest, HttpHeaderNames.DATE.toString()).get().equals(TestResource.DATE));
    }
    
    @Test
    public void testFromJsonStringJsonObjectNull() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	HttpHeaders headers = new DefaultHttpHeaders();
	headers.add(HttpHeaderNames.LAST_MODIFIED, TestResource.DATE);
	headers.add(HttpHeaderNames.DATE, TestResource.DATE);
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.JSON_OBJECT_EXPECTED_EMPTY_FOUND);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, null);
    }
    
    @Test
    public void testFromJsonStringJsonObjectEmptyString() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	HttpHeaders headers = new DefaultHttpHeaders();
	headers.add(HttpHeaderNames.LAST_MODIFIED, TestResource.DATE);
	headers.add(HttpHeaderNames.DATE, TestResource.DATE);
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.JSON_OBJECT_EXPECTED_EMPTY_FOUND);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, EMPTY_STRING);
    }
    
    @Test
    public void testFromJsonStringJsonObjectEmptyObject() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	JsonObject request = new JsonObject();
	
	HttpHeaders headers = new DefaultHttpHeaders();
	headers.add(HttpHeaderNames.LAST_MODIFIED, TestResource.DATE);
	headers.add(HttpHeaderNames.DATE, TestResource.DATE);
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.HTTP_METHOD_MUST_BE_SPECIFIED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectIgnoreContentLengthHeader() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(Fields.METHOD, HttpMethod.GET.name());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
	
	Assert.assertTrue("Expected absent content-length header, but was not", 
		!translator.getHeader(translatedRequest, HttpHeaderNames.CONTENT_LENGTH.toString()).isPresent());
    }
    
    @Test
    public void testFromJsonStringJsonObjectAndHttpHeadersIgnoreContentLengthHeader() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(Fields.METHOD, HttpMethod.GET.name());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	headers.add(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
	
	Assert.assertTrue("Expected absent content-length header, but was not", 
		!translator.getHeader(translatedRequest, HttpHeaderNames.CONTENT_LENGTH.toString()).isPresent());
    }
    
    @Test
    public void testFromJsonStringJsonObjectUnknownPropertyNames() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	JsonObject request = new JsonObject();
	request.addProperty("Property1", "PropertyValue1");
	request.addProperty("Property2", "PropertyValue2");
	request.addProperty("Property3", "PropertyValue3");
	request.add("Property4", new JsonObject());
	request.add("Property5", new JsonArray());
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.ENDPOINT_URL_MUST_BE_PROVIDED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectWithoutEnpointField() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.METHOD, HttpMethod.GET.name());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.ENDPOINT_URL_MUST_BE_PROVIDED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectWithEnpointFieldNull() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String endpoint = null;
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, endpoint);
	request.addProperty(Fields.METHOD, HttpMethod.GET.name());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.ENDPOINT_URL_MUST_BE_PROVIDED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectWithEnpointFieldEmptyString() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String endpoint = EMPTY_STRING;
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, endpoint);
	request.addProperty(Fields.METHOD, HttpMethod.GET.name());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.ENDPOINT_URL_MUST_BE_PROVIDED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectWithEnpointFieldSpaceCharacter() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String endpoint = SPACE;
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, endpoint);
	request.addProperty(Fields.METHOD, HttpMethod.GET.name());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.ENDPOINT_URL_MUST_BE_PROVIDED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectWithEnpointFieldNewLineCharacter() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String endpoint = NEWLINE;
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, endpoint);
	request.addProperty(Fields.METHOD, HttpMethod.GET.name());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.ENDPOINT_URL_MUST_BE_PROVIDED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectWithEnpointFieldUppercase() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String endpointKey = Fields.ENDPOINT.toUpperCase();
	String endpoint = TestResource.TEST_ENDPOINT;
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(endpointKey, endpoint);
	request.addProperty(Fields.METHOD, HttpMethod.GET.name());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
	Assert.assertTrue("Expected correct endpoint, but was not", 
		translatedRequest.getURI().toURL().toString().equals(TestResource.TEST_ENDPOINT));
    }
    
    @Test
    public void testFromJsonStringJsonObjectWithEnpointFieldUrlInvalid() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String endpoint = "INVALID_URL";
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, endpoint);
	request.addProperty(Fields.METHOD, HttpMethod.GET.name());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.ENDPOINT_URL_MUST_BE_VALID);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectWithMethodKeyLowercase() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String method = HttpMethod.GET.name().toLowerCase();
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(Fields.METHOD, method);
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
	Assert.assertTrue("Expected correct HTTP Method, but was not", 
		HttpMethod.valueOf(translatedRequest.getMethod()).equals(HttpMethod.GET));
    }
    
    @Test
    public void testFromJsonStringJsonObjectWithMethodKeyUpperCase() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String method = HttpMethod.GET.name().toUpperCase();
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(Fields.METHOD, method);
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
	Assert.assertTrue("Expected correct HTTP Method, but was not", 
		HttpMethod.valueOf(translatedRequest.getMethod()).equals(HttpMethod.GET));
    }
    
    @Test
    public void testFromJsonStringJsonObjectWithMethodKeyCamelCase() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String method = HttpMethod.GET.name().toUpperCase().replace("G", "g").replace("T", "t");
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(Fields.METHOD, method);
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
	Assert.assertTrue("Expected correct HTTP Method, but was not", 
		HttpMethod.valueOf(translatedRequest.getMethod()).equals(HttpMethod.GET));
    }
    
    @Test
    public void testFromJsonStringJsonObjectWithMethodKeyLeadingTrailingSpaces() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	StringBuilder method = new StringBuilder(HttpMethod.GET.name().toUpperCase());
	method.append(SPACE);
	method.insert(0, SPACE);
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(Fields.METHOD, method.toString());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
	Assert.assertTrue("Expected correct HTTP Method, but was not", 
		HttpMethod.valueOf(translatedRequest.getMethod()).equals(HttpMethod.GET));
    }
    
    @Test
    public void testFromJsonStringJsonObjectWithMethodFieldAbsent() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.HTTP_METHOD_MUST_BE_SPECIFIED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectWithMethodFieldNull() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String method = null;
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(Fields.METHOD, method);
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.HTTP_METHOD_MUST_BE_SPECIFIED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectWithMethodFieldEmptyString() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String method = EMPTY_STRING;
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(Fields.METHOD, method);
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.HTTP_METHOD_MUST_BE_SPECIFIED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectWithMethodFieldSpaceCharacter() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String method = SPACE;
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(Fields.METHOD, method);
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.HTTP_METHOD_MUST_BE_SPECIFIED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectWithMethodFieldNewLineCharacter() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String method = NEWLINE;
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(Fields.METHOD, method);
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.HTTP_METHOD_MUST_BE_SPECIFIED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectWithMethodFieldInvalid() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String method = "SomeMethod";
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(Fields.METHOD, method);
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.CORRECT_HTTP_METHOD_MUST_BE_SPECIFIED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectWithMethodFieldUppercase() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String methodKey = Fields.METHOD.toUpperCase();
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(methodKey, HttpMethod.GET.name());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
	
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
	Assert.assertTrue("Expected correct HTTP Method, but was not", 
		HttpMethod.valueOf(translatedRequest.getMethod()).equals(HttpMethod.GET));
    }
    
    @Test
    public void testFromJsonStringJsonObjectPUTRequest() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String methodKey = Fields.METHOD.toUpperCase();
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(methodKey, HttpMethod.PUT.name());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
		
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.ONLY_GET_POST_METHODS_SUPPORTED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectDELETERequest() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String methodKey = Fields.METHOD.toUpperCase();
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), 100);
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(methodKey, HttpMethod.DELETE.name());
	request.addProperty(Fields.REQUEST, TestResource.requestContentAsKeyValue().toString());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
		
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.ONLY_GET_POST_METHODS_SUPPORTED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectGETRequestRequestContentMissing() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String methodKey = Fields.METHOD;
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString());
	headersJson.addProperty(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString());
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(methodKey, HttpMethod.GET.name());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();
		
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
	Assert.assertTrue("Expected GET HTTP Method, but was not", 
		HttpMethod.valueOf(translatedRequest.getMethod()).equals(HttpMethod.GET));
	Assert.assertTrue("Expected Request without body, but it contained a body", 
		!HttpEntityEnclosingRequest.class.isInstance(translatedRequest));
    }
    
    @Test
    public void testFromJsonStringJsonObjectGETRequestRequestWithContent() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String methodKey = Fields.METHOD;
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString());
	headersJson.addProperty(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString());
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(methodKey, HttpMethod.GET.name());
	request.add(Fields.HEADERS, headersJson);
	request.addProperty(Fields.REQUEST, TestResource.REQUEST_STRING_VALUE);
	
	HttpHeaders headers = new DefaultHttpHeaders();
		
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
	Assert.assertTrue("Expected GET HTTP Method, but was not", 
		HttpMethod.valueOf(translatedRequest.getMethod()).equals(HttpMethod.GET));
	Assert.assertTrue("Expected Request without body, but it contained a body", 
		!HttpEntityEnclosingRequest.class.isInstance(translatedRequest));
    }
    
    @Test
    public void testFromJsonStringJsonObjectPOSTRequestRequestWithoutContent() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String methodKey = Fields.METHOD;
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString());
	headersJson.addProperty(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString());
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(methodKey, HttpMethod.POST.name());
	request.add(Fields.HEADERS, headersJson);
	
	HttpHeaders headers = new DefaultHttpHeaders();

	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
	Assert.assertTrue("Expected POST HTTP Method, but was not", 
		HttpMethod.valueOf(translatedRequest.getMethod()).equals(HttpMethod.POST));
	Assert.assertTrue("Expected Request without body, but it contained a body", 
		!translator.getBody(translatedRequest).isPresent());
    }
    
    @Test
    public void testFromJsonStringJsonObjectPOSTRequestRequestWithContentNull() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String methodKey = Fields.METHOD;
	String requestValue = null;
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString());
	headersJson.addProperty(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString());
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(methodKey, HttpMethod.POST.name());
	request.add(Fields.HEADERS, headersJson);
	request.addProperty(Fields.REQUEST, requestValue);
	
	HttpHeaders headers = new DefaultHttpHeaders();
		
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.REQUEST_PARAMS_MUST_BE_PROVIDED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectPOSTRequestRequestWithContentEmptyString() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String methodKey = Fields.METHOD;
	String requestValue = EMPTY_STRING;
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString());
	headersJson.addProperty(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString());
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(methodKey, HttpMethod.POST.name());
	request.add(Fields.HEADERS, headersJson);
	request.addProperty(Fields.REQUEST, requestValue);
	
	HttpHeaders headers = new DefaultHttpHeaders();
		
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.REQUEST_PARAMS_MUST_BE_PROVIDED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectPOSTRequestRequestWithContentSpaceStringCharacter() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String methodKey = Fields.METHOD;
	String requestValue = SPACE;
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString());
	headersJson.addProperty(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString());
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(methodKey, HttpMethod.POST.name());
	request.add(Fields.HEADERS, headersJson);
	request.addProperty(Fields.REQUEST, requestValue);
	
	HttpHeaders headers = new DefaultHttpHeaders();
		
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.REQUEST_PARAMS_MUST_BE_PROVIDED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectPOSTRequestRequestWithContentNewLineCharacter() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String methodKey = Fields.METHOD;
	String requestValue = SPACE;
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString());
	headersJson.addProperty(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString());
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(methodKey, HttpMethod.POST.name());
	request.add(Fields.HEADERS, headersJson);
	request.addProperty(Fields.REQUEST, requestValue);
	
	HttpHeaders headers = new DefaultHttpHeaders();
		
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.REQUEST_PARAMS_MUST_BE_PROVIDED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectPOSTRequestRequestWithContentJsonNull() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String methodKey = Fields.METHOD;
	JsonElement requestValue = JsonNull.INSTANCE;
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString());
	headersJson.addProperty(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString());
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(methodKey, HttpMethod.POST.name());
	request.add(Fields.HEADERS, headersJson);
	request.add(Fields.REQUEST, requestValue);
	
	HttpHeaders headers = new DefaultHttpHeaders();
		
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.REQUEST_PARAMS_MUST_BE_PROVIDED);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectPOSTRequestRequestWithContentJsonArray() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String methodKey = Fields.METHOD;
	JsonArray requestValue = new JsonArray();
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString());
	headersJson.addProperty(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString());
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(methodKey, HttpMethod.POST.name());
	request.add(Fields.HEADERS, headersJson);
	request.add(Fields.REQUEST, requestValue);
	
	HttpHeaders headers = new DefaultHttpHeaders();
		
	exception.expect(IllegalArgumentException.class);
	exception.expectMessage(Messages.REQUEST_PARAMS_MUST_BE_JSON_OR_STRING);
	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
    }
    
    @Test
    public void testFromJsonStringJsonObjectPOSTRequestRequestWithContentString() throws Exception {
	RequestTranslator translator = new RequestTranslator();
	String methodKey = Fields.METHOD;
	
	JsonObject request = new JsonObject();
	JsonObject headersJson = new JsonObject();
	headersJson.addProperty(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString());
	headersJson.addProperty(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString());
	request.addProperty(Fields.ENDPOINT, TestResource.TEST_ENDPOINT);
	request.addProperty(methodKey, HttpMethod.POST.name());
	request.add(Fields.HEADERS, headersJson);
	request.addProperty(Fields.REQUEST, TestResource.REQUEST_STRING_VALUE);
	
	HttpHeaders headers = new DefaultHttpHeaders();

	HttpRequestBase translatedRequest = translator.fromJsonString(headers, request.toString());
	Assert.assertTrue("Expected POST HTTP Method, but was not", 
		HttpMethod.valueOf(translatedRequest.getMethod()).equals(HttpMethod.POST));
	Assert.assertTrue("Expected Request with body, but it was absent", 
		translator.getBody(translatedRequest).isPresent());
	Assert.assertTrue("Request body did not match expected value", 
		translator.getBody(translatedRequest).get().equals(TestResource.REQUEST_STRING_VALUE));
    }
}
