package com.ailab.storage.upload;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface UploadTicketRepository extends JpaRepository<UploadTicketEntity, String>, UploadTicketStore {

    Optional<UploadTicketEntity> findByTokenHash(String tokenHash);

    @Override
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update UploadTicketEntity t
               set t.status = com.ailab.storage.upload.UploadTicketStatus.UPLOADING
             where t.id = :id
               and t.status = com.ailab.storage.upload.UploadTicketStatus.ISSUED
               and t.expiresAt > :now
            """)
    int claimIssued(@Param("id") String id, @Param("now") Instant now);

    @Override
    @Query("""
            select t from UploadTicketEntity t
             where t.assetId = :assetId
               and t.actorId = :actorId
               and t.scope = :scope
               and (:workspaceId is null or t.workspaceId = :workspaceId)
               and (:previewId is null or t.previewId = :previewId)
               and (:variant is null or t.variant = :variant)
             order by t.createdAt desc
            limit 1
            """)
    Optional<UploadTicketEntity> findLatest(@Param("assetId") String assetId,
                                            @Param("actorId") String actorId,
                                            @Param("scope") UploadScope scope,
                                            @Param("workspaceId") String workspaceId,
                                            @Param("previewId") String previewId,
                                            @Param("variant") String variant);

    @Override
    @Query("""
            select t from UploadTicketEntity t
             where t.assetId = :assetId
             order by t.createdAt desc
            limit 1
            """)
    Optional<UploadTicketEntity> findLatestByAssetId(@Param("assetId") String assetId);
}
