package vn.ifine.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.ifine.model.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
  List<Rating> findByBookId(long bookId);

  Optional<Rating> findByBookIdAndUserId(long bookId, long userId);

  Optional<Rating> findByIdAndUserId(Long ratingId, Long userId);
}
