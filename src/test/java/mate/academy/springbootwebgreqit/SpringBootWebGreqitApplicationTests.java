package mate.academy.springbootwebgreqit;

import mate.academy.springbootwebgreqit.ratelimit.BookRateLimiterService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class SpringBootWebGreqitApplicationTests {
    @MockBean
    private BookRateLimiterService bookRateLimiterService;

    @Test
    void contextLoads() {
    }
}
