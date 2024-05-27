package com.forestfull.helper.controller;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.entity.Json;
import com.forestfull.helper.entity.NetworkVO;
import com.forestfull.helper.handler.JsonTypeHandler;
import com.forestfull.helper.service.ManagementService;
import com.forestfull.helper.util.IpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ManagementController {

    private final ManagementService managementService;

    public static class URI {
        public static final String MANAGEMENT = "/management";
    }

    @GetMapping(URI.MANAGEMENT + "/{serviceCode}")
    NetworkVO.Response<List<Client.History>> getManagementHistory(@PathVariable("serviceCode") String serviceCode) {
        final List<Client.History> managementHistory = managementService.getManagementHistory(serviceCode);

        return ObjectUtils.isEmpty(managementHistory)
                ? NetworkVO.Response.fail(HttpStatus.NOT_FOUND)
                : NetworkVO.Response.ok(NetworkVO.DATA_TYPE.JSON, managementHistory);
    }

    @PostMapping(URI.MANAGEMENT + "/{serviceCode}")
    NetworkVO.Response<String> toResponseForSolution(@RequestBody Json requestData, @PathVariable("serviceCode") String serviceCode) {
        managementService.toRequestForSolution(serviceCode, requestData);
        return NetworkVO.Response.ok(NetworkVO.DATA_TYPE.STRING, "Success");
    }
}
