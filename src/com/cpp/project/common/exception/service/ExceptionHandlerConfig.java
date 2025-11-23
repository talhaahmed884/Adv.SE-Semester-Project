package com.cpp.project.common.exception.service;

import com.cpp.project.common.exception.entity.ExceptionHandler;
import com.cpp.project.user.service.AuthenticationExceptionHandler;
import com.cpp.project.user.service.UserExceptionHandler;
import com.cpp.project.user_credential.service.UserCredentialExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExceptionHandlerConfig {
    @Bean
    public ExceptionHandler exceptionHandlerChain() {
        // Build chain of responsibility
        ExceptionHandler userHandler = new UserExceptionHandler();
        ExceptionHandler credentialHandler = new UserCredentialExceptionHandler();
        ExceptionHandler authHandler = new AuthenticationExceptionHandler();

        // Chain them together
        userHandler.setNext(credentialHandler).setNext(authHandler);

        return userHandler;
    }
}
