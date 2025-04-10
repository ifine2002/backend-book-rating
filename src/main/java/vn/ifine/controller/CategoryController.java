package vn.ifine.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.ifine.dto.request.ReqCategoryDTO;
import vn.ifine.dto.response.ApiResponse;
import vn.ifine.dto.response.ResCategory;
import vn.ifine.exception.ResourceAlreadyExistsException;
import vn.ifine.exception.ResourceNotFoundException;
import vn.ifine.model.Category;
import vn.ifine.service.CategoryService;

@RestController
@RequestMapping("/category")
@Slf4j(topic = "CATEGORY-CONTROLLER")
@Validated
@Tag(name = "Category Controller")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @PostMapping("/")
  public ResponseEntity<ApiResponse<ResCategory>> create(@Valid @RequestBody ReqCategoryDTO reqCategoryDTO)
  {
    boolean isNameExist = this.categoryService.isNameExist(reqCategoryDTO.getName());
    if (isNameExist) {
      throw new ResourceAlreadyExistsException(
          "Category with name = " + reqCategoryDTO.getName() + " already exist");
    }
    ResCategory category = this.categoryService.create(reqCategoryDTO);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Create a category user successfully", category));
  }

  @GetMapping("/list")
  public ResponseEntity<ApiResponse<List<ResCategory>>> getAllCategory() {
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Fetch all category successfully",
        this.categoryService.getAll()));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<ResCategory>> update(@PathVariable @Min(1) int id, @Valid @RequestBody ReqCategoryDTO reqCategoryDTO) {
    return ResponseEntity.ok().body(ApiResponse.success("Update a category successfully", this.categoryService.update(id, reqCategoryDTO)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") @Min(1) int id) {
    this.categoryService.remove(id);
    return ResponseEntity.ok().body(ApiResponse.success("Delete a category successfully", null));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ResCategory>> getCategoryById(@PathVariable("id") @Min(1) int id)
       {
    Category category = this.categoryService.getById(id);
    return ResponseEntity.ok().body(ApiResponse.success("Fetch a company successfully", categoryService.convertToResCategory(category)));
  }
}
