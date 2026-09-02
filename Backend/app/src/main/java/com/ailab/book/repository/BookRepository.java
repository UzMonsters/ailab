package com.ailab.book.repository;

import com.ailab.book.domain.Book;
import com.ailab.book.domain.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, String> {

    Optional<Book> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("SELECT b FROM Book b WHERE " +
            "(:status IS NULL OR b.status = :status) AND " +
            "(:query IS NULL OR :query = '' OR LOWER(b.slug) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Book> findAllWithFilter(@Param("status") BookStatus status,
                                 @Param("query") String query,
                                 Pageable pageable);
}
