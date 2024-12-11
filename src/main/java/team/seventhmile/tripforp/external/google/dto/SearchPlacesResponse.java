package team.seventhmile.tripforp.external.google.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

@Getter
@NoArgsConstructor
public class SearchPlacesResponse {

    private List<GoogleMapsPlaceDto> places = new ArrayList<>();
    private String nextPageToken;

    @Builder
    public SearchPlacesResponse(List<GoogleMapsPlaceDto> places, String nextPageToken) {
        this.places = places;
        this.nextPageToken = nextPageToken;
    }
}
