package com.forestfull.helper.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.forestfull.helper.entity.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@Data
public class Client {
    private Long id;
    private String code;
    private String token;
    private String description;

    public boolean isValidated(String token) {
        final String decodedToken = Arrays.toString(Base64.getDecoder().decode(token));
        return Optional.ofNullable(token).orElse("").equals(decodedToken);
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
        private Json data;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdTime;
    }
}