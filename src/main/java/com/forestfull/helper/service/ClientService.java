package com.forestfull.helper.service;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.entity.Json;
import com.forestfull.helper.mapper.ClientMapper;
import com.forestfull.helper.util.IpUtil;
import com.forestfull.helper.util.ScheduleManager;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientMapper clientMapper;
    private final JavaMailSender mailSender;

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

    public boolean toRequestForSolution(Client client, String requestData) {
        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setSubject("[" + client.getCode() + "]accepted request");
            helper.setText(requestData);
            helper.setTo("bigfoot7774@gmail.com");
            mailSender.send(mimeMessage);

            clientMapper.toRequestForSolution(client.getId(), IpUtil.getIpAddress(), requestData);
            return true;
        } catch (MessagingException e) {
            return false;
        }

    }
}