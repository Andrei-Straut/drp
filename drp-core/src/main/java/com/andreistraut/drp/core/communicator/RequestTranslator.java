package com.andreistraut.drp.core.communicator;

import com.andreistraut.drp.core.model.Fields;
import com.andreistraut.drp.core.model.Messages;
import com.andreistraut.drp.core.model.ValidationErrorLog;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.CharsetUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

public class RequestTranslator {

    private static final List<HttpMethod> SUPPORTED_HTTP_METHODS = Lists.newArrayList(
	    HttpMethod.DELETE, HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS, HttpMethod.POST, HttpMethod.PUT);

    private ValidationErrorLog errorLog;

    public RequestTranslator() {
	this.errorLog = new ValidationErrorLog();
    }

    /**
     * Parse the given string request and turn it into a HttpComponent request
     * @param requestHeaders The request headers
     * @param content The request parameters, as Json
     * @return An HttpComponents request
     * @throws IllegalArgumentException On invalid input
     * @throws IllegalStateException On invalid input
     * @throws UnsupportedEncodingException On invalid input encoding
     */
    public HttpRequestBase fromJsonString(HttpHeaders requestHeaders, String content)
	    throws IllegalArgumentException, IllegalStateException, UnsupportedEncodingException {

	Map<String, String> headersMap = Maps.<String, String>newHashMap();

	if(requestHeaders != null) {
	    requestHeaders.entries().forEach((header) -> {
		headersMap.put(header.getKey().trim(), header.getValue());
	    });
	}

	return fromJsonString(headersMap, content);
    }
    
    /**
     * Parse the given string request and turn it into a HttpComponent request
     * @param requestHeaders The request headers
     * @param content The request parameters, as Json
     * @return An HttpComponents request
     * @throws IllegalArgumentException On invalid input
     * @throws IllegalStateException On invalid input
     * @throws UnsupportedEncodingException On invalid input encoding
     */
    public HttpRequestBase fromJsonString(Map<String, String> requestHeaders, String content)
	    throws IllegalArgumentException, IllegalStateException, UnsupportedEncodingException {

	this.errorLog = new ValidationErrorLog();
	
	if(content == null || isEmptyString(content)) {
	    this.errorLog.addValidationError(Messages.JSON_OBJECT_EXPECTED_EMPTY_FOUND);
	    throw new IllegalArgumentException(this.errorLog.getValidationErrorsString()
		    .orElse(Messages.UNKNOWN_VALIDATION_ERROR));

	}

	JsonObject requestObject = getLowerCasedKeysRequestObject(content);
	
	/** Validate the parameters, and throw an error if anything wrong*/
	if (!validate(requestObject)) {
	    throw new IllegalArgumentException(this.errorLog.getValidationErrorsString()
		    .orElse(Messages.UNKNOWN_VALIDATION_ERROR));
	}

	String endpoint = requestObject.get(Fields.ENDPOINT).getAsString();
	HttpMethod method = HttpMethod.valueOf(requestObject.get(Fields.METHOD).getAsString().trim().toUpperCase());	

	/** Parse the request headers if present */
	Map<String, String> headers = requestObject.has(Fields.HEADERS)
		? parseRequestHeaders(requestObject.get(Fields.HEADERS).getAsJsonObject())
		: requestHeaders;

	HttpRequestBase request = getRequestBase(method, endpoint);
	
	/** Set the headers from the proxy request, if present */
	for(Map.Entry<String, String> header : headers.entrySet()) {
		
	    if (header.getKey().trim().equalsIgnoreCase(HttpHeaderNames.CONTENT_LENGTH.toString())) {
		continue;
	    }
		
	    request.addHeader(header.getKey(), header.getValue());
	}
	
	/** If request is POST or other request type that contains a body, set it */
	if(HttpEntityEnclosingRequest.class.isInstance(request) && requestObject.has(Fields.REQUEST)) {
	    String requestBody = parseRequestBody(requestObject.get(Fields.REQUEST));
	    HttpEntityEnclosingRequest.class.cast(request).setEntity(new StringEntity(requestBody));
	}
	
	return request;
    }

