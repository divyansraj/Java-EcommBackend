package com.luffy.EcommerceBackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a product in the e-commerce system (e.g., hoodie), created by a user (seller).
 * schema with fields like name, price, user, reviews, and photos.
 */

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Please enter the product name")
    @Size(max = 120, message = "Product name must be within 120 characters")
    @Column(nullable = false, length = 120)
    private String name;

    @NotNull(message = "Please enter the product price")
    @Column(nullable = false)
    private Double price;

    @NotBlank(message = "Please enter the product description")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * Photos associated with this product (e.g., product images).
     * Bidirectional relationship with Photo.product, lazily fetched.
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Photo> photos = new ArrayList<>();

    /**
     * Product category, required, from Category enum.
     */
    @NotNull(message = "Please select a category")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @NotBlank(message = "Please enter the product brand")
    @Column(nullable = false)
    private String brand;

    @Min(value = 0, message = "Ratings must be non-negative")
    @Column(nullable = false)
    private Double ratings = 0.0;

    @Min(value = 0, message = "Number of reviews must be non-negative")
    @Column(name = "number_of_reviews", nullable = false)
    private Integer numberOfReviews = 0;

    /**
     * Reviews for this product, written by users.
     * Bidirectional relationship with Review.product, lazily fetched.
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    /**
     * User who created this product (seller).
     * Bidirectional relationship with User.products, required.
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
