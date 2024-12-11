package team.seventhmile.tripforp.external.google.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

@Getter
@NoArgsConstructor
public class GoogleMapsPlaceDto {

    private String id;
    private String name;
    private String category;
    private String address;
    private int rating;
    private int reviewCount;

    public GoogleMapsPlaceDto(GoogleMapsPlaceApiDto place) {
        this.id = place.getId();
        this.name = place.getDisplayName().getText();
        this.category = place.getPrimaryTypeDisplayName().getText();
        this.address = place.getFormattedAddress();
        this.rating = place.getRating();
        this.reviewCount = place.getUserRatingCount();
    }
}
