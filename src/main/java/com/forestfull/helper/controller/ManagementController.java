package com.forestfull.helper.controller;

import com.forestfull.helper.service.ManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ManagementController {

    private final ManagementService managementService;

}
