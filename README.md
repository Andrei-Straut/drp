[![Build Status](https://img.shields.io/jenkins/s/https/www.andreistraut.info/jenkins/job/DRP-master.svg)](https://www.andreistraut.info/jenkins/job/DRP-master/) [![Tests](https://img.shields.io/jenkins/t/https/www.andreistraut.info/jenkins/job/DRP-master.svg)](https://www.andreistraut.info/jenkins/job/DRP-master/lastCompletedBuild/) [![License: MIT](https://img.shields.io/badge/License-MIT-brightgreen.svg)](https://opensource.org/licenses/MIT)

# DRP (Dynamic Reverse Proxy)

## What is it?
DRP is an application (3 actually: maven, server, and local) that proxies requests received from clients (js, browser, etc) to their server targets.  
It thus avoids the need for server CORS configurations (if there is even access to the server to define it), or JSONP requests. It can in fact be seen as the server-side API for any client application that uses it.  
DRP exposes a webservice endpoint, through which you can communicate with it and issue your requests for forwarding. It supports GET and POST methods (POST with JSON format as request payload), and header forwarding.  

## Why does it exist?
It exists because I was working on another project in which I needed to make requests from my client-side (AngularJS) to different servers, in order to gather and compile data, and I was unable to do so easily. I did not have control over all servers, and not everything was JSONP-usable, and js' eval() was not an option.  
Hence, I started creating some wrapper functions on my project's server side in order to proxy these requests, and I realized that I could extract this functionality into a different project, and thus DRP was born.

## What do I need in order to install and run it?
- drp-core: maven and java (it is meant to be used as maven dependency)
- drp-local: java (it will  be run from console)
- drp-web: a webserver (tomcat, glassfish, jboss, wildfly)

## How do I set it up and how do I use it?
There's 3 options:
### Java projects
Use `drp-core.jar` as a dependency or in your `pom.xml`, and in the dependencies section, include the following:  
```xml
<dependency>
	<groupId>com.andreistraut.drp</groupId>
	<artifactId>drp-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
Code usage would be the following:
```java
/** the request headers */
Map<String, String> requestHeaders = ... 

/** the request content. Must be a valid JsonObject */
String requestContent = ...	

/** instantiate the object that will take care of the request format translation */
RequestTranslator translator = new RequestTranslator();

/** call the translator to build the request that will be forwarded */
HttpRequestBase proxyRequest = translator.fromJsonString(requestHeaders, requestContent);

/** Dispatch the request to its final destination, and retrieve the response */
HttpResponse response = RequestDispatcher.dispatch(proxyRequest);

/** That's it! From here, just forward the response to your client-side, as you would normally do */
```
### Local standalone server
Run the project from the console:  
`drp-local-1.0-SNAPSHOT-jar-with-dependencies 8090`  
where `8090` is the port number where the application will be listening. If no port number is provided, the default port is `8089`  
  
From here, you can start issuing the requests to this endpoint. It will probably be located at `http://localhost:8090/`

### Running on a webserver
Deploy the war file `drp-web-1.0-SNAPSHOT.war` to your webserver. From here, you can start issuing the requests to this endpoint. If your webserver is running on port 80, it will probably be located at `http://localhost/drp`

## What can it do?
DRP relays requests from your client to the destination endpoint. It supports:
- GET and POST requests for the DRP endpoint
- Forwarding of GET, POST, HEAD, OPTIONS and DELETE methods to the remote endpoint
- For GET requests, headers are automatically forwarded to the destination. There is no request payload possible
- For POST requests, there is more control and customization possibilities. Request payload must be a JSON object, with the following format:
	```json
	{
		"endpoint": "String - the full URL of the remote endpoint (incl. port)"
		"method": "String - the HTTP method of the request to be forwarded (recommended: All-caps)"
		"headers": "JsonObject - the request headers to be forwarded (key-value)"
		"request": "JsonObject or String - the request payload to be forwarded"
	}
	```
	Of these, only `endpoint` and `method` parameters are mandatory.  
  
	If headers are specified in both the JsonObject and RequestTranslator method call, the headers in JsonObject take precedence.  
  
	`Request` object can take two forms: JsonObject (key-value), and in this case, the data will be translated as `application/x-www-form-urlencoded` when forwarded, or a simple `string`, which will be the payload of the forwarded request.  
	If `request` parameter is present, it cannot be empty

## What can't it do?
- At the moment, DRP does not support forwarding of request payloads other than `application/text`, `text/plain`, or `application/x-www-form-urlencoded`
- Only forwarding of GET, POST, HEAD, OPTIONS and DELETE is supported
- No forwarding of multipart forms or files is supported
- No type of authentication is supported

## Do you take contributions / How can I contribute?
Sure. If you're interested, just get in touch, or simply send me a pull request. And thank you in advance!

## What are the plans for future development?
None for now, or none known. DRP already does what I needed it to do, and for me that's enough. If something needs to be supported in the future, I will implement it, but future plans are pretty much undefined at this point.

## Special thanks
The [Netty guys](https://github.com/netty/netty) for an awesome lightweight webserver.  
[Diogo Serra](https://github.com/pdiogomserra) for ideas on how to do this