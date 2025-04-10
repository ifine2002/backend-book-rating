package vn.ifine.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReqCategoryDTO {
  @NotBlank(message = "name must be not blank")
  private String name;

  @NotBlank(message = "description must be not blank")
  private String description;

  private String image;
}
