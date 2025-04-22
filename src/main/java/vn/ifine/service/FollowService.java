package vn.ifine.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ifine.dto.response.ResFollowDTO;
import vn.ifine.dto.response.ResUserFollow;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.model.Follow;

@Service
public interface FollowService {

  ResFollowDTO create(Long followerId, Long followingId);

  ResFollowDTO followUser(String email, Long followingId);

  void delete(Long id);

  void unFollowForFollower(String email, Long followingId);

  void unFollowForFollowing(Long followerId, String email);
  //List user đang follow bạn
  List<ResUserFollow> getListFollower(String email);
  //List user bạn đang follow
  List<ResUserFollow> getListFollowing(String email);

  ResultPaginationDTO getPermissions(Specification<Follow> spec, Pageable pageable);
}
