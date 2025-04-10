package vn.ifine.controller;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.ifine.dto.request.ReqBookDTO;
import vn.ifine.dto.response.ApiResponse;
import vn.ifine.dto.response.ResBook;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.model.Book;
import vn.ifine.service.BookService;

@RestController
@RequestMapping("/book")
@Slf4j(topic = "BOOK-CONTROLLER")
@Validated
@Tag(name = "Book Controller")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @PostMapping("/")
  public ResponseEntity<ApiResponse<ResBook>> create(
      @Valid @RequestBody ReqBookDTO reqBookDTO) {
    log.info("Request create book, {}", reqBookDTO.getName());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Create a book success",
            this.bookService.create(reqBookDTO)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ResBook>> getUserById(@PathVariable @Min(1) long id) {
    log.info("Request get book, id={}", id);
    Book book = bookService.getById(id);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Fetch a book success",
            bookService.convertToResBook(book)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<ResBook>> update(@PathVariable @Min(1) long id,
      @Valid @RequestBody ReqBookDTO reqBookDTO) {
    log.info("Request update book, id={}", id);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Update a book success",
            bookService.update(id, reqBookDTO)));
  }

  // Remove
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> remove(@PathVariable("id") @Min(1) long id) {
    log.info("Request remove book, id={}", id);
    bookService.remove(id);
    return ResponseEntity.ok().body(ApiResponse.success("Remove a book success", null));
  }

  @GetMapping("/list")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getAllBook(
      @Filter Specification<Book> spec,
      Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch all book success",
            this.bookService.getAll(spec, pageable)));
  }

  @GetMapping("/list-active")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getActiveBook(
      @Filter Specification<Book> spec,
      Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch all book active success",
            this.bookService.getAllActive(spec, pageable)));
  }

  @GetMapping("/list-book-user")
  public ResponseEntity<ApiResponse<?>> getAllBookOfUser(@RequestParam String email) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch all book active success",
            this.bookService.getAllBookOfUser(email)));
  }


}
