package com.forestfull.helper.util;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.mapper.ClientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class ScheduleManager {

    public static volatile Map<String, Client> tokenMap = new HashMap<>();
    private final ClientMapper clientMapper;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    void recyclingTokenSet() {
        final List<Client> usedAllClient = clientMapper.getUsedAllClient();
        if (ObjectUtils.isEmpty(usedAllClient)) return;

        tokenMap = usedAllClient.stream().collect(Collectors.toMap(Client::getToken, client -> client));
    }



}
