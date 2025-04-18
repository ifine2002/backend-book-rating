package vn.ifine.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.ifine.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);

  User findByRefreshTokenAndEmail(String token, String email);

}
