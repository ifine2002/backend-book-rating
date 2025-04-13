package vn.ifine.service;

import java.util.List;
import org.springframework.stereotype.Service;
import vn.ifine.dto.response.ResBook;
import vn.ifine.dto.response.ResFavoriteBook;
import vn.ifine.model.FavoriteBook;

@Service
public interface FavoriteBookService {

  ResFavoriteBook createFavoriteBook(Long bookId, String email);

  void deleteFavoriteBook(Long favoriteId, String email);

  List<ResBook> getUserFavorite(String email);
}
