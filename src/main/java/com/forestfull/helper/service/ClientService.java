package com.forestfull.helper.service;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.entity.Json;
import com.forestfull.helper.mapper.ClientMapper;
import com.forestfull.helper.util.IpUtil;
import com.forestfull.helper.util.ScheduleManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.Comparator;
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

    public List<Client.History> getHistoriesByClientToken(String token, List<Long> exceptedIds) {
        final List<Client.History> historiesByClientToken = clientMapper.getHistoriesByClientToken(token, exceptedIds);
        if (ObjectUtils.isEmpty(historiesByClientToken)) return Collections.emptyList();

        return historiesByClientToken.stream()
                .peek(history -> getClientByToken(token).ifPresent(history::setClient))
                .sorted(Comparator.comparing(Client.History::getCreatedTime).reversed())
                .toList();
    }

    public void toRequestForSolution(Long clientId, Json requestData) {
        clientMapper.toRequestForSolution(clientId, IpUtil.getIpAddress(), requestData);
    }
}
