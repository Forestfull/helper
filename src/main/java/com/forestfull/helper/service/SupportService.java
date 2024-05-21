package com.forestfull.helper.service;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.mapper.SupportMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportMapper supportMapper;

    public List<Client.History> getHistoriesByClientToken(String token) {
        return supportMapper.getHistoriesByClientToken(token);
    }
}
