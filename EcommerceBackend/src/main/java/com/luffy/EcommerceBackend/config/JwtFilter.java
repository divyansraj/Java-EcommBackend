package com.luffy.EcommerceBackend.config;

import com.luffy.EcommerceBackend.security.jwt.JwtService;
import com.luffy.EcommerceBackend.security.service.MyUserDetailsService;
import com.luffy.EcommerceBackend.security.service.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MyUserDetailsService myUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token =null;
        String userName=null;

//        if the Authorization header exists and starts with "Bearer "
        if(authHeader !=null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
            userName = jwtService.extractUsername(token);
        }
//      A userName was extracted from the token and No authentication is currently set in the SecurityContextHolder (i.e., the user isn’t already authenticated).
        if(userName!=null && SecurityContextHolder.getContext().getAuthentication() ==null){

            //Calls MyUserDetailsService to load the UserDetails (as UserPrincipal) for the extracted userName.
            //the database (e.g., UserRepository) to fetch user details (e.g., username, password, roles).
            UserDetails userDetails = myUserDetailsService.loadUserByUsername(userName);

            //Validates the JWT using JwtService.isTokenValid.
            if(jwtService.isTokenValid(token, (UserPrincipal) userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,null,userDetails.getAuthorities()
                );

                //Attaches request details (e.g., IP address, session ID) to the authentication token for auditing or logging.
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                //Stores the authToken in the SecurityContextHolder, marking the user as authenticated for the request.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }
        //Passes the request and response to the next filter in the chain or to the target resource (e.g., ProductController) if no more filters exist.
        filterChain.doFilter(request,response);
    }
}
