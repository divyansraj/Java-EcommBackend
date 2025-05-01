package com.luffy.EcommerceBackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the e-commerce system, who can be a buyer (adding to cart, reviewing, ordering)
 * or a seller (creating and managing products).
 */


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "users")
public class User {
    /**
     * Unique identifier for the user, auto-generated.
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Please enter your name" )
    @Size(min=2,max = 40, message = "Enter your name within 40 letters") @Column(length = 40, nullable = false)
    private String name;

    @NotBlank(message = "Please enter your email") @Email(message = "Please enter a valid email address") @Size(max = 254, message = "Email address is too long")
    @Column(unique = true,nullable = false, length = 254)
    private String email;


    /**
     * BCrypt-hashed password, required, 8-100 characters before hashing.
     * Hashed in the service layer before storage.
     */
    @NotBlank(message = "Please enter the password") @Size(min = 6,max = 100, message = "Password should be atleast 6 characters long")
    @Column(length = 255, nullable = false)
    private String password;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER; // Default to USER

    /**
     * Products created by this user (seller role).
     * Bidirectional relationship with Product.user.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Product> products = new ArrayList<>();

    /**
     * Reviews written by this user (buyer role).
     * Bidirectional relationship with Review.user, lazily fetched.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    /**
     * Cart items added by this user (buyer role).
     * Bidirectional relationship with CartItem.user
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    /**
     * Orders placed by this user (buyer role).
     * * Bidirectional relationship with Order.user
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();


}
/**
 *
 * Field        |      Mapping                      | Meaning
 * products     | @OneToMany(mappedBy = "user")     | A user (seller) can create multiple products.
 * reviews      | @OneToMany(mappedBy = "user")     | A user (buyer) can write multiple reviews.
 * cartItems    | @OneToMany(mappedBy = "user")    | A user can have multiple items in cart.
 * orders       | @OneToMany(mappedBy = "user")    | A user can place multiple orders.
 *
 * @OneToMany One User can have many Orders (1-to-many relationship).
 * mappedBy = "user"	The Order entity has a field called user. That field owns the relationship. (i.e., in Order.java you have @ManyToOne User user;)
 * cascade = CascadeType.ALL	Whenever you save, update, delete a User, automatically the related Orders will also be saved, updated, or deleted.
 * orphanRemoval = true	If you remove an Order from user.getOrders() list and then save the User, Hibernate will delete that Order from database automatically.
 * fetch = FetchType.LAZY	Hibernate will NOT load Orders immediately with User. It will load Orders only when needed (on demand).
 *
 * In a bidirectional relationship (like User → Orders), you define the relationship on both sides (in both models: User and Order).
 * mappedBy tells Hibernate which side "owns" the relationship.
 * The side that "owns" the relationship will have the foreign key.
 *
 * You say that User has many Orders, and you don't own the relationship.
 * You’re referring to the user field in the Order model, which actually holds the foreign key to User.
 * The mappedBy = "user" part tells Hibernate that the user field in Order is what defines the relationship from Order’s perspective.
 * It does not create a foreign key here; it just says, "I’m the inverse side of the relationship, the foreign key is in Order."
 * Now, in the Order model, the User object is actually the owning side of the relationship, because the Order model holds the foreign key to the User.
 * @ManyToOne means each Order belongs to one User.
 * @JoinColumn(name = "user_id") defines the foreign key column in the Order table (which links to the User table).
 *
 * cascade = CascadeType.ALL: Changes on User (like delete) cascade to child entities.
 * orphanRemoval = true: If a child (e.g., product) is removed from the list, it’s also deleted from the DB.
 * fetch = FetchType.LAZY: The related entities are loaded only when needed, not immediately.
 *
 */