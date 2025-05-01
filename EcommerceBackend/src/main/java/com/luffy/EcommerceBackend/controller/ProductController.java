package com.luffy.EcommerceBackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luffy.EcommerceBackend.dto.ProductResponseDTO;
import com.luffy.EcommerceBackend.model.Product;
import com.luffy.EcommerceBackend.service.ProductService;
import com.luffy.EcommerceBackend.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductService services;
    @Autowired
    private ObjectMapper objectMapper;

    //Add a product
    @PostMapping("/addproduct")
    public ResponseEntity<?> addProduct(
            @Valid @RequestPart(value="product") String productRequest,//likely to make mistake
            @RequestParam("userId") Long userId,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ){
        try {
            //ObjectMapper objectMapper = new ObjectMapper();///likely to make mistake
            Product productMapper=objectMapper.readValue(productRequest,Product.class);
            Product addProduct = services.addProduct(productMapper, userId, files);
            return ResponseEntity.status(HttpStatus.OK).body(addProduct);
        }
        catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error uploading images" + e.getMessage());
        }
    }

    //Update a product
    @PutMapping("/updateproduct/{productId}")
    public ResponseEntity<?> updateProduct(
            @Valid @RequestPart(value="product") String productRequest,
            @PathVariable("productId") Long productId,
            @RequestParam("userId") Long userId,
            @RequestPart(value = "files") List<MultipartFile> files
    ){
        try{
            Product productMapper = objectMapper.readValue(productRequest, Product.class);

            Product updateProduct = services.updateAndSave(userId,productId,productMapper,files);
            return ResponseEntity.status(HttpStatus.OK).body(updateProduct);

        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.OK).body("Error updating product"+ e.getMessage());
        }
    }

    //Delete a Product
    @DeleteMapping("/deleteprouct/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable("productId") Long productId,@RequestParam Long userId) throws IOException {
        services.deleteProduct(productId,userId);
        return ResponseEntity.status(HttpStatus.OK).body("Product Deleted Successfully");
    }

    //search a product
    @GetMapping("/search")
    public ResponseEntity<?> searchProduct(@RequestParam String searchString){
        try{
            List<Product> searchProduct= services.searchProduct(searchString);
            return ResponseEntity.status(HttpStatus.OK).body(searchProduct);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error getting products"+ e.getMessage());
        }
    }

    //View all products
    @GetMapping("/getallproducts")
    public ResponseEntity<?> getAllProducts(){
        try{
            List<Product> showProducts = services.getAllProducts();
            return ResponseEntity.status(HttpStatus.OK).body(showProducts);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error getting Products" + e.getMessage());
        }
    }

    //View Single product
    @GetMapping("/getproduct/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable("productId") long productId){
        ProductResponseDTO singleProduct = services.getProductById(productId);
        return ResponseEntity.status(HttpStatus.OK).body(singleProduct);
    }
}
