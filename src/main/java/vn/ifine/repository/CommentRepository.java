package vn.ifine.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.ifine.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findByBookIdOrderByCreatedAtDesc(long bookId);

  Optional<Comment> findByIdAndUserId(Long commentId, Long userId);
}
