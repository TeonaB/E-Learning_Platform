package com.elearning.platform.config;

import com.elearning.platform.service.impl.JpaUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JpaUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll() // permit H2 console locally
                .requestMatchers("/web/auth/**", "/web/home", "/css/**", "/js/**", "/webjars/**", "/favicon.ico").permitAll()
                .requestMatchers("/web/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/web/auth/login")
                .loginProcessingUrl("/web/auth/login") // POST requests to this URL will be processed by Spring Security
                .usernameParameter("email")            // the login input name is "email"
                .passwordParameter("password")
                .defaultSuccessUrl("/web/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/web/auth/logout")
                .logoutSuccessUrl("/web/home")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("elearningSecretKey")
                .tokenValiditySeconds(86400) // 1 day
                .userDetailsService(userDetailsService)
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/web/auth/access-denied")
            );

        // Allow frames and ignore CSRF for the local H2 console
        http.csrf(csrf -> csrf
            .ignoringRequestMatchers("/h2-console/**")
        );
        http.headers(headers -> headers
            .frameOptions(frame -> frame.sameOrigin())
        );

        return http.build();
    }
}
