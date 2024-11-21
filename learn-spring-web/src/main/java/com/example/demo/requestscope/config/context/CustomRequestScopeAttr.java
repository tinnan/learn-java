package com.example.demo.requestscope.config.context;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.context.request.RequestAttributes;

public class CustomRequestScopeAttr implements RequestAttributes {
    private final Map<String, Object> requestAttributeMap = new HashMap<>();

    @Override
    public Object getAttribute(String name, int scope) {
        if (scope == RequestAttributes.SCOPE_REQUEST) {
            return requestAttributeMap.get(name);
        }

        return null;
    }

    @Override
    public void setAttribute(String name, Object value, int scope) {
        if (scope == RequestAttributes.SCOPE_REQUEST) {
            requestAttributeMap.put(name, value);
        }
    }

    @Override
    public void removeAttribute(String name, int scope) {
        if (scope == RequestAttributes.SCOPE_REQUEST) {
            requestAttributeMap.remove(name);
        }
    }

    @Override
    public String[] getAttributeNames(int scope) {
        if (scope == RequestAttributes.SCOPE_REQUEST) {
            return requestAttributeMap
                .keySet()
                .toArray(new String[0]);
        }

        return new String[0];
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback, int scope) {
        // Not supported.
    }

    @Override
    public Object resolveReference(String key) {
        // Not supported.
        return null;
    }

    @Override
    public String getSessionId() {
        // Not supported.
        return null;
    }

    @Override
    public Object getSessionMutex() {
        // Not supported.
        return null;
    }
}
