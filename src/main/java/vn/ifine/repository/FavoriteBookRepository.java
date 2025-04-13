package vn.ifine.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.ifine.model.FavoriteBook;

public interface FavoriteBookRepository extends JpaRepository<FavoriteBook, Long> {

  List<FavoriteBook> findByUserId(Long userId);

  Optional<FavoriteBook> findByBookIdAndUserId(Long bookId, Long userId);
}
