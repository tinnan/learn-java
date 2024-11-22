package com.example.demo.requestscope.config.context;

import static com.example.demo.requestscope.config.HttpHeadersConfig.HTTP_HEADERS_REQUEST_SCOPE_ATTR;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ContextUtils {

    public static final List<String> ALLOWED_HEADERS = List.of("x-location", "x-correlation-id");

    public static CustomRequestScopeAttr cloneRequestAttributes(RequestAttributes requestAttributes) {
        try {
            CustomRequestScopeAttr clonedRequestAttribute = new CustomRequestScopeAttr();
            Object attribute = requestAttributes.getAttribute(HTTP_HEADERS_REQUEST_SCOPE_ATTR,
                RequestAttributes.SCOPE_REQUEST);
            if (Objects.nonNull(attribute)) {
                clonedRequestAttribute.setAttribute(HTTP_HEADERS_REQUEST_SCOPE_ATTR, attribute,
                    RequestAttributes.SCOPE_REQUEST);
            } else if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
                clonedRequestAttribute.setAttribute(HTTP_HEADERS_REQUEST_SCOPE_ATTR,
                    createHttpHeaders(servletRequestAttributes), RequestAttributes.SCOPE_REQUEST);
            }
            return clonedRequestAttribute;
        } catch (Exception e) {
            return new CustomRequestScopeAttr();
        }
    }

    public static HttpHeaders getHttpHeaders() throws IllegalStateException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(requestAttributes) && requestAttributes instanceof ServletRequestAttributes attrs) {
            return createHttpHeaders(attrs);
        }
        HttpHeaders httpHeaders = HttpHeadersContextHolder.get();
        if (Objects.nonNull(httpHeaders)) {
            HttpHeaders clonedHeaders = new HttpHeaders();
            httpHeaders.forEach(clonedHeaders::addAll);
            return clonedHeaders;
        }
        throw new IllegalStateException("No thread-bound HttpHeaders attribute found. "
            + "Current thread must be request-bound or must contain HttpHeaders attribute in HttpHeadersContextHolder");
    }

    public static HttpHeaders createHttpHeaders(ServletRequestAttributes attributes) {
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpServletRequest request = attributes.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String value = request.getHeader(headerName);
                if (ALLOWED_HEADERS.contains(headerName.toLowerCase())) {
                    httpHeaders.add(headerName, value);
                }
            }
        }
        return httpHeaders;
    }

    private ContextUtils() {
    }
}
