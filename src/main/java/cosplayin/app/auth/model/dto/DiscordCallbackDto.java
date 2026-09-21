package cosplayin.app.auth.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscordCallbackDto {

    private String id;

    private String username;

    private String avatar;

    private String discriminator;

    @JsonProperty("public_flags")
    private Integer publicFlags;

    private Integer flags;

    private String banner;

    @JsonProperty("accent_color")
    private Integer accentColor;

    @JsonProperty("global_name")
    private String globalName;

    @JsonProperty("avatar_decoration_data")
    private Object avatarDecorationData;

    private Object collectibles;

    @JsonProperty("display_name_styles")
    private Object displayNameStyles;

    @JsonProperty("vad_colors")
    private Object vadColors;

    @JsonProperty("banner_color")
    private String bannerColor;

    private Object clan;

    @JsonProperty("primary_guild")
    private Object primaryGuild;

    @JsonProperty("mfa_enabled")
    private Boolean mfaEnabled;

    private String locale;

    @JsonProperty("premium_type")
    private Integer premiumType;

    private String email;

    private Boolean verified;

}
