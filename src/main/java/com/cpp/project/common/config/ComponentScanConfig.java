package com.cpp.project.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Component scan configuration
 * Ensures all packages are scanned for Spring components
 */
@Configuration
@ComponentScan(basePackages = {
        "com.cpp.project",
})
public class ComponentScanConfig {
    // Spring will automatically scan these packages for:
    // @Component, @Service, @Repository, @Controller, @RestController
}
