package mate.academy.springbootwebgreqit.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootwebgreqit.model.User;
import mate.academy.springbootwebgreqit.ratelimit.BookRateLimiterService;
import mate.academy.springbootwebgreqit.ratelimit.RateLimitResult;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class BookRateLimitFilter extends OncePerRequestFilter {
    private final BookRateLimiterService bookRateLimiterService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!request.getServletPath().startsWith("/books")) {
            filterChain.doFilter(request, response);
            return;
        }

        RateLimitResult result;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticatedUser(auth)) {
            User user = (User) auth.getPrincipal();
            result = bookRateLimiterService.checkAuthenticated(user.getId());
        } else {
            result = bookRateLimiterService.checkAnonymous(resolveClientIp(request));
        }

        if (result == RateLimitResult.LIMITED) {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value());
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isAuthenticatedUser(Authentication auth) {
        return auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)
                && auth.getPrincipal() instanceof User;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].strip();
        }
        return request.getRemoteAddr();
    }
}
