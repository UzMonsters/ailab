package com.ailab.book.repository;

import com.ailab.book.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PageRepository extends JpaRepository<Page, String> {

    List<Page> findByChapterIdOrderByPositionAsc(String chapterId);

    List<Page> findByBookIdOrderByPositionAsc(String bookId);

    Optional<Page> findByIdAndBookId(String id, String bookId);

    Optional<Page> findByIdAndChapterId(String id, String chapterId);

    Optional<Page> findByBookIdAndSlug(String bookId, String slug);

    long countByChapterId(String chapterId);

    void deleteByBookId(String bookId);

    void deleteByChapterId(String chapterId);
}
