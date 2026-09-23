package cosplayin.app.profiles.model.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmitOnBoardingRequests {

    @Size(min = 4, max = 255, message = "Username must be between 1 and 255 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._]+$", message = "Username can only contain letters, numbers, periods, and underscores")
    private String username;

    private MultipartFile profilePicture;

    private MultipartFile bannerPicture;

}
