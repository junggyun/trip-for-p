package team.seventhmile.tripforp.external.google.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchPlacesApiRequest {
    private String textQuery;
    private String languageCode;
    private String regionCode;
    private int pageSize;
    private String pageToken;

    @Builder
    public SearchPlacesApiRequest(String textQuery, String languageCode, String regionCode,
        int pageSize, String pageToken) {
        this.textQuery = textQuery;
        this.languageCode = languageCode;
        this.regionCode = regionCode;
        this.pageSize = pageSize;
        this.pageToken = pageToken;
    }
}
