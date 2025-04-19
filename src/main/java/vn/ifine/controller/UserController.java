package vn.ifine.controller;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.ifine.dto.request.ReqCreateUser;
import vn.ifine.dto.request.ReqChangeInfo;
import vn.ifine.dto.request.ReqUpdateUser;
import vn.ifine.dto.response.ApiResponse;
import vn.ifine.dto.response.ResFollowDTO;
import vn.ifine.dto.response.ResUserDetail;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.dto.response.UserResponse;
import vn.ifine.model.User;
import vn.ifine.service.UserService;
import vn.ifine.util.UserStatus;

@RestController
@RequestMapping("/user")
@Validated
@Slf4j(topic = "USER-CONTROLLER")
@Tag(name = "User Controller")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<UserResponse>> create(
      @Valid ReqCreateUser reqUser) {
    log.info("Request add user, {} {}", reqUser.getEmail(), reqUser.getFullName());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Create a user success",
            this.userService.createUser(reqUser)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable @Min(1) long id) {
    log.info("Request get user, id={}", id);
    // check exist by id
    User user = this.userService.getById(id);
    UserResponse resUser = userService.convertToUserResponse(user);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Fetch a user success",
            resUser));
  }

  @GetMapping("/detail/{id}")
  public ResponseEntity<ApiResponse<?>> getUserDetailById(@PathVariable @Min(1) Long id) {
    log.info("Request get user detail, id={}", id);
    // check exist by id
    ResUserDetail resUser = userService.getUserDetail(id);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Get user detail success",
            resUser));
  }

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable @Min(1) long id,
      @Valid ReqUpdateUser request) {
    log.info("Request update user, id={}", id);
    UserResponse user = userService.update(id, request);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Update a user success",
            user));
  }

  @PutMapping(value = "/change-info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<UserResponse>> changeInfoForUser(Principal principal,
      @Valid ReqChangeInfo request) {
    log.info("Request change info user, emailUser={}", principal.getName());
    UserResponse user = userService.changeInfo(principal.getName(), request);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Change info user success",
            user));
  }

  // Remove
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> remove(@PathVariable("id") @Min(1) long id) {
    log.info("Request remove user, id={}", id);
    // check exist by id
    User user = this.userService.getById(id);

    this.userService.remove(id);
    return ResponseEntity.ok().body(ApiResponse.success("Remove a user success", null));
  }

  // Delete soft
  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteSoft(@PathVariable @Min(1) long id) {
    log.info("Request delete-soft user, id={}", id);
    // check exist by id
    User user = this.userService.getById(id);

    this.userService.changeStatus(id, UserStatus.DELETED);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Delete-soft a user success", null));
  }

  // Change status: ACTIVE
  @PatchMapping("/changeIsActive/{id}")
  public ResponseEntity<ApiResponse<Void>> changeIsActive(@PathVariable @Min(1) long id) {
    log.info("Request change isActive user, id={}", id);
    // check exist by id
    User user = this.userService.getById(id);

    this.userService.changeStatus(id, UserStatus.ACTIVE);
    return ResponseEntity.ok()
        .body(ApiResponse.success("User is active success", null));
  }

  @GetMapping("/list")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getAllUser(
      @Filter Specification<User> spec,
      Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch all user success",
            this.userService.getAll(spec, pageable)));
  }

  @GetMapping("/active")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getActiveUser(
      @Filter Specification<User> spec,
      Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch all user active success",
            this.userService.getAllActive(spec, pageable)));
  }

  @PostMapping("/follow")
  public ResponseEntity<ApiResponse<ResFollowDTO>> followUser(
      @RequestParam @Min(1) Long followingId, Principal principal) {
    log.info("Request follow user, followingId={}, emailFollower={}", followingId,
        principal.getName());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Follow user success",
            this.userService.followUser(principal.getName(), followingId)));
  }

  // Lấy danh sách user đang follow bạn
  @GetMapping("/list-follower")
  public ResponseEntity<ApiResponse<?>> getListFollower(
      Principal principal) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Get list follower success",
            this.userService.getListFollower(principal.getName())));
  }

  // Lấy danh sách user bạn đang follow
  @GetMapping("/list-following")
  public ResponseEntity<ApiResponse<?>> getListFollowing(
      Principal principal) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Get list follower success",
            this.userService.getListFollowing(principal.getName())));
  }

  //Hủy follow từ follower (từ bên đi follow)
  @DeleteMapping("/unfollow-follower")
  public ResponseEntity<ApiResponse<Void>> unFollowFromFollower(Principal principal,
      @RequestParam @Min(1) Long followingId) {
    log.info("Request unfollow from follower, emailFollower={}, followingId={}",
        principal.getName(), followingId);
    this.userService.unFollowForFollower(principal.getName(), followingId);
    return ResponseEntity.ok().body(ApiResponse.success("Unfollow user from follower success", null));
  }

  //Hủy follow từ following (từ bên được follow)
  @DeleteMapping("/unfollow-following")
  public ResponseEntity<ApiResponse<Void>> unFollowFromFollowing(
      @RequestParam @Min(1) Long followerId, Principal principal) {
    log.info("Request unfollow from following, emailFollowing={}, followerId={}",
        principal.getName(), followerId);
    this.userService.unFollowForFollowing(followerId, principal.getName());
    return ResponseEntity.ok().body(ApiResponse.success("Unfollow user from following success", null));
  }

  // Update avatar for user
  @PatchMapping("/updateAvatar")
  public ResponseEntity<ApiResponse<UserResponse>> updateAvatar(@RequestParam("file") MultipartFile file,
      Principal principal) {
    log.info("Request update avatar user, emailUser={}", principal.getName());
    return ResponseEntity.ok()
        .body(ApiResponse.success("Update role for user success",
            this.userService.updateAvatar(file, principal.getName())));
  }


}