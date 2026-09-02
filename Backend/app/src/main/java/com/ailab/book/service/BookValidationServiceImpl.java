package com.ailab.book.service;

import com.ailab.book.domain.AssetStatus;
import com.ailab.book.domain.Book;
import com.ailab.book.domain.Chapter;
import com.ailab.book.domain.Page;
import com.ailab.book.dto.BookDtos;
import com.ailab.book.repository.BookAssetRepository;
import com.ailab.book.repository.BookRepository;
import com.ailab.book.repository.ChapterRepository;
import com.ailab.book.repository.PageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class BookValidationServiceImpl implements BookValidationService {

    private static final Set<String> SUPPORTED_LOCALES = Set.of("ru", "en", "uz");

    private static final Set<String> VALID_BLOCK_TYPES = Set.of(
            "RICH_TEXT", "PARAGRAPH", "HEADING", "LIST", "QUOTE", "FORMULA",
            "TABLE", "CALLOUT", "IMAGE", "SVG", "SANDBOX_EQUIPMENT",
            "SANDBOX_MATERIAL", "DIVIDER", "INTERACTIVE_EXPERIMENT_LINK",
            "rich_text", "paragraph", "heading", "list", "quote", "formula",
            "table", "callout", "image", "svg", "sandbox_equipment",
            "sandbox_material", "divider", "interactive_experiment_link",
            "sandbox-equipment", "sandbox-material", "interactive-experiment-link"
    );

    private final BookRepository bookRepository;
    private final ChapterRepository chapterRepository;
    private final PageRepository pageRepository;
    private final BookAssetRepository assetRepository;

    public BookValidationServiceImpl(BookRepository bookRepository,
                                    ChapterRepository chapterRepository,
                                    PageRepository pageRepository,
                                    BookAssetRepository assetRepository) {
        this.bookRepository = bookRepository;
        this.chapterRepository = chapterRepository;
        this.pageRepository = pageRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    public BookDtos.ValidationReport validateBook(String bookId, Long expectedVersion) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND: Book not found with id " + bookId));

        if (expectedVersion != null && !expectedVersion.equals(book.getDraftVersion())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "VERSION_CONFLICT: Expected draft version " + expectedVersion + " but current is " + book.getDraftVersion());
        }

        List<BookDtos.ValidationError> errors = new ArrayList<>();
        List<BookDtos.ValidationWarning> warnings = new ArrayList<>();

        if (book.getSlug() == null || book.getSlug().isBlank()) {
            errors.add(new BookDtos.ValidationError("slug", "VALIDATION_ERROR", "Book slug cannot be empty"));
        }

        String defaultLocale = book.getDefaultLocale() != null ? book.getDefaultLocale() : "ru";
        Map<String, Object> bookTranslations = book.getTranslations() != null ? book.getTranslations() : Map.of();

        if (!bookTranslations.containsKey(defaultLocale)) {
            errors.add(new BookDtos.ValidationError("translations." + defaultLocale, "MISSING_REQUIRED_TRANSLATION", "Default locale (" + defaultLocale + ") translation is required for book metadata"));
        }

        for (String loc : SUPPORTED_LOCALES) {
            if (!bookTranslations.containsKey(loc)) {
                warnings.add(new BookDtos.ValidationWarning("translations." + loc, "MISSING_LOCALE_TRANSLATION", "Missing metadata translation for locale: " + loc));
            }
        }

        List<Chapter> chapters = chapterRepository.findByBookIdOrderByPositionAsc(bookId);
        if (chapters.isEmpty()) {
            errors.add(new BookDtos.ValidationError("chapters", "VALIDATION_ERROR", "Book must contain at least one chapter"));
        }

        Set<String> pageSlugs = new HashSet<>();

        for (int cIdx = 0; cIdx < chapters.size(); cIdx++) {
            Chapter ch = chapters.get(cIdx);
            String chPath = "chapters[" + cIdx + "]";

            Map<String, Object> chTranslations = ch.getTranslations() != null ? ch.getTranslations() : Map.of();
            if (!chTranslations.containsKey(defaultLocale)) {
                errors.add(new BookDtos.ValidationError(chPath + ".translations." + defaultLocale, "MISSING_REQUIRED_TRANSLATION", "Default locale (" + defaultLocale + ") translation is required for chapter " + ch.getId()));
            }

            for (String loc : SUPPORTED_LOCALES) {
                if (!chTranslations.containsKey(loc)) {
                    warnings.add(new BookDtos.ValidationWarning(chPath + ".translations." + loc, "MISSING_LOCALE_TRANSLATION", "Missing chapter translation for locale: " + loc));
                }
            }

            List<Page> pages = pageRepository.findByChapterIdOrderByPositionAsc(ch.getId());
            for (int pIdx = 0; pIdx < pages.size(); pIdx++) {
                Page pg = pages.get(pIdx);
                String pgPath = chPath + ".pages[" + pIdx + "]";

                if (pg.getSlug() == null || pg.getSlug().isBlank()) {
                    errors.add(new BookDtos.ValidationError(pgPath + ".slug", "VALIDATION_ERROR", "Page slug cannot be empty"));
                } else if (!pageSlugs.add(pg.getSlug())) {
                    errors.add(new BookDtos.ValidationError(pgPath + ".slug", "DUPLICATE_PAGE_SLUG", "Duplicate page slug within book: " + pg.getSlug()));
                }

                Map<String, Object> pgTranslations = pg.getTranslations() != null ? pg.getTranslations() : Map.of();
                if (!pgTranslations.containsKey(defaultLocale)) {
                    warnings.add(new BookDtos.ValidationWarning(pgPath + ".translations." + defaultLocale, "MISSING_LOCALE_TRANSLATION", "Page should have default locale (" + defaultLocale + ") title translation"));
                }

                List<Map<String, Object>> blocks = pg.getBlocks() != null ? pg.getBlocks() : List.of();
                for (int bIdx = 0; bIdx < blocks.size(); bIdx++) {
                    Map<String, Object> block = blocks.get(bIdx);
                    String bPath = pgPath + ".blocks[" + bIdx + "]";
                    validateBlockStructure(block, bIdx, bPath, errors, warnings);
                }
            }
        }

        return new BookDtos.ValidationReport(errors.isEmpty(), errors, warnings);
    }

    @Override
    public List<String> findMissingLocales(Map<String, Object> translations, List<String> requiredFields) {
        if (translations == null || translations.isEmpty()) {
            return new ArrayList<>(SUPPORTED_LOCALES);
        }
        List<String> missing = new ArrayList<>();
        for (String loc : SUPPORTED_LOCALES) {
            Object locObj = translations.get(loc);
            if (!(locObj instanceof Map<?, ?> locMap)) {
                missing.add(loc);
                continue;
            }
            if (requiredFields != null) {
                for (String field : requiredFields) {
                    Object val = locMap.get(field);
                    if (val == null || String.valueOf(val).isBlank()) {
                        missing.add(loc);
                        break;
                    }
                }
            }
        }
        return missing;
    }

    @Override
    public List<String> findPageMissingLocales(Map<String, Object> translations, List<Map<String, Object>> blocks) {
        Set<String> missing = new HashSet<>();
        missing.addAll(findMissingLocales(translations, List.of("title")));
        if (blocks != null) {
            for (Map<String, Object> block : blocks) {
                @SuppressWarnings("unchecked")
                Map<String, Object> bTrans = block.get("translations") instanceof Map<?, ?> m ? (Map<String, Object>) m : Map.of();
                String type = block.get("type") != null ? String.valueOf(block.get("type")).toUpperCase() : "";
                if ("IMAGE".equals(type) || "SVG".equals(type) || "SANDBOX_EQUIPMENT".equals(type) || "SANDBOX-EQUIPMENT".equals(type)) {
                    missing.addAll(findMissingLocales(bTrans, List.of("caption")));
                } else if (!"DIVIDER".equals(type)) {
                    missing.addAll(findMissingLocales(bTrans, List.of("content")));
                }
            }
        }
        return new ArrayList<>(missing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void validateBlockStructure(Map<String, Object> block, int blockIndex, String pathPrefix,
                                       List<BookDtos.ValidationError> errors,
                                       List<BookDtos.ValidationWarning> warnings) {
        if (block == null) {
            errors.add(new BookDtos.ValidationError(pathPrefix, "BLOCK_SCHEMA_INVALID", "Block cannot be null"));
            return;
        }

        Object rawType = block.get("type");
        if (rawType == null || String.valueOf(rawType).isBlank()) {
            errors.add(new BookDtos.ValidationError(pathPrefix + ".type", "BLOCK_SCHEMA_INVALID", "Block type is required"));
            return;
        }

        String typeStr = String.valueOf(rawType).trim();
        if (!VALID_BLOCK_TYPES.contains(typeStr)) {
            errors.add(new BookDtos.ValidationError(pathPrefix + ".type", "BLOCK_SCHEMA_INVALID", "Unsupported block type: " + typeStr));
            return;
        }

        String normType = typeStr.toUpperCase().replace('-', '_');

        Map<String, Object> trans = block.get("translations") instanceof Map<?, ?> m ? (Map<String, Object>) m : Map.of();
        Map<String, Object> data = block.get("data") instanceof Map<?, ?> m ? (Map<String, Object>) m : Map.of();

        if (normType.equals("IMAGE") || normType.equals("SVG") || normType.equals("SANDBOX_EQUIPMENT") || normType.equals("SANDBOX_MATERIAL")) {
            Object assetVariantsObj = data.get("assetVariants");
            if (assetVariantsObj instanceof Map<?, ?> variants) {
                Object light = variants.get("light");
                Object dark = variants.get("dark");
                if (light != null) {
                    checkAssetReady(String.valueOf(light), pathPrefix + ".data.assetVariants.light", errors, warnings);
                }
                if (dark == null && light != null) {
                    warnings.add(new BookDtos.ValidationWarning(pathPrefix, "ASSET_VARIANT_MISSING", "Dark SVG variant will fall back to light"));
                } else if (dark != null) {
                    checkAssetReady(String.valueOf(dark), pathPrefix + ".data.assetVariants.dark", errors, warnings);
                }
            }

            for (String loc : SUPPORTED_LOCALES) {
                if (trans.containsKey(loc)) {
                    Map<String, Object> locMap = trans.get(loc) instanceof Map<?, ?> lm ? (Map<String, Object>) lm : Map.of();
                    if (!locMap.containsKey("caption") || String.valueOf(locMap.get("caption")).isBlank()) {
                        warnings.add(new BookDtos.ValidationWarning(pathPrefix + ".translations." + loc + ".caption", "MISSING_TRANSLATION_CAPTION", "Caption is missing for " + loc));
                    }
                } else {
                    warnings.add(new BookDtos.ValidationWarning(pathPrefix + ".translations." + loc, "MISSING_LOCALE_TRANSLATION", "Missing image/equipment translation for locale: " + loc));
                }
            }
        } else if (!normType.equals("DIVIDER")) {
            for (String loc : SUPPORTED_LOCALES) {
                if (trans.containsKey(loc)) {
                    Map<String, Object> locMap = trans.get(loc) instanceof Map<?, ?> lm ? (Map<String, Object>) lm : Map.of();
                    if (!locMap.containsKey("content") && !data.containsKey("document")) {
                        warnings.add(new BookDtos.ValidationWarning(pathPrefix + ".translations." + loc + ".content", "MISSING_TRANSLATION_CONTENT", "Content is empty for " + loc));
                    }
                } else {
                    warnings.add(new BookDtos.ValidationWarning(pathPrefix + ".translations." + loc, "MISSING_LOCALE_TRANSLATION", "Missing block translation for locale: " + loc));
                }
            }
        }
    }

    private void checkAssetReady(String assetId, String path, List<BookDtos.ValidationError> errors, List<BookDtos.ValidationWarning> warnings) {
        if (assetId == null || assetId.isBlank() || assetId.startsWith("sample_") || assetId.startsWith("asset_")) {
            return;
        }
        assetRepository.findById(assetId).ifPresentOrElse(asset -> {
            if (asset.getStatus() == AssetStatus.FAILED) {
                errors.add(new BookDtos.ValidationError(path, "ASSET_NOT_READY", "Asset failed processing: " + assetId));
            } else if (asset.getStatus() == AssetStatus.PENDING) {
                warnings.add(new BookDtos.ValidationWarning(path, "ASSET_PENDING", "Asset is still pending upload/processing: " + assetId));
            }
        }, () -> {
            warnings.add(new BookDtos.ValidationWarning(path, "ASSET_NOT_FOUND", "Asset not found in database: " + assetId));
        });
    }
}
