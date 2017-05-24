
package com.andreistraut.drp.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;

public class TestResource {
    
    public static final String TEST_ENDPOINT = "http://localhost:8089";
    public static final int TEST_PORT = 8089;
    public static final HttpMethod HTTP_METHOD = HttpMethod.GET;
    public static final String SERVER = "GlassFish Server Open Source Edition 4.0";
    public static final String DATE = "Thu, 13 Apr 2017 14:12:14 GMT";
    public static final String REQUEST_STRING_VALUE = "requestValue";
    public static final String REQUEST_PARAM_NAME = "Param1Name";
    public static final String REQUEST_PARAM_VALUE = "Param1Value";
    
    public static JsonObject requestContentAsKeyValue() {
	JsonObject content = new JsonObject();
	
	content.addProperty(REQUEST_PARAM_NAME, REQUEST_PARAM_VALUE);
	content.addProperty("Param2Name", "Param2Value");
	content.addProperty("Param3Name", "Param3Value");
	content.addProperty("Param4Name", "Param4Value");
	
	return content;
    }
    
    public static String requestContentAsString() {
	return REQUEST_STRING_VALUE;
    }
    
    public static String requestWithoutEndpointProperty() {
	JsonObject request = validGETRequestWithRequestJsonObject();
	request.remove("endpoint");
	
	return request.toString();
    }
    
    public static String requestWithEmptyEndpointValue() {
	JsonObject request = validGETRequestWithRequestJsonObject();
	request.remove("endpoint");
	request.addProperty("endpoint", "");
	
	return request.toString();
    }
    
    public static String requestWithInvalidEndpointValue() {
	JsonObject request = validGETRequestWithRequestJsonObject();
	request.remove("endpoint");
	request.addProperty("endpoint", "http:/invalidUrl");
	
	return request.toString();
    }
    
    public static String requestWithoutMethodProperty() {
	JsonObject request = validGETRequestWithRequestJsonObject();
	request.remove("method");
	
	return request.toString();
    }
    
    public static String requestWithEmptyMethodValue() {
	JsonObject request = validGETRequestWithRequestJsonObject();
	request.remove("method");
	request.addProperty("method", "");
	
	return request.toString();
    }
    
    public static String requestWithInvalidMethodValue() {
	JsonObject request = validGETRequestWithRequestJsonObject();
	request.remove("method");
	request.addProperty("method", "SOMEMETHOD");
	
	return request.toString();
    }
    
    public static String requestPOSTWithoutRequestProperty() {
	JsonObject request = validGETRequestWithRequestJsonObject();
	request.remove("request");
	request.remove("method");
	request.addProperty("method", "POST");
	
	return request.toString();
    }
    
    public static String requestPOSTWithEmptyRequestValue() {
	JsonObject request = validGETRequestWithRequestJsonObject();
	request.remove("request");
	request.addProperty("request", "");
	request.remove("method");
	request.addProperty("method", "POST");
	
	return request.toString();
    }
    
    public static String requestWithInvalidRequestValue_JsonArray() {
	JsonObject request = validGETRequestWithRequestJsonObject();
	request.remove("request");
	
	JsonArray requestArray =  new JsonArray();
	requestArray.add(new JsonPrimitive("SOMEARRAYELEMENT"));
	
	request.add("request", requestArray);
	
	return request.toString();
    }
    
    public static String requestWithInvalidRequestValue_Integer() {
	JsonObject request = validGETRequestWithRequestJsonObject();
	request.remove("request");
	
	request.addProperty("request", 12345);
	
	return request.toString();
    }
    
    public static String requestWithInvalidRequestValue_Char() {
	JsonObject request = validGETRequestWithRequestJsonObject();
	request.remove("request");
	
	request.addProperty("request", 'a');
	
	return request.toString();
    }
    
    public static String validGETRequestWithRequestJsonObjectAsString() {
	return validGETRequestWithRequestJsonObject().toString();
    }
    
