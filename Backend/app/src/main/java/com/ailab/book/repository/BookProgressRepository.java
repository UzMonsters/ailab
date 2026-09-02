package com.ailab.book.repository;

import com.ailab.book.domain.BookProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookProgressRepository extends JpaRepository<BookProgress, String> {

    Optional<BookProgress> findByUserIdAndBookId(String userId, String bookId);
}
