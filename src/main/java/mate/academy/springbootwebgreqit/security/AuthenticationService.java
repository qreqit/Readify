package mate.academy.springbootwebgreqit.security;

import lombok.RequiredArgsConstructor;
import mate.academy.springbootwebgreqit.dto.user.RefreshTokenRequestDto;
import mate.academy.springbootwebgreqit.dto.user.UserLoginRequestDto;
import mate.academy.springbootwebgreqit.dto.user.UserLoginResponseDto;
import mate.academy.springbootwebgreqit.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Transactional
    public UserLoginResponseDto authenticate(UserLoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String email = authentication.getName();
        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);
        return new UserLoginResponseDto(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public UserLoginResponseDto refreshAccessToken(RefreshTokenRequestDto requestDto) {
        String refreshToken = requestDto.getRefreshToken();
        if (!jwtUtil.isRefreshTokenValid(refreshToken)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }
        String email = jwtUtil.getUsername(refreshToken);
        userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User no longer exists"));
        return new UserLoginResponseDto(
                jwtUtil.generateAccessToken(email),
                jwtUtil.generateRefreshToken(email)
        );
    }
}
