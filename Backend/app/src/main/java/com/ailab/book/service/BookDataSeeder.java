package com.ailab.book.service;

import com.ailab.book.domain.Book;
import com.ailab.book.domain.BookStatus;
import com.ailab.book.domain.Chapter;
import com.ailab.book.domain.Page;
import com.ailab.book.dto.BookDtos;
import com.ailab.book.repository.BookPublishedSnapshotRepository;
import com.ailab.book.repository.BookRepository;
import com.ailab.book.repository.ChapterRepository;
import com.ailab.book.repository.PageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@Order(10)
public class BookDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BookDataSeeder.class);

    private final BookRepository bookRepository;
    private final ChapterRepository chapterRepository;
    private final PageRepository pageRepository;
    private final BookPublishedSnapshotRepository snapshotRepository;
    private final BookPublishService publishService;

    public BookDataSeeder(
            BookRepository bookRepository,
            ChapterRepository chapterRepository,
            PageRepository pageRepository,
            BookPublishedSnapshotRepository snapshotRepository,
            BookPublishService publishService
    ) {
        this.bookRepository = bookRepository;
        this.chapterRepository = chapterRepository;
        this.pageRepository = pageRepository;
        this.snapshotRepository = snapshotRepository;
        this.publishService = publishService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedChemistryLabBookIfMissing();
    }

    public void seedChemistryLabBookIfMissing() {
        String slug = "chemistry-lab";
        if (bookRepository.existsBySlug(slug)) {
            Book existing = bookRepository.findBySlug(slug).orElse(null);
            if (existing != null && existing.getStatus() == BookStatus.PUBLISHED && existing.getPublishedVersion() != null) {
                log.info("Book '{}' already exists and is published (version {})", slug, existing.getPublishedVersion());
                return;
            }
            if (existing != null && existing.getStatus() != BookStatus.PUBLISHED) {
                log.info("Book '{}' exists as draft; publishing it now", slug);
                try {
                    publishService.publishBook(existing.getId(), new BookDtos.PublishBookRequest(existing.getDraftVersion(), "seed-chem-lab-publish", "Initial seed publication"), "system", "System Seeder");
                } catch (Exception e) {
                    log.warn("Could not publish existing book '{}': {}", slug, e.getMessage());
                }
                return;
            }
        }

        log.info("Seeding book '{}'...", slug);
        String bookId = "book-chem-lab";

        Map<String, Object> bookTranslations = Map.of(
                "ru", Map.of(
                        "title", "Интерактивная химическая лаборатория",
                        "description", "Практикум и теория"
                ),
                "en", Map.of(
                        "title", "Interactive Chemistry Laboratory",
                        "description", "Practice and theory"
                ),
                "uz", Map.of(
                        "title", "Interaktiv kimyo laboratoriyasi",
                        "description", "Amaliyot va nazariya"
                )
        );

        Book book = new Book(bookId, slug, "ru", bookTranslations);
        bookRepository.save(book);

        String chapterId = "chapter-basics";
        Map<String, Object> chapterTranslations = Map.of(
                "ru", Map.of("title", "Основы"),
                "en", Map.of("title", "Basics"),
                "uz", Map.of("title", "Asoslar")
        );
        Chapter chapter = new Chapter(chapterId, bookId, 1, chapterTranslations);
        chapterRepository.save(chapter);

        String pageId = "page-mixtures";
        Map<String, Object> pageTranslations = Map.of(
                "ru", Map.of("title", "Смеси"),
                "en", Map.of("title", "Mixtures"),
                "uz", Map.of("title", "Aralashmalar")
        );

        List<Map<String, Object>> blocks = List.of(
                Map.of(
                        "id", "b-head-1",
                        "type", "HEADING",
                        "data", Map.of("level", 1),
                        "translations", Map.of(
                                "ru", Map.of("content", "Введение в химические смеси"),
                                "en", Map.of("content", "Introduction to Chemical Mixtures"),
                                "uz", Map.of("content", "Kimyoviy aralashmalarga kirish")
                        )
                ),
                Map.of(
                        "id", "b-para-1",
                        "type", "PARAGRAPH",
                        "data", Map.of(),
                        "translations", Map.of(
                                "ru", Map.of("content", "Смеси состоят из двух или более веществ, соединенных физически."),
                                "en", Map.of("content", "Mixtures consist of two or more substances combined physically."),
                                "uz", Map.of("content", "Aralashmalar jismoniy jihatdan birlashtirilgan ikki yoki undan ortiq moddalardan iborat.")
                        )
                )
        );

        Page page = new Page(pageId, chapterId, bookId, "mixtures", 1, "DEFAULT", pageTranslations);
        page.setBlocks(blocks);
        pageRepository.save(page);

        // Publish book
        try {
            publishService.publishBook(bookId, new BookDtos.PublishBookRequest(1L, "seed-chem-lab-publish", "Initial seed publication"), "system", "System Seeder");
            log.info("Book '{}' seeded and published successfully", slug);
        } catch (Exception e) {
            log.error("Failed to publish seeded book '{}'", slug, e);
        }
    }
}
