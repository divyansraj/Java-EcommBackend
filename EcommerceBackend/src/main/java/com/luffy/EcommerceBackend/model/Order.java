package com.luffy.EcommerceBackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user's order, including items, amount, and address.
 * Maps to the Mongoose orderSchema (userId, items, amount, address, status, date, payment).
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User who placed the order (buyer).
     * Bidirectional relationship with User.orders, required.
     */
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Items in the order.
     * Unidirectional relationship to OrderItem, cascaded.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be non-negative")
    @Column(nullable = false)
    private Double amount;

    /**
     * Shipping address, required (simplified as string; can be expanded to a separate entity).
     */
    @NotBlank(message = "Address is required")
    @Column(nullable = false)
    private String address;

    /**
     * Order status, default "Processing".
     */
    @NotBlank(message = "Status is required")
    @Column(nullable = false)
    private String status = "Processing";

    /**
     * Order creation timestamp, default to now.
     */
    @NotNull(message = "Date is required")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Payment status, default false (not paid).
     */
    @Column(nullable = false)
    private Boolean payment = false;
}