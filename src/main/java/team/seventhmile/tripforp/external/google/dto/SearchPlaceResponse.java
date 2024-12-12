package team.seventhmile.tripforp.external.google.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchPlaceResponse {

    private String id;
    private String name;
    private String category;
    private String address;
    private int rating;
    private int reviewCount;

    public SearchPlaceResponse(GoogleMapsPlaceApiDto place) {
        this.id = place.getId();
        this.name = place.getDisplayName().getText();
        this.category = place.getPrimaryTypeDisplayName().getText();
        this.address = place.getFormattedAddress();
        this.rating = place.getRating();
        this.reviewCount = place.getUserRatingCount();
    }
}
