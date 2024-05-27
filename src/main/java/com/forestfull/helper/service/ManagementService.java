package com.forestfull.helper.service;

import com.forestfull.helper.entity.Json;
import com.forestfull.helper.mapper.ManagementMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagementService {

    private final ManagementMapper managementMapper;

    public void toRequestForSolution(Long id, String ipAddress, Json requestData) {
        managementMapper.toRequestForSolution(id, ipAddress, requestData);
    }
}
