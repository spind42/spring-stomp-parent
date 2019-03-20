package org.springframework.boot.autoconfigure.stomp.client.netty;

import asia.stampy.client.listener.validate.ClientMessageValidationListener;
import asia.stampy.client.netty.ClientNettyChannelHandler;
import asia.stampy.client.netty.ClientNettyMessageGateway;
import asia.stampy.client.netty.connected.NettyConnectedMessageListener;
import asia.stampy.client.netty.disconnect.NettyDisconnectListenerAndInterceptor;
import asia.stampy.common.heartbeat.HeartbeatContainer;
import org.springframework.boot.autoconfigure.stomp.client.StompClientProperties;

public class ClientNettyMessageGatewayFactory {

    private final StompClientProperties stompClientProperties;

    public ClientNettyMessageGatewayFactory(StompClientProperties stompClientProperties) {
        this.stompClientProperties = stompClientProperties;
    }

    public ClientNettyMessageGateway createGateway() {
        HeartbeatContainer heartbeatContainer = new HeartbeatContainer();

        ClientNettyMessageGateway gateway = new ClientNettyMessageGateway();
        gateway.setPort(stompClientProperties.getPort());
        gateway.setHost(stompClientProperties.getHostname());
        gateway.setHeartbeat(stompClientProperties.getHeartbeat());

        ClientNettyChannelHandler channelHandler = new ClientNettyChannelHandler();
        channelHandler.setGateway(gateway);
        channelHandler.setHeartbeatContainer(heartbeatContainer);

//        gateway.addMessageListener(new IDontNeedSecurity()); // DON'T DO THIS!!!

        gateway.addMessageListener(new ClientMessageValidationListener());

        NettyConnectedMessageListener cml = new NettyConnectedMessageListener();
        cml.setHeartbeatContainer(heartbeatContainer);
        cml.setGateway(gateway);
        gateway.addMessageListener(cml);

        NettyDisconnectListenerAndInterceptor disconnect = new NettyDisconnectListenerAndInterceptor();
        disconnect.setCloseOnDisconnectMessage(false);
        gateway.addMessageListener(disconnect);
        gateway.addOutgoingMessageInterceptor(disconnect);
        disconnect.setGateway(gateway);

        gateway.setHandler(channelHandler);

        return gateway;
    }

}
