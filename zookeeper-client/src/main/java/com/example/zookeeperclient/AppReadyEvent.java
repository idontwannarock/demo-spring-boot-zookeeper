package com.example.zookeeperclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class AppReadyEvent implements ApplicationListener<ApplicationReadyEvent> {

    private final DiscoveryClient discoveryClient;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        printServiceInstances();
    }

    public void printServiceInstances() {
        Optional.ofNullable(discoveryClient.getInstances("zookeeper-client"))
                .ifPresentOrElse(this::printInstanceInfo, () -> log.info("no instance found"));
    }

    private void printInstanceInfo(List<ServiceInstance> serviceInstances) {
        if (serviceInstances.isEmpty()) {
            log.info("no instance found");
        } else {
            serviceInstances.forEach(instance -> {
                log.info("instance : {");
                log.info("  serviceId: {}", instance.getServiceId());
                log.info("  instanceId: {}", instance.getInstanceId());
                log.info("  host: {}", instance.getHost());
                log.info("  port: {}", instance.getPort());
                log.info("  uri: {}", instance.getUri());
                log.info("  schema: {}", instance.getScheme());
                log.info("}");
            });
        }
    }
}
