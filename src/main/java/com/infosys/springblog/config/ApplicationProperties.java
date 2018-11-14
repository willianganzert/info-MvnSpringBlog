package com.infosys.springblog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to MvnSpringBlog.
 * <p>
 * Properties are configured in the application.yml file.
 */
@ConfigurationProperties(prefix = "blog.config", ignoreUnknownFields = false)
public class ApplicationProperties {
    private boolean securedAPI;

    public boolean isSecuredAPI() {
        return securedAPI;
    }

    public void setSecuredAPI(boolean securedAPI) {
        this.securedAPI = securedAPI;
    }
}
