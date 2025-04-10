package vn.ifine.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ifine.dto.request.ReqBookDTO;
import vn.ifine.dto.response.ResBook;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.model.Book;

@Service
public interface BookService {
  //admin
  ResBook create(ReqBookDTO reqBookDTO);

  //user post
  ResBook uploadBook(ReqBookDTO reqBookDTO);

  ResBook update(long bookId, ReqBookDTO reqBookDTO);

  Book getById(long id);

  void remove(long id);

  ResultPaginationDTO getAll(Specification<Book> spec, Pageable pageable);

  ResultPaginationDTO getAllActive(Specification<Book> spec, Pageable pageable);

  List<ResBook> getAllBookOfUser(String email);

  ResBook convertToResBook(Book book);
}
