package vn.ifine.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.ifine.model.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>,
    JpaSpecificationExecutor<Permission> {

  boolean existsByModuleAndApiPathAndMethod(String module, String apiPath, String method);

  List<Permission> findByIdIn(List<Long> id);

  Optional<Permission> findByApiPathAndMethod(String apiPath, String method);

  List<Permission> findByModule(String module);
}