    /**
     * Convert the given Netty httprequest and turn it into a HttpComponent request
     * @param fullHttpRequest The Netty HttpRequest
     * @return An HttpComponents request
     * @throws IllegalArgumentException On invalid input
     * @throws IllegalStateException On invalid input
     * @throws UnsupportedEncodingException On invalid input encoding
     */
    public HttpRequestBase fromFullHttpRequest(FullHttpRequest fullHttpRequest)
	    throws IllegalArgumentException, IllegalStateException, UnsupportedEncodingException {

	this.errorLog = new ValidationErrorLog();
	String endpoint = fullHttpRequest.uri();
	HttpMethod method = fullHttpRequest.method();
		
	HttpRequestBase request = getRequestBase(method, endpoint);
	
	for(Entry<String, String> header : fullHttpRequest.headers().entries()) {
	    if (header.getKey().trim().equalsIgnoreCase(HttpHeaderNames.CONTENT_LENGTH.toString())) {
		continue;
	    }
	    
	    request.addHeader(header.getKey(), header.getValue());
	}
	
	/** If request is POST or other request type that contains a body, set it */
	if(HttpEntityEnclosingRequest.class.isInstance(request)) {
	    HttpEntityEnclosingRequest.class.cast(request).setEntity(
		    new StringEntity(fullHttpRequest.content().toString(CharsetUtil.UTF_8)));
	}
	
	return request;
    }

    /**
     * Create an HttpComponents request from headers and an url
     * @param requestHeaders The headers to use
     * @param url The remote endpoint
     * @return An HttpComponents request
     * @throws IllegalArgumentException On invalid input
     * @throws IllegalStateException On invalid input
     * @throws UnsupportedEncodingException  On invalid input encoding
     */
    public HttpRequestBase fromStringUrl(Map<String, String> requestHeaders, String url)
	    throws IllegalArgumentException, IllegalStateException, UnsupportedEncodingException {

	this.errorLog = new ValidationErrorLog();
	
	if(!validateURL(url)) {
	    this.errorLog.addValidationError(Messages.ENDPOINT_URL_MUST_BE_PROVIDED);
	    throw new IllegalArgumentException(this.errorLog.getValidationErrorsString()
		    .orElse(Messages.UNKNOWN_VALIDATION_ERROR));
	}
		
	HttpRequestBase request = getRequestBase(HttpMethod.GET, url);
	
	if(requestHeaders == null) {
	    return request;
	}
	
	for(Entry<String, String> header : requestHeaders.entrySet()) {
	    if (header.getKey().trim().equalsIgnoreCase(HttpHeaderNames.CONTENT_LENGTH.toString())) {
		continue;
	    }
	    
	    if(isEmptyString(header.getKey())) {
		continue;
	    }
	    
	    request.addHeader(header.getKey(), header.getValue());
	}
	
	return request;
    }
    
    /**
     * Extract and return the header with the given name from the given request
     * @param request The request
     * @param headerName The name of the header
     * @return An Optional containing the header, as Name-Value entry, or an Optional.absent if header is not found
     */
    public Optional<Entry<String, String>> getHeader(HttpRequestBase request, String headerName) {
	Map<String, String> headers = Lists.newArrayList(request.getAllHeaders()).stream()
		.filter(header -> header != null)
		.map(header -> new AbstractMap.SimpleImmutableEntry<>(header.getName(), header.getValue()))
		.collect(Collectors.toMap(header -> header.getKey(), header -> header.getValue()));
	
	return this.getHeader(headers, headerName);
    }
    
    /**
     * Extract and return the header with the given name from the given request
     * @param request The request
     * @param headerName The name of the header
     * @return An Optional containing the header, as Name-Value entry, or an Optional.absent if header is not found
     */
    public Optional<Entry<String, String>> getHeader(FullHttpRequest request, String headerName) {
	Map<String, String> headers = request.headers().entries().stream()
		.map(header -> new AbstractMap.SimpleImmutableEntry<>(header.getKey(), header.getValue()))
		.collect(Collectors.toMap(header -> header.getKey(), header -> header.getValue()));
	
	return this.getHeader(headers, headerName);
    }
    
    /**
     * Extract and return the value of the header with the given name from the given request
     * @param request The request
     * @param headerName The name of the header
     * @return An Optional containing the header value, or an Optional.absent if header is not found
     */
    public Optional<String> getHeaderValue(HttpRequestBase request, String headerName) {
	Map<String, String> headers = Lists.newArrayList(request.getAllHeaders()).stream()
		.filter(header -> header != null)
		.map(header -> new AbstractMap.SimpleImmutableEntry<>(header.getName(), header.getValue()))
		.collect(Collectors.toMap(header -> header.getKey(), header -> header.getValue()));
	
	return this.getHeaderValue(headers, headerName);
    }
    
    /**
     * Extract and return the value of the header with the given name from the given request
     * @param request The request
     * @param headerName The name of the header
     * @return An Optional containing the header value, or an Optional.absent if header is not found
     */
    public Optional<String> getHeaderValue(FullHttpRequest request, String headerName) {
	Map<String, String> headers = request.headers().entries().stream()
		.map(header -> new AbstractMap.SimpleImmutableEntry<>(header.getKey(), header.getValue()))
		.collect(Collectors.toMap(header -> header.getKey(), header -> header.getValue()));
	
	return this.getHeaderValue(headers, headerName);
    }
    
