package vn.ifine.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.ifine.dto.request.ReqCategoryDTO;
import vn.ifine.dto.response.ResCategory;
import vn.ifine.exception.ResourceNotFoundException;
import vn.ifine.model.Category;
import vn.ifine.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CATEGORY-SERVICE-IMPL")
public class CategoryServiceImpl implements CategoryService{

  @Override
  public boolean isNameExist(String name) {
    return categoryRepository.existsByName(name);
  }

  private final CategoryRepository categoryRepository;

  @Override
  public ResCategory create(ReqCategoryDTO reqCategory) {
    log.info("Request create category, name={}", reqCategory.getName());
    Category category = Category.builder()
        .name(reqCategory.getName())
        .description(reqCategory.getDescription())
        .image(reqCategory.getImage())
        .build();

    categoryRepository.save(category);
    return this.convertToResCategory(category);
  }

  @Override
  public ResCategory update(int categoryId, ReqCategoryDTO reqCategory) {
    log.info("Request update category, name={}", reqCategory.getName());
    Category categoryDB = this.getById(categoryId);
    if(categoryDB != null){
      categoryDB.setName(reqCategory.getName());
      categoryDB.setDescription(reqCategory.getDescription());
      categoryDB.setImage(reqCategory.getImage());
    }
    categoryRepository.save(categoryDB);
    return this.convertToResCategory(categoryDB);
  }

  @Override
  public void remove(int categoryId) {
    Category category = this.getById(categoryId);
    if(category == null){
      throw new ResourceNotFoundException("Not found category with id = " + categoryId);
    }
    log.info("Request delete category, id={}", categoryId);
    categoryRepository.delete(category);
  }

  @Override
  public List<ResCategory> getAll() {
    List<Category> list = categoryRepository.findAll();
    List<ResCategory> res = list.stream().map(this::convertToResCategory).toList();
    return res;
  }

  @Override
  public Category getById(int id) {
    log.info("Request get category, id={}", id);
    Optional<Category> categoryOptional = categoryRepository.findById(id);
    if (!categoryOptional.isPresent()){
      throw new ResourceNotFoundException("Not found category with id = " + id);
    }
    return categoryOptional.get();
  }

  @Override
  public ResCategory convertToResCategory(Category category) {
    ResCategory res = ResCategory.builder()
        .id(category.getId())
        .name(category.getName())
        .description(category.getDescription())
        .image(category.getImage())
        .isActive(category.getIsActive())
        .createdAt(category.getCreatedAt())
        .updatedAt(category.getUpdatedAt())
        .createdBy(category.getCreatedBy())
        .updatedBy(category.getUpdatedBy())
        .build();
    return res;
  }
}
