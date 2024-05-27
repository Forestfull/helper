package com.forestfull.helper.service;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.mapper.ClientMapper;
import com.forestfull.helper.util.ScheduleManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientMapper clientMapper;

    public Optional<Client> getClientByToken(String token) {
        final Client client = ScheduleManager.tokenMap.get(token);
        if (!ObjectUtils.isEmpty(client)) return Optional.of(client);

        return clientMapper.getClientByToken(token);
    }

    public List<Client.History> getHistoriesByClientToken(String token) {
        return clientMapper.getHistoriesByClientToken(token);
    }

    public void toRequestForSolution(Long clientId, String ipAddress, String requestData) {
        clientMapper.toRequestForSolution(clientId, ipAddress, requestData);
    }
}
