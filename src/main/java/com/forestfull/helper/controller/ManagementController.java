package com.forestfull.helper.controller;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.entity.Json;
import com.forestfull.helper.entity.NetworkVO;
import com.forestfull.helper.handler.JsonTypeHandler;
import com.forestfull.helper.service.ManagementService;
import com.forestfull.helper.util.IpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ManagementController {

    private final ManagementService managementService;

    public static class URI {
        public static final String MANAGEMENT = "/management";
    }

    @PostMapping(URI.MANAGEMENT)
    NetworkVO.Response<String> toResponseForSolution(
            @RequestBody Json requestData
            , @RequestHeader String serviceCode
    ) throws IOException {
        managementService.toRequestForSolution(JsonTypeHandler.reader.readValue(serviceCode, Client.class).getId(), requestData);
        return NetworkVO.Response.ok(NetworkVO.DATA_TYPE.STRING, "Success");
    }
}
