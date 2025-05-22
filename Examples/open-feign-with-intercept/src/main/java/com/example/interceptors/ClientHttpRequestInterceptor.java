package com.example.interceptors;

import com.example.interceptors.wrappers.ClientHttpResponseWrapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
public class ClientHttpRequestInterceptor implements org.springframework.http.client.ClientHttpRequestInterceptor {
    @Value("${spring.application.name}")
    private String applicationName;

    @SneakyThrows
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LogStructure logStructure = logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        stopWatch.stop();
        response = logResponse(logStructure, response, stopWatch);
        log.info("LogStructure: {}", logStructure);

        return response;
    }

    private LogStructure logRequest(HttpRequest request, byte[] body) {

        LogStructure logStructure = new LogStructure();

        logStructure.setType("API");
        logStructure.setService(applicationName);
        logStructure.setMethod(request.getMethod().name());
        logStructure.setUrl(request.getURI().getHost());
        logStructure.setPath(request.getURI().getPath());
        logStructure.setTraceId(UUID.randomUUID());
        logStructure.setTimestamp(LocalDateTime.now().toString());
        logStructure.setHeaders(request.getHeaders().toString());
        String requestBody = new String(body, StandardCharsets.UTF_8);
        logStructure.setRequest(requestBody);

        return logStructure;
    }

    private ClientHttpResponse logResponse(
            LogStructure logStructure,
            ClientHttpResponse response,
            StopWatch stopWatch) throws Exception {
        ClientHttpResponse responseCopy = new ClientHttpResponseWrapper(response);

        logStructure.setTimeSpent(stopWatch.getTotalTimeMillis() + "ms");
        logStructure.setStatusCode(responseCopy.getStatusCode().value());

        String responseBody = new String(StreamUtils.copyToByteArray(responseCopy.getBody()), StandardCharsets.UTF_8);
        logStructure.setResponse(responseBody);

        return responseCopy;
    }
}