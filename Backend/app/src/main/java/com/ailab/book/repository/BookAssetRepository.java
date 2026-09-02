package com.ailab.book.repository;

import com.ailab.book.domain.BookAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookAssetRepository extends JpaRepository<BookAsset, String> {

    Optional<BookAsset> findByChecksum(String checksum);
}
