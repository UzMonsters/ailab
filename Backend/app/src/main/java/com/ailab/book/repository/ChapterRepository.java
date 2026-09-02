package com.ailab.book.repository;

import com.ailab.book.domain.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChapterRepository extends JpaRepository<Chapter, String> {

    List<Chapter> findByBookIdOrderByPositionAsc(String bookId);

    Optional<Chapter> findByIdAndBookId(String id, String bookId);

    void deleteByBookId(String bookId);
}
