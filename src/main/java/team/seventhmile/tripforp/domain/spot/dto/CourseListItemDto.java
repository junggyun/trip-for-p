package team.seventhmile.tripforp.domain.spot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CourseListItemDto {
    private int sequence;       // Spot sequence
    private String placeTitle;  // Place's title from Spot
}
