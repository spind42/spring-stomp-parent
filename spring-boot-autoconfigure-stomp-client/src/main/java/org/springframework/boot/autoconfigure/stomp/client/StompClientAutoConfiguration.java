package org.springframework.boot.autoconfigure.stomp.client;


import org.springframework.boot.autoconfigure.stomp.client.netty.ClientNettyConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(StompClientProperties.class)
@Import({ClientNettyConfiguration.class})
public class StompClientAutoConfiguration {
}
