package com.luffy.EcommerceBackend.service;

import com.cloudinary.utils.ObjectUtils;
import com.luffy.EcommerceBackend.config.CloudinaryConfig;
import com.luffy.EcommerceBackend.model.Photo;
import com.luffy.EcommerceBackend.model.Product;
import com.luffy.EcommerceBackend.model.User;
import com.luffy.EcommerceBackend.repository.ProductRepository;
import com.luffy.EcommerceBackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CloudinaryConfig cloudinaryConfig;
    private final PasswordEncoder passwordEncoder;



    @Transactional
    public User createUser(@Valid User user) throws IllegalAccessException {
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new IllegalAccessException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User validateLogin(String email, String plainPassword) throws IllegalAccessException {
        User user = userRepository.findByEmail(email).orElseThrow(()->new IllegalAccessException("Invalid email or password"));
        if(!passwordEncoder.matches(plainPassword, user.getPassword())){
            throw new IllegalAccessException("Invalid email or password");
        }
        return user;
    }

    @Transactional
    public void deleteUser(Long userId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("User not found"));
        List<Product> products = user.getProducts();
        for(Product product: products){
            List<Photo> photos = product.getPhotos();
            for(Photo photo: photos){
                String secureUrl= photo.getSecureUrl();
                String publicUrl= secureUrl.substring(secureUrl.lastIndexOf("/")+1,secureUrl.lastIndexOf("."));
                publicUrl="ecommerce/products/" + publicUrl;
                cloudinaryConfig.cloudinary().uploader().destroy(publicUrl, ObjectUtils.emptyMap());
            }

        }
        userRepository.deleteById(userId);
    }

    public User findByEmail(String email) throws Exception {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not find"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


}
