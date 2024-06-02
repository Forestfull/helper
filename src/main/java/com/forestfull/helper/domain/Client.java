package com.forestfull.helper.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.forestfull.helper.entity.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

@Data
public class Client {
    private Long id;
    private String code;
    private String token;
    private String encodedToken;
    private String description;

    public String getEncodedToken() {
        if (Objects.nonNull(encodedToken)) return encodedToken;

        final byte[] bytes = this.token.getBytes();
        return Optional.of(new String(Base64.getEncoder().encode(bytes))).orElse("");
    }

    public boolean isValidated(String token) {
        return this.getEncodedToken().equals(token);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class History {
        private Long id;
        private Long clientId;
        private Client client;
        private String type;
        private String ipAddress;
        private String data;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdTime;
    }
}