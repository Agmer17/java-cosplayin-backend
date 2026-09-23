package cosplayin.app.utils.storage;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import cosplayin.app.utils.storage.type.FileModel;
import cosplayin.app.utils.storage.type.FileValidationPolicy;
import cosplayin.app.utils.storage.type.SupportedFileExt;

public interface StorageUtils {

    FileModel saveFile(String basePath, MultipartFile filehader, SupportedFileExt extension, String... paths);

    List<FileModel> saveFile(String basePath, List<MultipartFile> filehader, List<SupportedFileExt> extension,
            String... paths);

    FileModel savePublicFile(MultipartFile fileheader, FileValidationPolicy policy, String... paths);

    List<FileModel> savePublicFile(List<MultipartFile> fileheader, FileValidationPolicy policy, String... paths);

    FileModel savePrivateFile(MultipartFile fileheader, FileValidationPolicy policy, String... paths);

    List<FileModel> savePrivateFile(List<MultipartFile> fileheader, FileValidationPolicy policy, String... paths);

    void deleteFile(String basePath, String filename, String... paths);

    void deleteFile(String basePath, List<String> filename, String... paths);

    void deletePublicFile(String filename, String... paths);

    void deletePublicFile(List<String> filename, String... paths);

    void deletePrivateFile(String filename, String... paths);

    void deletePrivateFile(List<String> filename, String... paths);

    SupportedFileExt validateFile(MultipartFile file, FileValidationPolicy policy);

    List<SupportedFileExt> validateFile(List<MultipartFile> file, FileValidationPolicy policy);

}
