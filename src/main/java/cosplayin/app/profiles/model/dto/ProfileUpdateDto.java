package cosplayin.app.profiles.model.dto;

import org.springframework.web.multipart.MultipartFile;

import cosplayin.app.profiles.model.type.Gender;
import cosplayin.app.profiles.model.type.ProfilesVisibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateDto {
    private String displayName;
    private String bio;

    private MultipartFile avatar;

    private MultipartFile banner;

    private ProfilesVisibility visibility = ProfilesVisibility.PUBLIC;

    private Gender gender;

}
