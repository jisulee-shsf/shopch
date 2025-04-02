package com.app.support;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.HttpHeaders.CONTENT_LENGTH;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsSupport {

    protected MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        mockMvc = standaloneSetup(initController())
                .apply(documentationConfiguration(provider)
                        .uris()
                        .withScheme("https")
                        .withHost("jisulee-shsf.com")
                        .withPort(443)
                        .and()
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint(), modifyHeaders().remove(CONTENT_LENGTH))
                        .withResponseDefaults(prettyPrint(), modifyHeaders().remove(CONTENT_LENGTH))
                )
                .build();
    }

    protected abstract Object initController();
}
