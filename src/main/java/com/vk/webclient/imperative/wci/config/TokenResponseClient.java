package com.vk.webclient.imperative.wci.config;


import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.reactive.function.client.WebClient;

public class TokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> {
    WebClientReactiveClientCredentialsTokenResponseClient accessTokenResponseClient;

    public TokenResponseClient(){
        accessTokenResponseClient = new WebClientReactiveClientCredentialsTokenResponseClient();
    }
    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2ClientCredentialsGrantRequest authorizationGrantRequest) {
        return accessTokenResponseClient.getTokenResponse(authorizationGrantRequest).block();

    }
    public void setWebClient(WebClient webClient){
        accessTokenResponseClient.setWebClient(webClient);
    }
}
