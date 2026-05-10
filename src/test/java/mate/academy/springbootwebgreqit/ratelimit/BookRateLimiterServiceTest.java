package mate.academy.springbootwebgreqit.ratelimit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BookRateLimiterServiceTest {
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    private BookRateLimiterService bookRateLimiterService;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        bookRateLimiterService = new BookRateLimiterService(stringRedisTemplate);
        ReflectionTestUtils.setField(bookRateLimiterService, "authenticatedPerMinute", 10);
        ReflectionTestUtils.setField(bookRateLimiterService, "anonymousPerMinute", 2);
        ReflectionTestUtils.setField(bookRateLimiterService, "windowSeconds", 60L);
    }

    @Test
    @DisplayName("Authenticated: count within limit returns ALLOWED (200 flow)")
    void checkAuthenticated_withinLimit_returnsAllowed() {
        when(valueOperations.increment(BookRateLimiterService.KEY_USER_PREFIX + "1")).thenReturn(5L);

        RateLimitResult result = bookRateLimiterService.checkAuthenticated(1L);

        assertEquals(RateLimitResult.ALLOWED, result);
        verify(stringRedisTemplate, never()).expire(any(String.class), any(Duration.class));
    }

    @Test
    @DisplayName("Authenticated: count over limit returns LIMITED (429 flow)")
    void checkAuthenticated_limitExceeded_returnsLimited() {
        when(valueOperations.increment(BookRateLimiterService.KEY_USER_PREFIX + "2")).thenReturn(11L);

        RateLimitResult result = bookRateLimiterService.checkAuthenticated(2L);

        assertEquals(RateLimitResult.LIMITED, result);
        verify(stringRedisTemplate, never()).expire(any(String.class), any(Duration.class));
    }

    @Test
    @DisplayName("Anonymous: count within limit returns ALLOWED (200 flow)")
    void checkAnonymous_withinLimit_returnsAllowed() {
        String ip = "192.168.1.1";
        when(valueOperations.increment(BookRateLimiterService.KEY_IP_PREFIX + ip)).thenReturn(1L);

        RateLimitResult result = bookRateLimiterService.checkAnonymous(ip);

        assertEquals(RateLimitResult.ALLOWED, result);
        verify(stringRedisTemplate).expire(eq(BookRateLimiterService.KEY_IP_PREFIX + ip),
                eq(Duration.ofSeconds(60L)));
    }

    @Test
    @DisplayName("Anonymous: count over limit returns LIMITED (429 flow)")
    void checkAnonymous_limitExceeded_returnsLimited() {
        String ip = "10.0.0.5";
        when(valueOperations.increment(BookRateLimiterService.KEY_IP_PREFIX + ip)).thenReturn(3L);

        RateLimitResult result = bookRateLimiterService.checkAnonymous(ip);

        assertEquals(RateLimitResult.LIMITED, result);
        verify(stringRedisTemplate, never()).expire(any(String.class), any(Duration.class));
    }

}
