package com.mycompany.smp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "reviews")
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long serviceId;
    private String serviceName;
    private String consumerName;

    private Integer rating; // 1 to 5 stars

    @Column(columnDefinition = "TEXT")
    private String reviewText;

    private boolean approved = false; // Admin must toggle this to true to publish it
    private LocalDateTime createdAt = LocalDateTime.now();
}
