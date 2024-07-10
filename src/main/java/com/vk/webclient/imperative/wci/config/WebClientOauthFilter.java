package com.vk.webclient.imperative.wci.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Clock;
import java.time.Duration;

public class WebClientOauthFilter implements WebClientCustomizer {

    @Autowired(required = false)
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired(required = false)
    private OAuth2AuthorizedClientService auth2AuthorizedClientService;

    @Override
    public void customize(WebClient.Builder webClientBuilder) {
        webClientBuilder.clientConnector(new ReactorClientHttpConnector(HttpClient.create()));
        if(clientRegistrationRepository!=null){
            ServletOAuth2AuthorizedClientExchangeFilterFunction oauth = servletOauth2AuthorizedClientExchangeFunction(webClientBuilder);
            webClientBuilder.filter(oauth);
        }
    }
    private ServletOAuth2AuthorizedClientExchangeFilterFunction servletOauth2AuthorizedClientExchangeFunction(WebClient.Builder webclientBuilder){
        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, auth2AuthorizedClientService);
        TokenResponseClient accessTokenResponseClient = new TokenResponseClient();
        accessTokenResponseClient.setWebClient(webclientBuilder.build());

        OAuth2AuthorizedClientProvider auth2AuthorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .refreshToken()
                .clientCredentials(c -> {
                    c.accessTokenResponseClient(accessTokenResponseClient);
                    c.clockSkew(Duration.ofSeconds(20));
                    c.clock(Clock.systemUTC());
                }).build();
        authorizedClientManager.setAuthorizedClientProvider(auth2AuthorizedClientProvider);
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth.setDefaultOAuth2AuthorizedClient(true);
        return oauth;
    }
}
