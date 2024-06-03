package com.forestfull.helper.service;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.entity.Json;
import com.forestfull.helper.mapper.ClientMapper;
import com.forestfull.helper.util.IpUtil;
import com.forestfull.helper.util.ScheduleManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientMapper clientMapper;

    public Optional<Client> getClientByToken(String token) {
        return Optional.of(ScheduleManager.tokenMap.get(token).clone());
    }

    public List<Client.History> getHistoriesByClientToken(String token, List<Long> exceptedIds) {
        final String decodedToken = new String(Base64.getDecoder().decode(token));
        final List<Client.History> historiesByClientToken = clientMapper.getHistoriesByClientToken(decodedToken, exceptedIds);
        if (ObjectUtils.isEmpty(historiesByClientToken)) return Collections.emptyList();

        return historiesByClientToken.stream()
                .peek(ch -> getClientByToken(token)
                        .ifPresent(c -> {
                            c.setToken(null);
                            ch.setClient(c);
                        }))
                .sorted(Comparator.comparing(Client.History::getCreatedTime))
                .toList();
    }

    public void toRequestForSolution(Long clientId, String requestData) {
        clientMapper.toRequestForSolution(clientId, IpUtil.getIpAddress(), requestData);
    }
}