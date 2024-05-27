package com.forestfull.helper.service;

import com.forestfull.helper.entity.Json;
import com.forestfull.helper.mapper.ManagementMapper;
import com.forestfull.helper.util.IpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagementService {

    private final ManagementMapper managementMapper;

    public void toRequestForSolution(Long id, Json requestData) {
        managementMapper.toRequestForSolution(id, IpUtil.getIpAddress(), requestData);
    }
}
