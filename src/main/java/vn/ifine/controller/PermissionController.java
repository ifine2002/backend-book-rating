package vn.ifine.controller;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.ifine.dto.request.PermissionRequestDTO;
import vn.ifine.dto.response.ApiResponse;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.exception.ResourceAlreadyExistsException;
import vn.ifine.exception.ResourceNotFoundException;
import vn.ifine.model.Permission;
import vn.ifine.service.PermissionService;

@RestController
@RequestMapping("/permission")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Permission Controller")
public class PermissionController {

  private final PermissionService permissionService;

  @PostMapping("/")
  public ResponseEntity<ApiResponse<Permission>> create(
      @Valid @RequestBody PermissionRequestDTO permission) {
    log.info("Request add permission, {} {}", permission.getName(), permission.getApiPath());
    // check exist permission
    boolean isExist = this.permissionService.existsByModuleAndApiPathAndMethod(permission);
    if (isExist) {
      throw new ResourceAlreadyExistsException("Permission already exists");
    }
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Create a permission success",
            this.permissionService.create(permission)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<Permission>> update(@PathVariable @Min(1) long id,
      @Valid @RequestBody PermissionRequestDTO permission) {
    log.info("Request update permission, permissionId={}", id);
    // check exist by id
    if (this.permissionService.getPermissionById(id) == null) {
      throw new ResourceNotFoundException("Permission with id = " + id + " not exists");
    }

    // check exist by module, apiPath and method
    if (this.permissionService.existsByModuleAndApiPathAndMethod(permission)) {
      // check name
      if (this.permissionService.isSameName(id, permission)) {
        throw new ResourceAlreadyExistsException("Permission already exists");
      }
    }
    return ResponseEntity.ok()
        .body(ApiResponse.success("Update a permission success",
            this.permissionService.update(id, permission)));
  }

  // Remove
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> remove(@PathVariable("id") long id) {
    log.info("Request remove permission, permissionId={}", id);
    // check exist by id
    if (this.permissionService.getPermissionById(id) == null) {
      throw new ResourceNotFoundException("Permission with id = " + id + " not exists");
    }
    this.permissionService.remove(id);
    return ResponseEntity.ok().body(ApiResponse.success("Remove a permission success", null));
  }

  // Delete soft
  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<Permission>> deleteSoft(@PathVariable @Min(1) long id) {
    log.info("Request delete-soft permission, permissionId={}", id);
    // check exist by id
    if (this.permissionService.getPermissionById(id) == null) {
      throw new ResourceNotFoundException("Permission with id = " + id + " not exists");
    }
    this.permissionService.deleteSoft(id);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Delete-soft a permission success", null));
  }

  @GetMapping("/list")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getPermissions(
      @Filter Specification<Permission> spec,
      Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch all permission success",
            this.permissionService.getPermissions(spec, pageable)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Permission>> getPermissionById(@PathVariable @Min(1) long id) {
    log.info("Request get permission, permissionId={}", id);
    // check exist by id
    if (this.permissionService.getPermissionById(id) == null) {
      throw new ResourceNotFoundException("Permission with id = " + id + " not exists");
    }
    return ResponseEntity.ok()
        .body(ApiResponse.success("Fetch a permission success",
            this.permissionService.getPermissionById(id)));
  }

  // Change isActive
  @PatchMapping("/change-active/{id}")
  public ResponseEntity<ApiResponse<Permission>> changeIsActive(@PathVariable @Min(1) long id) {
    log.info("Request change isActive permission, permissionId={}", id);
    // check exist by id
    if (this.permissionService.getPermissionById(id) == null) {
      throw new ResourceNotFoundException("Permission with id = " + id + " not exists");
    }
    this.permissionService.changeIsActive(id);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Change is active a permission success", null));
  }

  @GetMapping("/active")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getActivePermissions(
      @Filter Specification<Permission> spec,
      Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch all avtive permission success",
            this.permissionService.getActivePermissions(spec, pageable)));
  }
}