package ch.bbw.m183.vulnerapp;

import java.util.List;

import ch.bbw.m183.vulnerapp.repository.UserRepository;
import ch.bbw.m183.vulnerapp.service.RestfulFormService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public UserDetailsService userDetailsService(
            UserRepository userRepository) {
        return username -> userRepository.findById(username)
                .map(entity -> new User(entity.getUsername(),
                        entity.getPassword(), List.of()))
                .orElseThrow(() -> new UsernameNotFoundException("sorry no user with this name"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http, RestfulFormService restfulFormService) {
        return http.formLogin(restfulFormService.restfulFormLogin())
                .exceptionHandling(restfulFormService.unauthorizedPerDefault())
                .csrf(CsrfConfigurer::spa)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/**")
                                .authenticated()
                                .anyRequest()
                                .permitAll())
                .build();
    }
}
