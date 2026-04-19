package com.prerak.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileStorageConfig {

    @Value("${file.upload-dir}")
    public String uploadDir;

    @Value("${file.output-dir}")
    public String outputDir;
}