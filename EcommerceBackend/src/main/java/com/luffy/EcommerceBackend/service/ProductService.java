package com.luffy.EcommerceBackend.service;

import com.cloudinary.utils.ObjectUtils;
import com.luffy.EcommerceBackend.config.CloudinaryConfig;
import com.luffy.EcommerceBackend.dto.ProductResponseDTO;
import com.luffy.EcommerceBackend.model.Photo;
import com.luffy.EcommerceBackend.model.Product;
import com.luffy.EcommerceBackend.model.Review;
import com.luffy.EcommerceBackend.model.User;
import com.luffy.EcommerceBackend.repository.ProductRepository;
import com.luffy.EcommerceBackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CloudinaryConfig cloudinaryConfig;

@Transactional
    public Product addProduct(Product product, Long userId,List<MultipartFile> files) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("User not found"));
        product.setUser(user);

        if(files != null && !files.isEmpty()){
            List<Photo> photos = new ArrayList<>();
            for(MultipartFile file : files){
                if(!file.isEmpty()){

                    // Upload to cloudinary
                    Map uploadResult = cloudinaryConfig.cloudinary().uploader().upload(file.getBytes(),
                            ObjectUtils.asMap(
                                    "upload_preset", "ecommerce_unsigned",
                                    "folder","ecommerce/products"
                            ));
                    String secureUrl = (String) uploadResult.get("secure_url");

                    // Photo Model
                    Photo photo =new Photo();
                    photo.setProduct(product);
                    photo.setSecureUrl(secureUrl);
                    photos.add(photo);
                }
            }
            product.setPhotos(photos);
        }
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
    return productRepository.findAll();
    }

    public ProductResponseDTO getProductById(long productId) {
    Product product = productRepository.findById(productId).orElseThrow(()-> new IllegalArgumentException("Product not found"));
    ProductResponseDTO dto = new ProductResponseDTO();

    dto.setId(product.getId());
    dto.setName(product.getName());
    dto.setPrice(product.getPrice());
    dto.setDescription(product.getDescription());
    dto.setPhoto(product.getPhotos());
    dto.setCategory(String.valueOf(product.getCategory()));
    dto.setBrand(product.getBrand());
    dto.setRatings(product.getRatings());
    dto.setNumberOfReviews(product.getNumberOfReviews());
    dto.setReview(product.getReviews());
    dto.setCreatedAt(product.getCreatedAt());

    dto.setUserId(product.getUser().getId());
    dto.setUserName(product.getUser().getName());
    dto.setUserEmail(product.getUser().getEmail());

    return dto;

    }

    @Transactional
    public Product updateAndSave(Long userId, Long productId,Product requestProduct, List<MultipartFile> files) throws IOException {
        Product existingProduct = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not Found"));
        // Verify user ownership
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!existingProduct.getUser().getId().equals(userId)) throw new IllegalArgumentException("User not authorized to update this product");

        if(requestProduct.getName() != null) existingProduct.setName(requestProduct.getName());
        if(requestProduct.getPrice() != null) existingProduct.setPrice(requestProduct.getPrice());
        if(requestProduct.getDescription() != null) existingProduct.setDescription(requestProduct.getDescription());
        if(requestProduct.getCategory() != null) existingProduct.setCategory(requestProduct.getCategory());
        if(requestProduct.getBrand() != null) existingProduct.setBrand(requestProduct.getBrand());
//        if(requestProduct.getRatings() != null) existingProduct.setRatings(requestProduct.getRatings());
//        if(requestProduct.getNumberOfReviews() != null) existingProduct.setNumberOfReviews(requestProduct.getNumberOfReviews());

//        if(requestProduct.getReviews() != null && !requestProduct.getReviews().isEmpty()){
//            existingProduct.getReviews().clear();
//            for(Review review: requestProduct.getReviews()){
//                review.setProduct(existingProduct);
//                existingProduct.getReviews().add(review);
//            }
//        }

        if(files !=null && !files.isEmpty()){
            List<Photo> existingPhotos = new ArrayList<>(existingProduct.getPhotos());
            for(Photo photo: existingPhotos){
                String secureUrl = photo.getSecureUrl();
                String publicId = secureUrl.substring(secureUrl.lastIndexOf("/") + 1, secureUrl.lastIndexOf("."));
                publicId = "ecommerce/products/" + publicId;
                cloudinaryConfig.cloudinary().uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
            existingProduct.getPhotos().clear();

            //Adding new photos
            for(MultipartFile file : files){
                if(!file.isEmpty()){

                    // Upload to cloudinary
                    Map uploadResult = cloudinaryConfig.cloudinary().uploader().upload(file.getBytes(),
                            ObjectUtils.asMap(
                                    "upload_preset", "ecommerce_unsigned",
                                    "folder","ecommerce/products"
                            ));
                    String secureUrl = (String) uploadResult.get("secure_url");

                    // Photo Model
                    Photo photo =new Photo();
                    photo.setProduct(existingProduct);
                    photo.setSecureUrl(secureUrl);
                    existingProduct.getPhotos().add(photo);// add to existing collection
                }
            }
        }
        return productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteProduct(Long productId,Long userId) throws IOException {
    Product product = productRepository.findById(productId).orElseThrow(()->new IllegalArgumentException("User not Authorised or Product not found"));
    if(!Objects.equals(product.getUser().getId(), userId)){
        throw new IllegalArgumentException("User not authorised to perform the operation");
    }
    if(product.getPhotos() != null && !product.getPhotos().isEmpty()){
        List<Photo> photos = product.getPhotos();
        for(Photo photo: photos){
            String secureUrl = photo.getSecureUrl();
            String publicId = secureUrl.substring(secureUrl.lastIndexOf("/") + 1, secureUrl.lastIndexOf("."));
            publicId = "ecommerce/products/" + publicId;
            cloudinaryConfig.cloudinary().uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }
    productRepository.deleteById(productId);
    }

    public List<Product> searchProduct(String key) {
        return productRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrBrandIgnoreCaseContaining(key,key,key);
    }
}
