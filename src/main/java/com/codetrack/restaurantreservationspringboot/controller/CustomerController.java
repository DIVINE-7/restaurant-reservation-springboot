package com.codetrack.restaurantreservationspringboot.controller;

import com.codetrack.restaurantreservationspringboot.entity.Customer;
import com.codetrack.restaurantreservationspringboot.repository.CustomerRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public record CustomerRequest(
            @jakarta.validation.constraints.NotBlank
            String fullName,

            @jakarta.validation.constraints.Email
            @jakarta.validation.constraints.NotBlank
            String email,

            @jakarta.validation.constraints.NotBlank
            String password,

            String phone
    ) {}

    public record CustomerResponse(
            Long id,
            String fullName,
            String email,
            String phone
    ) {}

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhone()
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createCustomer(
            @Valid @RequestBody CustomerRequest request) {

        if (customerRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        Customer customer = Customer.builder()
                .fullName(request.fullName().trim())
                .email(request.email().trim().toLowerCase())
                .password(request.password())
                .phone(request.phone())
                .build();

        return toResponse(customerRepository.save(customer));
    }

    @GetMapping
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public CustomerResponse getCustomerById(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"
                ));

        return toResponse(customer);
    }
}