package team.seventhmile.tripforp.external.google.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleMapsPlaceApiDto {

    private String id;
    private String formattedAddress;
    private int rating;
    private int userRatingCount;
    private PlaceName displayName;
    private PlaceType primaryTypeDisplayName;
    private PlaceLocation location;

    @Getter
    @NoArgsConstructor
    public static class PlaceName {
        private String text;
    }

    @Getter
    @NoArgsConstructor
    public static class PlaceType {
        private String text;
    }

    @Getter
    @NoArgsConstructor
    public static class PlaceLocation {
        private double latitude;
        private double longitude;
    }
}