    /**
     * Extract request body (entity) from the given request
     * @param request The request
     * @return An Optional containing the request body/entity, as string, or an Optional.absent if request has no body
     * @throws java.io.IOException
     */
    public Optional<String> getBody(HttpRequestBase request) throws IOException {
	if(!HttpEntityEnclosingRequest.class.isInstance(request)
		|| HttpEntityEnclosingRequest.class.cast(request).getEntity() == null) {
	    return Optional.empty();
	}
	
	HttpEntityEnclosingRequest requestWithBody = HttpEntityEnclosingRequest.class.cast(request);
	Charset charset = CharsetUtil.UTF_8;
	
	String responseString = EntityUtils.toString(requestWithBody.getEntity(), charset);
		
	if(isEmptyString(responseString)) {
	    return Optional.empty();
	}
	
	return Optional.ofNullable(EntityUtils.toString(requestWithBody.getEntity(), charset));
    }
    
    /**
     * Extract response body (entity) from the given response
     * @param response The response
     * @return An Optional containing the response body/entity, as string, or an Optional.absent if response has no body
     * @throws java.io.IOException
     */
    public Optional<String> getBody(HttpResponse response) throws IOException {
	HttpEntity entity = response.getEntity();
	
	if(entity == null) {
	    return Optional.empty();
	}
	
	Charset charset = CharsetUtil.UTF_8;
	String responseString = EntityUtils.toString(entity, charset);
	
	if(isEmptyString(responseString)) {
	    return Optional.empty();
	}
	
	return Optional.ofNullable(responseString);
    }

    /**
     * Get the log containing the validation errors
     * @return The error log
     */
    public ValidationErrorLog getErrorLog() {
	return this.errorLog;
    }
    
    private HttpRequestBase getRequestBase(HttpMethod method, String endpoint) {
	
	if(method.name().equals(HttpMethod.GET.name())) {
	    return new HttpGet(endpoint);
	} else if(method.name().equals(HttpMethod.POST.name())) {
	    return new HttpPost(endpoint);
	} else {
	    throw new IllegalArgumentException(Messages.ONLY_GET_POST_METHODS_SUPPORTED);
	}
    }

    private boolean validate(JsonObject object) {

	boolean valid = true;

	if (!object.has(Fields.ENDPOINT) || isEmptyString(object.get(Fields.ENDPOINT)) 
		|| isEmptyString(object.get(Fields.ENDPOINT).getAsString())) {
	    this.errorLog.addValidationError(Messages.ENDPOINT_URL_MUST_BE_PROVIDED);
	    valid = false;
	}
	
	if (object.has(Fields.ENDPOINT) && !isEmptyString(object.get(Fields.ENDPOINT))
		 && !isEmptyString(object.get(Fields.ENDPOINT).getAsString())) {
	    /** validateUrl method adds its own messages to validation log, no need to do it here */
	    if(!this.validateURL(object.get(Fields.ENDPOINT).getAsString())) {
		valid = false;
	    }
	}

	if (!object.has(Fields.METHOD) || isEmptyString(object.get(Fields.METHOD)) 
		|| isEmptyString(object.get(Fields.METHOD).getAsString())) {
	    this.errorLog.addValidationError(Messages.HTTP_METHOD_MUST_BE_SPECIFIED);
	    valid = false;
	}

	if (object.has(Fields.METHOD) && !isEmptyString(object.get(Fields.METHOD))
		 && !isEmptyString(object.get(Fields.METHOD).getAsString())) {
	    HttpMethod method = HttpMethod.valueOf(object.get(Fields.METHOD).getAsString().trim().toUpperCase());

	    if (method == null || !SUPPORTED_HTTP_METHODS.contains(method)) {
		this.errorLog.addValidationError(Messages.CORRECT_HTTP_METHOD_MUST_BE_SPECIFIED);
		valid = false;
	    }

	    if (method != null && !SUPPORTED_HTTP_METHODS.contains(method)) {
		this.errorLog.addValidationError(Messages.ONLY_GET_POST_METHODS_SUPPORTED);
		valid = false;
	    }
	}
		    
	if (hasRequestField(object) && !hasRequestContent(object)) {
	    this.errorLog.addValidationError(Messages.REQUEST_PARAMS_MUST_BE_PROVIDED);
	    valid = false;
	}

	if (hasRequestField(object) && !object.get(Fields.REQUEST).isJsonObject() 
		&& !object.get(Fields.REQUEST).isJsonPrimitive()) {
	    this.errorLog.addValidationError(Messages.REQUEST_PARAMS_MUST_BE_JSON_OR_STRING);
	    valid = false;
	}

	return valid;
    }

