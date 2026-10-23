package cosplayin.app.utils.storage;

import cosplayin.app.core.exception.model.FatalErrorExceptions;
import cosplayin.app.core.exception.model.RequestValidationException;
import cosplayin.app.utils.storage.type.FileModel;
import cosplayin.app.utils.storage.type.FileValidationPolicy;
import cosplayin.app.utils.storage.type.SupportedFileExt;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public class S3FileStorageUtils  implements StorageUtils{

    @Value("${storage.s3.bucket.private}")
    private String privateBucket;

    @Value("${storage.s3.bucket.private}")
    private String publicBucket;

    private final S3Client client;

    private final MimeTypeResolver mimeTypeResolver;

    @Override
    public FileModel saveFile(String bucket, MultipartFile filehader, SupportedFileExt extension, String... paths) {
        String filename = UUID.randomUUID().toString() + extension.getExtension();

        String relativePath = String.join("/", paths) + "/" + filename;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(relativePath)
                .contentType(extension.getMimeType())
                .contentLength(filehader.getSize())
                .build();
        try {
            client.putObject(putObjectRequest, RequestBody.fromInputStream(filehader.getInputStream(), filehader.getSize()));
        System.out.println("file saved at : " + relativePath);
        } catch (AwsServiceException | SdkClientException | IOException e) {
            throw new FatalErrorExceptions("something wrong while trying to upload the file! : " + e.getMessage());
        }


        return FileModel.builder()
                .filename(filename)
                .filePath(relativePath)
                .fileType(extension.getType())
                .fileExt(extension)
                .build();
    }

    @Override
    public List<FileModel> saveFile(String bucket, List<MultipartFile> files,
                                    List<SupportedFileExt> extensions, String... paths) {
        if (files.size() != extensions.size()) {
            throw new RequestValidationException("unmatch filetype and file binary count");
        }
        int total = files.size();
        List<FileModel> saved = new ArrayList<>(total);
        Throwable failure = null;

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<FileModel>> futures = new ArrayList<>(total);
            for (int i = 0; i < total; i++) {
                int idx = i;
                futures.add(executor.submit(
                        () -> saveFile(bucket, files.get(idx), extensions.get(idx), paths)));
            }

            for (Future<FileModel> f : futures) {
                try {
                    saved.add(f.get());
                } catch (ExecutionException e) {
                    if (failure == null)
                        failure = e.getCause();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    if (failure == null)
                        failure = e;
                    futures.forEach(x -> x.cancel(true));
                    break;
                }
            }
        }

        if (failure != null) {

            List<String> failedSavedFile = saved.stream().map(FileModel::getFilePath).toList();

            deleteFile(bucket, failedSavedFile, paths);
            throw new FatalErrorExceptions(
                    "failed to save files: " + failure.getMessage());
        }
        return saved;
    }

    @Override
    public FileModel savePublicFile(MultipartFile fileheader, FileValidationPolicy policy, String... paths) {
        SupportedFileExt ext = validateFile(fileheader, policy);
        return saveFile(this.publicBucket, fileheader, ext, paths);
    }

    @Override
    public List<FileModel> savePublicFile(List<MultipartFile> fileheader, FileValidationPolicy policy, String... paths) {
        List<SupportedFileExt> fileExts = validateFile(fileheader, policy);

        return saveFile(this.publicBucket, fileheader, fileExts, paths);
    }

    @Override
    public FileModel savePrivateFile(MultipartFile fileheader, FileValidationPolicy policy, String... paths) {
        SupportedFileExt ext = validateFile(fileheader, policy);

        return saveFile(this.privateBucket, fileheader, ext, paths);
    }

    @Override
    public List<FileModel> savePrivateFile(List<MultipartFile> fileheader, FileValidationPolicy policy, String... paths) {
        List<SupportedFileExt> filesExt = validateFile(fileheader, policy);

        return saveFile(this.privateBucket, fileheader, filesExt, paths);
    }

    @Override
    public void deleteFile(String bucket, String key, String... paths) {

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();


        try {
        client.deleteObject(request);

        } catch ( AwsServiceException | SdkClientException e) {
            System.out.println("FAILED TO DELETE THE FILE : " + e.getMessage());
        }

    }

    @Override
    public void deleteFile(String bucket, List<String> keys, String... paths) {
        DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(Delete.builder()
                        .objects(
                                keys.stream()
                                        .map(key -> ObjectIdentifier.builder()
                                                .key(key)
                                                .build())
                                        .toList()
                        )
                        .build())
                .build();

        try {
        client.deleteObjects(request);

        } catch (AwsServiceException | SdkClientException e) {
            System.out.println("FAILED TO DELETE THE FILES : " + e.getMessage());
        }
    }

    @Override
    public void deletePublicFile(String keys, String... paths) {

        deleteFile(this.publicBucket, keys, paths);

    }

    @Override
    public void deletePublicFile(List<String> keys, String... paths) {

        deleteFile(this.publicBucket, keys, paths);
    }

    @Override
    public void deletePrivateFile(String keys, String... paths) {
        deleteFile(this.privateBucket, keys, paths);
    }

    @Override
    public void deletePrivateFile(List<String> keys, String... paths) {
        deleteFile(this.privateBucket, keys, paths);
    }

    @Override
    public SupportedFileExt validateFile(MultipartFile file, FileValidationPolicy policy) {
        if (file.getSize() > policy.maxSize()) {
            throw new RequestValidationException("file are too large");
        }

        try (InputStream stream = file.getInputStream()) {

            SupportedFileExt ext = mimeTypeResolver.detect(stream)
                    .orElseThrow(() -> new RequestValidationException("the filetype are not supported currently!"));

            if (!policy.supportedType().contains(ext.getType())) {
                throw new RequestValidationException(
                        "this file type are not allowed, the allowed are : " + policy.supportedType().toString());
            }

            return ext;

        } catch (IOException e) {
            throw new FatalErrorExceptions("something wrong while trying to process the file : " + e.getMessage());
        }
    }

    @Override
    public List<SupportedFileExt> validateFile(List<MultipartFile> files, FileValidationPolicy policy) {
        ArrayList<SupportedFileExt> ext = new ArrayList<>(files.size());

        for (MultipartFile f : files) {
            ext.add(validateFile(f, policy));
        }

        return ext;
    }
}
