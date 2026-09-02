package com.ailab.book.repository;

import com.ailab.book.domain.BookPublishedSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookPublishedSnapshotRepository extends JpaRepository<BookPublishedSnapshot, String> {

    Optional<BookPublishedSnapshot> findByBookIdAndVersion(String bookId, Long version);

    Optional<BookPublishedSnapshot> findTopByBookIdOrderByVersionDesc(String bookId);

    List<BookPublishedSnapshot> findByBookIdOrderByVersionDesc(String bookId);

    Optional<BookPublishedSnapshot> findByIdempotencyKey(String idempotencyKey);
}
