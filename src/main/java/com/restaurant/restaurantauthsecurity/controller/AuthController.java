
package com.restaurant.restaurantauthsecurity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccessTestController {

    @GetMapping("/api/customer/test")
    public String customerTest() {
        return "Customer access granted";
    }

    @GetMapping("/api/admin/test")
    public String adminTest() {
        return "Admin access granted";
    }
}
