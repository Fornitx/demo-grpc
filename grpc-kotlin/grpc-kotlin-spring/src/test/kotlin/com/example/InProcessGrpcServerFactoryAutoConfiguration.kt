package com.example

import io.grpc.BindableService
import io.grpc.inprocess.InProcessServerBuilder
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.grpc.autoconfigure.client.ClientInterceptorsConfiguration
import org.springframework.grpc.autoconfigure.client.GrpcClientAutoConfiguration
import org.springframework.grpc.autoconfigure.server.GrpcServerFactoryAutoConfiguration
import org.springframework.grpc.client.ClientInterceptorsConfigurer
import org.springframework.grpc.server.ServerBuilderCustomizer
import org.springframework.grpc.server.service.GrpcServiceDiscoverer
import org.springframework.grpc.test.InProcessGrpcChannelFactory
import org.springframework.grpc.test.InProcessGrpcServerFactory

@AutoConfiguration(before = [GrpcServerFactoryAutoConfiguration::class, GrpcClientAutoConfiguration::class])
@ConditionalOnProperty(prefix = "spring.grpc.inprocess", name = ["enabled"], havingValue = "true")
@ConditionalOnClass(BindableService::class)
@Import(ClientInterceptorsConfiguration::class)
class InProcessGrpcServerFactoryAutoConfiguration {
    private val address: String = InProcessServerBuilder.generateName()

    @Bean
    @ConditionalOnBean(BindableService::class)
    fun grpcServerFactory(
        grpcServicesDiscoverer: GrpcServiceDiscoverer,
        customizers: List<ServerBuilderCustomizer<InProcessServerBuilder>>,
    ): InProcessGrpcServerFactory = InProcessGrpcServerFactory(address, customizers).apply {
        grpcServicesDiscoverer.findServices().forEach(this::addService)
    }

    @Bean
    fun grpcChannelFactory(
        interceptorsConfigurer: ClientInterceptorsConfigurer,
    ): InProcessGrpcChannelFactory = InProcessGrpcChannelFactory(interceptorsConfigurer).apply {
        setVirtualTargets { path -> address }
    }
}
