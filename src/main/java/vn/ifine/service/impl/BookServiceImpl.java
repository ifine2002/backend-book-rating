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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ifine.dto.request.ReqBookDTO;
import vn.ifine.dto.request.ReviewRequestDto;
import vn.ifine.dto.response.ResBook;
import vn.ifine.dto.response.ResCategoryInBook;
import vn.ifine.dto.response.ResCommentDto;
import vn.ifine.dto.response.ResDetailBook;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.exception.ResourceNotFoundException;
import vn.ifine.model.Book;
import vn.ifine.model.Category;
import vn.ifine.model.Comment;
import vn.ifine.model.Rating;
import vn.ifine.model.User;
import vn.ifine.repository.BookRepository;
import vn.ifine.repository.CategoryRepository;
import vn.ifine.repository.CommentRepository;
import vn.ifine.repository.RatingRepository;
import vn.ifine.service.BookService;
import vn.ifine.service.FileService;
import vn.ifine.service.UserService;
import vn.ifine.specification.BookSpecification;
import vn.ifine.util.BookStatus;

@Service
@Slf4j(topic = "BOOK-SERVICE-IMPL")
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

  private final UserService userService;
  private final BookRepository bookRepository;
  private final CategoryRepository categoryRepository;
  private final RatingRepository ratingRepository;
  private final CommentRepository commentRepository;
  private final SimpMessagingTemplate messagingTemplate;
  private final FileService fileService;

  //for admin
  @Override
  @Transactional
  public ResBook create(ReqBookDTO reqBookDTO) {
    Book book = Book.builder()
        .name(reqBookDTO.getName())
        .description(reqBookDTO.getDescription())
        .image(reqBookDTO.getImage() != null ? fileService.upload(reqBookDTO.getImage()) : null)
        .publishedDate(reqBookDTO.getPublishedDate())
        .bookFormat(reqBookDTO.getBookFormat())
        .bookSaleLink(reqBookDTO.getBookSaleLink())
        .language(reqBookDTO.getLanguage())
        .author(reqBookDTO.getAuthor())
        .status(reqBookDTO.getStatus())
        .build();

    if (reqBookDTO.getCategoryIds() != null && !reqBookDTO.getCategoryIds().isEmpty()) {
      Set<Category> categoriesDB = this.categoryRepository.findByIdIn(reqBookDTO.getCategoryIds());
      book.setCategories(categoriesDB);
    }
    bookRepository.save(book);
    log.info("Admin save book success, bookId={}", book.getId());
    return this.convertToResBook(book);
  }

  //for user
  @Transactional
  @Override
  public ResBook uploadBook(ReqBookDTO reqBookDTO) {
    Book book = Book.builder()
        .name(reqBookDTO.getName())
        .description(reqBookDTO.getDescription())
        .image(reqBookDTO.getImage() != null ? fileService.upload(reqBookDTO.getImage()) : null)
        .publishedDate(reqBookDTO.getPublishedDate())
        .bookFormat(reqBookDTO.getBookFormat())
        .bookSaleLink(reqBookDTO.getBookSaleLink())
        .language(reqBookDTO.getLanguage())
        .author(reqBookDTO.getAuthor())
        .status(BookStatus.NONE)
        .build();

    if (reqBookDTO.getCategoryIds() != null && !reqBookDTO.getCategoryIds().isEmpty()) {
      Set<Category> categoriesDB = this.categoryRepository.findByIdIn(reqBookDTO.getCategoryIds());
      book.setCategories(categoriesDB);
    }
    bookRepository.save(book);
    log.info("User upload book success, bookId={}", book.getId());
    return this.convertToResBook(book);
  }

  //admin
  @Transactional
  @Override
  public ResBook update(long bookId, ReqBookDTO reqBookDTO) {
    Book book = this.getById(bookId);
    book.setName(reqBookDTO.getName());
    book.setDescription(reqBookDTO.getDescription());

    if(reqBookDTO.getImage() != null){
      book.setImage(fileService.upload(reqBookDTO.getImage()));
    }
    if(reqBookDTO.isDeleteImage()){
      book.setImage(null);
    }

    book.setPublishedDate(reqBookDTO.getPublishedDate());
    book.setBookFormat(reqBookDTO.getBookFormat());
    book.setBookSaleLink(reqBookDTO.getBookSaleLink());
    book.setLanguage(reqBookDTO.getLanguage());
    book.setAuthor(reqBookDTO.getAuthor());
    book.setStatus(reqBookDTO.getStatus());

    if (reqBookDTO.getCategoryIds() != null && !reqBookDTO.getCategoryIds().isEmpty()) {

      Set<Category> categoriesDB = this.categoryRepository.findByIdIn(reqBookDTO.getCategoryIds());
      book.setCategories(categoriesDB);
    }
    bookRepository.save(book);
    log.info("Update book success, bookId={}", book.getId());
    return this.convertToResBook(book);
  }

  @Override
  public Book getById(long id) {
    log.info("Request get book by id, bookId={}", id);
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

  @Override
  public ResDetailBook getBookDetail(long id) {
    Book book = this.getById(id);
    List<Rating> ratings = ratingRepository.findByBookId(id);
    double avgRating = ratings.stream()
        .mapToLong(Rating::getStars)
        .average()
        .orElse(0.0);
    List<Comment> comments = commentRepository.findByBookIdOrderByCreatedAtDesc(id);
    List<ResCommentDto> commentDtos = comments.stream()
        .map(c -> ResCommentDto.builder()
            .id(c.getId())
            .email(c.getUser().getEmail())
            .comment(c.getComment())
            .createdAt(c.getCreatedAt())
            .updatedAt(c.getUpdatedAt())
            .build())
        .toList();
    Set<ResCategoryInBook> resCategories = book.getCategories().stream()
        .map(c -> {
          ResCategoryInBook x = new ResCategoryInBook();
          x.setId(c.getId());
          x.setName(c.getName());
          return x;
        })
        .collect(Collectors.toSet());
    return ResDetailBook.builder()
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
        .averageRating(avgRating)
        .ratingCount(ratings.size())
        .comments(commentDtos)
        .categories(resCategories)
        .createdBy(book.getCreatedBy())
        .updatedBy(book.getUpdatedBy())
        .createdAt(book.getCreatedAt())
        .updatedAt(book.getUpdatedAt())
        .build();
  }

  @Override
  @Transactional
  public void submitReview(long bookId, ReviewRequestDto request, String email) {
    log.info("Request create review commentId, bookId={}, emailUser={}", bookId, email);
    User user = userService.getUserByEmail(email);
    Book book = this.getById(bookId);

    boolean hasRating = request.getStars() != null;
    boolean hasComment = request.getComment() != null && !request.getComment().isBlank();

    if (!hasRating && !hasComment) {
      throw new IllegalArgumentException("Must have at least one star rating or comment");
    }

    if (hasRating) {
      Rating rating = ratingRepository.findByBookIdAndUserId(bookId, user.getId())
          .orElse(new Rating());

      rating.setStars(request.getStars());
      rating.setBook(book);
      rating.setUser(user);

      ratingRepository.save(rating);
    }

    if (hasComment) {
      Comment comment = new Comment();
      comment.setBook(book);
      comment.setUser(user);
      comment.setComment(request.getComment());
      comment.setRatingComment(hasRating);

      comment = commentRepository.save(comment);

      // 👉 Gửi WebSocket comment sau khi lưu
      ResCommentDto commentDTO = new ResCommentDto();
      commentDTO.setId(comment.getId());
      commentDTO.setEmail(user.getEmail()); // hoặc user.getEmail()
      commentDTO.setComment(comment.getComment());
      commentDTO.setCreatedAt(comment.getCreatedAt());
      commentDTO.setUpdatedAt(comment.getUpdatedAt());

      messagingTemplate.convertAndSend("/topic/comments/" + bookId, commentDTO);
    }
  }

  @Override
  @Transactional
  public void updateReview(Long commentId, Long ratingId, ReviewRequestDto request, String email) {
    log.info("Request update review commentId, commentId={}, ratingId={}", commentId, ratingId);
    User user = userService.getUserByEmail(email);

    if (request.getStars() == null && (request.getComment() == null || request.getComment()
        .isBlank())) {
      throw new IllegalArgumentException("Must have at least one star rating or comment");
    }

    if (request.getStars() != null) {
      Rating rating = ratingRepository.findByIdAndUserId(ratingId, user.getId()).orElseThrow(() ->
          new ResourceNotFoundException(
              "Not found rating with ratingId=" + ratingId + " and userId=" + user.getId()));

      rating.setStars(request.getStars());
      ratingRepository.save(rating);
    }
    if (request.getComment() != null && !request.getComment().isBlank()) {
      Comment comment = commentRepository.findByIdAndUserId(commentId, user.getId()).orElseThrow(
          () -> new ResourceNotFoundException(
              "Not found comment with commentId = " + commentId + " and userId=" + user.getId()));
      comment.setComment(request.getComment());
      commentRepository.save(comment);
    }
  }

  @Override
  @Transactional
  public void deleteReview(Long commentId, Long ratingId, String email) {
    log.info("Request delete review commentId, commentId={}, ratingId={}", commentId, ratingId);
    User user = userService.getUserByEmail(email);

    Rating rating = ratingRepository.findByIdAndUserId(ratingId, user.getId()).orElseThrow(() ->
        new ResourceNotFoundException(
            "Not found rating with ratingId=" + ratingId + " and userId=" + user.getId()));
    ratingRepository.delete(rating);
    if (commentId != null) {
      Comment comment = commentRepository.findByIdAndUserId(commentId, user.getId()).orElseThrow(
          () -> new ResourceNotFoundException(
              "Not found comment with commentId = " + commentId + " and userId=" + user.getId()));
      commentRepository.delete(comment);
    }
  }

  @Override
  public void deleteComment(Long commentId, String email) {
    log.info("Request delete only comment, commentId={}", commentId);
    User user = userService.getUserByEmail(email);
    Comment comment = commentRepository.findByIdAndUserId(commentId, user.getId()).orElseThrow(
        () -> new ResourceNotFoundException(
            "Not found comment with commentId = " + commentId + " and userId=" + user.getId()));
    commentRepository.delete(comment);
  }
}
