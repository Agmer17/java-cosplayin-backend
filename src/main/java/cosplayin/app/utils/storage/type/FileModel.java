package cosplayin.app.utils.storage.type;

import java.nio.file.Path;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileModel {
    private String filename; // filename.mp4
    private Path filePath; // misal : public/video/filename.mp4
    private SupportedFileType fileType;
    private SupportedFileExt fileExt;
}
