package hexlet.project.components;

import hexlet.project.services.impl.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserServiceImpl userService;

    public JwtRequestFilter(JwtUtils jwtUtils, @Lazy UserServiceImpl userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№№А ТЕПЕРЬ ВЫВОДИМ");

        var header = request.getHeader("Authorization");

        System.out.println("DEBUG: Request URI: " + request.getRequestURI());
        System.out.println("DEBUG: Auth Header: " + header);

        if (header != null && header.startsWith("Bearer ")) {
            var token = header.substring(7);

            if (jwtUtils.validateToken(token)) {
                var email = jwtUtils.getEmailFromToken(token);
                var userDetails = userService.loadUserByUsername(email);
                var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                System.out.println("DEBUG: User: " + email);
                System.out.println("DEBUG: Authorities: " + userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                System.out.println("DEBUG: Token validation FAILED");
            }
        }

        filterChain.doFilter(request, response);
    }
}

