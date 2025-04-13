package vn.ifine.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.ifine.dto.request.ReqCreateUser;
import vn.ifine.dto.request.ReqUpdateUser;
import vn.ifine.dto.response.ResFollowDTO;
import vn.ifine.dto.response.ResUserFollow;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.dto.response.UserResponse;
import vn.ifine.exception.CustomException;
import vn.ifine.exception.ResourceAlreadyExistsException;
import vn.ifine.exception.ResourceNotFoundException;
import vn.ifine.model.Follow;
import vn.ifine.model.Role;
import vn.ifine.model.User;
import vn.ifine.repository.FollowRepository;
import vn.ifine.repository.UserRepository;
import vn.ifine.service.RoleService;
import vn.ifine.service.UserService;
import vn.ifine.specification.UserSpecification;
import vn.ifine.util.UserStatus;

@Slf4j(topic = "USER-SERVICE-IMPL")
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final RoleService roleService;
  private final FollowRepository followRepository;

  @Override
  public User getById(long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id = " + id));
    return user;
  }

  @Override
  public UserResponse createUser(ReqCreateUser request) {
    // check email
    if (this.isEmailExist(request.getEmail())) {
      throw new ResourceAlreadyExistsException("Email = " + request.getEmail() + " already exist");
    }
    User user = User.builder()
        .fullName(request.getFullName())
        .email(request.getEmail())
        .image(request.getImage())
        .phone(request.getPhone())
        .gender(request.getGender())
        .userDOB(request.getUserDOB())
        .address(request.getAddress())
        .status(request.getStatus())
        .build();
    String hashPassword = this.passwordEncoder.encode(request.getPassword());
    user.setPassword(hashPassword);
    if (request.getRole() != null) {
      Role role = roleService.getById(request.getRole().getId());
      user.setRole(role);
    }
    user = userRepository.save(user);
    UserResponse resUser = this.convertToUserResponse(user);
    log.info("User has been created successfully, id={}", resUser.getId());
    return resUser;
  }

  @Override
  public UserResponse update(long id, ReqUpdateUser reqUser) {
    User user = this.getById(id);

    user.setFullName(reqUser.getFullName());
    user.setImage(reqUser.getImage());
    user.setPhone(reqUser.getPhone());
    user.setGender(reqUser.getGender());
    user.setUserDOB(reqUser.getUserDOB());
    user.setAddress(reqUser.getAddress());

    user = userRepository.save(user);
    log.info("User has been updated successfully, id={}", user.getId());
    return this.convertToUserResponse(user);
  }

  @Override
  public UserResponse changeRole(long userId, int roleId) {
    User user = this.getById(userId);
    Role role = roleService.getById(roleId);
    user.setRole(role);

    // save
    user = userRepository.save(user);
    log.info("User has been changed role successfully, id={}", user.getId());
    return this.convertToUserResponse(user);
  }

  @Override
  public void remove(long id) {
    User user = this.getById(id);
    this.userRepository.deleteById(id);
    log.info("User has been removed successfully, id={}", user.getId());
  }

  @Override
  public void changeStatus(long id, UserStatus status) {
    User user = this.getById(id);
    user.setStatus(status);
    // save
    user = userRepository.save(user);
    log.info("User has been changed status successfully, id={}", user.getId());
  }

  @Override
  public User getUserByEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("Bad credentials"));
    log.info("Get user by email successfully, id={}", user.getId());
    return user;
  }

  @Override
  public boolean isEmailExist(String email) {
    return userRepository.existsByEmail(email);
  }

  @Override
  public UserResponse convertToUserResponse(User user) {
    UserResponse resUser = UserResponse.builder()
        .id(user.getId())
        .fullName(user.getFullName())
        .email(user.getEmail())
        .image(user.getImage())
        .phone(user.getPhone())
        .gender(user.getGender())
        .userDOB(user.getUserDOB())
        .address(user.getAddress())
        .status(user.getStatus())
        .role(user.getRole())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .createBy(user.getCreatedBy())
        .updatedBy(user.getUpdatedBy())
        .build();
    return resUser;
  }

  @Override
  public void updateUserToken(String token, String email) {
    User currentUser = this.getUserByEmail(email);
    if (currentUser != null) {
      currentUser.setRefreshToken(token);
      this.userRepository.save(currentUser);
    }
    log.info("Updated token success, userId= {}", currentUser.getId());
  }

  @Override
  public User getUserByRefreshAndEmail(String token, String email) {
    return this.userRepository.findByRefreshTokenAndEmail(token, email);
  }

  @Override
  public ResultPaginationDTO getAll(Specification<User> spec, Pageable pageable) {
    Page<User> pageUser = userRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());
    rs.setTotalPages(pageUser.getTotalPages());
    rs.setTotalElements(pageUser.getTotalElements());
    // convert data
    List<UserResponse> listUser = pageUser.getContent()
        .stream().map(this::convertToUserResponse)
        .toList();

    rs.setResult(listUser);

    return rs;
  }

  @Override
  public ResultPaginationDTO getAllActive(Specification<User> spec, Pageable pageable) {
    // Kết hợp điều kiện isActive với các điều kiện khác
    Specification<User> activeSpec = UserSpecification.withFilter(spec);

    Page<User> pageUser = userRepository.findAll(activeSpec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());
    rs.setTotalPages(pageUser.getTotalPages());
    rs.setTotalElements(pageUser.getTotalElements());
    // convert data
    List<UserResponse> listUser = pageUser.getContent()
        .stream().map(this::convertToUserResponse)
        .toList();

    rs.setResult(listUser);
    return rs;
  }

  @Override
  public ResFollowDTO followUser(String email, Long followingId) {
    log.info("Request follow user, emailFollower={}, followingId={}", email, followingId);
    User follower = this.getUserByEmail(email);
    if(follower.getId() == followingId){
      throw  new CustomException("Can't follow myself");
    }
    User following = this.getById(followingId);
    Follow follow = Follow.builder()
        .follower(follower)
        .following(following)
        .build();
    follow = followRepository.save(follow);

    ResFollowDTO res = ResFollowDTO.builder()
        .id(follow.getId())
        .followerId(follow.getFollower().getId())
        .followingId(follow.getFollowing().getId())
        .createdAt(follow.getCreatedAt())
        .createBy(follow.getCreatedBy())
        .build();
    return res;
  }

  // unfollow cho người đi follow (A follow B) -> A hủy follow B
  @Override
  public void unFollowForFollower(String email, Long followingId) {
    log.info("Request unfollow form follower user, emailFollower={}, followingId={}", email, followingId);
    User follower = this.getUserByEmail(email);
    Follow follow = followRepository.findByFollowerIdAndFollowingId(follower.getId(), followingId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Not found follow with followerId=" + follower.getId() + " and followingId="
                + followingId));
    followRepository.delete(follow);
  }

  // unfollow cho người được follow (A follow B) -> B hủy follow của A
  @Override
  public void unFollowForFollowing(Long followerId, String email) {
    log.info("Request unfollow from following user, followerId={}, emailFollowing={}", followerId, email);
    User following = this.getUserByEmail(email);
    Follow follow = followRepository.findByFollowerIdAndFollowingId(followerId, following.getId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Not found follow with followerId=" + followerId + " and followingId="
                + following.getId()));
    followRepository.delete(follow);
  }
  //List user đang follow bạn
  @Override
  public List<ResUserFollow> getListFollower(String email) {
    User user = this.getUserByEmail(email);
    List<Follow> listFollows = followRepository.findByFollowingId(user.getId());
    List<User> follower = listFollows.stream().map(Follow::getFollower).toList();
    List<ResUserFollow> listRes = follower.stream().map(this::convertToResUserFollow).toList();
    return listRes;
  }

  //List user bạn đang follow
  @Override
  public List<ResUserFollow> getListFollowing(String email) {
    User user = this.getUserByEmail(email);
    List<Follow> listFollows = followRepository.findByFollowerId(user.getId());
    List<User> following = listFollows.stream().map(Follow::getFollowing).toList();
    List<ResUserFollow> listRes = following.stream().map(this::convertToResUserFollow).toList();
    return listRes;
  }

  private ResUserFollow convertToResUserFollow(User user){
    ResUserFollow res = ResUserFollow.builder()
        .id(user.getId())
        .fullName(user.getFullName())
        .email(user.getEmail())
        .image(user.getImage())
        .build();
    return res;
  }
}