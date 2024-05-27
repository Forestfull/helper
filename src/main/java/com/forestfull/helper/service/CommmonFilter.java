package com.forestfull.helper.service;

import com.forestfull.helper.config.SecurityConfig;
import com.forestfull.helper.domain.Client;
import com.forestfull.helper.handler.JsonTypeHandler;
import com.forestfull.helper.mapper.CommonMapper;
import com.forestfull.helper.util.IpUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.filters.RemoteIpFilter;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@RequiredArgsConstructor
public class CommmonFilter implements Filter {

    private final ClientService clientService;
    private final CommonMapper commonMapper;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        try {
            final HttpServletRequest request = (HttpServletRequest) req;
            if (Arrays.stream(SecurityConfig.ignoringPattern).anyMatch(uri -> SecurityConfig.antPathMatcher.match(uri, request.getRequestURI()))) {
                chain.doFilter(req, res);
                return;
            }

            final RemoteIpFilter.XForwardedRequest forwardedRequest = new RemoteIpFilter.XForwardedRequest(request);
            final String token = HttpMethod.GET.name().equalsIgnoreCase(request.getMethod())
                    ? String.valueOf(request.getParameter("token"))
                    : request.getHeader("token");
            final Optional<Client> clientByToken = clientService.getClientByToken(token);

            if (clientByToken.isPresent()) {
                final String ipAddress = IpUtil.getIpAddress(forwardedRequest);
                if (!StringUtils.hasText(ipAddress)) {
                    chain.doFilter(req, res);
                    return;
                }

                forwardedRequest.setHeader("ipAddress", ipAddress);
                forwardedRequest.setHeader("client", JsonTypeHandler.writer.writeValueAsString(clientByToken.get()));

                final Map<String, List<String>> header = new LinkedHashMap<>();
                final Iterator<String> headerNames = forwardedRequest.getHeaderNames().asIterator();
                while (headerNames.hasNext()) {
                    final List<String> values = new LinkedList<>();

                    final String headerName = headerNames.next();
                    final Iterator<String> headerContents = forwardedRequest.getHeaders(headerName).asIterator();
                    headerContents.forEachRemaining(values::add);
                    header.put(headerName, values);
                }

                final Map<String, String> attributes = new LinkedHashMap<>();
                final Iterator<String> attributeNames = forwardedRequest.getAttributeNames().asIterator();
                while (attributeNames.hasNext()) {
                    final String attributeName = attributeNames.next();
                    attributes.put(attributeName, String.valueOf(forwardedRequest.getAttribute(attributeName)));
                }

                ServletInputStream inputStream = request.getInputStream();
                final String requestHeader = JsonTypeHandler.writer.writeValueAsString(header);
                final String requestAttributes = JsonTypeHandler.writer.writeValueAsString(attributes);
                final String requestBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

                commonMapper.recordRequestHistory(request.getRequestURI(), requestHeader, requestAttributes, requestBody);
            }
            chain.doFilter(forwardedRequest, res);
        } catch (IOException | ServletException e) {
            e.printStackTrace(System.out);
            throw new RuntimeException();
        }
    }
}
