package com.eazybytes.eazystore.dto;

public record LoginResponseDto(String message, UserDto userDto, String jwtToken) {
}
