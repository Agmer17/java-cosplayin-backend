package cosplayin.app.utils.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import cosplayin.app.core.exception.model.FatalErrorExceptions;
import cosplayin.app.core.exception.model.RequestValidationException;
import cosplayin.app.utils.storage.type.FileModel;
import cosplayin.app.utils.storage.type.FileValidationPolicy;
import cosplayin.app.utils.storage.type.SupportedFileExt;

@Component
public class ServerStorageimpl implements StorageUtils {

    private final MimeTypeResolver mimeTypeResolver;

    private Path root;
    private String publicPath;
    private String privatePath;

    public ServerStorageimpl(MimeTypeResolver resolver) {
        this.mimeTypeResolver = resolver;

        this.root = Paths.get("").toAbsolutePath().resolve("uploads");
        this.privatePath = "private";
        this.publicPath = "public";

        try {
            Files.createDirectories(root);
            Files.createDirectories(root.resolve(publicPath));
            Files.createDirectories(root.resolve(privatePath));
        } catch (IOException e) {
            throw new IllegalArgumentException("CANNOT CREATE SERVER STORAGE BEAN! : " + e.getMessage());
        }
    }

    @Override
    public FileModel saveFile(String basePath, MultipartFile filehader, SupportedFileExt extension, String... paths) {
        String filename = UUID.randomUUID().toString() + extension.getExtension();
        String relativePath = String.join("/", paths) + "/" + filename;

        try {
            Files.createDirectories(this.root.resolve(basePath, paths));
        } catch (IOException e) {
            e.printStackTrace();

            throw new FatalErrorExceptions("something wrong while trying to process the file");
        }

        Path savedFilePath = this.root.resolve(basePath, paths).resolve(filename);

        try {
            filehader.transferTo(savedFilePath);

            return FileModel.builder()
                    .filename(filename)
                    .filePath(relativePath)
                    .fileType(extension.getType())
                    .fileExt(extension)
                    .build();
        } catch (IOException e) {
            throw new FatalErrorExceptions("something went wrong while trying to save the files : " + e.getMessage());
        }
    }

    @Override
    public List<FileModel> saveFile(String basePath, List<MultipartFile> filehader, List<SupportedFileExt> extension,
            String... paths) {

        if (filehader.size() != extension.size()) {
            throw new RequestValidationException("unmatch filetype and file binary count");
        }

        int total = filehader.size();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            CompletionService<IndexedResult> completionService = new ExecutorCompletionService<>(executor);

            List<Future<IndexedResult>> futures = new ArrayList<>(total);
            for (int idx = 0; idx < total; idx++) {
                int i = idx;
                futures.add(completionService.submit(
                        () -> new IndexedResult(i, saveFile(basePath, filehader.get(i), extension.get(i), paths))));
            }

            FileModel[] savedArr = new FileModel[total];

            try {
                for (int i = 0; i < total; i++) {
                    IndexedResult result = completionService.take().get();
                    savedArr[result.index()] = result.model();
                }
            } catch (ExecutionException | InterruptedException e) {

                throw new FatalErrorExceptions(
                        "something went wrong while trying to save the files : " + e.getMessage());
            }

            return Arrays.asList(savedArr);
        }
    }

    private record IndexedResult(int index, FileModel model) {
    }

    @Override
    public FileModel savePublicFile(MultipartFile fileheader, FileValidationPolicy policy, String... paths) {
        SupportedFileExt ext = validateFile(fileheader, policy);
        FileModel saved = saveFile(publicPath, fileheader, ext, paths);

        return saved;
    }

    @Override
    public List<FileModel> savePublicFile(List<MultipartFile> fileheader, FileValidationPolicy policy,
            String... paths) {

        List<SupportedFileExt> fileExts = validateFile(fileheader, policy);
        List<FileModel> saved = saveFile(publicPath, fileheader, fileExts, paths);

        return saved;
    }

    @Override
    public FileModel savePrivateFile(MultipartFile fileheader, FileValidationPolicy policy, String... paths) {
        SupportedFileExt ext = validateFile(fileheader, policy);
        FileModel model = saveFile(privatePath, fileheader, ext, paths);

        return model;
    }

    @Override
    public List<FileModel> savePrivateFile(List<MultipartFile> fileheader, FileValidationPolicy policy,
            String... paths) {
        List<SupportedFileExt> filesExt = validateFile(fileheader, policy);

        List<FileModel> fileModels = saveFile(privatePath, fileheader, filesExt, paths);

        return fileModels;
    }

    @Override
    public void deleteFile(String basePath, String filename, String... paths) {
        Path filepath = this.root.resolve(basePath, paths).resolve(filename);
        System.out.println("FULL DELETED FILE PATH : " + filepath.toString());

        try {
            Files.deleteIfExists(filepath);
        } catch (IOException e) {
            // throw new FatalErrorExceptions("something wrong while")
            // just ignore it bruh
        }
    }

    @Override
    public void deleteFile(String basePath, List<String> filename, String... paths) {
        filename.forEach(f -> {
            Thread.startVirtualThread(() -> deleteFile(basePath, f, paths));
        });
    }

    @Override
    public void deletePublicFile(String filename, String... paths) {
        deleteFile(publicPath, filename, paths);
    }

    @Override
    public void deletePublicFile(List<String> filename, String... paths) {
        deleteFile(publicPath, filename, paths);
    }

    @Override
    public void deletePrivateFile(String filename, String... paths) {
        deleteFile(privatePath, filename, paths);
    }

    @Override
    public void deletePrivateFile(List<String> filename, String... paths) {
        deleteFile(privatePath, filename, paths);
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
