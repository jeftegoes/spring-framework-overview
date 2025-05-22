package com.example.handlers;

import com.example.utils.LogStructure;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequestScope
@Component
public class LoggingInterceptor implements HandlerInterceptor {
    @Value("${spring.application.name}")
    private String applicationName;

    private final StopWatch stopWatch = new StopWatch();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        stopWatch.start();

        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request);
        }

        if (!(response instanceof ContentCachingResponseWrapper)) {
            response = new ContentCachingResponseWrapper(response);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (request.getDispatcherType() == DispatcherType.REQUEST) {
            ContentCachingRequestWrapper wrappedRequest = request instanceof ContentCachingRequestWrapper
                    ? (ContentCachingRequestWrapper) request
                    : new ContentCachingRequestWrapper(request);
            ContentCachingResponseWrapper wrappedResponse = response instanceof ContentCachingResponseWrapper
                    ? (ContentCachingResponseWrapper) response
                    : new ContentCachingResponseWrapper(response);

            Map<String, String> headers = getRequestHeaders(wrappedRequest);

            LogStructure logStructure = new LogStructure();
            logStructure.setType("Controller");
            logStructure.setService(applicationName);
            logStructure.setMethod(wrappedRequest.getMethod());
            logStructure.setUrl(wrappedRequest.getRequestURL().toString());
            logStructure.setPath(wrappedRequest.getRequestURI());
            logStructure.setTraceId(UUID.randomUUID());
            logStructure.setTimestamp(LocalDateTime.now().toString());
            logStructure.setHeaders(headers.toString());
            logStructure.setRequest(getRequestBody(request));

            String responseBody = getResponseBody(wrappedResponse);
            logStructure.setResponse(responseBody);

            if (ex == null)
                logStructure.setStatusCode(response.getStatus());
            else {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String sStackTrace = sw.toString();
                logStructure.setStatusCode(500);
                logStructure.setStackTrace(sStackTrace);
            }

            stopWatch.stop();
            logStructure.setTimeSpent(stopWatch.getTotalTimeMillis() + " ms");

            log.info(new ObjectMapper().writeValueAsString(logStructure));

            wrappedResponse.copyBodyToResponse();
        }

        if (stopWatch.isRunning())
            stopWatch.stop();
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        String requestBody = "";

        if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {
            requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        }

        return requestBody.isEmpty() ? "No request body" : requestBody;
    }

    private String getResponseBody(ContentCachingResponseWrapper response) throws IOException {
        byte[] buf = response.getContentAsByteArray();
        if (buf.length > 0) {
            return new String(buf, response.getCharacterEncoding());
        }
        return "No response body";
    }

    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }

        return headers;
    }
}