package team.seventhmile.tripforp.external.google.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleMapsPlaceApiDto {

    private String id;
    private List<String> types;
    private String formattedAddress;
    private int rating;
    private int userRatingCount;
    private PlaceName displayName;
    private PlaceType primaryTypeDisplayName;

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
}
