package com.audrina.eccommerce;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigTestController {

    @Value("${spring.profiles.active:none}")
    private String profile;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.jpa.show-sql}")
    private String showSql;

    @GetMapping("/api/test-config")
    public ResponseEntity<String> testConfig() {
        return ResponseEntity.ok(
                "Profile: " + profile +
                        " | DB: " + dbUrl +
                        " | ShowSQL: " + showSql
        );
    }
}