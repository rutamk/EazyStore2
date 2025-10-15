package com.eazybytes.eazystore.controller;

import com.eazybytes.eazystore.dto.LoginRequestDto;
import com.eazybytes.eazystore.dto.LoginResponseDto;
import com.eazybytes.eazystore.dto.RegisterRequestDto;
import com.eazybytes.eazystore.dto.UserDto;
import com.eazybytes.eazystore.entity.Customer;
import com.eazybytes.eazystore.repository.CustomerRepository;
import com.eazybytes.eazystore.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
//    private final InMemoryUserDetailsManager inMemoryUserDetailsManager;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> apiLogin(@RequestBody
                                                         LoginRequestDto loginRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.username(),
                            loginRequestDto.password())
            );
            var userDto = new UserDto();
            var loggedInUser = (User) authentication.getPrincipal();
            userDto.setName(loggedInUser.getUsername());
            String jwtToken = jwtUtil.generateJwtToken(authentication);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new LoginResponseDto(HttpStatus.OK.getReasonPhrase(), null, jwtToken));
        } catch (BadCredentialsException ex) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED,
                    "Invalid username or password");
        } catch (AuthenticationException ex) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED,
                    "Authentication failed");
        } catch (Exception ex) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        Optional<Customer> existingCustomer = customerRepository.findByEmailOrMobileNumber(
                registerRequestDto.getEmail(), registerRequestDto.getMobileNumber()
        );
        if (existingCustomer.isPresent()) {
            Map<String, String> errors = new HashMap<>();
            Customer customer = existingCustomer.get();

            if (customer.getEmail().equalsIgnoreCase(registerRequestDto.getEmail())) {
                errors.put("email", "Email is already registered");
            }
            if (customer.getMobileNumber().equals(registerRequestDto.getMobileNumber())) {
                errors.put("mobileNumber", "Mobile number is already registered");
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        Customer customer = new Customer();
        BeanUtils.copyProperties(registerRequestDto, customer);
        customer.setPasswordHash(passwordEncoder.encode(registerRequestDto.getPassword()));
        customerRepository.save(customer);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("REGISTERED SUCCESSFULLY");
    }

    // for in-memory database user creation and storage
//    @PostMapping("/register")
//    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
//        inMemoryUserDetailsManager
//                .createUser(new User(
//                        registerRequestDto.getEmail(),
//                        passwordEncoder.encode(registerRequestDto.getPassword()),
//                        List.of(new SimpleGrantedAuthority("USER"))));
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body("REGISTERED SUCCESSFULLY");
//    }

    private ResponseEntity<LoginResponseDto> buildErrorResponse(HttpStatus status,
                                                                String message) {
        return ResponseEntity
                .status(status)
                .body(new LoginResponseDto(message, null, null));
    }
}
