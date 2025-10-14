package com.eazybytes.eazystore.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class EazyStoreSecurityConfig {

    private final List<String> publicPaths;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrfConfig -> csrfConfig.disable())
                .cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()))// ✅ Enable CORS support
                .authorizeHttpRequests((requests) -> {
                    publicPaths.forEach(path -> requests.requestMatchers(path).permitAll());
                    requests.anyRequest().authenticated();
                })
                .formLogin(withDefaults())
                .httpBasic(withDefaults())
                .build();
    }
//    @Bean
//    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
//        return http.authorizeHttpRequests((requests) ->
//                 requests.requestMatchers("api/v1/products/**").permitAll()
//                        .requestMatchers("api/v1/dummy/**").authenticated()
//                        .anyRequest().authenticated())
//                .formLogin(withDefaults())
//                .httpBasic(withDefaults()).build();
//    }\

    @Bean
    public UserDetailsService userDetailsService(){
        var user1 = User.builder().username("rutam")
                .password("$2a$12$2zXXgsWspXoA5U7sBFEi1u/hictFQk2Bs5cujA7R716HC3xdZxhaS").roles("USER").build();
        var user2 = User.builder().username("admin")
                .password("$2a$12$5a/Vi.hF7eGTQlOZB7GaA.P5SZI6pGg6uRrjB22qb6nSA5fGNT8dq").roles("USER","ADMIN").build();

        return new InMemoryUserDetailsManager(Arrays.asList(user1,user2));
    }

//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
//        var user1 = User.builder().username("rutam")
//                .password(passwordEncoder.encode("eazystore")).roles("USER").build();
//        var user2 = User.builder().username("admin")
//                .password("admin").roles("USER","ADMIN").build();
//
//        return new InMemoryUserDetailsManager(Arrays.asList(user1,user2));
//    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService
    ,PasswordEncoder passwordEncoder){

        var daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        var providerManager = new ProviderManager(daoAuthenticationProvider);
        return providerManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
