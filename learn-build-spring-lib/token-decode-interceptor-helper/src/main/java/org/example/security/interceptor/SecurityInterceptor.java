package org.example.security.interceptor;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.security.annotation.HasAuthority;
import org.example.security.exception.AccessDeniedException;
import org.example.security.service.JwtDecodeService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
public class SecurityInterceptor implements HandlerInterceptor {
    private final JwtDecodeService jwtDecodeService;

    @Override
    public boolean preHandle(jakarta.servlet.http.HttpServletRequest request,
        jakarta.servlet.http.HttpServletResponse response, Object handler) throws Exception {
        Class<?> clazz = handler.getClass();
        if (clazz.isAnnotationPresent(HasAuthority.class)) {
            HasAuthority annotation = clazz.getAnnotation(HasAuthority.class);
            String requiredPermission = annotation.value();
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            List<String> permissions = jwtDecodeService.extractPermissions(authorization);
            if (!permissions.contains(requiredPermission)) {
                throw new AccessDeniedException("Requires permission " + requiredPermission + " to perform the action.");
            }
        }
        return true;
    }
}
