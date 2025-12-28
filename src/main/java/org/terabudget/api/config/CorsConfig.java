package org.terabudget.api.config;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@ConfigurationProperties(prefix = "terabudget.cors")
public class CorsConfig {
    private String allowedHeadersString;
    private String allowedOriginsString;
    private String allowedMethodsString;

    @Getter
    List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
    @Getter
    List<String> allowedOrigins = List.of("*");
    @Getter
    List<String> allowedHeaders = List.of("Content-Type", "Authorization", "RequestBy");

    public String getAllowedHeadersString() {
        if (allowedHeadersString == null) {
            synchronized (this) {
                if (allowedHeadersString == null) {
                    allowedHeadersString = StringUtils.join(allowedHeaders, ",");
                }
            }
        }
        return allowedHeadersString;
    }

    public String getAllowedOriginsString() {
        if (allowedOriginsString == null) {
            synchronized (this) {
                if (allowedOriginsString == null) {
                    allowedOriginsString = StringUtils.join(allowedOrigins, ",");
                }
            }
        }
        return allowedOriginsString;
    }

    public String getAllowedMethodsString() {
        if (allowedMethodsString == null) {
            synchronized (this) {
                if (allowedMethodsString == null) {
                    allowedMethodsString = StringUtils.join(allowedMethods, ",");
                }
            }
        }
        return allowedMethodsString;
    }
}