    private boolean validateURL(String url) {
	if(url == null || url.isEmpty()) {
	    this.errorLog.addValidationError(Messages.ENDPOINT_URL_MUST_BE_PROVIDED);
	    return false;
	}
	
	UrlValidator validator = new UrlValidator(
		UrlValidator.ALLOW_ALL_SCHEMES + UrlValidator.ALLOW_LOCAL_URLS);
	
	if(!validator.isValid(url.trim())) {
	    this.errorLog.addValidationError(Messages.ENDPOINT_URL_MUST_BE_VALID);
	    return false;
	}
	
	return true;
    }
    
    private JsonObject getLowerCasedKeysRequestObject(String content) {
	JsonObject requestObject = (new JsonParser()).parse(content).getAsJsonObject();
	JsonObject normalized = new JsonObject();
	
	for(Map.Entry<String, JsonElement> field : requestObject.entrySet()) {
	    normalized.add(field.getKey().toLowerCase().trim(), requestObject.get(field.getKey()));
	}
	
	return normalized;
    }
    
    private boolean hasRequestField(JsonObject object) {
	return object.has(Fields.REQUEST);
    }
    
    private boolean hasRequestContent(JsonObject object) {
	
	final String EMPTY_STRING = "";
	final String SPACE = " ";
	final String QUOTE = "\"";
	
	if(!object.has(Fields.REQUEST)) {
	    return false;
	}
	
	if(object.get(Fields.REQUEST) == null) {
	    return false;
	}
	
	if(object.get(Fields.REQUEST).isJsonNull()) {
	    return false;
	}
	
	return !isEmptyString(object.get(Fields.REQUEST).toString());
    }

    private Map<String, String> parseRequestHeaders(JsonObject requestHeaders) {
	Map<String, String> headers = Maps.<String, String>newHashMap();

	for (Map.Entry<String, JsonElement> header : requestHeaders.entrySet()) {

	    if (header.getValue().isJsonPrimitive() || header.getValue().isJsonObject()) {
		headers.put(header.getKey().trim(), header.getValue().getAsString());
	    }
	}

	return headers;
    }

    private String parseRequestBody(JsonElement requestBody) throws UnsupportedEncodingException {
	StringBuilder builder = new StringBuilder();

	if (requestBody.isJsonPrimitive()) {
	    builder.append(URLEncoder.encode(requestBody.getAsString(), "UTF-8"));

	} else if (requestBody.isJsonObject()) {
	    for (Map.Entry<String, JsonElement> requestParameter : requestBody.getAsJsonObject().entrySet()) {

		if (requestParameter.getValue().isJsonPrimitive() 
			|| requestParameter.getValue().isJsonObject()) {
		    builder.append(URLEncoder.encode(requestParameter.getKey(), "UTF-8")).append("=")
			    .append(URLEncoder.encode(requestParameter.getValue().getAsString(), "UTF-8"));

		}
	    }
	}

	return builder.toString();
    }
    
    private Optional<Entry<String, String>> getHeader(Map<String, String> headers, String headerName) {
	for(Entry<String, String> header : headers.entrySet()) {
	    if(header.getKey().trim().equalsIgnoreCase(headerName.trim())) {
		return Optional.of(new AbstractMap.SimpleImmutableEntry(header.getKey(), header.getValue()));
	    }
	}
	
	return Optional.empty();
    }
    
    private Optional<String> getHeaderValue(Map<String, String> headers, String headerName) {
	for(Entry<String, String> header : headers.entrySet()) {
	    if(header.getKey().trim().equalsIgnoreCase(headerName.trim())) {
		return Optional.of(header.getValue());
	    }
	}
	
	return Optional.empty();
    }
    
    private boolean isEmptyString(String string) {
	
	final String EMPTY_STRING = "";
	final String SPACE = " ";
	final String QUOTE = "\"";
	final String NEWLINE = System.lineSeparator();
	
	String cleanedContent = string
		.trim()
		.replace(SPACE, EMPTY_STRING)
		.replace(QUOTE, EMPTY_STRING)
		.replace(NEWLINE, EMPTY_STRING);
	
	return cleanedContent.equals(EMPTY_STRING);
    }
    
    private boolean isEmptyString(JsonElement json) {
	
	if(json.isJsonNull()) {
	    return true;
	}
	
	try {
	    return isEmptyString(json.toString());
	} catch(UnsupportedOperationException e) {
	    return true;
	}
    }
}
