package com.example.interceptors;

import lombok.Data;

import java.util.UUID;

@Data
public class LogStructure {
    private String type;
    private String service;
    private String method;
    private String url;
    private String path;
    private UUID traceId;
    private String timestamp;
    private String headers;
    private String request;
    private int statusCode;
    private String response;
    private String timeSpent;
}
