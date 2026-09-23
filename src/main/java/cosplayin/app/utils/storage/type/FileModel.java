package cosplayin.app.utils.storage.type;

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
    private String filename;
    private String filePath; // misal : public/video/filename.mp4
    private SupportedFileType fileType;
    private SupportedFileExt fileExt;
}
