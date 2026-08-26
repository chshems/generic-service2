package com.mycompany.smp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "bookings")
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long serviceId;
    private String serviceName;
    private Double price;

    private Long consumerId;
    private String consumerName;

    private LocalDateTime bookingDate;
    private String status; // PENDING, CONFIRMED, CANCELLED, RESCHEDULED
    private LocalDateTime createdAt = LocalDateTime.now();
}
