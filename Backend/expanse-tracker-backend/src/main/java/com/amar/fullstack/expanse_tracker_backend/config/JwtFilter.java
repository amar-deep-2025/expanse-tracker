package com.amar.fullstack.expanse_tracker_backend.config;

import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {

        String header=req.getHeader("Authorization");
        String path=req.getServletPath();

        if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")) {
            chain.doFilter(req, res);
            return;
        }

        if (header!=null && header.startsWith("Bearer ")){
            String token =header.substring((7));

            try{
                String email=jwtUtil.extractEmail(token);
                Optional<User> user=userRepository.findByEmail(email);

                if (user.isPresent()){
                    User u=user.get();
                    UsernamePasswordAuthenticationToken auth=new UsernamePasswordAuthenticationToken(u,null, List.of(new SimpleGrantedAuthority("ROLE_"+u.getRole().name())));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }


            }catch (Exception e){
                System.out.println("Token not found");
                e.printStackTrace();
            }
        }
        chain.doFilter(req,res);
    }
}
