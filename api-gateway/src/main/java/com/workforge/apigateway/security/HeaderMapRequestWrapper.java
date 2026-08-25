package com.workforge.apigateway.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

public class HeaderMapRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> customHeaders = new HashMap<>();

    public HeaderMapRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public void addHeader(String name, String value) {
        customHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        for (String headerName : customHeaders.keySet()) {
            if (headerName.equalsIgnoreCase(name)) {
                return customHeaders.get(headerName);
            }
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> names = new HashSet<>(Collections.list(super.getHeaderNames()));
        names.addAll(customHeaders.keySet());
        return Collections.enumeration(names);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        for (String headerName : customHeaders.keySet()) {
            if (headerName.equalsIgnoreCase(name)) {
                return Collections.enumeration(Collections.singletonList(customHeaders.get(headerName)));
            }
        }
        return super.getHeaders(name);
    }


}
