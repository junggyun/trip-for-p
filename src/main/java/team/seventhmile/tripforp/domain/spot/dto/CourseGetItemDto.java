package team.seventhmile.tripforp.domain.spot.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.place.dto.PlaceDto;
import team.seventhmile.tripforp.domain.spot.entity.Spot;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CourseGetItemDto {
    private Long id;
    private PlaceDto place;
    private LocalDate tripDate;
    private String memo;
    private int sequence;

    public CourseGetItemDto(Spot spot) {
        this.id = spot.getId();
        this.place = new PlaceDto(spot.getPlace());
        this.tripDate = spot.getTripDate();
        this.sequence = spot.getSequence();
        this.memo = spot.getMemo();
    }
}

