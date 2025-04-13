package vn.ifine.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.ifine.dto.response.ApiResponse;
import vn.ifine.dto.response.ResFavoriteBook;
import vn.ifine.service.FavoriteBookService;

@RestController
@RequestMapping("/favorite-book")
@Slf4j(topic = "FAVORITE-BOOK-CONTROLLER")
@Validated
@Tag(name = "Favorite Book Controller")
@RequiredArgsConstructor
public class FavoriteBookController {

  private final FavoriteBookService favoriteBookService;

  @PostMapping("/")
  public ResponseEntity<ApiResponse<ResFavoriteBook>> addToFavorite(
      @RequestParam @Min(1) Long bookId, Principal principal) {
    log.info("Request add to favorite book, bookId={}", bookId);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Add book to favorite success",
            this.favoriteBookService.createFavoriteBook(bookId, principal.getName())));
  }

  @GetMapping("/list-of-user")
  public ResponseEntity<ApiResponse<?>> getFavoritesOfUser(Principal principal) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.success("Fetch favorites book of user success",
            this.favoriteBookService.getUserFavorite(principal.getName())));
  }

  @DeleteMapping("/")
  public ResponseEntity<ApiResponse<?>> deleteFavoriteBook(@RequestParam @Min(1) Long bookId,
      Principal principal) {
    log.info("Request delete book from favorite, bookId={}", bookId);
    favoriteBookService.deleteFavoriteBook(bookId, principal.getName());
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.success("Delete book from favorites book success",
           null));
  }
}
