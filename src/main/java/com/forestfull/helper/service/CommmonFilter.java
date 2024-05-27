package com.forestfull.helper.service;

import com.forestfull.helper.config.SecurityConfig;
import com.forestfull.helper.domain.Client;
import com.forestfull.helper.handler.JsonTypeHandler;
import com.forestfull.helper.mapper.CommonMapper;
import com.forestfull.helper.util.IpUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.filters.RemoteIpFilter;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
            final String requestURI = request.getRequestURI();
            if (Arrays.stream(SecurityConfig.ignoringPattern)
                    .anyMatch(uri -> SecurityConfig.antPathMatcher.match(uri, requestURI))) {
                chain.doFilter(req, res);

            } else if (Arrays.stream(SecurityConfig.clientUriPatterns)
                    .anyMatch(uri -> SecurityConfig.antPathMatcher.match(uri, requestURI))) {
                final RemoteIpFilter.XForwardedRequest forwardedRequest = new RemoteIpFilter.XForwardedRequest(request);
                final String token = HttpMethod.GET.name().equalsIgnoreCase(request.getMethod())
                        ? String.valueOf(request.getParameter("token"))
                        : request.getHeader("token");
                final Optional<Client> clientByToken = clientService.getClientByToken(token);

                if (clientByToken.isPresent()) {
                    if (!StringUtils.hasText(IpUtil.getIpAddress())) {
                        chain.doFilter(req, res);
                        return;
                    }

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

                    final String requestHeader = JsonTypeHandler.writer.writeValueAsString(header);
                    final String requestAttributes = JsonTypeHandler.writer.writeValueAsString(attributes);

                    commonMapper.recordRequestHistory(requestURI, requestHeader, requestAttributes, new RequestWrapper(forwardedRequest).getBody()); //request body 일회성 방지용
                }
                chain.doFilter(forwardedRequest, res);

            } else if (Arrays.stream(SecurityConfig.managementUriPatterns)
                    .anyMatch(uri -> SecurityConfig.antPathMatcher.match(uri, requestURI))) {
                final RemoteIpFilter.XForwardedRequest forwardedRequest = new RemoteIpFilter.XForwardedRequest(request);

                if (!StringUtils.hasText(IpUtil.getIpAddress())) {
                    chain.doFilter(req, res);
                    return;
                }

                chain.doFilter(forwardedRequest, res);

            } else {
                chain.doFilter(req, res);
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace(System.out);
            throw new RuntimeException();
        }
    }

    @Getter
    static class RequestWrapper extends HttpServletRequestWrapper {

        private final String body;

        public RequestWrapper(HttpServletRequest request) {
            super(request);

            final StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader bufferedReader = getCustomReader()) {
                char[] charBuffer = new char[128];
                int bytesRead;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } catch (Exception ignore) {
            } finally {
                body = stringBuilder.toString();
            }
        }

        @Override
        public ServletInputStream getInputStream() {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                    throw new UnsupportedOperationException();
                }

                public int read() {
                    return byteArrayInputStream.read();
                }
            };
        }

        public BufferedReader getCustomReader() {
            return new BufferedReader(new InputStreamReader(this.getInputStream()));
        }
    }
}
