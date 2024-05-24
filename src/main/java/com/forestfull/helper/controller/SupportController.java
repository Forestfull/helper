package com.forestfull.helper.controller;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.domain.Json;
import com.forestfull.helper.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @GetMapping("/support/history")
    List<Client.History> getHistoriesByClientToken(String token){
        return supportService.getHistoriesByClientToken(token);
    }

    @PostMapping("/support")
    List<Client.History> toRequestForSolution(@RequestBody Json requestData, String token){
        supportService.toRequestForSolution(token, requestData);
        return getHistoriesByClientToken(token);
    }
}