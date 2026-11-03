package cosplayin.app.cosplayevent.model.dto;


import jakarta.validation.constraints.*;
import lombok.NonNull;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCosplayEventDto(
        @NotBlank(message = "nama event tidak bisa kosong")
        @Size(min = 1, max = 500)
        String title,

        @NonNull
        MultipartFile media,

        @FutureOrPresent
        LocalDateTime startAt,

        @FutureOrPresent
        LocalDateTime endAt,

        String description,

        @URL(message = "tolong masukkan url yang valid")
        String eventInformationUrl,


        // Location information (Nominatim)
        @NotNull(message = "OSM ID wajib diisi")
        Long osmId,

        @NotBlank(message = "OSM type wajib diisi")
        String osmType,

        @NotBlank(message = "nama lokasi wajib diisi")
        String locationName,

        @NotBlank(message = "alamat lokasi wajib diisi")
        String locationAddress,

        @NotBlank(message = "kategori lokasi wajib diisi")
        String locationCategory,

        @NotBlank(message = "tipe lokasi wajib diisi")
        String locationType,

        @NotNull
        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        BigDecimal latitude,

        @NotNull
        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        BigDecimal longitude
) {
}
