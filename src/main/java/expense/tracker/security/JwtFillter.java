package expense.tracker.security;

import expense.tracker.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFillter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer")){
            final String token = authHeader.substring(7);

            if (jwtService.validateToken(token)){
                String userId = jwtService.extractUserId(token);
                String email = jwtService.extractEmail(token);

                request.setAttribute("userId", userId);
                request.setAttribute("email", email);
            }
        }
        filterChain.doFilter(request, response);
    }
}
