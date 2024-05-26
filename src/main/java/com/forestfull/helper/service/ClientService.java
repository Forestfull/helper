package com.forestfull.helper.service;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.domain.Json;
import com.forestfull.helper.mapper.ClientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientMapper clientMapper;

    public Optional<Client> getClientByToken(String token){



        return clientMapper.getClientByToken(token);
    }

    public List<Client.History> getHistoriesByClientToken(String token) {
        return clientMapper.getHistoriesByClientToken(token);
    }

    public void toRequestForSolution(String token, Json requestData) {
        clientMapper.toRequestForSolution(token, requestData);
    }
}
