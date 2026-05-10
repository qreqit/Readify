package mate.academy.springbootwebgreqit.ratelimit;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookRateLimiterService {
    static final String KEY_USER_PREFIX = "rate:books:user:";
    static final String KEY_IP_PREFIX = "rate:books:ip:";

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${rate-limit.books.authenticated-per-minute:10}")
    private int authenticatedPerMinute;

    @Value("${rate-limit.books.anonymous-per-minute:2}")
    private int anonymousPerMinute;

    @Value("${rate-limit.books.window-seconds:60}")
    private long windowSeconds;

    public RateLimitResult checkAuthenticated(Long userId) {
        String key = KEY_USER_PREFIX + userId;
        return incrementAndCheck(key, authenticatedPerMinute);
    }

    public RateLimitResult checkAnonymous(String clientIp) {
        String key = KEY_IP_PREFIX + clientIp;
        return incrementAndCheck(key, anonymousPerMinute);
    }

    private RateLimitResult incrementAndCheck(String key, int maxPerWindow) {
        Long count = stringRedisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            stringRedisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
        }
        if (count != null && count > maxPerWindow) {
            return RateLimitResult.LIMITED;
        }
        return RateLimitResult.ALLOWED;
    }
}
