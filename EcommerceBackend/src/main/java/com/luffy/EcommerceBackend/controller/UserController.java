package com.luffy.EcommerceBackend.controller;

import com.luffy.EcommerceBackend.dto.LoginResponseDTO;
import com.luffy.EcommerceBackend.model.User;
import com.luffy.EcommerceBackend.security.jwt.JwtService;
import com.luffy.EcommerceBackend.security.service.UserPrincipal;
import com.luffy.EcommerceBackend.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService services;
    @Autowired
    private JwtService jwtService;
    @GetMapping("/getuser")
    public ResponseEntity<?> getuser(@RequestBody String email){
        try{
            User user = services.findByEmail(email);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user){
        try{
            User createdUser = services.createUser(user);
            //UserResponseDTO responseDTO = new UserResponseDTO(createdUser.getEmail(), createdUser.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user){
        try{
//            User loginUser = services.validateLogin(user.getEmail(),user.getPassword());
//            UserPrincipal principal = new UserPrincipal(loginUser);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            String jwtToken = jwtService.generateToken(principal);



            LoginResponseDTO response=new LoginResponseDTO();
            response.setName(principal.getName());
            response.setEmail(principal.getUsername());
            response.setRole(principal.getRole());
            response.setJwt_token(jwtToken);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> delete(@PathVariable("userId") Long userId){
        try{
            services.deleteUser(userId);
            return ResponseEntity.status(HttpStatus.OK).body("User Deleted");
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @GetMapping("/allusers")
    public ResponseEntity<?> allusers(){
        try{
            List<User> users = services.getAllUsers();
            Map<String, Object> response = new HashMap<>();
            response.put("data",users);
            response.put("status","success");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/hello")
    public ResponseEntity<?> hello(){
        return ResponseEntity.status(HttpStatus.OK).body("hello");
    }
}
