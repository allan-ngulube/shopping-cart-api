package com.allan.notification_service.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProcessedOrderService {

    private final Set<Long> processedOrders =
            ConcurrentHashMap.newKeySet();

    public boolean isProcessed(Long orderId) {
        return processedOrders.contains(orderId);
    }

    public void markProcessed(Long orderId) {
        processedOrders.add(orderId);
    }
}