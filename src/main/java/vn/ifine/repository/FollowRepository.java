package vn.ifine.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.ifine.model.Follow;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

  Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

  List<Follow> findByFollowingId(Long followingId);

  List<Follow> findByFollowerId(Long followerId);
}
