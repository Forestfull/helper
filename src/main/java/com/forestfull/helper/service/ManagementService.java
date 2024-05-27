package com.forestfull.helper.service;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.entity.Json;
import com.forestfull.helper.mapper.ManagementMapper;
import com.forestfull.helper.util.IpUtil;
import com.forestfull.helper.util.ScheduleManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManagementService {

    private final ManagementMapper managementMapper;

    public void toRequestForSolution(String solutionCode, Json requestData) {
        final Optional<Client> optionalClient = ScheduleManager.tokenMap.values().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(solutionCode))
                .findFirst();

        optionalClient
                .ifPresent(client -> managementMapper.toRequestForSolution(client.getId(), IpUtil.getIpAddress(), requestData));
    }
}