package uz.shuhrat.lms.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import uz.shuhrat.lms.component.jwt.JwtAuthFilter;
import uz.shuhrat.lms.db.repository.admin.UserRepository;

import java.util.List;

@Configuration
@Slf4j
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class HttpSecurityConfig {
    @Value("${app.cors.allowed-origins[0]}")
    private String frontendURL;
    private final JwtAuthFilter authFilter;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .accessDeniedHandler((req, res, e) -> {
                    log.warn("ACCESS DENIED: {}", req.getRequestURI());
                    res.sendError(HttpServletResponse.SC_FORBIDDEN);
                });

        return http
                .cors()
                .and()
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/auth/logout",
                        "/auth/login",
                        "/auth/refresh",
                        "/password/reset/request",
                        "/password/reset/confirm",
                        "/actuator/health",
                        "/ws/**",
                        "/websocket/**",
                        "/sockjs-node/**",
                        "/info/**",              // Instead of "/info" + "/info/**"
                        "/xhr/**",               // Remove "/**/xhr"
                        "/xhr_streaming/**"  // Allow WebSocket connections
                ).permitAll()
                .requestMatchers("/user/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                .requestMatchers("/user-info").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                .requestMatchers("/download/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                .requestMatchers("/password/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                .requestMatchers("/conversation/**", "/conversation/*", "/conversation/*/message")
                .hasAnyRole("ADMIN", "TEACHER", "STUDENT") // Add message endpoints
                .requestMatchers("/messages/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT") // Add message endpoints
                .requestMatchers("/groups/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT") // Add message endpoints
                .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                .requestMatchers("/teacher/**").hasAnyRole("TEACHER")
                .requestMatchers("/student/**").hasAnyRole("STUDENT")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider(passwordEncoder()))
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username)));
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(frontendURL));
//        configuration.setAllowedOriginPatterns(List.of("https://*.ngrok-free.app")); // wildcard ngrok
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
