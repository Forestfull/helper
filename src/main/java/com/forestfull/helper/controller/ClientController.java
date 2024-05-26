package com.forestfull.helper.controller;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.entity.Json;
import com.forestfull.helper.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    public static class URI {
        public static final String SUPPORT = "/support";
        public static final String SUPPORT_HISTORY = "/support/history";
    }

    @GetMapping(URI.SUPPORT_HISTORY)
    List<Client.History> getHistoriesByClientToken(@RequestAttribute("ipAddress") String ipAddress, String token){

        return clientService.getHistoriesByClientToken(token);
    }

    @PostMapping(URI.SUPPORT)
    List<Client.History> toRequestForSolution(@RequestBody Json requestData, String token){
        clientService.toRequestForSolution(token, requestData);
//        return getHistoriesByClientToken(token);
        return null;
    }
}