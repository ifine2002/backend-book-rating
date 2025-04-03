package vn.ifine.service.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ifine.dto.request.PermissionRequestDTO;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.model.Permission;
import vn.ifine.repository.PermissionRepository;
import vn.ifine.service.PermissionService;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

  private final PermissionRepository permissionRepository;

  @Override
  public boolean existsByModuleAndApiPathAndMethod(PermissionRequestDTO p) {
    return this.permissionRepository.existsByModuleAndApiPathAndMethod(p.getModule(), p.getApiPath(),
        p.getMethod());
  }

  @Override
  public Permission create(PermissionRequestDTO p) {
    Permission permission = Permission.builder()
        .name(p.getName())
        .apiPath(p.getApiPath())
        .method(p.getMethod())
        .module(p.getModule())
        .build();
    log.info("Permission has added successfully, permissionId={}", permission.getId());
    return permissionRepository.save(permission);
  }

  @Override
  public Permission getPermissionById(long id) {
    Optional<Permission> permissOptional = this.permissionRepository.findById(id);
    if (permissOptional.isPresent()) {
      return permissOptional.get();
    }
    return null;
  }

  @Override
  public Permission update(long permissionId, PermissionRequestDTO permission) {
    Permission permissionDB = this.getPermissionById(permissionId);
    if(permissionDB != null){
      permissionDB.setName(permission.getName());
      permissionDB.setApiPath(permission.getApiPath());
      permissionDB.setMethod(permission.getMethod());
      permissionDB.setModule(permission.getModule());

      // update
      permissionDB = this.permissionRepository.save(permissionDB);

      log.info("Permission has updated successfully, permissionId={}", permissionDB.getId());
      return permissionDB;
    }
    return null;
  }

  @Override
  public void deleteSoft(long id) {
    Permission permissionDB = this.getPermissionById(id);
    if(permissionDB != null){
      permissionDB.setIsActive(false);

      // update
      permissionDB = this.permissionRepository.save(permissionDB);

      log.info("Permission has delete-soft successfully, permissionId={}", permissionDB.getId());
    }
  }

  @Override
  public void remove(long id) {
    this.permissionRepository.deleteById(id);
    log.info("Permission has delete-soft successfully, permissionId={}", id);
  }

  @Override
  public ResultPaginationDTO getPermissions(Specification<Permission> spec, Pageable pageable) {
    Page<Permission> pagePermission = this.permissionRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());

    rs.setTotalPages(pagePermission.getTotalPages());
    rs.setTotalElements(pagePermission.getTotalElements());

    rs.setResult(pagePermission.getContent());

    return rs;
  }

  @Override
  public boolean isSameName(long permissionId, PermissionRequestDTO p) {
    Permission permissionDB = this.getPermissionById(permissionId);
    if (permissionDB != null) {
      if (permissionDB.getName().equals(p.getName()))
        return true;
    }
    return false;
  }

  @Override
  public void changeIsActive(long id) {
    Permission permissionDB = this.getPermissionById(id);
    if(permissionDB != null){
      permissionDB.setIsActive(true);

      // update
      permissionDB = this.permissionRepository.save(permissionDB);

      log.info("Permission has change isActive successfully, permissionId={}", permissionDB.getId());
    }
  }
}
