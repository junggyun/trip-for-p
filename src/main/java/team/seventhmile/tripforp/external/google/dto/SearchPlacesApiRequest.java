package team.seventhmile.tripforp.external.google.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchPlacesApiRequest {
    private String textQuery;
    private final String languageCode = "ko";
    private final String regionCode = "kr";
    private int pageSize;
    private String pageToken;

}
