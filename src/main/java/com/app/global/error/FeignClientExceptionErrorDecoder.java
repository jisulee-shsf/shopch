package com.app.global.error;

import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class FeignClientExceptionErrorDecoder implements ErrorDecoder {

    private ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("{} 요청 실패, status = {}, reason = {}", methodKey, response.status(), response.reason());

        FeignException feignException = FeignException.errorStatus(methodKey, response);
        HttpStatus httpStatus = HttpStatus.valueOf(response.status());
        if (httpStatus.is5xxServerError()) {
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
