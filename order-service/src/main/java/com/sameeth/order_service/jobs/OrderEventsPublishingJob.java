package com.sameeth.order_service.jobs;

import com.sameeth.order_service.domain.OrderEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OrderEventsPublishingJob {
    private static final Logger log = LoggerFactory.getLogger(OrderEventsPublishingJob.class);
    private final OrderEventService orderEventService;

    OrderEventsPublishingJob(OrderEventService orderEventService) {
        this.orderEventService = orderEventService;
    }

    @Scheduled(cron = "${orders.publish-order-events-job-cron}")
    public void publishOrderEvents() {
        log.info("Publishing Order Events at {}", Instant.now());
        orderEventService.publishOrderEvents();
    }
}
