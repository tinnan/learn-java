package org.example.security.model;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthentication extends AbstractAuthenticationToken {

    private final String staffId;

    private JwtAuthentication() {
        super(null);
        this.staffId = null;
        setAuthenticated(false);
    }

    public JwtAuthentication(String staffId) {
        super(null);
        this.staffId = staffId;
        setAuthenticated(true);
    }

    public JwtAuthentication(String staffId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.staffId = staffId;
        setAuthenticated(true);
    }

    public static JwtAuthentication unauthenticated() {
        return new JwtAuthentication();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return staffId;
    }
}
