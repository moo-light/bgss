// package com.server.blockchainserver.platform.hyperledgerfabric.network;
//
// import io.grpc.Channel;
// import org.hyperledger.fabric.client.Gateway;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// import java.util.concurrent.TimeUnit;
//
// import static com.server.blockchainserver.platform.hyperledgerfabric.network.FabricGateway.*;
//
// @Configuration
// public class GatewayConfig {
//
//    @Bean
//    public Gateway gateway() throws Exception{
//        return Gateway.newInstance().identity(newIdentity()).signer(newSigner()).connection(newGrpcConnection())
//                .evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
//                .endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
//                .submitOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
//                .commitStatusOptions(options -> options.withDeadlineAfter(1, TimeUnit.MINUTES)).connect();
//    }
//
//}
