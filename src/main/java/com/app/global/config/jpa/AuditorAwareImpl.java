package com.app.global.config.jpa;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.util.StringUtils;

import java.util.Optional;

@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private final HttpServletRequest httpServletRequest;

    @Override
    public Optional<String> getCurrentAuditor() {
        String requestUri = httpServletRequest.getRequestURI();
        if (StringUtils.hasText(requestUri)) {
            return Optional.of(requestUri);
        }
        return Optional.of("Unknown");
    }
}
