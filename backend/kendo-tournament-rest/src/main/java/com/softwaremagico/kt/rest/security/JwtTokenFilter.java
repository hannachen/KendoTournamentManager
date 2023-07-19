package com.softwaremagico.kt.rest.security;

/*-
 * #%L
 * Kendo Tournament Manager (Rest)
 * %%
 * Copyright (C) 2021 - 2023 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> Valencia (Spain).
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.softwaremagico.kt.core.providers.AuthenticatedUserProvider;
import com.softwaremagico.kt.logger.JwtFilterLogger;
import com.softwaremagico.kt.persistence.entities.AuthenticatedUser;
import com.softwaremagico.kt.rest.exceptions.InvalidIpException;
import com.softwaremagico.kt.rest.exceptions.InvalidMacException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"};

    private final boolean checkClientIp;

    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticatedUserProvider userRepo;

    private final NetworkController networkController;

    @Autowired
    public JwtTokenFilter(@Value("${jwt.ip.check:false}") String ipCheck, JwtTokenUtil jwtTokenUtil, AuthenticatedUserProvider userRepo,
                          NetworkController networkController) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepo = userRepo;
        checkClientIp = Boolean.parseBoolean(ipCheck);
        this.networkController = networkController;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain chain)
            throws ServletException, IOException {
        // Get authorization header and validate
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (ObjectUtils.isEmpty(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            JwtFilterLogger.debug(this.getClass().getName(), "No Bearer token found on headers");
            return;
        }

        // Get jwt token and validate
        final String token = header.split(" ")[1].trim();
        if (!jwtTokenUtil.validate(token)) {
            JwtFilterLogger.errorMessage(this.getClass().getName(), "JWT token invalid!");
            chain.doFilter(request, response);
            return;
        }

        if (JwtFilterLogger.isDebugEnabled()) {
            JwtFilterLogger.debug(this.getClass().getName(), "\nJWT Obtained:\n"
                            + "\tExpiration date: '{}'\n\tUser id: '{}'\n\tUsername: '{}'\n\tIp: '{}'\n\tMAC: '{}'\n",
                    jwtTokenUtil.getExpirationDate(token), jwtTokenUtil.getUserId(token), jwtTokenUtil.getUsername(token),
                    jwtTokenUtil.getUserIp(token), jwtTokenUtil.getHostMac(token));
        }

        // Get user identity and set it on the spring security context
        final AuthenticatedUser userDetails = userRepo.findByUsername(jwtTokenUtil.getUsername(token)).orElse(null);

        final UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                userDetails == null ? new ArrayList<>() : userDetails.getAuthorities()
        );

        final String userTokenIp = jwtTokenUtil.getUserIp(token);
        if (checkClientIp && (userTokenIp == null || userTokenIp.isEmpty() || !getClientIpAddress(request).contains(userTokenIp))) {
            throw new InvalidIpException(this.getClass(), "User token issued for ip '" + userTokenIp + "'.");
        }

        final String hostMac = networkController.getHostMac();
        if (checkClientIp && hostMac != null && !hostMac.isEmpty() && !Objects.equals(jwtTokenUtil.getHostMac(token), hostMac)) {
            throw new InvalidMacException(this.getClass(), "User token issued for ip '" + userTokenIp + "'.");
        }

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private List<String> getClientIpAddress(HttpServletRequest request) {
        for (final String header : HEADERS_TO_TRY) {
            final String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip.contains(",") ? Arrays.asList(ip.split(",")) : Collections.singletonList(ip);
            }
        }

        return Collections.singletonList(request.getRemoteAddr());
    }
}
