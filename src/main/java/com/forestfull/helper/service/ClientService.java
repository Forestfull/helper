package com.forestfull.helper.service;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.domain.Json;
import com.forestfull.helper.mapper.ClientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientMapper clientMapper;

    public List<Client.History> getHistoriesByClientToken(String token) {
        return clientMapper.getHistoriesByClientToken(token);
    }

    public void toRequestForSolution(String token, Json requestData) {
        clientMapper.toRequestForSolution(token, requestData);
    }
}
