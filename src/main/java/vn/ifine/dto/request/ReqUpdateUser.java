package vn.ifine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.ifine.dto.validator.EnumPattern;
import vn.ifine.dto.validator.ValidImage;
import vn.ifine.util.GenderEnum;
import vn.ifine.util.UserStatus;

@Setter
@Getter
public class ReqUpdateUser {
  @NotBlank(message = "fullName must be not blank")
  private String fullName;

  @ValidImage(
      message = "Invalid image file",
      maxSize = 1024 * 1024 * 10, // 10MB
      allowedExtensions = {"jpg", "jpeg", "png", "gif"}
  )
  private MultipartFile image;

  @Pattern(regexp = "^\\d{10}$", message = "phone invalid format")
  private String phone;

  @EnumPattern(name = "gender", regexp = "(?i)MALE|FEMALE|OTHER")
  private GenderEnum gender;

  @NotNull(message = "dateOfBirth must be not null")
  private LocalDate userDOB;

  @NotNull(message = "address must be not null")
  private String address;

  @EnumPattern(name = "status", regexp = "(?i)ACTIVE|INACTIVE|NONE|DELETED")
  private UserStatus status;

  @NotNull(message = "role must be not null")
  private Integer roleId;
}
