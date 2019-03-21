package org.springframework.stomp.client.netty;

import asia.stampy.client.netty.ClientNettyMessageGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.stomp.client.configuration.StompClientProperties;

@Configuration
public class ClientNettyConfiguration {

    @Autowired
    StompClientProperties stompClientProperties;


    @Bean
    @ConditionalOnMissingBean(ClientNettyMessageGatewayFactory.class)
    public ClientNettyMessageGatewayFactory clientNettyMessageGatewayFactory() {
        ClientNettyMessageGatewayFactory clientNettyMessageGatewayFactory = new ClientNettyMessageGatewayFactory(stompClientProperties);

        //TODO: call

        return clientNettyMessageGatewayFactory;
    }

    @Bean
    @ConditionalOnMissingBean(ClientNettyMessageGateway.class)
    public ClientNettyMessageGateway createStompClient(ClientNettyMessageGatewayFactory clientNettyMessageGatewayFactory) {
        return clientNettyMessageGatewayFactory.createGateway();
    }

}
