package com.ailab.book.service;

import com.ailab.book.dto.BookDtos;

import java.util.List;
import java.util.Map;

public interface BookAssetService {

    BookDtos.AssetUploadUrlsResponse generateUploadUrls(List<BookDtos.FileUploadSpec> files);

    default BookDtos.AssetUploadUrlsResponse generateUploadUrls(List<BookDtos.FileUploadSpec> files, String actorId) {
        return generateUploadUrls(files);
    }

    BookDtos.AssetResponse completeAsset(String assetId, BookDtos.CompleteAssetRequest request);

    default BookDtos.AssetResponse completeAsset(String assetId, BookDtos.CompleteAssetRequest request, String actorId) {
        return completeAsset(assetId, request);
    }

    BookDtos.AssetResponse getAsset(String assetId);
}
