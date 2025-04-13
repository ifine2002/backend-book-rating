package vn.ifine.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ifine.dto.request.ReqCreateUser;
import vn.ifine.dto.request.ReqRegisterDTO;
import vn.ifine.dto.request.ReqUpdateUser;
import vn.ifine.dto.response.ResFollowDTO;
import vn.ifine.dto.response.ResUserFollow;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.dto.response.UserResponse;
import vn.ifine.model.Follow;
import vn.ifine.model.User;
import vn.ifine.util.UserStatus;

@Service
public interface UserService {

  User getById(long id);

  UserResponse createUser(ReqCreateUser request);

  UserResponse update(long id, ReqUpdateUser reqUser);

  void remove(long id);

  void changeStatus(long id, UserStatus status);

  UserResponse changeRole(long userId, int roleId);

  User getUserByEmail(String email);

  boolean isEmailExist(String email);

  UserResponse convertToUserResponse(User user);

  void updateUserToken(String token, String email);

  User getUserByRefreshAndEmail(String token, String email);

  ResultPaginationDTO getAll(Specification<User> spec, Pageable pageable);

  ResultPaginationDTO getAllActive(Specification<User> spec, Pageable pageable);

  ResFollowDTO followUser(String email, Long followingId);

  void unFollowForFollower(String email, Long followingId);

  void unFollowForFollowing(Long followerId, String email);
  //List user đang follow bạn
  List<ResUserFollow> getListFollower(String email);
  //List user bạn đang follow
  List<ResUserFollow> getListFollowing(String email);
}
