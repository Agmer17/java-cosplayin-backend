package cosplayin.app.utils.storage.type;

import java.util.Set;

public record FileValidationPolicy(
        Set<SupportedFileType> supportedType,
        long maxSize) {

}
