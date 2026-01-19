package com.example.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class DummyController {
    @GetMapping("/")
    public Map<String, Object> greeting(@RequestHeader(value = "User-Agent", required = false) String userAgent) {
        boolean mobile = isMobile(userAgent);
        Map<String, Object> resp = new HashMap<>();
        resp.put("deviceType", mobile ? "mobile" : "desktop");
        resp.put("userAgent", userAgent == null ? "" : userAgent);
        return resp;
    }

    private boolean isMobile(String userAgent) {
        if (userAgent == null) return false;
        String ua = userAgent.toLowerCase();
        return ua.contains("mobi")
                || ua.contains("android")
                || ua.contains("iphone")
                || ua.contains("ipad")
                || ua.contains("ipod")
                || ua.contains("windows phone")
                || ua.contains("iemobile")
                || ua.contains("opera mini")
                || ua.contains("blackberry");
    }
}
