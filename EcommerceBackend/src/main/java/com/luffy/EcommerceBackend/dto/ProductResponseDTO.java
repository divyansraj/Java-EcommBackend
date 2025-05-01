package com.luffy.EcommerceBackend.dto;

import com.luffy.EcommerceBackend.model.Photo;
import com.luffy.EcommerceBackend.model.Review;
import jdk.jfr.consumer.RecordedEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ProductResponseDTO {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private List<Photo> photo;
    private String category;
    private String brand;
    private Double ratings;
    private Integer numberOfReviews;
    private List<Review> review;
    private LocalDateTime createdAt;


    // User Info (Partial)
    private Long userId;
    private String userName;
    private String userEmail;
}
