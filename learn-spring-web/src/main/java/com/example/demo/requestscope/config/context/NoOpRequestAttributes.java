package com.example.demo.requestscope.config.context;

import org.springframework.web.context.request.RequestAttributes;

public class NoOpRequestAttributes implements RequestAttributes {

    @Override
    public Object getAttribute(String name, int scope) {
        return null;
    }

    @Override
    public void setAttribute(String name, Object value, int scope) {
        // No-op
    }

    @Override
    public void removeAttribute(String name, int scope) {
        // No-op
    }

    @Override
    public String[] getAttributeNames(int scope) {
        return new String[0];
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback, int scope) {
        // No-op
    }

    @Override
    public Object resolveReference(String key) {
        return null;
    }

    @Override
    public String getSessionId() {
        return null;
    }

    @Override
    public Object getSessionMutex() {
        return null;
    }
}
