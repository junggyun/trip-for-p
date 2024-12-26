package team.seventhmile.tripforp.domain.spot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.external.google.dto.PhotoPlaceResponse;

@Getter
@NoArgsConstructor
public class GetPopularPlaceResponse {
    private PhotoPlaceResponse place;
    private Long count;

    @Builder
    public GetPopularPlaceResponse(PhotoPlaceResponse place, Long count) {
        this.place = place;
        this.count = count;
    }
}
