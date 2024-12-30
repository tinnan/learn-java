package com.example.demo.requestscope.config.context;

import static com.example.demo.requestscope.config.HttpHeadersConfig.HTTP_HEADERS_REQUEST_SCOPE_ATTR;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class ContextUtils {

    public static final List<String> ALLOWED_HEADERS = List.of("x-location", "x-correlation-id");

    public static CustomRequestScopeAttr cloneRequestAttributes() {
        try {
            HttpHeaders httpHeaders = getHttpHeaders();
            CustomRequestScopeAttr clonedRequestAttribute = new CustomRequestScopeAttr();
            clonedRequestAttribute.setAttribute(HTTP_HEADERS_REQUEST_SCOPE_ATTR, httpHeaders,
                RequestAttributes.SCOPE_REQUEST);
            return clonedRequestAttribute;
        } catch (Exception e) {
            return new CustomRequestScopeAttr();
        }
    }

    public static HttpHeaders getHttpHeaders() throws IllegalStateException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(requestAttributes)) {
            Object attribute = requestAttributes.getAttribute(HTTP_HEADERS_REQUEST_SCOPE_ATTR,
                RequestAttributes.SCOPE_REQUEST);
            if (Objects.nonNull(attribute)) {
                log.info("Create HttpHeaders from existing HttpHeaders bean {}", attribute);
                return cloneHttpHeaders((HttpHeaders) attribute);
            }
        }
        if (Objects.nonNull(requestAttributes) && requestAttributes instanceof ServletRequestAttributes attrs) {
            log.info("Create HttpHeaders from ServletRequestAttributes");
            return createHttpHeaders(attrs);
        }
        if (Objects.nonNull(requestAttributes) && requestAttributes instanceof CustomRequestScopeAttr attrs) {
            log.info("Create HttpHeaders from CustomRequestScopeAttr");
            return (HttpHeaders) attrs.getAttribute(HTTP_HEADERS_REQUEST_SCOPE_ATTR, RequestAttributes.SCOPE_REQUEST);
        }
        HttpHeaders httpHeaders = HttpHeadersContextHolder.get();
        if (Objects.nonNull(httpHeaders)) {
            log.info("Create HttpHeaders from HttpHeadersContextHolder");
            return cloneHttpHeaders(httpHeaders);
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

    private static HttpHeaders cloneHttpHeaders(HttpHeaders original) {
        HttpHeaders clonedHeaders = new HttpHeaders();
        original.forEach(clonedHeaders::addAll);
        return clonedHeaders;
    }

    private ContextUtils() {
    }
}
