package vn.ifine.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ifine.dto.request.ReqBookDTO;
import vn.ifine.dto.response.ResBook;
import vn.ifine.dto.response.ResCategoryInBook;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.exception.ResourceNotFoundException;
import vn.ifine.model.Book;
import vn.ifine.model.Category;
import vn.ifine.repository.BookRepository;
import vn.ifine.repository.CategoryRepository;
import vn.ifine.service.BookService;
import vn.ifine.specification.BookSpecification;
import vn.ifine.util.BookStatus;

@Service
@Slf4j(topic = "BOOK-SERVICE-IMPL")
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;
  private final CategoryRepository categoryRepository;

  //for admin
  @Override
  public ResBook create(ReqBookDTO reqBookDTO) {
    Book book = Book.builder()
        .name(reqBookDTO.getName())
        .description(reqBookDTO.getDescription())
        .image(reqBookDTO.getImage())
        .publishedDate(reqBookDTO.getPublishedDate())
        .bookFormat(reqBookDTO.getBookFormat())
        .bookSaleLink(reqBookDTO.getBookSaleLink())
        .language(reqBookDTO.getLanguage())
        .author(reqBookDTO.getAuthor())
        .status(reqBookDTO.getStatus())
        .build();

    if (reqBookDTO.getCategories() != null && !reqBookDTO.getCategories().isEmpty()) {
      Set<Integer> reqCategory = reqBookDTO.getCategories().stream().map(x -> x.getId()).collect(
          Collectors.toSet());
      Set<Category> categoriesDB = this.categoryRepository.findByIdIn(reqCategory);
      book.setCategories(categoriesDB);
    }
    bookRepository.save(book);
    log.info("Admin save book success, bookId={}", book.getId());
    return this.convertToResBook(book);
  }

  //for user
  @Override
  public ResBook uploadBook(ReqBookDTO reqBookDTO) {
    Book book = Book.builder()
        .name(reqBookDTO.getName())
        .description(reqBookDTO.getDescription())
        .image(reqBookDTO.getImage())
        .publishedDate(reqBookDTO.getPublishedDate())
        .bookFormat(reqBookDTO.getBookFormat())
        .bookSaleLink(reqBookDTO.getBookSaleLink())
        .language(reqBookDTO.getLanguage())
        .author(reqBookDTO.getAuthor())
        .status(BookStatus.NONE)
        .build();

    if (reqBookDTO.getCategories() != null && !reqBookDTO.getCategories().isEmpty()) {
      Set<Integer> reqCategory = reqBookDTO.getCategories().stream().map(x -> x.getId()).collect(
          Collectors.toSet());
      Set<Category> categoriesDB = this.categoryRepository.findByIdIn(reqCategory);
      book.setCategories(categoriesDB);
    }
    bookRepository.save(book);
    log.info("User save book success, bookId={}", book.getId());
    return this.convertToResBook(book);
  }

  //admin
  @Override
  public ResBook update(long bookId, ReqBookDTO reqBookDTO) {
    Book book = this.getById(bookId);
    book.setName(reqBookDTO.getName());
    book.setDescription(reqBookDTO.getDescription());
    book.setImage(reqBookDTO.getImage());
    book.setPublishedDate(reqBookDTO.getPublishedDate());
    book.setBookFormat(reqBookDTO.getBookFormat());
    book.setBookSaleLink(reqBookDTO.getBookSaleLink());
    book.setLanguage(reqBookDTO.getLanguage());
    book.setAuthor(reqBookDTO.getAuthor());
    book.setStatus(reqBookDTO.getStatus());

    if (reqBookDTO.getCategories() != null && !reqBookDTO.getCategories().isEmpty()) {
      Set<Integer> reqCategory = reqBookDTO.getCategories().stream().map(x -> x.getId()).collect(
          Collectors.toSet());
      Set<Category> categoriesDB = this.categoryRepository.findByIdIn(reqCategory);
      book.setCategories(categoriesDB);
    }
    bookRepository.save(book);
    log.info("Update book success, bookId={}", book.getId());
    return this.convertToResBook(book);
  }

  @Override
  public Book getById(long id) {
    log.info("Request get book by id, bookId=[}", id);
    Optional<Book> bookOptional = bookRepository.findById(id);
    if (!bookOptional.isPresent()) {
      throw new ResourceNotFoundException("Not found book with id = " + id);
    }
    return bookOptional.get();
  }

  @Override
  public void remove(long id) {
    //check comment
    Book book = this.getById(id);
    bookRepository.delete(book);
  }

  @Override
  public ResultPaginationDTO getAll(Specification<Book> spec, Pageable pageable) {
    Page<Book> pageBook = bookRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());
    rs.setTotalPages(pageBook.getTotalPages());
    rs.setTotalElements(pageBook.getTotalElements());
    // convert data
    List<ResBook> listBook = pageBook.getContent()
        .stream().map(this::convertToResBook)
        .toList();

    rs.setResult(listBook);

    return rs;
  }

  @Override
  public ResultPaginationDTO getAllActive(Specification<Book> spec, Pageable pageable) {
    // Kết hợp điều kiện isActive với các điều kiện khác
    Specification<Book> activeSpec = BookSpecification.withFilter(spec);

    Page<Book> pageBook = bookRepository.findAll(activeSpec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());
    rs.setTotalPages(pageBook.getTotalPages());
    rs.setTotalElements(pageBook.getTotalElements());
    // convert data
    List<ResBook> listBook = pageBook.getContent()
        .stream().map(this::convertToResBook)
        .toList();

    rs.setResult(listBook);
    return rs;
  }

  @Override
  public List<ResBook> getAllBookOfUser(String email) {
    List<Book> list = bookRepository.findByCreatedBy(email);
    List<ResBook> res = list.stream().map(this::convertToResBook).toList();
    return res;
  }

  @Override
  public ResBook convertToResBook(Book book) {
    Set<ResCategoryInBook> resCategories = book.getCategories().stream()
        .map(c -> {
          ResCategoryInBook x = new ResCategoryInBook();
          x.setId(c.getId());
          x.setName(c.getName());
          return x;
        })
        .collect(Collectors.toSet());

    return ResBook.builder()
        .id(book.getId())
        .name(book.getName())
        .description(book.getDescription())
        .image(book.getImage())
        .publishedDate(book.getPublishedDate())
        .bookFormat(book.getBookFormat())
        .bookSaleLink(book.getBookSaleLink())
        .language(book.getLanguage())
        .author(book.getAuthor())
        .status(book.getStatus())
        .categories(resCategories)
        .createdBy(book.getCreatedBy())
        .updatedBy(book.getUpdatedBy())
        .createdAt(book.getCreatedAt())
        .updatedAt(book.getUpdatedAt())
        .build();
  }
}
