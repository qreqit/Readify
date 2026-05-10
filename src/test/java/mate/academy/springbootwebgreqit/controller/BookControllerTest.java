package mate.academy.springbootwebgreqit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.springbootwebgreqit.ratelimit.BookRateLimiterService;
import mate.academy.springbootwebgreqit.dto.BookDto;
import mate.academy.springbootwebgreqit.dto.CreateBookRequestDto;
import mate.academy.springbootwebgreqit.dto.UpdateBookRequestDto;
import mate.academy.springbootwebgreqit.model.Book;
import mate.academy.springbootwebgreqit.model.Category;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    protected static MockMvc mockMvc;
    @MockBean
    private BookRateLimiterService bookRateLimiterService;
    @Autowired
    private ObjectMapper objectMapper;

    private BookDto bookDto;
    private UpdateBookRequestDto updateRequestDto;
    private Category category;
    private BookDto updatedBookDto;
    private CreateBookRequestDto requestDto;
    private Book book;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Roman");

        requestDto = new CreateBookRequestDto();
        requestDto.setTitle("wallet");
        requestDto.setAuthor("John");
        requestDto.setIsbn("9283234577892");
        requestDto.setPrice(BigDecimal.valueOf(60.99));
        requestDto.setDescription("Updated description");
        requestDto.setCoverImage("https://example.com/updated-cover-.jpg");
        requestDto.setCategoriesIds(Collections.singletonList(1L));

        book = new Book();
        book.setId(1L);
        book.setTitle("wallet");
        book.setAuthor("John");
        book.setIsbn("9283234577892");
        book.setPrice(BigDecimal.valueOf(60.99));
        book.setDescription("Updated description");
        book.setCoverImage("https://example.com/updated-cover-.jpg");
        book.setCategories(Collections.singleton(category));

        bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("wallet");
        bookDto.setAuthor("John");
        bookDto.setIsbn("9283234577892");
        bookDto.setPrice(BigDecimal.valueOf(60.99));
        bookDto.setDescription("Updated description");
        bookDto.setCoverImage("https://example.com/updated-cover-.jpg");
        bookDto.setCategoriesIds(List.of(1L));

        updateRequestDto = new UpdateBookRequestDto();
        updateRequestDto.setId(book.getId());
        updateRequestDto.setTitle("updated title");
        updateRequestDto.setAuthor("updated author");
        updateRequestDto.setIsbn("updated isbn");
        updateRequestDto.setPrice(BigDecimal.valueOf(70.99));
        updateRequestDto.setDescription("Updated description");
        updateRequestDto.setCoverImage("https://example.com/updated-cover.jpg");

        updatedBookDto = new BookDto();
        updatedBookDto.setId(book.getId());
        updatedBookDto.setTitle("updated title");
        updatedBookDto.setAuthor("updated author");
        updatedBookDto.setIsbn("updated isbn");
        updatedBookDto.setPrice(BigDecimal.valueOf(70.99));
        updatedBookDto.setDescription("Updated description");
        updatedBookDto.setCoverImage("https://example.com/updated-cover.jpg");
        updatedBookDto.setCategoriesIds(List.of(1L));
    }

    @Sql(scripts = "/data-sql/create-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/create-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/clear-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Given available books in the database, retrieve the next available books")
    void getAllBooks_ReturnsBooksFromDatabase() throws Exception {
        MvcResult result = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("wallet"))
                .andExpect(jsonPath("$.content[0].author").value("John"))
                .andReturn();
    }

    @Sql(scripts = "/data-sql/create-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/create-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/clear-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Get available book by id")
    void getBookById_ShouldReturnBookWithCurrentId() throws Exception{
        Long bookId = 1L;
        MvcResult result = mockMvc.perform(get("/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.title").value("wallet"))
                .andExpect(jsonPath("$.author").value("John"))
                .andReturn();
    }

    @Sql(scripts = "/data-sql/create-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/clear-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a book")
    void createBook_ShouldReturnCreatedBook() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("wallet"))
                .andExpect(jsonPath("$.author").value("John"))
                .andReturn();
    }

    @Sql(scripts = "/data-sql/create-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/create-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/clear-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete a book")
    void deleteBook_ShouldReturnisOk() throws Exception{
        Long bookId = 1L;

        mockMvc.perform(delete("/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Sql(scripts = "/data-sql/create-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/create-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/clear-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Search books with valid parameters")
    void searchBooks_ShouldReturnBooks() throws Exception{
        String searchParameters = "?title=wallet&author=John";

        MvcResult result = mockMvc.perform(get("/books/search" + searchParameters)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("wallet"))
                .andExpect(jsonPath("$.content[0].author").value("John"))
                .andReturn();
    }

    @Sql(scripts = "/data-sql/create-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/clear-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create book with invalid data")
    void createBook_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        CreateBookRequestDto invalidBookRequest = new CreateBookRequestDto();
        invalidBookRequest.setTitle("");
        invalidBookRequest.setAuthor("John");
        invalidBookRequest.setIsbn("9283234577892");
        invalidBookRequest.setPrice(BigDecimal.valueOf(100000));
        invalidBookRequest.setDescription(" description");
        invalidBookRequest.setCoverImage("https://example.com/updated-cover-.jpg");
        invalidBookRequest.setCategoriesIds(Collections.singletonList(1L));

        String jsonRequest = objectMapper.writeValueAsString(invalidBookRequest);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }
}
