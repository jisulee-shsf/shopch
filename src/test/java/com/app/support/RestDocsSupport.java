package com.app.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.HttpHeaders.CONTENT_LENGTH;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsSupport {

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        mockMvc = standaloneSetup(initController())
                .apply(documentationConfiguration(provider)
                        .operationPreprocessors()
                        .withRequestDefaults(
                                modifyUris()
                                        .scheme("https")
                                        .host("jisulee-shsf.com")
                                        .removePort(),
                                prettyPrint(),
                                modifyHeaders().remove(CONTENT_LENGTH)
                        )
                        .withResponseDefaults(
                                prettyPrint(),
                                modifyHeaders().remove(CONTENT_LENGTH)
                        )
                )
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    protected abstract Object initController();
}
