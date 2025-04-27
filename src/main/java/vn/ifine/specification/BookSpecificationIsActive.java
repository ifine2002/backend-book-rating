package vn.ifine.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.ifine.model.Book;
import vn.ifine.util.BookStatus;

public class BookSpecificationIsActive {
  public static Specification<Book> isActive() {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("status"), BookStatus.ACTIVE);
  }

  // Kết hợp nhiều điều kiện
  public static Specification<Book> withFilter(Specification<Book> spec) {
    return Specification.where(isActive()).and(spec);
  }
}
