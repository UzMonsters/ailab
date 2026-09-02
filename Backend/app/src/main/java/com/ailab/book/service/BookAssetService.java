package com.ailab.book.service;

import com.ailab.book.dto.BookDtos;

import java.util.List;
import java.util.Map;

public interface BookAssetService {

    BookDtos.AssetUploadUrlsResponse generateUploadUrls(List<BookDtos.FileUploadSpec> files);

    BookDtos.AssetResponse completeAsset(String assetId, BookDtos.CompleteAssetRequest request);

    BookDtos.AssetResponse getAsset(String assetId);
}
