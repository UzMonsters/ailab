package com.ailab.book.service;

import com.ailab.book.domain.Book;
import com.ailab.book.dto.BookDtos;

import java.util.List;
import java.util.Map;

public interface BookValidationService {

    BookDtos.ValidationReport validateBook(String bookId, Long expectedVersion);

    List<String> findMissingLocales(Map<String, Object> translations, List<String> requiredFields);

    List<String> findPageMissingLocales(Map<String, Object> translations, List<Map<String, Object>> blocks);

    void validateBlockStructure(Map<String, Object> block, int blockIndex, String pathPrefix,
                                List<BookDtos.ValidationError> errors,
                                List<BookDtos.ValidationWarning> warnings);
}
