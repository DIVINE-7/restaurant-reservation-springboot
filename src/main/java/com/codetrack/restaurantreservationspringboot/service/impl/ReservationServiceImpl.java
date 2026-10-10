package com.codetrack.restaurantreservationspringboot.service.impl;

import com.codetrack.restaurantreservationspringboot.dto.ReservationRequest;
import com.codetrack.restaurantreservationspringboot.entity.*;
import com.codetrack.restaurantreservationspringboot.enums.ReservationStatus;
import com.codetrack.restaurantreservationspringboot.repository.*;
import com.codetrack.restaurantreservationspringboot.service.ReservationService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository tableRepository;

    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            CustomerRepository customerRepository,
            RestaurantRepository restaurantRepository,
            RestaurantTableRepository tableRepository) {
        this.reservationRepository = reservationRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.tableRepository = tableRepository;
    }

    @Override
    public Reservation createReservation(ReservationRequest request) {

        if (request.reservationDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reservation date cannot be in the past");
        }

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer not found"));

        Restaurant restaurant = restaurantRepository.findById(
                        request.restaurantId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Restaurant not found"));

        RestaurantTable table = null;

        if (request.tableId() != null) {
            table = tableRepository.findById(request.tableId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Table not found"));

            if (!table.isActive()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Table is inactive");
            }

            if (!table.getRestaurant().getId().equals(restaurant.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Table does not belong to this restaurant");
            }

            if (table.getCapacity() < request.guests()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Table capacity is insufficient");
            }
        }

        Reservation reservation = Reservation.builder()
                .customer(customer)
                .restaurant(restaurant)
                .table(table)
                .reservationDate(request.reservationDate())
                .reservationTime(request.reservationTime())
                .guests(request.guests())
                .status(ReservationStatus.PENDING)
                .qrToken(UUID.randomUUID().toString())
                .build();

        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reservation not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByCustomer(Long customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Customer not found");
        }

        return reservationRepository
                .findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Override
    public Reservation updateReservationStatus(
            Long id, ReservationStatus status) {

        Reservation reservation = getReservationById(id);

        if (reservation.getStatus() == ReservationStatus.CANCELLED
                || reservation.getStatus() == ReservationStatus.COMPLETED
                || reservation.getStatus() == ReservationStatus.NO_SHOW) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot change a terminal reservation");
        }

        reservation.setStatus(status);

        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation cancelReservation(Long id) {

        Reservation reservation = getReservationById(id);

        if (reservation.getStatus() == ReservationStatus.COMPLETED
                || reservation.getStatus() == ReservationStatus.CANCELLED
                || reservation.getStatus() == ReservationStatus.NO_SHOW) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reservation cannot be cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        return reservationRepository.save(reservation);
    }
}