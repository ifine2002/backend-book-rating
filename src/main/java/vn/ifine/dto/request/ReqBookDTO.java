package vn.ifine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;
import vn.ifine.dto.validator.EnumPattern;
import vn.ifine.model.Category;
import vn.ifine.util.BookStatus;

@Getter
public class ReqBookDTO {

  @NotBlank(message = "name must be not blank")
  private String name;

  @NotBlank(message = "description must be not blank")
  private String description;

  private String image;

  @NotNull(message = "publishedDate must be not null")
  private LocalDate publishedDate;

  @NotBlank(message = "bookFormat must be not blank")
  private String bookFormat;

  @NotBlank(message = "bookSaleLink must be not blank")
  private String bookSaleLink;

  @NotBlank(message = "language must be not blank")
  private String language;

  @NotBlank(message = "author must be not blank")
  private String author;

  @EnumPattern(name = "status", regexp = "(?i)ACTIVE|INACTIVE|NONE|DELETED")
  private BookStatus status;

  private Set<Category> categories;

}
