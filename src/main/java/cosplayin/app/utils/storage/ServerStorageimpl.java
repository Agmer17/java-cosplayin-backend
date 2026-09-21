package cosplayin.app.utils.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import cosplayin.app.utils.storage.type.FileModel;
import cosplayin.app.utils.storage.type.SupportedFileExt;
import cosplayin.app.utils.storage.type.SupportedFileType;

@Component
public class ServerStorageimpl implements StorageUtils {

    private final Tika apacheTika;
    private final MimeTypeResolver mimeTypeResolver;

    private Path root;
    private Path publicPath;
    private Path privatePath;

    // private final Map<String, SupportedFileExt> BY_MIME =

    public ServerStorageimpl(Tika tika, MimeTypeResolver resolver) {
        this.apacheTika = tika;
        this.mimeTypeResolver = resolver;

        this.root = Paths.get("").toAbsolutePath().resolve("uploads");
        this.privatePath = root.resolve("private");
        this.publicPath = root.resolve("public");

        try {
            Files.createDirectories(root);
            Files.createDirectories(publicPath);
            Files.createDirectories(privatePath);
        } catch (IOException e) {
            throw new IllegalArgumentException("CANNOT CREATE SERVER STORAGE BEAN! : " + e.getMessage());
        }
    }

    @Override
    public FileModel saveFile(Path basePath, MultipartFile fileHeader, String... paths) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveFile'");
    }

    @Override
    public List<FileModel> saveFile(Path basePath, List<MultipartFile> fileHeader, String... paths) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveFile'");
    }

    @Override
    public FileModel savePublicFile(MultipartFile fileHeader, String... paths) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'savePublicFile'");
    }

    @Override
    public List<FileModel> savePublicFile(List<MultipartFile> fileHeader, String... paths) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'savePublicFile'");
    }

    @Override
    public FileModel savePrivateFile(MultipartFile fileHeader, String... paths) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'savePrivateFile'");
    }

    @Override
    public List<FileModel> savePrivateFile(List<MultipartFile> fileHeader, String... paths) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'savePrivateFile'");
    }

    @Override
    public void deleteFile(Path basePath, String filename, String... paths) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteFile'");
    }

    @Override
    public void deleteFile(Path basePath, List<String> files, String... paths) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteFile'");
    }

    @Override
    public void deletePrivateFile(String files, String... paths) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deletePrivateFile'");
    }

    @Override
    public void deletePrivateFile(List<String> files, String... paths) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deletePrivateFile'");
    }

    @Override
    public void deletePublicFile(String filename, String... paths) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deletePublicFile'");
    }

    @Override
    public void deletePublicFile(List<String> files, String... paths) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deletePublicFile'");
    }

    @Override
    public SupportedFileExt validateFiles(MultipartFile file, SupportedFileType desiredType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateFiles'");
    }

    @Override
    public SupportedFileExt validateFiles(List<MultipartFile> files, SupportedFileType desiredType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateFiles'");
    }

}
