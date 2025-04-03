package vn.ifine.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.ifine.model.Permission;

public class PermissionSpecification {

  public static Specification<Permission> isActive() {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("isActive"), true);
  }

  // Kết hợp nhiều điều kiện
  public static Specification<Permission> withFilter(Specification<Permission> spec) {
    return Specification.where(isActive()).and(spec);
  }
}