package cosplayin.app.utils.storage;

import java.nio.file.Path;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import cosplayin.app.utils.storage.type.FileModel;
import cosplayin.app.utils.storage.type.SupportedFileExt;
import cosplayin.app.utils.storage.type.SupportedFileType;

public interface StorageUtils {
    FileModel saveFile(Path basePath, MultipartFile fileHeader, String... paths);

    List<FileModel> saveFile(Path basePath, List<MultipartFile> fileHeader, String... paths);

    FileModel savePublicFile(MultipartFile fileHeader, String... paths);

    List<FileModel> savePublicFile(List<MultipartFile> fileHeader, String... paths);

    FileModel savePrivateFile(MultipartFile fileHeader, String... paths);

    List<FileModel> savePrivateFile(List<MultipartFile> fileHeader, String... paths);

    void deleteFile(Path basePath, String filename, String... paths);

    void deleteFile(Path basePath, List<String> files, String... paths);

    void deletePrivateFile(String files, String... paths);

    void deletePrivateFile(List<String> files, String... paths);

    void deletePublicFile(String filename, String... paths);

    void deletePublicFile(List<String> files, String... paths);

    SupportedFileExt validateFiles(MultipartFile file, SupportedFileType desiredType);

    SupportedFileExt validateFiles(List<MultipartFile> files, SupportedFileType desiredType);

}
