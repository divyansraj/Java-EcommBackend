package com.luffy.EcommerceBackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Represents a user review for a product, including rating and comment.
 * Maps to the Mongoose productSchema.reviews array.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reviews")
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User who wrote the review (reviewer).
     * Bidirectional relationship with User.reviews, required.
     */
    @NotNull(message = "User is required") @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Reviewer name is required")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1") @Max(value = 5, message = "Rating must be at most 5")
    @Column(nullable = false)
    private Integer rating;

    @NotBlank(message = "Comment is required") @Column(nullable = false)
    private String comment;

    /**
     * Product being reviewed.
     * Bidirectional relationship with Product.reviews, required.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

}
