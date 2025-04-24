package vn.ifine.dto.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResCommentDTO {

  private Long id;

  private String fullName;

  private Long userId;

  private String image;

  private String comment;

  private Date createdAt;

  private Date updatedAt;
}
