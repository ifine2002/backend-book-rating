package vn.ifine.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.ifine.dto.response.ResBook;
import vn.ifine.dto.response.ResFavoriteBook;
import vn.ifine.exception.ResourceNotFoundException;
import vn.ifine.model.Book;
import vn.ifine.model.FavoriteBook;
import vn.ifine.model.User;
import vn.ifine.repository.BookRepository;
import vn.ifine.repository.FavoriteBookRepository;
import vn.ifine.service.BookService;
import vn.ifine.service.FavoriteBookService;
import vn.ifine.service.UserService;

@Service
@Slf4j(topic = "FAVORITE-BOOK-SERVICE-IMPL")
@RequiredArgsConstructor
public class FavoriteBookServiceImpl implements FavoriteBookService {

  private final FavoriteBookRepository favoriteBookRepository;
  private final BookRepository bookRepository;
  private final UserService userService;
  private final BookService bookService;

  @Override
  public ResFavoriteBook createFavoriteBook(Long bookId, String email) {
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new ResourceNotFoundException("Not found book with bookId = " + bookId));
    User user = userService.getUserByEmail(email);

    FavoriteBook favoriteBook = FavoriteBook.builder()
        .book(book)
        .user(user)
        .build();
    favoriteBook = favoriteBookRepository.save(favoriteBook);
    ResFavoriteBook res = this.convertToRes(favoriteBook);
    return res;
  }

  @Override
  public void deleteFavoriteBook(Long bookId, String email) {
    User user = userService.getUserByEmail(email);
    FavoriteBook favoriteBook = favoriteBookRepository.findByBookIdAndUserId(bookId, user.getId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Not found favoriteBook with bookId=" + bookId + " and userId=" + user.getId()));
    favoriteBookRepository.delete(favoriteBook);
  }

  @Override
  public List<ResBook> getUserFavorite(String email) {
    User user = userService.getUserByEmail(email);
    List<FavoriteBook> list = favoriteBookRepository.findByUserId(user.getId());
    List<Long> listBookId = list.stream().map(x -> x.getBook().getId()).toList();
    List<Book> listBook = bookRepository.findByIdIn(listBookId);
    List<ResBook> res = listBook.stream().map(bookService::convertToResBook).toList();
    return res;
  }

  private ResFavoriteBook convertToRes(FavoriteBook favoriteBook) {
    ResFavoriteBook res = ResFavoriteBook.builder()
        .id(favoriteBook.getId())
        .userId(favoriteBook.getUser().getId())
        .bookId(favoriteBook.getBook().getId())
        .createdAt(favoriteBook.getCreatedAt())
        .createdBy(favoriteBook.getCreatedBy())
        .build();
    return res;
  }
}
