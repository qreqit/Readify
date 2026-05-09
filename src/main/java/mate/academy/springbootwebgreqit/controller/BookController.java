package mate.academy.springbootwebgreqit.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootwebgreqit.dto.BookDto;
import mate.academy.springbootwebgreqit.dto.BookSearchParameters;
import mate.academy.springbootwebgreqit.dto.CreateBookRequestDto;
import mate.academy.springbootwebgreqit.dto.UpdateBookRequestDto;
import mate.academy.springbootwebgreqit.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all books with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public Page<BookDto> getAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Get book by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/{id}")
    public BookDto getBookById(@Valid @PathVariable Long id) {
        return bookService.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Book created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public BookDto createBook(@Valid @RequestBody CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book updated successfully"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @PutMapping("/{id}")
    public BookDto updateBook(@PathVariable Long id,
                              @RequestBody @Valid UpdateBookRequestDto updateBookRequestDto) {
        return bookService.update(updateBookRequestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Search a book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    @GetMapping("/search")
    public Page<BookDto> searchBooks(BookSearchParameters searchParameters, Pageable pageable) {
        return bookService.search(searchParameters, pageable);
    }
}
