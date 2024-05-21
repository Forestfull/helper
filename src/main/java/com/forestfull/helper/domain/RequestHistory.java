package com.forestfull.helper.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RequestHistory {
    private Long id;
    private String uri;
    private String requestHeader;
    private LocalDateTime createdTime;
}