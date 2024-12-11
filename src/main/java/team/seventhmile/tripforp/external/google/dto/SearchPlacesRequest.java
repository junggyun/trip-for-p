package team.seventhmile.tripforp.external.google.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchPlacesRequest {
    private String textQuery;
    private int pageSize;
    private String pageToken;

}
