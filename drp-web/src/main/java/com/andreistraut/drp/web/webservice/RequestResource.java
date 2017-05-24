package com.andreistraut.drp.web.webservice;

import com.andreistraut.drp.core.communicator.RequestDispatcher;
import com.andreistraut.drp.core.communicator.RequestTranslator;
import com.google.common.collect.Maps;
import io.netty.util.CharsetUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;

/**
 * Root REST Web Service
 *
 * @author Andrei Straut
 */
@Path("/")
public class RequestResource {

    @Context
    private UriInfo context;

    private static final Logger LOGGER = Logger.getLogger(RequestResource.class.getName());

    /**
     * Creates a new instance of RequestResource
     */
    public RequestResource() {
    }

    /**
     * Returns an error with a message stating that GET parameters must be provided
     *
     * @return plain text response
     */
    @GET
    @Path("get/")
    @Consumes({MediaType.WILDCARD})
    @Produces({MediaType.TEXT_PLAIN})
    public Response get() {

	return Response.status(Response.Status.BAD_REQUEST)
		.type(MediaType.TEXT_PLAIN)
		.entity("Endpoint URL must be provided")
		.build();
    }

    /**
     * Forwards a GET request to the url specified as request parameter
     *
     * @param headers The request headers
     * @param url the endpoint url
     * 
     * @return HTTP response with default text/plain encoding, or the encoding defined by the request headers, if present
     */
    @GET
    @Path("get/{url}")
    @Consumes({MediaType.WILDCARD})
    @Produces({MediaType.WILDCARD})
    public Response get(@Context HttpHeaders headers, @PathParam("url") String url) {
	
	Map<String, String> requestHeaders = Maps.<String, String>newHashMap();
	
	for(Map.Entry<String, List<String>> header : headers.getRequestHeaders().entrySet()) {
	    requestHeaders.put(header.getKey(), String.join(";", header.getValue()));
	}
	
	try {
	
	    HttpRequestBase request = new RequestTranslator().fromStringUrl(requestHeaders, url);
	    HttpResponse response = RequestDispatcher.dispatch(request);

	    ResponseBuilder builder = Response
		    .status(Response.Status.OK)
		    .entity(EntityUtils.toString(response.getEntity(), CharsetUtil.UTF_8));

	    for(Header header : response.getAllHeaders()) {
		builder.header(header.getName(), header.getValue());
	    }

	    return builder.build();
	} catch (IOException | ParseException e) {
	    Logger.getLogger(RequestDispatcher.class.getName()).log(Level.SEVERE, 
		    String.format("Exception raised: %s", e.getMessage()), e);

	    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
		    .type(MediaType.TEXT_PLAIN)
		    .entity(e.getMessage())
		    .build();
	}
    }

    /**
     * PUT method for updating or creating an instance of RequestResource
     *
     * @param headers
     * @param content representation for the resource
     * @return an instance of json
     * @throws java.io.UnsupportedEncodingException
     */
    @POST
    @Path("post/")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.WILDCARD})
    public Response post(@Context HttpHeaders headers, String content) throws UnsupportedEncodingException {
	
	Map<String, String> requestHeaders = Maps.<String, String>newHashMap();
	
	for(Map.Entry<String, List<String>> header : headers.getRequestHeaders().entrySet()) {
	    requestHeaders.put(header.getKey(), String.join(";", header.getValue()));
	}
	
	try {
	    HttpRequestBase request = new RequestTranslator().fromJsonString(requestHeaders, content);
	    HttpResponse response = RequestDispatcher.dispatch(request);

	    ResponseBuilder builder = Response
		    .status(Response.Status.OK)
		    .entity(EntityUtils.toString(response.getEntity(), CharsetUtil.UTF_8));

	    for(Header header : response.getAllHeaders()) {
		builder.header(header.getName(), header.getValue());
	    }

	    return builder.build();
	} catch(IllegalStateException | IllegalArgumentException | IOException e) {
	    Logger.getLogger(RequestDispatcher.class.getName()).log(Level.SEVERE, 
		    String.format("Invalid JSON Object submitted, exception raised: %s", e.getMessage()), e);

	    return Response.status(Response.Status.BAD_REQUEST)
		    .type(MediaType.TEXT_PLAIN)
		    .entity(e.getMessage())
		    .build();
	}
    }
}
