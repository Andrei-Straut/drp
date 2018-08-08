
package com.andreistraut.drp.core.model;

public class Messages {
    
    public static final String UNSUPPORTED_HTTP_METHOD = "HTTP Method is not supported. Supported HTTP Methods are: %s, %s";
    public static final String ENDPOINT_URL_MUST_BE_PROVIDED = "Endpoint URL must be provided";
    public static final String ENDPOINT_URL_MUST_BE_VALID = "Endpoint URL must be a valid URL";
    public static final String HTTP_METHOD_MUST_BE_SPECIFIED = "An HTTP Method must be specified";
    public static final String CORRECT_HTTP_METHOD_MUST_BE_SPECIFIED = "A correct HTTP Method must be specified";
    public static final String REQUEST_PARAMS_MUST_BE_PROVIDED = "Request parameters must be provided";
    public static final String REQUEST_PARAMS_MUST_BE_JSON_OR_STRING = "Request parameters must be either a String, or a JsonObject";
    public static final String JSON_OBJECT_EXPECTED_EMPTY_FOUND = "A Json object was expected as POST entity, but nothing was found";
    public static final String UNKNOWN_VALIDATION_ERROR = "Unknown Validation Error";
    public static final String STATUS_CANNOT_BE_NULL_ERROR = "ProxyResponse status cannot be null";
    public static final String HEADER_CANNOT_BE_NULL_ERROR = "ProxyResponse header name or header value cannot be null";
    public static final String HEADER_CANNOT_BE_EMPTY_ERROR = "ProxyResponse header name or header value cannot be empty string";
    public static final String ONLY_GET_POST_PUT_METHODS_SUPPORTED = "Only GET, POST and PUT methods are supported";
}
