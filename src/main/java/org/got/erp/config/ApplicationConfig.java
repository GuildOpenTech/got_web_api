package org.got.erp.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = "org.got.erp.entity")
@ComponentScan(basePackages = "org.got.erp.repository")
public class ApplicationConfig {
}
