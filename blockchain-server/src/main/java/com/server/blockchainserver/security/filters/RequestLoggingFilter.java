package com.server.blockchainserver.security.filters;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain)
            throws IOException, ServletException {
        try {
            String pathInfo = request.getServletPath();
            logger.info("Starting request for {} {} {}", request.getLocale(), request.getMethod(), pathInfo);
            filterChain.doFilter(request, response);

        } catch (UnexpectedRollbackException rollbackException) {
            logger.error(rollbackException.getMessage());
            logger.debug("If you get this Message you might need to change the database");
        }
    }
}
