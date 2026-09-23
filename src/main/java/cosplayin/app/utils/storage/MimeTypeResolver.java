package cosplayin.app.utils.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import cosplayin.app.utils.storage.type.SupportedFileExt;

@Component
public class MimeTypeResolver {

    private final Map<String, SupportedFileExt> mimeToExt;
    private final Tika apacheTika;

    public MimeTypeResolver(Tika tika) {
        Map<String, SupportedFileExt> map = new HashMap<>(
                Arrays.stream(SupportedFileExt.values())
                        .collect(Collectors.toMap(
                                ext -> ext.getMimeType().toLowerCase(Locale.ROOT),
                                Function.identity())));
        map.put("audio/x-wav", SupportedFileExt.WAV);
        map.put("audio/vnd.wave", SupportedFileExt.WAV);
        map.put("audio/wave", SupportedFileExt.WAV);

        this.apacheTika = tika;
        this.mimeToExt = Map.copyOf(map);
    }

    public Optional<SupportedFileExt> fromMimeType(String mimeType) {
        if (mimeType == null)
            return Optional.empty();
        String normalized = mimeType.split(";", 2)[0].trim().toLowerCase(Locale.ROOT);
        return Optional.ofNullable(mimeToExt.get(normalized));
    }

    public boolean isSupported(String mimeType) {
        return fromMimeType(mimeType).isPresent();
    }

    public Optional<SupportedFileExt> detect(InputStream in) throws IOException {
        return fromMimeType(apacheTika.detect(in));
    }

}
