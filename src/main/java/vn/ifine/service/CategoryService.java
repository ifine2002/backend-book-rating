package vn.ifine.service;

import java.util.List;
import org.springframework.stereotype.Service;
import vn.ifine.dto.request.ReqCategoryDTO;
import vn.ifine.dto.response.ResCategory;
import vn.ifine.model.Category;

@Service
public interface CategoryService {

  ResCategory create(ReqCategoryDTO reqCategory);

  ResCategory update(int categoryId, ReqCategoryDTO reqCategory);

  void remove(int categoryId);

  List<ResCategory> getAll();

  Category getById(int id);

  boolean isNameExist(String name);

  ResCategory convertToResCategory(Category category);
}
