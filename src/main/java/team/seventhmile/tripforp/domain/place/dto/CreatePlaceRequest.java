package team.seventhmile.tripforp.domain.place.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CreatePlaceRequest {

    private Long mapPlaceId;

    @Builder
    public CreatePlaceRequest(Long mapPlaceId) {
        this.mapPlaceId = mapPlaceId;
    }
}
