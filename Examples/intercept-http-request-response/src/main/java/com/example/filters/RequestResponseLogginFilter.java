package com.example.filters;

import com.example.utils.LogStructure;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class RequestResponseLogginFilter extends OncePerRequestFilter {
    @Value("${spring.application.name}")
    private String applicationName;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        StopWatch stopWatch = new StopWatch();
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            stopWatch.start();
            filterChain.doFilter(wrappedRequest, wrappedResponse);
            getLogRequestResponse(wrappedRequest, wrappedResponse, stopWatch, null);
        } catch (Exception ex) {
            getLogRequestResponse(wrappedRequest, wrappedResponse, stopWatch, ex);
        } finally {
            if (stopWatch.isRunning())
                stopWatch.stop();
        }
    }

    private void getLogRequestResponse(
            ContentCachingRequestWrapper wrappedRequest,
            ContentCachingResponseWrapper wrappedResponse,
            StopWatch stopWatch,
            Exception ex) throws IOException {

        Charset requestCharset = Optional.ofNullable(wrappedRequest.getCharacterEncoding())
                .map(Charset::forName)
                .orElse(StandardCharsets.UTF_8);

        Charset responseCharset = Optional.ofNullable(wrappedResponse.getCharacterEncoding())
                .map(Charset::forName)
                .orElse(StandardCharsets.UTF_8);

        String requestBody = new String(
                wrappedRequest.getContentAsByteArray(),
                requestCharset
        );

        String responseBody = new String(
                wrappedResponse.getContentAsByteArray(),
                responseCharset
        );

        LogStructure logStructure = getLogStructure(wrappedRequest, wrappedResponse, stopWatch, ex, requestBody, responseBody);

        log.info(logStructure.toString());
    }

    private LogStructure getLogStructure(ContentCachingRequestWrapper wrappedRequest, ContentCachingResponseWrapper wrappedResponse, StopWatch stopWatch, Exception ex, String requestBody, String responseBody) throws IOException {
        LogStructure logStructure = new LogStructure();
        logStructure.setType("Controller");
        logStructure.setService(applicationName);
        logStructure.setMethod(wrappedRequest.getMethod());
        logStructure.setUrl(wrappedRequest.getRequestURL().toString());
        logStructure.setPath(wrappedRequest.getRequestURI());
        logStructure.setTraceId(UUID.randomUUID());
        logStructure.setTimestamp(LocalDateTime.now().toString());
        logStructure.setHeaders(getLogRequestHeader(wrappedRequest));
        logStructure.setRequest(requestBody);
        logStructure.setResponse(responseBody);
        logStructure.setStatusCode(wrappedResponse.getStatus());

        if (ex == null)
            logStructure.setStatusCode(wrappedResponse.getStatus());
        else {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String sStackTrace = sw.toString();
            logStructure.setStatusCode(500);
            logStructure.setStackTrace(sStackTrace);
        }

        wrappedResponse.copyBodyToResponse();
        stopWatch.stop();
        logStructure.setTimeSpent(stopWatch.getTotalTimeMillis() + " ms");
        return logStructure;
    }

    private String getLogRequestHeader(ContentCachingRequestWrapper request) {
        Map<String, String> headers = new HashMap<>();

        Collections.list(request.getHeaderNames()).forEach(headerName ->
                Collections.list(request.getHeaders(headerName)).forEach(headerValue ->
                        headers.put(headerName, headerValue)));

        return headers.toString();
    }
}