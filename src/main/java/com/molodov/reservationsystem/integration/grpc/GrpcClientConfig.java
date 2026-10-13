package com.molodov.reservationsystem.integration.grpc;

import com.molodov.notification.grpc.NotificationServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientConfig {

    @Bean
    public NotificationServiceGrpc.NotificationServiceBlockingStub notificationStub(
            GrpcChannelFactory channelFactory
    ) {
        return NotificationServiceGrpc.newBlockingStub(
                channelFactory.createChannel("static://localhost:9090")
        );
    }
}
