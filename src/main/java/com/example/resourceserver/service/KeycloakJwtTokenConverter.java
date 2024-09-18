package com.example.resourceserver.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class KeycloakJwtTokenConverter implements Converter<Jwt, JwtAuthenticationToken> {

    private static final String REALM_ACCESS = "realm_access";
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLES = "roles";
    private static final String ROLE_PREFIX = "ROLE_";

    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public JwtAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractResourceRoles(jwt);

        authorities.addAll(defaultGrantedAuthoritiesConverter.convert(jwt));

        String principalName = jwt.getClaimAsString("preferred_username");
        return new JwtAuthenticationToken(jwt, authorities, principalName);
    }

    private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Stream<String> realmRoles = extractRolesFromRealmAccess(jwt);
        Stream<String> resourceRoles = extractRolesFromResourceAccess(jwt);

        return Stream.concat(realmRoles, resourceRoles)
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .collect(Collectors.toSet());
    }


    private Stream<String> extractRolesFromRealmAccess(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap(REALM_ACCESS);
        if (realmAccess != null && realmAccess.containsKey(ROLES)) {
            return ((List<String>) realmAccess.get(ROLES)).stream();
        }
        return Stream.empty();
    }


    private Stream<String> extractRolesFromResourceAccess(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap(RESOURCE_ACCESS);
        if (resourceAccess != null) {
            return resourceAccess.values().stream()
                    .filter(Map.class::isInstance)
                    .map(Map.class::cast)
                    .filter(map -> map.containsKey(ROLES))
                    .flatMap(map -> ((List<String>) map.get(ROLES)).stream());
        }
        return Stream.empty();
    }
}
