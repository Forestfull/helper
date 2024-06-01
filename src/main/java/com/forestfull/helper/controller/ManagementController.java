package com.forestfull.helper.controller;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.entity.Json;
import com.forestfull.helper.entity.NetworkVO;
import com.forestfull.helper.service.ManagementService;
import com.forestfull.helper.util.ScheduleManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ManagementController {

    private final ManagementService managementService;

    public static class URI {
        public static final String MANAGEMENT = "/management";
    }

    @GetMapping(URI.MANAGEMENT)
    String forwardManagementPage(Model model) {
        model.addAttribute("serviceCodes"
                , ScheduleManager.tokenMap.values().stream()
                        .map(Client::getCode)
                        .toList());

        return "management";
    }

    @ResponseBody
    @GetMapping(URI.MANAGEMENT + "/{serviceCode}")
    NetworkVO.Response<List<Client.History>> getManagementHistory(@PathVariable("serviceCode") String serviceCode) {
        final List<Client.History> managementHistory = managementService.getManagementHistory(serviceCode);

        return ObjectUtils.isEmpty(managementHistory)
                ? NetworkVO.Response.fail(HttpStatus.NOT_FOUND)
                : NetworkVO.Response.ok(NetworkVO.DATA_TYPE.JSON, managementHistory);
    }

    @ResponseBody
    @PostMapping(URI.MANAGEMENT + "/{serviceCode}")
    NetworkVO.Response<String> toResponseForSolution(@RequestBody Json requestData, @PathVariable("serviceCode") String serviceCode) {
        managementService.toRequestForSolution(serviceCode, requestData);
        return NetworkVO.Response.ok(NetworkVO.DATA_TYPE.STRING, "Success");
    }
}
