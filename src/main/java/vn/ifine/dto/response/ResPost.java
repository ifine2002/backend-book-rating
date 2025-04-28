package vn.ifine.dto.response;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.ifine.util.BookStatus;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResPost {
  private Long bookId;
  private String name;
  private String description;
  private String bookImage;
  private LocalDate publishedDate;
  private String bookFormat;
  private String bookSaleLink;
  private String language;
  private String author;
  private BookStatus status;
  private Set<ResCategoryInBook> categories;

  private ResFeedBack stars;

  private List<ResReviewDTO> reviews;

  private Long userId;
  private String fullName;
  private String avatar;
  private String createdBy;
  private String updatedBy;
  private Date createdAt;
  private Date updatedAt;

}
