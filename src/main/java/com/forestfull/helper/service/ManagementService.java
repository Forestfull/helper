package com.forestfull.helper.service;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.entity.Json;
import com.forestfull.helper.mapper.ManagementMapper;
import com.forestfull.helper.util.IpUtil;
import com.forestfull.helper.util.ScheduleManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManagementService {

    private final ManagementMapper managementMapper;
    private final ClientService clientService;

    public void toRequestForSolution(String serviceCode, Json requestData) {
        ScheduleManager.tokenMap.values().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(serviceCode))
                .findFirst()
                .ifPresent(client -> managementMapper.toRequestForSolution(client.getId(), IpUtil.getIpAddress(), requestData));
    }

    public List<Client.History> getManagementHistory(String serviceCode) {
        return ScheduleManager.tokenMap.values().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(serviceCode))
                .findFirst()
                .map(c -> clientService.getHistoriesByClientToken(c.getToken(), Collections.emptyList()))
                .orElse(Collections.emptyList());
    }
}