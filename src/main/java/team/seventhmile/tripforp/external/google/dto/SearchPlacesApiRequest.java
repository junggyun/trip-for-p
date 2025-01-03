package team.seventhmile.tripforp.external.google.dto;

import lombok.AllArgsConstructor;
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
    private LocationBias locationBias = new LocationBias(
        new Circle(
            new Center(36.6357, 127.4912),
            50000.0
        )
    );

}
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class LocationBias {
    private Circle circle;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Circle {
    private Center center;
    private double radius;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Center {
    private double latitude;
    private double longitude;
}
