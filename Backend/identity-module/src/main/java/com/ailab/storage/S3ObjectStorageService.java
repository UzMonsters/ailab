package com.ailab.storage;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

@Service
@ConditionalOnProperty(prefix = "app.storage", name = "provider", havingValue = "s3")
public class S3ObjectStorageService implements ObjectStorageService {
    private static final Logger log = LoggerFactory.getLogger(S3ObjectStorageService.class);

    private final StorageProperties properties;
    private final S3Client s3Client;
    private final S3Presigner presigner;

    public S3ObjectStorageService(StorageProperties properties) {
        this.properties = properties;
        S3Configuration configuration = S3Configuration.builder()
                .pathStyleAccessEnabled(properties.pathStyle())
                .chunkedEncodingEnabled(false)
                .build();
        S3ClientBuilder clientBuilder = S3Client.builder()
                .region(Region.of(properties.region()))
                .serviceConfiguration(configuration);
        S3Presigner.Builder presignerBuilder = S3Presigner.builder()
                .region(Region.of(properties.region()))
                .serviceConfiguration(configuration);
        if (properties.accessKey() != null && !properties.accessKey().isBlank()
                && properties.secretKey() != null && !properties.secretKey().isBlank()) {
            StaticCredentialsProvider credentials = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(properties.accessKey(), properties.secretKey()));
            clientBuilder.credentialsProvider(credentials);
            presignerBuilder.credentialsProvider(credentials);
        } else {
            clientBuilder.credentialsProvider(DefaultCredentialsProvider.create());
            presignerBuilder.credentialsProvider(DefaultCredentialsProvider.create());
        }
        if (properties.endpoint() != null) {
            clientBuilder.endpointOverride(properties.endpoint());
            presignerBuilder.endpointOverride(properties.endpoint());
        }
        this.s3Client = clientBuilder.build();
        this.presigner = presignerBuilder.build();
    }

    @PostConstruct
    void initializeBucket() {
        RuntimeException lastException = null;
        for (int attempt = 1; attempt <= 5; attempt++) {
            try {
                s3Client.headBucket(HeadBucketRequest.builder().bucket(properties.bucket()).build());
                return;
            } catch (NoSuchBucketException exception) {
                if (!properties.createBucket()) {
                    throw new StorageException("Storage bucket does not exist.", exception);
                }
                createBucket();
                return;
            } catch (RuntimeException exception) {
                lastException = exception;
                if (attempt < 5) {
                    sleepBeforeRetry(attempt);
                }
            }
        }
        throw new StorageException("Storage bucket is unavailable.", lastException);
    }

    private void createBucket() {
        RuntimeException lastException = null;
        for (int attempt = 1; attempt <= 5; attempt++) {
            try {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(properties.bucket()).build());
                return;
            } catch (S3Exception exception) {
                if (exception.statusCode() == 409) {
                    return;
                }
                lastException = exception;
                if (attempt < 5) {
                    sleepBeforeRetry(attempt);
                }
            } catch (RuntimeException exception) {
                lastException = exception;
                if (attempt < 5) {
                    sleepBeforeRetry(attempt);
                }
            }
        }
        throw new StorageException("Storage bucket could not be created.", lastException);
    }

    @Override
    public StoredObject upload(StorageUpload upload) {
        try {
            byte[] bytes = upload.inputStream().readAllBytes();
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(properties.bucket())
                            .key(upload.storageKey())
                            .contentType(upload.contentType())
                            .contentLength((long) bytes.length)
                            .build(),
                    RequestBody.fromBytes(bytes));
            return new StoredObject(upload.storageKey(), upload.contentType(), bytes.length);
        } catch (IOException exception) {
            throw new StorageException("Object upload input could not be read.", exception);
        } catch (RuntimeException exception) {
            throw new StorageException("Object upload failed.", exception);
        }
    }

    @Override
    public StoredObjectDownload download(String storageKey) {
        try {
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(properties.bucket())
                    .key(storageKey)
                    .build());
            return new StoredObjectDownload(
                    response.response().contentType(),
                    response.response().contentLength(),
                    response);
        } catch (NoSuchKeyException exception) {
            throw new StorageException("Object not found.", exception);
        } catch (RuntimeException exception) {
            throw new StorageException("Object download failed.", exception);
        }
    }

    @Override
    public boolean exists(String storageKey) {
        try {
            s3Client.headObject(HeadObjectRequest.builder().bucket(properties.bucket()).key(storageKey).build());
            return true;
        } catch (NoSuchKeyException exception) {
            return false;
        } catch (S3Exception exception) {
            if (exception.statusCode() == 404) {
                return false;
            }
            throw new StorageException("Object existence check failed.", exception);
        } catch (RuntimeException exception) {
            throw new StorageException("Object existence check failed.", exception);
        }
    }

    @Override
    public void delete(String storageKey) {
        RuntimeException lastException = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                s3Client.deleteObject(DeleteObjectRequest.builder().bucket(properties.bucket()).key(storageKey).build());
                return;
            } catch (RuntimeException exception) {
                lastException = exception;
                if (attempt < 3) {
                    sleepBeforeRetry(attempt);
                }
            }
        }
        throw new StorageException("Object deletion failed.", lastException);
    }

    @Override
    public URI createPresignedDownloadUrl(String storageKey, String downloadFileName, Duration ttl) {
        try {
            URI uri = URI.create(presigner.presignGetObject(GetObjectPresignRequest.builder()
                            .signatureDuration(ttl)
                            .getObjectRequest(GetObjectRequest.builder()
                                    .bucket(properties.bucket())
                                    .key(storageKey)
                                    .build())
                            .build())
                    .url()
                    .toString());
            log.info("Created presigned S3 download URL storageKey={} ttl={}", storageKey, ttl);
            return uri;
        } catch (RuntimeException exception) {
            throw new StorageException("Download URL creation failed.", exception);
        }
    }

    private void sleepBeforeRetry(int attempt) {
        try {
            Thread.sleep(200L * attempt);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new StorageException("Storage retry interrupted.", exception);
        }
    }
}
