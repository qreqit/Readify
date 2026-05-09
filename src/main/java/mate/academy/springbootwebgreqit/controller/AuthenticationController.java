package mate.academy.springbootwebgreqit.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootwebgreqit.dto.user.RefreshTokenRequestDto;
import mate.academy.springbootwebgreqit.dto.user.UserLoginRequestDto;
import mate.academy.springbootwebgreqit.dto.user.UserLoginResponseDto;
import mate.academy.springbootwebgreqit.dto.user.UserRegistrationRequestDto;
import mate.academy.springbootwebgreqit.dto.user.UserResponseDto;
import mate.academy.springbootwebgreqit.security.AuthenticationService;
import mate.academy.springbootwebgreqit.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth managemant", description = "Endpoint to auth")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/registration")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestBody) {
        return userService.register(requestBody);
    }

    @Operation(summary = "Authenticate a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto response) {
        return authenticationService.authenticate(response);
    }

    @Operation(summary = "Refresh access token using refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tokens issued successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    @PostMapping("/refresh")
    public UserLoginResponseDto refresh(@RequestBody @Valid RefreshTokenRequestDto request) {
        return authenticationService.refreshAccessToken(request);
    }
}
