package com.app.global.error;

import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class FeignExceptionErrorDecoder implements ErrorDecoder {

    private static final String ERROR_LOG_FORMAT = "[{}] {} {}";

    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error(ERROR_LOG_FORMAT, methodKey, response.status(), response.reason());

        HttpStatus httpStatus = HttpStatus.valueOf(response.status());
        if (httpStatus.is5xxServerError()) {
            FeignException feignException = FeignException.errorStatus(methodKey, response);
            return new RetryableException(
                    response.status(),
                    feignException.getMessage(),
                    response.request().httpMethod(),
                    feignException,
                    (Long) null,
                    response.request()
            );
        }
        return errorDecoder.decode(methodKey, response);
    }
}
