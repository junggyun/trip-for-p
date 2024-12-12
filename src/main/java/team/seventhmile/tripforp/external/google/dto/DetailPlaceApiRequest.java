package team.seventhmile.tripforp.external.google.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetailPlaceApiRequest {
    private String id;
    private final String languageCode = "ko";
    private final String regionCode = "kr";
}
