package mate.academy.springbootwebgreqit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.springbootwebgreqit.dto.BookDtoWithoutCategotyIds;
import mate.academy.springbootwebgreqit.dto.CreateBookRequestDto;
import mate.academy.springbootwebgreqit.dto.category.CategoryDto;
import mate.academy.springbootwebgreqit.dto.category.CreateCategoryRequestDto;
import mate.academy.springbootwebgreqit.dto.category.UpdateCategoryRequestDto;
import mate.academy.springbootwebgreqit.model.Book;
import mate.academy.springbootwebgreqit.model.Category;
import mate.academy.springbootwebgreqit.ratelimit.BookRateLimiterService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.math.BigDecimal;
import java.util.Collections;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {
    protected static MockMvc mockMvc;

    @MockBean
    private BookRateLimiterService bookRateLimiterService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateBookRequestDto requestDto;
    private Book book;
    private Category category;
    private CreateCategoryRequestDto createCategoryRequestDto;
    private CategoryDto categoryDto;
    private UpdateCategoryRequestDto updateCategoryRequestDto;
    private BookDtoWithoutCategotyIds withoutCategotyIds;

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

        createCategoryRequestDto = new CreateCategoryRequestDto();
        createCategoryRequestDto.setName("Roman");
        createCategoryRequestDto.setDescription("Some description");

        categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Roman");
        categoryDto.setDescription("Some description");

        updateCategoryRequestDto = new UpdateCategoryRequestDto();
        updateCategoryRequestDto.setName("Horror");
        updateCategoryRequestDto.setDescription("Updated description");

        book = new Book();
        book.setId(1L);
        book.setTitle("wallet");
        book.setAuthor("John");
        book.setIsbn("9283234577892");
        book.setPrice(BigDecimal.valueOf(60.99));
        book.setDescription("Updated description");
        book.setCoverImage("https://example.com/updated-cover-.jpg");
        book.setCategories(Collections.singleton(category));

        withoutCategotyIds = new BookDtoWithoutCategotyIds();
        withoutCategotyIds.setId(1L);
        withoutCategotyIds.setTitle("wallet");
        withoutCategotyIds.setAuthor("John");
        withoutCategotyIds.setIsbn("9283234577892");
        withoutCategotyIds.setPrice(BigDecimal.valueOf(60.99));
        withoutCategotyIds.setDescription("Updated description");
        withoutCategotyIds.setCoverImage("https://example.com/updated-cover-.jpg");
    }


    @Sql(scripts = "/data-sql/clear-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a category")
    void createCategory_ShouldReturnCreatedCategory() throws Exception{
        String jsonRequest = objectMapper.writeValueAsString(createCategoryRequestDto);

        MvcResult result = mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Roman"))
                .andExpect(jsonPath("$.description").value("Some description"))
                .andReturn();

    }

    @Sql(scripts = "/data-sql/create-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/clear-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Get all categories")
    void getAll_ShouldReturnAllCategories() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Roman"))
                .andExpect(jsonPath("$[0].description").value("Some description"))
                .andReturn();
    }

    @Sql(scripts = "/data-sql/create-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/clear-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Get category by ID")
    void getCategoryById_ShouldReturnCategory() throws Exception {
        Long categoryId = 1L;

        MvcResult result = mockMvc.perform(get("/categories/{id}", categoryId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Roman"))
                .andExpect(jsonPath("$.description").value("Some description"))
                .andReturn();
    }

    @Sql(scripts = "/data-sql/create-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/clear-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update a category")
    void updateCategory_ShouldReturnUpdatedCategory() throws Exception {
        Long categoryId = 1L;
        String jsonRequest = objectMapper.writeValueAsString(updateCategoryRequestDto);

        MvcResult result = mockMvc.perform(put("/categories/{id}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Horror"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andReturn();
    }

    @Sql(scripts = "/data-sql/create-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/clear-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete a category")
    void deleteCategory_ShouldReturnIsOk() throws Exception {
        Long categoryId = 1L;

        mockMvc.perform(delete("/categories/{id}", categoryId)
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
    @DisplayName("Get books by category ID")
    void getBooksByCategoryId_ShouldReturnBooks() throws Exception {
        Long categoryId = 1L;

        MvcResult result = mockMvc.perform(get("/categories/{id}/books", categoryId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("wallet"))
                .andExpect(jsonPath("$[0].author").value("John"))
                .andReturn();
    }

    @Sql(scripts = "/data-sql/clear-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Create category with invalid data")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCategory_ShouldReturnBadRequest_WhenDataIsInvalid() throws Exception {
        CreateCategoryRequestDto invalidDto = new CreateCategoryRequestDto();
        invalidDto.setDescription("Description without name");

        String jsonRequest = objectMapper.writeValueAsString(invalidDto);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Sql(scripts = "/data-sql/create-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/data-sql/clear-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update category with invalid data")
    void updateCategory_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        Long categoryId = 1L;

        UpdateCategoryRequestDto invalidRequest = new UpdateCategoryRequestDto();
        invalidRequest.setName("");
        invalidRequest.setDescription("Updated description");

        String jsonRequest = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(put("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }
}
