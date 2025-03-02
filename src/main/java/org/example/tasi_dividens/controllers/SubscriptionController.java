package org.example.tasi_dividens.controllers;

import org.example.tasi_dividens.models.SubscriptionRequest;
import org.example.tasi_dividens.services.SubscriptionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService service;

    public SubscriptionController(SubscriptionService service) {
        this.service = service;
    }

    @PostMapping
    public void subscribe(@RequestBody SubscriptionRequest request) {
        service.saveSubscription(request.getDeviceToken(), request.getCompanySymbol());
    }


    @PostMapping("/unSubscribe")
    public void unSubscribe(@RequestBody SubscriptionRequest request) {
        service.unsubscribe(request.getDeviceToken(), request.getCompanySymbol());
    }
}
