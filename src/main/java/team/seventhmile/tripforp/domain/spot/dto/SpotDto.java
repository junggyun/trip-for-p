package team.seventhmile.tripforp.domain.spot.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.place.dto.PlaceDto;
import team.seventhmile.tripforp.domain.spot.entity.Spot;

@NoArgsConstructor
@Getter
public class SpotDto {

    private PlaceDto place;
    private LocalDate tripDate;
    private int sequence;
    private String memo;

    public SpotDto(Spot spot) {
        this.place = new PlaceDto(spot.getPlace());
        this.tripDate = spot.getTripDate();
        this.sequence = spot.getSequence();
        this.memo = spot.getMemo();
    }
}
