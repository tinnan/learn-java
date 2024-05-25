package org.example.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.example.security.model.JwtAuthentication;
import org.example.security.model.JwtPayload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

@Slf4j
public class JwtDecodeService {
    public Authentication decodeToken(String authorizationHeader) {
        String decodedTokenPayload = extractPayload(authorizationHeader);
        if (decodedTokenPayload == null) {
            return JwtAuthentication.unauthenticated();
        }
        JwtPayload payload = toPayloadObject(decodedTokenPayload);
        if (payload == null) {
            return JwtAuthentication.unauthenticated();
        }
        return createAuthentication(payload);
    }

    private JwtAuthentication createAuthentication(JwtPayload payload) {
        if (payload.getPermissions() == null) {
            return new JwtAuthentication(payload.getStaffId());
        }

        List<SimpleGrantedAuthority> grantedAuthorities = payload.getPermissions().stream()
            .map((p) -> new SimpleGrantedAuthority(p.toUpperCase())).toList();
        return new JwtAuthentication(payload.getStaffId(), grantedAuthorities);
    }

    private JwtPayload toPayloadObject(String tokenPayload) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(tokenPayload, JwtPayload.class);
        } catch (JsonProcessingException e) {
            if (log.isErrorEnabled()) {
                log.error("Fail to convert payload to object.", e);
            }
            return null;
        }
    }

    private String extractPayload(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            if (log.isDebugEnabled()) {
                log.debug("Authorization header does not exist or is empty.");
            }
            return null;
        }
        String token = authorization.replaceAll("^[Bb]earer(\\s)*", "");
        if (!StringUtils.hasText(token)) {
            if (log.isDebugEnabled()) {
                log.debug("Token is empty.");
            }
            return null;
        }
        String[] split = token.split("\\.");
        if (split.length != 3) {
            // Not found section separator or
            // found only 1 section separator.
            if (log.isDebugEnabled()) {
                log.debug("Token format is invalid.");
            }
            return null;
        }
        String base64TokenPayload = split[1];
        if (!StringUtils.hasText(base64TokenPayload)) {
            return null;
        }
        String decodedTokenPayload;
        try {
            decodedTokenPayload = new String(Base64.getDecoder().decode(base64TokenPayload));
        } catch (IllegalArgumentException iaex) {
            if (log.isErrorEnabled()) {
                log.error("Fail to decode token claims.", iaex);
            }
            return null;
        }
        return decodedTokenPayload;
    }
}
