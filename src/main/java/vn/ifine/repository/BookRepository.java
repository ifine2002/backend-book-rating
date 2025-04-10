package vn.ifine.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.ifine.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

  List<Book> findByCreatedBy(String createdBy);
}
