package com.forestfull.helper.controller;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @GetMapping("/support")
    List<Client.History> getHistoriesByClientToken(String token){
        return supportService.getHistoriesByClientToken(token);
    }
}