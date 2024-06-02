package com.forestfull.helper.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

@Data
public class Client implements Cloneable {
    private Long id;
    private String code;
    private String token;
    private String description;

    public String encodeToken() {
        final byte[] bytes = this.token.getBytes();
        return Optional.of(new String(Base64.getEncoder().encode(bytes))).orElse("");
    }

    public boolean isValidated(String token) {
        return this.encodeToken().equals(token);
    }

    @Override
    public Client clone() {
        try {
            return (Client) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
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