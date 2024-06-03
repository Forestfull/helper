package com.forestfull.helper.controller;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.entity.Json;
import com.forestfull.helper.entity.NetworkVO;
import com.forestfull.helper.handler.JsonTypeHandler;
import com.forestfull.helper.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    public static class URI {
        public static final String SUPPORT = "/support";
        public static final String SUPPORT_HISTORY = "/support/history";
    }

    @GetMapping("/{token}")
    String indexPage(@PathVariable("token") String token) {
        return "index.html";
    }

    @GetMapping("/master")
    String managementPage() {
        return "management.html";
    }

    @ResponseBody
    @GetMapping(URI.SUPPORT_HISTORY)
    NetworkVO.Response<List<Client.History>> getHistoriesByClientToken(
            @RequestParam(name = "token") String token
            , @RequestParam(name = "exceptedIds", required = false) List<Long> exceptedIds) {
        return NetworkVO.Response.ok(NetworkVO.DATA_TYPE.JSON, clientService.getHistoriesByClientToken(token, exceptedIds));
    }

    @ResponseBody
    @PostMapping(URI.SUPPORT)
    NetworkVO.Response<String> toRequestForSolution(
            @RequestBody String requestData
            , @RequestHeader String client
    ) throws IOException {
        final boolean isSucceed = clientService.toRequestForSolution(JsonTypeHandler.reader.readValue(client, Client.class), requestData);
        return isSucceed
                ? NetworkVO.Response.ok(NetworkVO.DATA_TYPE.STRING, "Success")
                : NetworkVO.Response.fail(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}