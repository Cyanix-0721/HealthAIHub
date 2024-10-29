package com.mole.health.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String endpoint;
    private String externalEndpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String bucketPolicy;
}