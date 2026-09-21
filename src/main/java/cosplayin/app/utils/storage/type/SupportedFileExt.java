package cosplayin.app.utils.storage.type;

public enum SupportedFileExt {

    JPEG("image/jpeg", SupportedFileType.IMAGE),
    PNG("image/png", SupportedFileType.IMAGE),
    WEBP("image/webp", SupportedFileType.IMAGE),

    MP4("video/mp4", SupportedFileType.VIDEO),
    WEBM("video/webm", SupportedFileType.VIDEO),
    QUICKTIME("video/quicktime", SupportedFileType.VIDEO),

    MPEG("audio/mpeg", SupportedFileType.AUDIO),
    WAV("audio/wav", SupportedFileType.AUDIO),
    OGG("audio/ogg", SupportedFileType.AUDIO),

    PDF("application/pdf", SupportedFileType.DOCUMENT),
    DOC("application/msword", SupportedFileType.DOCUMENT),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", SupportedFileType.DOCUMENT),
    XLS("application/vnd.ms-excel", SupportedFileType.DOCUMENT),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", SupportedFileType.DOCUMENT),
    PPT("application/vnd.ms-powerpoint", SupportedFileType.DOCUMENT),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", SupportedFileType.DOCUMENT),
    TXT("text/plain", SupportedFileType.DOCUMENT);

    private final String mimeType;
    private final SupportedFileType type;

    SupportedFileExt(String mimeType, SupportedFileType type) {
        this.mimeType = mimeType;
        this.type = type;
    }

    public String getMimeType() {
        return mimeType;
    }

    public SupportedFileType getType() {
        return type;
    }

    public String getExtension() {
        return "." + this.name().toLowerCase();
    }
}