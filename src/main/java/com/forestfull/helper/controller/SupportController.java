package com.forestfull.helper.controller;

import com.forestfull.helper.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;
}