    public static JsonObject validGETRequestWithRequestJsonObject() {
	JsonObject request = new JsonObject();
	JsonObject headers = new JsonObject();
	JsonObject content = new JsonObject();
	
	headers.addProperty(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString());
	headers.addProperty(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString());
	headers.addProperty(HttpHeaderNames.SERVER.toString(), TestResource.SERVER);
	headers.addProperty(HttpHeaderNames.DATE.toString(), TestResource.DATE);
	
	content.addProperty(REQUEST_PARAM_NAME, REQUEST_PARAM_VALUE);
	content.addProperty("Param2Name", "Param2Value");
	content.addProperty("Param3Name", "Param3Value");
	content.addProperty("Param4Name", "Param4Value");
	
	request.addProperty("endpoint", TEST_ENDPOINT);
	request.addProperty("method", HTTP_METHOD.toString());
	request.add("headers", headers);
	request.add("request", content);
	
	return request;
    }
    
    public static String validGETRequestWithRequestStringAsString() {
	return validGETRequestWithRequestString().toString();
    }
    
    public static JsonObject validGETRequestWithRequestString() {
	JsonObject request = new JsonObject();
	JsonObject headers = new JsonObject();
	
	headers.addProperty(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString());
	headers.addProperty(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString());
	headers.addProperty(HttpHeaderNames.SERVER.toString(), TestResource.SERVER);
	headers.addProperty(HttpHeaderNames.DATE.toString(), TestResource.DATE);
		
	request.addProperty("endpoint", TEST_ENDPOINT);
	request.addProperty("method", HTTP_METHOD.toString());
	request.add("headers", headers);
	
	return request;
    }
    
    public static String validGETRequestWithNoHeadersRequestStringAsString() {
	return validGETRequestWithNoHeadersRequestString().toString();
    }
    
    public static JsonObject validGETRequestWithNoHeadersRequestString() {
	JsonObject request = new JsonObject();
	JsonObject content = new JsonObject();
	
	content.addProperty(REQUEST_PARAM_NAME, REQUEST_PARAM_VALUE);
	content.addProperty("Param2Name", "Param2Value");
	content.addProperty("Param3Name", "Param3Value");
	content.addProperty("Param4Name", "Param4Value");
	
	request.addProperty("endpoint", TEST_ENDPOINT);
	request.addProperty("method", HTTP_METHOD.toString());
	request.add("request", content);
	
	return request;
    }
    
    public static String validGETRequestWithNoHeadersRequestJsonObjectAsString() {
	return validGETRequestWithNoHeadersRequestJsonObject().toString();
    }
    
    public static JsonObject validGETRequestWithNoHeadersRequestJsonObject() {
	JsonObject request = new JsonObject();
	
	request.addProperty("endpoint", TEST_ENDPOINT);
	request.addProperty("method", HTTP_METHOD.toString());
	request.addProperty("request", REQUEST_STRING_VALUE);
	
	return request;
    }
    
    public static String validPOSTRequestWithRequestStringAsString() {
	return validPOSTRequestWithRequestString().toString();
    }
    
    public static JsonObject validPOSTRequestWithRequestString() {
	JsonObject request = new JsonObject();
	JsonObject headers = new JsonObject();
	JsonObject content = new JsonObject();
	
	headers.addProperty(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString());
	headers.addProperty(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString());
	headers.addProperty(HttpHeaderNames.SERVER.toString(), TestResource.SERVER);
	headers.addProperty(HttpHeaderNames.DATE.toString(), TestResource.DATE);
	
	content.addProperty(REQUEST_PARAM_NAME, REQUEST_PARAM_VALUE);
	content.addProperty("Param2Name", "Param2Value");
	content.addProperty("Param3Name", "Param3Value");
	content.addProperty("Param4Name", "Param4Value");
		
	request.addProperty("endpoint", TEST_ENDPOINT);
	request.addProperty("method", HttpMethod.POST.toString());
	request.add("headers", headers);
	request.add("request", content);
	
	return request;
    }
}
